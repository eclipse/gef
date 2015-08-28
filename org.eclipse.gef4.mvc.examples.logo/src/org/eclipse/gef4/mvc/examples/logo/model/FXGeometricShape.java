/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.model;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;

public class FXGeometricShape extends AbstractFXGeometricElement<IShape> {

	private final Set<AbstractFXGeometricElement<? extends IGeometry>> anchorages = new HashSet<AbstractFXGeometricElement<? extends IGeometry>>();

	public static final String FILL_PROPERTY = "fill";

	private Paint fill;

	public FXGeometricShape(IShape shape, AffineTransform transform,
			Color stroke, double strokeWidth, Paint fill, Effect effect) {
		super(shape, transform, stroke, strokeWidth, effect);
		setFill(fill);
	}

	public FXGeometricShape(IShape shape, AffineTransform transform, Paint fill,
			Effect effect) {
		this(shape, transform, new Color(0, 0, 0, 1), 1.0, fill, effect);
	}

	public void addAnchorage(
			AbstractFXGeometricElement<? extends IGeometry> anchorage) {
		this.anchorages.add(anchorage);
	}

	public Set<AbstractFXGeometricElement<? extends IGeometry>> getAnchorages() {
		return anchorages;
	}

	public Paint getFill() {
		return fill;
	}

	public void setFill(Paint fill) {
		Paint oldFill = this.fill;
		this.fill = fill;
		pcs.firePropertyChange(FILL_PROPERTY, oldFill, fill);
	}

}
