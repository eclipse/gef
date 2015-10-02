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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

abstract public class AbstractFXGeometricElement<G extends IGeometry>
		implements IPropertyChangeNotifier {

	public static final String GEOMETRY_PROPERTY = "geometry";
	public static final String TRANSFORM_PROPERTY = "transform";
	public static final String STROKE_WIDTH_PROPERTY = "strokeWidth";

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private G geometry;
	private AffineTransform transform;
	private Paint stroke = new Color(0, 0, 0, 1);
	private Effect effect;
	private double strokeWidth = 0.5;

	public AbstractFXGeometricElement(G geometry) {
		setGeometry(geometry);
	}

	public AbstractFXGeometricElement(G geometry, AffineTransform transform,
			Paint stroke, double strokeWidth, Effect effect) {
		this(geometry);
		setTransform(transform);
		setEffect(effect);
		setStroke(stroke);
		setStrokeWidth(strokeWidth);
	}

	public AbstractFXGeometricElement(G geometry, Paint stroke,
			double strokeWidth, Effect effect) {
		setGeometry(geometry);
		setEffect(effect);
		setStroke(stroke);
		setStrokeWidth(strokeWidth);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public Effect getEffect() {
		return effect;
	}

	public G getGeometry() {
		return geometry;
	}

	public Paint getStroke() {
		return stroke;
	}

	public double getStrokeWidth() {
		return strokeWidth;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public void setGeometry(G geometry) {
		G old = this.geometry;
		this.geometry = geometry;
		pcs.firePropertyChange(GEOMETRY_PROPERTY, old, geometry);
	}

	public void setStroke(Paint stroke) {
		this.stroke = stroke;
	}

	public void setStrokeWidth(double strokeWidth) {
		double oldStrokeWidth = this.strokeWidth;
		this.strokeWidth = strokeWidth;
		pcs.firePropertyChange(STROKE_WIDTH_PROPERTY, oldStrokeWidth,
				strokeWidth);
	}

	public void setTransform(AffineTransform transform) {
		AffineTransform old = this.transform;
		this.transform = transform;
		pcs.firePropertyChange(TRANSFORM_PROPERTY, old, transform);
	}

}