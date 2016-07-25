/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.policies;

import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef.mvc.examples.logo.parts.FXGeometricShapePart;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;

// only applicable for FXGeometricShapePart
public class CloneShapePolicy extends AbstractCloneContentPolicy {

	@Override
	public Object cloneContent() {
		FXGeometricShape originalShape = getHost().getContent();
		FXGeometricShape shape = new FXGeometricShape((IShape) originalShape.getGeometry().getCopy(),
				originalShape.getTransform().getCopy(), copyPaint(originalShape.getFill()),
				copyEffect(originalShape.getEffect()));
		shape.setStroke(copyPaint(originalShape.getStroke()));
		shape.setStrokeWidth(originalShape.getStrokeWidth());
		return shape;
	}

	private Effect copyEffect(Effect effect) {
		// FIXME [JDK-internal]: Do not use deprecated method to copy Effect.
		return effect.impl_copy();
	}

	private Paint copyPaint(Paint paint) {
		// TODO: Verify this is sufficient.
		return Paint.valueOf(paint.toString());
	}

	@Override
	public FXGeometricShapePart getHost() {
		return (FXGeometricShapePart) super.getHost();
	}

}
