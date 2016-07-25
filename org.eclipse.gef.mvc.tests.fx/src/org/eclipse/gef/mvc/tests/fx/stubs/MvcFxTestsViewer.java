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
package org.eclipse.gef.mvc.tests.fx.stubs;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.parts.IVisualPart;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Node;

public class MvcFxTestsViewer extends FXViewer {
	ReadOnlyBooleanWrapper focusedProperty = new ReadOnlyBooleanWrapper(true);
	private InfiniteCanvas canvas;

	@Override
	public InfiniteCanvas getCanvas() {
		if (canvas == null) {
			canvas = new InfiniteCanvas();
		}
		return canvas;
	}

	@Override
	public boolean isViewerFocused() {
		return true;
	}

	@Override
	public boolean isViewerVisual(Node node) {
		return true;
	}

	@Override
	public void reveal(IVisualPart<Node, ? extends Node> visualPart) {
	}

	@Override
	public ReadOnlyBooleanProperty viewerFocusedProperty() {
		return focusedProperty.getReadOnlyProperty();
	}
}