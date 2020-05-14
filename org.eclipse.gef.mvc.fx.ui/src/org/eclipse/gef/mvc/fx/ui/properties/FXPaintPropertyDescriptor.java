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
package org.eclipse.gef.mvc.fx.ui.properties;

import org.eclipse.gef.fx.jface.FXPaintCellEditor;
import org.eclipse.gef.fx.jface.FXPaintLabelProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * The {@link FXPaintPropertyDescriptor} is a {@link PropertyDescriptor} that
 * uses the {@link FXPaintCellEditor} to edit its value.
 *
 * @author anyssen
 *
 */
public class FXPaintPropertyDescriptor extends PropertyDescriptor {

	/**
	 * Creates an property descriptor with the given id and display name.
	 *
	 * @param id
	 *            The id of this property
	 * @param displayName
	 *            The name to display for this property
	 */
	public FXPaintPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		// set an appropriate label provider by default
		setLabelProvider(new FXPaintLabelProvider());
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new FXPaintCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
}
