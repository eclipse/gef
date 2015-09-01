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

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Scale;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public class FXScaleRelocatePolicy extends AbstractPolicy<Node> implements
		ITransactional {

	private AffineTransform oldTransform;

	@Override
	public IUndoableOperation commit() {
		// assemble commits of delegate policies to one operation
		ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation(
				"ScaleRelocate");
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

	protected FXResizePolicy getResizePolicy() {
		return getHost().getAdapter(FXResizePolicy.class);
	}

	protected FXTransformPolicy getTransformPolicy() {
		return getHost().getAdapter(FXTransformPolicy.class);
	}

	@Override
	public void init() {
		// initialize delegate policies
		getTransformPolicy().init();
		getResizePolicy().init();
		oldTransform = JavaFX2Geometry.toAffineTransform(getTransformPolicy()
				.getNodeTransform());
	}

	public void performScaleRelocate(Bounds oldBoundsInScene,
			Bounds newBoundsInScene) {
		// compute scale
		double sx = newBoundsInScene.getWidth() / oldBoundsInScene.getWidth();
		double sy = newBoundsInScene.getHeight() / oldBoundsInScene.getHeight();
		AffineTransform scale = JavaFX2Geometry.toAffineTransform(new Scale(sx,
				sy, 0, 0));
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
