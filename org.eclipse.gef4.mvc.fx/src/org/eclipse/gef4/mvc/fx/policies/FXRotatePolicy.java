/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link FXRotatePolicy} is a {@link ITransactional transactional}
 * {@link AbstractPolicy policy} that handles the rotation of its
 * {@link #getHost() host}.
 *
 * @author anyssen
 *
 */
public class FXRotatePolicy extends AbstractPolicy<Node>
		implements ITransactional {

	/**
	 * Stores the <i>initialized</i> flag for this policy, i.e.
	 * <code>true</code> after {@link #init()} was called, and
	 * <code>false</code> after {@link #commit()} was called, respectively.
	 */
	protected boolean initialized;

	@Override
	public IUndoableOperation commit() {
		if (!initialized) {
			return null;
		}
		// after commit, we need to be re-initialized
		initialized = false;
		return getTransformPolicy().commit();
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
		// initialize transaction policy
		getTransformPolicy().init();
		initialized = true;
	}

	/**
	 * Rotates the {@link #getHost() host} by the given {@link Angle} around the
	 * given pivot {@link Point}.
	 *
	 * @param rotationAngle
	 *            The rotation {@link Angle}.
	 * @param pivotInScene
	 *            The pivot {@link Point} in scene coordinates.
	 */
	public void performRotation(Angle rotationAngle, Point pivotInScene) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		// convert to local coordinates
		Point2D pivotLocal = getHost().getVisual().sceneToLocal(pivotInScene.x,
				pivotInScene.y);
		// take scaling into account
		FXTransformPolicy transformPolicy = getTransformPolicy();
		AffineTransform oldTransform = JavaFX2Geometry
				.toAffineTransform(transformPolicy.getNodeTransform());
		double scaleX = oldTransform.getScaleX();
		double scaleY = oldTransform.getScaleY();
		// update operation
		updateOperation(rotationAngle, new Point(pivotLocal.getX() * scaleX,
				pivotLocal.getY() * scaleY));
	}

	/**
	 * Applies the rotation given by the rotation {@link Angle} and the pivot
	 * {@link Point} to the {@link #getHost() host}.
	 *
	 * @param rotationAngle
	 *            The rotation {@link Angle}.
	 * @param pivotInHostVisual
	 *            The pivot {@link Point} in host coordinates.
	 */
	protected void updateOperation(Angle rotationAngle,
			Point pivotInHostVisual) {
		// determine scaling
		FXTransformPolicy transformPolicy = getTransformPolicy();
		Affine nodeTransform = transformPolicy.getNodeTransform();
		AffineTransform oldTransform = JavaFX2Geometry
				.toAffineTransform(nodeTransform);
		double scaleX = oldTransform.getScaleX();
		double scaleY = oldTransform.getScaleY();
		// compute rotation; ensure rotation is done before scaling
		AffineTransform rotate = new AffineTransform()
				.scale(1 / scaleX, 1 / scaleY).rotate(rotationAngle.rad(),
						pivotInHostVisual.x, pivotInHostVisual.y)
				.scale(scaleX, scaleY);
		// apply rotation to the current transformations
		transformPolicy.setConcatenation(rotate);
	}

}
