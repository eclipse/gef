/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.ui.properties;

import java.util.List;

import org.eclipse.gef.mvc.examples.logo.ui.view.MvcLogoExampleView;
import org.eclipse.gef.mvc.fx.handlers.ISnapToStrategy;
import org.eclipse.gef.mvc.fx.handlers.SnapToGeometry;
import org.eclipse.gef.mvc.fx.handlers.SnapToGrid;
import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.gef.mvc.fx.models.SnappingModel;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import javafx.collections.ObservableList;

public class MvcLogoExampleViewPropertySource implements IPropertySource {

	// snap-to strategies
	private static final SnapToGrid SNAP_TO_GRID_STRATEGY = new SnapToGrid();
	private static final SnapToGeometry SNAP_TO_GEOMETRY_STRATEGY = new SnapToGeometry();
	
	// IDs for snap-to-strategy properties
	private static final String SNAP_TO_GRID_ID = "SNAP_TO_GRID";
	private static final String SNAP_TO_GEOMETRY_ID = "SNAP_TO_GEOMETRY";

	// descriptors
	private static final IPropertyDescriptor SNAP_TO_GEOMETRY_PROPERTY_DESCRIPTOR = new ComboBoxPropertyDescriptor(
			SNAP_TO_GEOMETRY_ID, "Snap To Geometry",
			new String[] { Boolean.FALSE.toString(), Boolean.TRUE.toString() });
	private static final IPropertyDescriptor SNAP_TO_GRID_PROPERTY_DESCRIPTOR = new ComboBoxPropertyDescriptor(
			SNAP_TO_GRID_ID, "Snap To Grid",
			new String[] { Boolean.FALSE.toString(), Boolean.TRUE.toString() });
	private static final IPropertyDescriptor GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(
			GridModel.GRID_CELL_WIDTH_PROPERTY, "Grid Cell Width");
	private static final IPropertyDescriptor GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(
			GridModel.GRID_CELL_HEIGHT_PROPERTY, "Grid Cell Height");

	// reference to the view
	private MvcLogoExampleView view;

	public MvcLogoExampleViewPropertySource(MvcLogoExampleView view) {
		this.view = view;
	}

	private GridModel getGridModel() {
		return view.getContentViewer().getAdapter(GridModel.class);
	}

	private SnapToGrid getSnapToGrid() {
		List<ISnapToStrategy> supportedSnapToStrategies = getSupportedSnapToStrategies();
		SnapToGrid snapToGrid = null;
		for (ISnapToStrategy s : supportedSnapToStrategies) {
			if (s instanceof SnapToGrid) {
				snapToGrid = (SnapToGrid) s;
				break;
			}
		}
		return snapToGrid;
	}

	private ObservableList<ISnapToStrategy> getSupportedSnapToStrategies() {
		return view.getContentViewer().getAdapter(SnappingModel.class)
				.snapToStrategiesProperty();
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { SNAP_TO_GEOMETRY_PROPERTY_DESCRIPTOR, SNAP_TO_GRID_PROPERTY_DESCRIPTOR,
				GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR,
				GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (SNAP_TO_GEOMETRY_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return getSnapToGeometry() != null ? 1 : 0;
		} else if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return getSnapToGrid() != null ? 1 : 0;
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
		if (SNAP_TO_GEOMETRY_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return getSnapToGeometry() != null;
		} else if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return getSnapToGrid() != null;
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

	private SnapToGeometry getSnapToGeometry() {
		List<ISnapToStrategy> supportedSnapToStrategies = getSupportedSnapToStrategies();
		SnapToGeometry snapToGeometry = null;
		for (ISnapToStrategy s : supportedSnapToStrategies) {
			if (s instanceof SnapToGeometry) {
				snapToGeometry = (SnapToGeometry) s;
				break;
			}
		}
		return snapToGeometry;
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (SNAP_TO_GEOMETRY_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			removeSnapToGeometry();
		} else if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			removeSnapToGrid();
		} else if (GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			getGridModel().gridCellWidthProperty()
					.set(GridModel.GRID_CELL_WIDTH_DEFAULT);
		} else if (GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			getGridModel().gridCellHeightProperty()
					.set(GridModel.GRID_CELL_HEIGHT_DEFAULT);
		}
	}

	private void addSnapToGrid() {
		if(getSnapToGrid() == null) {
			getSupportedSnapToStrategies().add(SNAP_TO_GRID_STRATEGY);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (SNAP_TO_GEOMETRY_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			if (value instanceof Integer && ((Integer) value) == 1) {
				addSnapToGeometry();
			} else {
				removeSnapToGeometry();
			}
		} else if (SNAP_TO_GRID_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			if (value instanceof Integer && ((Integer) value) == 1) {
				addSnapToGrid();
			} else {
				removeSnapToGrid();
			}
		} else if (GRID_CELL_WIDTH_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			getGridModel().gridCellWidthProperty()
					.set(Double.parseDouble((String) value));
		} else if (GRID_CELL_HEIGHT_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			getGridModel().gridCellHeightProperty()
					.set(Double.parseDouble((String) value));
		}
	}

	private void removeSnapToGeometry() {
		getSupportedSnapToStrategies().remove(getSnapToGeometry());
	}

	private void addSnapToGeometry() {
		if(getSnapToGeometry() == null) {
			getSupportedSnapToStrategies().add(SNAP_TO_GEOMETRY_STRATEGY);
		}
	}

	private void removeSnapToGrid() {
		getSupportedSnapToStrategies().remove(getSnapToGrid());
	}
}
