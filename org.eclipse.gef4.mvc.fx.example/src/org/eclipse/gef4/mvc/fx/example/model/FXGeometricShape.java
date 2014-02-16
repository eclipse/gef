/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.model;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;

public class FXGeometricShape extends AbstractFXGeometricElement<IShape> {

	public Color fill;

	public FXGeometricShape(IShape shape, AffineTransform transform,
			Color fill, Effect effect) {
		this(shape, transform, new Color(0, 0, 0, 1), 1.0, fill, effect);
	}

	public FXGeometricShape(IShape shape, AffineTransform transform,
			Color stroke, double strokeWidth, Color fill, Effect effect) {
		super(shape, transform, stroke, strokeWidth, effect);
		this.fill = fill;
	}

}
