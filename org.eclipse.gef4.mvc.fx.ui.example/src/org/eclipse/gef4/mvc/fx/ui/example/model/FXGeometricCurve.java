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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

// TODO: parameterize with concrete ICurve and encapsulate construction of geometry; limit the number of waypoints if needed
public class FXGeometricCurve extends AbstractFXGeometricElement<ICurve>
		implements IPropertySource {

	public enum Decoration {
		NONE, ARROW, CIRCLE
	}

	private static final IPropertyDescriptor SOURCE_DECORATION_PROPERTY = new ComboBoxPropertyDescriptor(
			"sourceDecoration", "Source Decoration", new String[] {
					Decoration.NONE.name(), Decoration.ARROW.name(),
					Decoration.CIRCLE.name() });
	private static final IPropertyDescriptor TARGET_DECORATION_PROPERTY = new ComboBoxPropertyDescriptor(
			"targetDecoration", "Target Decoration", new String[] {
					Decoration.NONE.name(), Decoration.ARROW.name(),
					Decoration.CIRCLE.name() });

	public double[] dashes = new double[0];

	private List<Point> waypoints = new ArrayList<>();

	private Decoration sourceDecoration = Decoration.NONE;
	private Decoration targetDecoration = Decoration.NONE;

	public Decoration getSourceDecoration() {
		return sourceDecoration;
	}

	public void setSourceDecoration(Decoration sourceDecoration) {
		Decoration oldSourceDecoration = this.sourceDecoration;
		this.sourceDecoration = sourceDecoration;
		pcs.firePropertyChange((String) SOURCE_DECORATION_PROPERTY.getId(),
				oldSourceDecoration, sourceDecoration);
	}

	public Decoration getTargetDecoration() {
		return targetDecoration;
	}

	public void setTargetDecoration(Decoration targetDecoration) {
		Decoration oldTargetDecoration = this.targetDecoration;
		this.targetDecoration = targetDecoration;
		pcs.firePropertyChange((String) TARGET_DECORATION_PROPERTY.getId(),
				oldTargetDecoration, targetDecoration);
	}

	public FXGeometricCurve(Point[] waypoints, Color stroke,
			double strokeWidth, double[] dashes, Effect effect) {
		super(constructCurveFromWayPoints(waypoints), stroke, strokeWidth,
				effect);
		this.waypoints.addAll(Arrays.asList(waypoints));
		this.dashes = dashes;
	}

	protected void setWayPoints(Point... waypoints) {
		// cache waypoints and polybezier
		this.waypoints.clear();
		this.waypoints.addAll(Arrays.asList(waypoints));
		setGeometry(constructCurveFromWayPoints(waypoints));
	}

	public static ICurve constructCurveFromWayPoints(Point... waypoints) {
		// return new Polyline(waypoints);
		return PolyBezier.interpolateCubic(waypoints);
	}

	public List<Point> getWayPoints() {
		return Collections.unmodifiableList(waypoints);
	}

	private List<Point> getWayPointsCopy() {
		return new ArrayList<Point>(waypoints);
	}

	public void addWayPoint(int i, Point p) {
		// TODO: check index != 0 && index != end
		List<Point> points = getWayPointsCopy();
		points.add(i, p);
		setWayPoints(points.toArray(new Point[] {}));
	}

	public void removeWayPoint(int i) {
		// TODO: check index
		List<Point> points = getWayPointsCopy();
		points.remove(i);
		setWayPoints(points.toArray(new Point[] {}));
	}

	public void setWayPoint(int i, Point p) {
		List<Point> points = getWayPointsCopy();
		points.set(i, p);
		setWayPoints(points.toArray(new Point[] {}));
	}

	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { SOURCE_DECORATION_PROPERTY,
				TARGET_DECORATION_PROPERTY };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			return getSourceDecoration().ordinal();
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			return getTargetDecoration().ordinal();
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			return !getSourceDecoration().equals(Decoration.NONE);
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			return !getTargetDecoration().equals(Decoration.NONE);
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			setSourceDecoration(Decoration.NONE);
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			setTargetDecoration(Decoration.NONE);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			setSourceDecoration(Decoration.values()[(int) value]);
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			setTargetDecoration(Decoration.values()[(int) value]);
		}
	}

}
