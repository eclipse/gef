/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.model;

import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.IGeometry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

abstract public class AbstractGeometricElement<G extends IGeometry> {

	public static final String GEOMETRY_PROPERTY = "geometry";
	public static final String TRANSFORM_PROPERTY = "transform";
	public static final String STROKE_WIDTH_PROPERTY = "strokeWidth";

	private final ObjectProperty<G> geometryProperty = new SimpleObjectProperty<>(
			this, GEOMETRY_PROPERTY);
	private final ObjectProperty<AffineTransform> transformProperty = new SimpleObjectProperty<>(
			this, TRANSFORM_PROPERTY);
	private Paint stroke = new Color(0, 0, 0, 1);
	private Effect effect;
	private final DoubleProperty strokeWidthProperty = new SimpleDoubleProperty(
			this, STROKE_WIDTH_PROPERTY, 0.5);

	public AbstractGeometricElement(G geometry) {
		setGeometry(geometry);
	}

	public AbstractGeometricElement(G geometry, AffineTransform transform,
			Paint stroke, double strokeWidth, Effect effect) {
		this(geometry);
		setTransform(transform);
		setEffect(effect);
		setStroke(stroke);
		setStrokeWidth(strokeWidth);
	}

	public AbstractGeometricElement(G geometry, Paint stroke,
			double strokeWidth, Effect effect) {
		setGeometry(geometry);
		setEffect(effect);
		setStroke(stroke);
		setStrokeWidth(strokeWidth);
	}

	public Effect getEffect() {
		return effect;
	}

	public G getGeometry() {
		return geometryProperty.get();
	}

	public Paint getStroke() {
		return stroke;
	}

	public double getStrokeWidth() {
		return strokeWidthProperty.get();
	}

	public AffineTransform getTransform() {
		return transformProperty.get();
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public void setGeometry(G geometry) {
		geometryProperty.set(geometry);
	}

	public void setStroke(Paint stroke) {
		this.stroke = stroke;
	}

	public void setStrokeWidth(double strokeWidth) {
		strokeWidthProperty.set(strokeWidth);
	}

	public void setTransform(AffineTransform transform) {
		transformProperty.set(transform);
	}

}