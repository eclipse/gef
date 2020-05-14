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

import org.eclipse.gef.mvc.examples.logo.model.GeometricShape;
import org.eclipse.gef.mvc.fx.ui.properties.FXPaintPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import javafx.scene.paint.Paint;

public class GeometricShapePropertySource implements IPropertySource {

	private static final IPropertyDescriptor FILL_PROPERTY_DESCRIPTOR = new FXPaintPropertyDescriptor(
			GeometricShape.FILL_PROPERTY, "Fill");

	private GeometricShape shape;

	public GeometricShapePropertySource(GeometricShape shape) {
		this.shape = shape;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { FILL_PROPERTY_DESCRIPTOR };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return shape.getFill();
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return shape.getFill() != null;
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			shape.setFill(null);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			shape.setFill((Paint) value);
		}
	}

}
