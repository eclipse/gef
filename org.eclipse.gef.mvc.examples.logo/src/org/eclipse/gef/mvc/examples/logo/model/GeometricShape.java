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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.IShape;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class GeometricShape extends AbstractGeometricElement<IShape> {

	public static final String FILL_PROPERTY = "fill";

	private final Set<AbstractGeometricElement<? extends IGeometry>> anchorages = new HashSet<>();

	private final ObjectProperty<Paint> fillProperty = new SimpleObjectProperty<>(this, FILL_PROPERTY);

	public GeometricShape(IShape shape, AffineTransform transform, Color stroke, double strokeWidth, Paint fill,
			Effect effect) {
		super(shape, transform, stroke, strokeWidth, effect);
		setFill(fill);
	}

	public GeometricShape(IShape shape, AffineTransform transform, Paint fill, Effect effect) {
		this(shape, transform, new Color(0, 0, 0, 1), 1.0, fill, effect);
	}

	public void addAnchorage(AbstractGeometricElement<? extends IGeometry> anchorage) {
		this.anchorages.add(anchorage);
	}

	public ObjectProperty<Paint> fillProperty() {
		return fillProperty;
	}

	public Set<AbstractGeometricElement<? extends IGeometry>> getAnchorages() {
		return anchorages;
	}

	public GeometricShape getCopy() {
		GeometricShape copy = new GeometricShape((IShape) getGeometry().getCopy(), getTransform().getCopy(),
				(Color) getStroke(), getStrokeWidth(), getFill(), getEffect());
		return copy;
	}

	public Paint getFill() {
		return fillProperty.get();
	}

	public void setFill(Paint fill) {
		fillProperty.set(fill);
	}

}
