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

import org.eclipse.gef.mvc.examples.logo.ui.view.MvcLogoExampleView;
import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class MvcLogoExampleViewPropertySource implements IPropertySource {

	private static final IPropertyDescriptor SNAP_TO_GRID_PROPERTY_DESCRIPTOR = new ComboBoxPropertyDescriptor(
			GridModel.SNAP_TO_GRID_PROPERTY, "Snap To Grid",
			new String[] { Boolean.FALSE.toString(), Boolean.TRUE.toString() });
	private static final IPropertyDescriptor GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(
			GridModel.GRID_CELL_WIDTH_PROPERTY, "Grid Cell Width");
	private static final IPropertyDescriptor GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(
			GridModel.GRID_CELL_HEIGHT_PROPERTY, "Grid Cell Height");

	private MvcLogoExampleView view;

	public MvcLogoExampleViewPropertySource(MvcLogoExampleView view) {
		this.view = view;
	}

	private GridModel getGridModel() {
		return view.getContentViewer().getAdapter(GridModel.class);
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { SNAP_TO_GRID_PROPERTY_DESCRIPTOR,
				GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR,
				GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return getGridModel().snapToGridProperty().get() ? 1 : 0;
		} else if (GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return Double
					.toString(getGridModel().gridCellWidthProperty().get());
		} else if (GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return Double
					.toString(getGridModel().gridCellHeightProperty().get());
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return getGridModel().snapToGridProperty()
					.get() == GridModel.SNAP_TO_GRID_DEFAULT;
		} else if (GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return getGridModel().gridCellWidthProperty()
					.get() == GridModel.GRID_CELL_WIDTH_DEFAULT;
		} else if (GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return getGridModel().gridCellHeightProperty()
					.get() == GridModel.GRID_CELL_HEIGHT_DEFAULT;
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			getGridModel().snapToGridProperty()
					.set(GridModel.SNAP_TO_GRID_DEFAULT);
		} else if (GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			getGridModel().gridCellWidthProperty()
					.set(GridModel.GRID_CELL_WIDTH_DEFAULT);
		} else if (GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			getGridModel().gridCellHeightProperty()
					.set(GridModel.GRID_CELL_HEIGHT_DEFAULT);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			getGridModel().snapToGridProperty()
					.set(value instanceof Integer && ((Integer) value) == 1);
		} else if (GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			getGridModel().gridCellWidthProperty()
					.set(Double.parseDouble((String) value));
		} else if (GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			getGridModel().gridCellHeightProperty()
					.set(Double.parseDouble((String) value));
		}
	}

}
