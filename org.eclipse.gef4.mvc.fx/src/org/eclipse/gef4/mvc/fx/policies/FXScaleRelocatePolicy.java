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

	private Point2D pivot;
	private AffineTransform oldTransform;

	@Override
	public IUndoableOperation commit() {
		// assemble commits of delegate policies to one operation
		ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation(
				"ScaleRelocate");
		fwd.add(getResizePolicy().commit());
		fwd.add(getTransformPolicy().commit());
		return fwd;
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
		// determine pivot point for scale
		Bounds bounds = getHost().getVisual().getLayoutBounds();
		pivot = new Point2D(bounds.getMinX() + bounds.getWidth() / 2,
				bounds.getMinY() + bounds.getHeight() / 2);
	}

	public void performScaleRelocate(Bounds oldBoundsInScene,
			Bounds newBoundsInScene) {
		double sx = newBoundsInScene.getWidth() / oldBoundsInScene.getWidth();
		double sy = newBoundsInScene.getHeight() / oldBoundsInScene.getHeight();
		AffineTransform scale = JavaFX2Geometry.toAffineTransform(new Scale(sx,
				sy, pivot.getX(), pivot.getY()));

		double dw = newBoundsInScene.getWidth() - oldBoundsInScene.getWidth();
		double dh = newBoundsInScene.getHeight() - oldBoundsInScene.getHeight();
		double dx = newBoundsInScene.getMinX() - oldBoundsInScene.getMinX();
		double dy = newBoundsInScene.getMinY() - oldBoundsInScene.getMinY();

		AffineTransform translate = new AffineTransform().setToTranslation(dx
				+ dw / 2, dy + dh / 2);

		getTransformPolicy().setTransform(
				translate.concatenate(oldTransform).concatenate(scale));
	}

}
