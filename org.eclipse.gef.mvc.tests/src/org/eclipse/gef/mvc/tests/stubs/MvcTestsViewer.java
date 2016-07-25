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
package org.eclipse.gef.mvc.tests.stubs;

import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.viewer.AbstractViewer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

public class MvcTestsViewer extends AbstractViewer<Object> {
	ReadOnlyBooleanWrapper focusedProperty = new ReadOnlyBooleanWrapper(true);

	@Override
	public boolean isViewerFocused() {
		return true;
	}

	@Override
	public boolean isViewerVisual(Object node) {
		return true;
	}

	@Override
	public void reveal(IVisualPart<Object, ? extends Object> visualPart) {
	}

	@Override
	public ReadOnlyBooleanProperty viewerFocusedProperty() {
		return focusedProperty.getReadOnlyProperty();
	}
}