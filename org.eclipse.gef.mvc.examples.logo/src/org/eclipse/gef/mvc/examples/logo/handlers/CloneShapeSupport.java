/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.handlers;

import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.mvc.examples.logo.model.GeometricShape;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricShapePart;

import javafx.scene.paint.Paint;

// only applicable for GeometricShapePart
public class CloneShapeSupport extends AbstractCloneContentSupport {

	@Override
	public Object cloneContent() {
		GeometricShape originalShape = getAdaptable().getContent();
		// It seems to be fine to not clone the effect (which is not supported
		// via JavaFX public API)
		GeometricShape shape = new GeometricShape((IShape) originalShape.getGeometry().getCopy(),
				originalShape.getTransform().getCopy(), copyPaint(originalShape.getFill()), originalShape.getEffect());
		shape.setStroke(copyPaint(originalShape.getStroke()));
		shape.setStrokeWidth(originalShape.getStrokeWidth());
		return shape;
	}

	private Paint copyPaint(Paint paint) {
		// TODO: Verify this is sufficient.
		return Paint.valueOf(paint.toString());
	}

	@Override
	public GeometricShapePart getAdaptable() {
		return (GeometricShapePart) super.getAdaptable();
	}
}
