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
package org.eclipse.gef4.mvc.fx.ui.example.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.IPropertyChangeSupport;
import org.eclipse.gef4.mvc.Pair;

abstract public class AbstractFXGeometricElement<G extends IGeometry>
		implements IPropertyChangeSupport {

	public static final String GEOMETRY_PROPERTY = "Geometry";
	public static final String TRANSFORM_PROPERTY = "Transform";

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private G geometry;
	private AffineTransform transform;
	private List<Pair<AbstractFXGeometricElement<? extends IGeometry>, Map<Object, Object>>> anchoreds = new ArrayList<Pair<AbstractFXGeometricElement<? extends IGeometry>, Map<Object, Object>>>();
	private Paint stroke = new Color(0, 0, 0, 1);
	private Effect effect;
	private double strokeWidth = 0.5;

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

	public AbstractFXGeometricElement(G geometry) {
		setGeometry(geometry);
	}

	public void addAnchored(
			AbstractFXGeometricElement<? extends IGeometry> anchored, Map<Object, Object> contextMap) {
		anchoreds.add(new Pair(anchored, contextMap));
	}

	public List<Pair<AbstractFXGeometricElement<? extends IGeometry>, Map<Object, Object>>> getAnchoreds() {
		return Collections.unmodifiableList(anchoreds);
	}

	public G getGeometry() {
		return geometry;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void setGeometry(G geometry) {
		G old = this.geometry;
		this.geometry = geometry;
		pcs.firePropertyChange(GEOMETRY_PROPERTY, old, geometry);
	}

	public void setTransform(AffineTransform transform) {
		AffineTransform old = this.transform;
		this.transform = transform;
		pcs.firePropertyChange(TRANSFORM_PROPERTY, old, transform);
	}

	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public Paint getStroke() {
		return stroke;
	}

	public void setStroke(Paint stroke) {
		this.stroke = stroke;
	}

	public double getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

}