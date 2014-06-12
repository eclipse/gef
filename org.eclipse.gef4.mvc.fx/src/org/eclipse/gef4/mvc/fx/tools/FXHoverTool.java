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

import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXHoverPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public class FXHoverTool extends AbstractTool<Node> {

	public static final Class<AbstractFXHoverPolicy> TOOL_POLICY_KEY = AbstractFXHoverPolicy.class;

	private final EventHandler<MouseEvent> hoverFilter = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			IVisualPart<Node> targetPart = FXPartUtils.getEventTargetPart(
					getDomain().getViewer(), event);
			if (targetPart != null) {
				AbstractFXHoverPolicy policy = getToolPolicy(targetPart);
				if (policy != null) {
					policy.hover(event);
				}
			}
		}
	};

	protected AbstractFXHoverPolicy getToolPolicy(IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(TOOL_POLICY_KEY);
	}

	@Override
	protected void registerListeners() {
		getDomain().getViewer().getRootPart().getVisual().getScene()
				.addEventFilter(MouseEvent.MOUSE_MOVED, hoverFilter);
	};

	@Override
	protected void unregisterListeners() {
		getDomain().getViewer().getRootPart().getVisual().getScene()
				.removeEventFilter(MouseEvent.MOUSE_MOVED, hoverFilter);
	}

}
