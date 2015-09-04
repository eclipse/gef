/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Scale;

/**
 * The {@link FXScaleRelocatePolicy} is a {@link ITransactional transactional}
 * {@link AbstractPolicy policy} that handles the scaling/relocation of its
 * {@link #getHost() host}.
 *
 * @author mwienand
 *
 */
public class FXScaleRelocatePolicy extends AbstractPolicy<Node>
		implements ITransactional {

	/**
	 * Stores the <i>initialized</i> flag for this policy, i.e.
	 * <code>true</code> after {@link #init()} was called, and
	 * <code>false</code> after {@link #commit()} was called, respectively.
	 */
	protected boolean initialized;
	private AffineTransform oldTransform;

	@Override
	public IUndoableOperation commit() {
		if (!initialized) {
			return null;
		}
		// after commit, we need to be re-initialized
		initialized = false;
		// assemble commits of delegate policies to one operation
		ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation(
				"ScaleRelocate");
		// TODO: entirely remove the FXResizePolicy from this class.
		IUndoableOperation operation = getResizePolicy().commit();
		if (operation != null) {
			fwd.add(operation);
		}
		operation = getTransformPolicy().commit();
		if (operation != null) {
			fwd.add(operation);
		}
		return fwd.unwrap();
	}

	/**
	 * Returns the {@link FXResizePolicy} that is installed on the
	 * {@link #getHost() host}.
	 *
	 * @return The {@link FXResizePolicy} that is installed on the
	 *         {@link #getHost() host}.
	 */
	// TODO: Remove this method and entirely remove the FXResizePolicy from this
	// class.
	protected FXResizePolicy getResizePolicy() {
		return getHost().getAdapter(FXResizePolicy.class);
	}

	/**
	 * Returns the {@link FXTransformPolicy} that is installed on the
	 * {@link #getHost() host}.
	 *
	 * @return The {@link FXTransformPolicy} that is installed on the
	 *         {@link #getHost() host}.
	 */
	protected FXTransformPolicy getTransformPolicy() {
		return getHost().getAdapter(FXTransformPolicy.class);
	}

	@Override
	public void init() {
		// initialize delegate policies
		getTransformPolicy().init();
		// TODO: entirely remove the FXResizePolicy from this class.
		getResizePolicy().init();
		oldTransform = JavaFX2Geometry
				.toAffineTransform(getTransformPolicy().getNodeTransform());
		initialized = true;
	}

	/**
	 * Applies a scale/relocate transformation to its {@link #getHost() host}.
	 * The transformation reflects the transformation from the given old
	 * {@link Bounds} to the given new {@link Bounds}.
	 *
	 * @param oldBoundsInScene
	 *            The old {@link Bounds} of the {@link #getHost() host}.
	 * @param newBoundsInScene
	 *            The new {@link Bounds} of the {@link #getHost() host}.
	 */
	public void performScaleRelocate(Bounds oldBoundsInScene,
			Bounds newBoundsInScene) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		// compute scale
		double sx = newBoundsInScene.getWidth() / oldBoundsInScene.getWidth();
		double sy = newBoundsInScene.getHeight() / oldBoundsInScene.getHeight();
		AffineTransform scale = JavaFX2Geometry
				.toAffineTransform(new Scale(sx, sy, 0, 0));
		// compute translation in host's parent
		double dx = newBoundsInScene.getMinX() - oldBoundsInScene.getMinX();
		double dy = newBoundsInScene.getMinY() - oldBoundsInScene.getMinY();
		Point2D originInParent = getHost().getVisual().getParent()
				.sceneToLocal(0, 0);
		Point2D deltaInParent = getHost().getVisual().getParent()
				.sceneToLocal(dx, dy);
		dx = deltaInParent.getX() - originInParent.getX();
		dy = deltaInParent.getY() - originInParent.getY();
		AffineTransform translate = new AffineTransform().setToTranslation(dx,
				dy);
		// put together
		getTransformPolicy().setTransform(
				translate.concatenate(oldTransform).concatenate(scale));
	}

}
