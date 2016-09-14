/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.ui.properties;

import org.eclipse.gef.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class FXModelPropertySource implements IPropertySource {
	
	private static final IPropertyDescriptor SNAP_TO_GRID_PROPERTY_DESCRIPTOR = new ComboBoxPropertyDescriptor(
			FXGeometricModel.SNAP_TO_GRID_PROPERTY, "Snap To Grid",
			new String[] { Boolean.FALSE.toString(), Boolean.TRUE.toString() });

	private FXGeometricModel model;

	public FXModelPropertySource(FXGeometricModel model) {
		this.model = model;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { SNAP_TO_GRID_PROPERTY_DESCRIPTOR };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return model.isSnapToGrid() ? 1 : 0;
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			model.setSnapToGrid(false);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			model.setSnapToGrid(value instanceof Integer && ((Integer) value) == 1);
		}
	}

}
