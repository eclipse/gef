/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.ui.properties;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricCurve.Decoration;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class FXCurvePropertySource implements IPropertySource {

	private static final IPropertyDescriptor SOURCE_DECORATION_PROPERTY = new ComboBoxPropertyDescriptor(
			FXGeometricCurve.SOURCE_DECORATION_PROPERTY, "Source Decoration",
			new String[] { Decoration.NONE.name(), Decoration.ARROW.name(),
					Decoration.CIRCLE.name() });
	private static final IPropertyDescriptor TARGET_DECORATION_PROPERTY = new ComboBoxPropertyDescriptor(
			FXGeometricCurve.TARGET_DECORATION_PROPERTY, "Target Decoration",
			new String[] { Decoration.NONE.name(), Decoration.ARROW.name(),
					Decoration.CIRCLE.name() });
	private static final IPropertyDescriptor STROKE_WIDTH_PROPERTY = new TextPropertyDescriptor(
			FXGeometricCurve.STROKE_WIDTH_PROPERTY, "Stroke Width");
	private static final IPropertyDescriptor IS_SEGMENT_BASED_PROPERTY = new TextPropertyDescriptor(
			FXGeometricCurve.IS_SEGMENT_BASED_PROPERTY, "Segment Based");

	private FXGeometricCurve curve;

	public FXCurvePropertySource(FXGeometricCurve curve) {
		this.curve = curve;
	}

	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { SOURCE_DECORATION_PROPERTY,
				TARGET_DECORATION_PROPERTY, STROKE_WIDTH_PROPERTY,
				IS_SEGMENT_BASED_PROPERTY };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			return curve.getSourceDecoration().ordinal();
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			return curve.getTargetDecoration().ordinal();
		} else if (STROKE_WIDTH_PROPERTY.getId().equals(id)) {
			return new Double(curve.getStrokeWidth()).toString();
		} else if (IS_SEGMENT_BASED_PROPERTY.getId().equals(id)) {
			return new Boolean(curve.isSegmentBased()).toString();
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			return !curve.getSourceDecoration().equals(Decoration.NONE);
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			return !curve.getTargetDecoration().equals(Decoration.NONE);
		} else if (STROKE_WIDTH_PROPERTY.getId().equals(id)) {
			return curve.getStrokeWidth() != 1;
		} else if (IS_SEGMENT_BASED_PROPERTY.getId().equals(id)) {
			return !curve.isSegmentBased();
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			curve.setSourceDecoration(Decoration.NONE);
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			curve.setTargetDecoration(Decoration.NONE);
		} else if (STROKE_WIDTH_PROPERTY.getId().equals(id)) {
			curve.setStrokeWidth(1);
		} else if (IS_SEGMENT_BASED_PROPERTY.getId().equals(id)) {
			curve.setSegmentBased(false);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			curve.setSourceDecoration(Decoration.values()[(int) value]);
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			curve.setTargetDecoration(Decoration.values()[(int) value]);
		} else if (STROKE_WIDTH_PROPERTY.getId().equals(id)) {
			curve.setStrokeWidth(Double.parseDouble((String) value));
		} else if (IS_SEGMENT_BASED_PROPERTY.getId().equals(id)) {
			// TODO: Changing the way points has to be undoable. We need to bind
			// an own UndoablePropertySheetEntry in the UiModule that chains an
			// operation changing the way points on the domain object.
			boolean isSegmentBased = Boolean.parseBoolean((String) value);
			if (isSegmentBased) {
				curve.setWayPoints(new Point[] {});
			}
			curve.setSegmentBased(isSegmentBased);
		}
	}

}
