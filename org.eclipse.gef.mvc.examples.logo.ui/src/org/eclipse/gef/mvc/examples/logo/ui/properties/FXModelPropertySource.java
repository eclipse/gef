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
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class FXModelPropertySource implements IPropertySource {
	
	private static final IPropertyDescriptor SNAP_TO_GRID_PROPERTY_DESCRIPTOR = new ComboBoxPropertyDescriptor(
			FXGeometricModel.SNAP_TO_GRID_PROPERTY, "Snap To Grid",
			new String[] { Boolean.FALSE.toString(), Boolean.TRUE.toString() });
	private static final IPropertyDescriptor GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(
			FXGeometricModel.GRID_CELL_WIDTH_PROPERTY, "Grid Cell Width");
	private static final IPropertyDescriptor GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(
			FXGeometricModel.GRID_CELL_HEIGHT_PROPERTY, "Grid Cell Height");

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
		return new IPropertyDescriptor[] { SNAP_TO_GRID_PROPERTY_DESCRIPTOR, GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR, GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return model.snapToGridProperty().get() ? 1 : 0;
		} else if (GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return Integer.toString(model.gridCellWidthProperty().get());
		} else if (GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return Integer.toString(model.gridCellHeightProperty().get());
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return model.snapToGridProperty().get() == FXGeometricModel.SNAP_TO_GRID_PROPERTY_DEFAULT;
		} else if (GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return model.gridCellWidthProperty().get() == FXGeometricModel.GRID_CELL_WIDTH_PROPERTY_DEFAULT;
		} else if (GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return model.gridCellHeightProperty().get() == FXGeometricModel.GRID_CELL_HEIGHT_PROPERTY_DEFAULT;
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			model.snapToGridProperty().set(FXGeometricModel.SNAP_TO_GRID_PROPERTY_DEFAULT);
		} else if (GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			model.gridCellWidthProperty().set(FXGeometricModel.GRID_CELL_WIDTH_PROPERTY_DEFAULT);
		} else if (GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			model.gridCellHeightProperty().set(FXGeometricModel.GRID_CELL_HEIGHT_PROPERTY_DEFAULT);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			model.snapToGridProperty().set(value instanceof Integer && ((Integer) value) == 1);
		} else if (GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			model.gridCellWidthProperty().set(Integer.parseInt((String) value));
		} else if (GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			model.gridCellHeightProperty().set(Integer.parseInt((String) value));
		}
	}

}
