/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.ui.properties;

import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve.Decoration;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve.RoutingStyle;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class GeometricCurvePropertySource implements IPropertySource {

	public static final IPropertyDescriptor SOURCE_DECORATION_PROPERTY = new ComboBoxPropertyDescriptor(
			GeometricCurve.SOURCE_DECORATION_PROPERTY, "Source Decoration",
			new String[] { Decoration.NONE.name(), Decoration.ARROW.name(),
					Decoration.CIRCLE.name() });
	public static final IPropertyDescriptor TARGET_DECORATION_PROPERTY = new ComboBoxPropertyDescriptor(
			GeometricCurve.TARGET_DECORATION_PROPERTY, "Target Decoration",
			new String[] { Decoration.NONE.name(), Decoration.ARROW.name(),
					Decoration.CIRCLE.name() });
	public static final IPropertyDescriptor STROKE_WIDTH_PROPERTY = new TextPropertyDescriptor(
			GeometricCurve.STROKE_WIDTH_PROPERTY, "Stroke Width");
	public static final IPropertyDescriptor ROUTING_STYLE_PROPERTY = new ComboBoxPropertyDescriptor(
			GeometricCurve.ROUTING_STYLE_PROPERTY, "Routing Style",
			new String[] { RoutingStyle.STRAIGHT.name(),
					RoutingStyle.ORTHOGONAL.name() });

	private GeometricCurve curve;

	public GeometricCurvePropertySource(GeometricCurve curve) {
		this.curve = curve;
	}

	public GeometricCurve getCurve() {
		return curve;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { SOURCE_DECORATION_PROPERTY,
				TARGET_DECORATION_PROPERTY, STROKE_WIDTH_PROPERTY,
				ROUTING_STYLE_PROPERTY };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			return curve.getSourceDecoration().ordinal();
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			return curve.getTargetDecoration().ordinal();
		} else if (STROKE_WIDTH_PROPERTY.getId().equals(id)) {
			return new Double(curve.getStrokeWidth()).toString();
		} else if (ROUTING_STYLE_PROPERTY.getId().equals(id)) {
			return curve.getRoutingStyle().ordinal();
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
		} else if (ROUTING_STYLE_PROPERTY.getId().equals(id)) {
			return !curve.getRoutingStyle().equals(RoutingStyle.STRAIGHT);
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
		} else if (ROUTING_STYLE_PROPERTY.getId().equals(id)) {
			curve.setRoutingStyle(RoutingStyle.STRAIGHT);
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
		} else if (ROUTING_STYLE_PROPERTY.getId().equals(id)) {
			curve.setRoutingStyle(RoutingStyle.values()[(int) value]);
		}
	}

}
