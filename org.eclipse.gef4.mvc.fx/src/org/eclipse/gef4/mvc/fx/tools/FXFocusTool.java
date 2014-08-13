/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.tools;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;

import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXFocusTool extends AbstractTool<Node> {

	private final Map<IViewer<Node>, ChangeListener<? super Boolean>> viewerFocusListenerMap = new HashMap<IViewer<Node>, ChangeListener<? super Boolean>>();

	private ChangeListener<? super Boolean> createWindowFocusedChangeListener(
			final IViewer<Node> viewer) {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				// TODO: use FocusPolicy for this
				viewer.getFocusModel().setViewerFocused(newValue);
			}
		};
	}

	@Override
	protected void registerListeners() {
		for (IViewer<Node> viewer : getDomain().getViewers().values()) {
			Scene scene = viewer.getRootPart().getVisual().getScene();
			ChangeListener<? super Boolean> listener = createWindowFocusedChangeListener(viewer);
			viewerFocusListenerMap.put(viewer, listener);
			scene.windowProperty().get().focusedProperty()
					.addListener(listener);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (IViewer<Node> viewer : getDomain().getViewers().values()) {
			Scene scene = viewer.getRootPart().getVisual().getScene();
			scene.windowProperty().get().focusedProperty()
					.removeListener(viewerFocusListenerMap.get(viewer));
		}
	}

}
