/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.handlers;

import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.mvc.examples.logo.model.GeometricShape;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricShapePart;

import com.sun.scenario.effect.EffectHelper;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;

// only applicable for GeometricShapePart
public class CloneShapeSupport extends AbstractCloneContentSupport {

	@Override
	public Object cloneContent() {
		GeometricShape originalShape = getAdaptable().getContent();
		GeometricShape shape = new GeometricShape((IShape) originalShape.getGeometry().getCopy(),
				originalShape.getTransform().getCopy(), copyPaint(originalShape.getFill()),
				copyEffect(originalShape.getEffect()));
		shape.setStroke(copyPaint(originalShape.getStroke()));
		shape.setStrokeWidth(originalShape.getStrokeWidth());
		return shape;
	}

	private Effect copyEffect(Effect effect) {
		return EffectHelper.copy(effect);
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
