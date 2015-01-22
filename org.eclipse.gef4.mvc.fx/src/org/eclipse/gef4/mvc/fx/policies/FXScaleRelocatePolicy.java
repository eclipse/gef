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
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.mvc.fx.operations.FXResizeRelocateNodeOperation;
import org.eclipse.gef4.mvc.fx.operations.FXTransformOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public class FXScaleRelocatePolicy extends AbstractPolicy<Node> implements
		ITransactional {

	private ForwardUndoCompositeOperation fwdOperation;
	private FXResizeRelocateNodeOperation rrOperation;
	private FXTransformOperation txOperation;
	private Point2D pivot;

	@Override
	public IUndoableOperation commit() {
		IUndoableOperation commit = fwdOperation;
		fwdOperation = null;
		rrOperation = null;
		txOperation = null;
		return commit;
	}

	@Override
	public void init() {
		// assemble operations
		fwdOperation = new ForwardUndoCompositeOperation("Scale");
		rrOperation = new FXResizeRelocateNodeOperation(getHost().getVisual());
		txOperation = new FXTransformOperation(getHost());
		fwdOperation.add(rrOperation);
		fwdOperation.add(txOperation);
		// determine pivot point for scale
		Bounds bounds = getHost().getVisual().getLayoutBounds();
		pivot = new Point2D(bounds.getMinX() + bounds.getWidth() / 2,
				bounds.getMinY() + bounds.getHeight() / 2);
	}

	public void performScaleRelocate(Bounds oldBoundsInScene,
			Bounds newBoundsInScene) {
		double sx = newBoundsInScene.getWidth() / oldBoundsInScene.getWidth();
		double sy = newBoundsInScene.getHeight() / oldBoundsInScene.getHeight();
		Scale scale = new Scale(sx, sy, pivot.getX(), pivot.getY());
		// concatenate initial transformation with the scale transformation
		Affine newTransform = Geometry2JavaFX.toFXAffine(JavaFX2Geometry
				.toAffineTransform(txOperation.getOldTransform()).concatenate(
						JavaFX2Geometry.toAffineTransform(scale)));
		txOperation.setNewTransform(newTransform);
	}

}
