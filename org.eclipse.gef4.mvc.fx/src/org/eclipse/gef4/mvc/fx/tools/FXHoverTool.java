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

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractHoverTool;

public class FXHoverTool extends AbstractHoverTool<Node> {

	private EventHandler<MouseEvent> hoverFilter = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			IVisualPart<Node> targetPart = FXPartUtils.getMouseTargetPart(getDomain().getViewer(), event);
			if (targetPart == null) {
				hover(null);
			} else if (targetPart instanceof IRootVisualPart) {
				hover(null);
//			} else if (targetPart instanceof IVisualPart) {
//				hover((IVisualPart<Node>) targetPart);
//			} else {
//				throw new IllegalArgumentException("Unsupported part type.");
				// IGNORE
			}
		}
	};

	@Override
	public void activate() {
		super.activate();
		getDomain().getViewer().getRootPart().getVisual().getScene()
				.addEventFilter(MouseEvent.MOUSE_MOVED, hoverFilter);
	}

	@Override
	public void deactivate() {
		getDomain().getViewer().getRootPart().getVisual().getScene()
				.removeEventFilter(MouseEvent.MOUSE_MOVED, hoverFilter);
		super.deactivate();
	}

}
