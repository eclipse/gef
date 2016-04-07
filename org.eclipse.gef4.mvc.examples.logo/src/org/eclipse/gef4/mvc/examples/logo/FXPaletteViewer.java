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
package org.eclipse.gef4.mvc.examples.logo;

import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.Node;

/**
 *
 * @author mwienand
 *
 */
public class FXPaletteViewer extends AbstractViewer<Node> {

	@Override
	public boolean isViewerFocused() {
		return false;
	}

	@Override
	public boolean isViewerVisual(Node node) {
		return false;
	}

	@Override
	public void reveal(IVisualPart<Node, ? extends Node> visualPart) {
	}

	@Override
	public ReadOnlyBooleanProperty viewerFocusedProperty() {
		return null;
	}

}
