/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.tools;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.fx.viewer.IFXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public class FXClickTool extends AbstractTool<Node> {

	public static final Class<AbstractFXClickPolicy> TOOL_POLICY_KEY = AbstractFXClickPolicy.class;

	private final EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			IVisualPart<Node> targetPart = FXPartUtils.getEventTargetPart(
					FXClickTool.this.getDomain().getViewer(), event);
			if (targetPart != null) {
				AbstractFXClickPolicy policy = getToolPolicy(targetPart);
				if (policy != null) {
					policy.click(event);
				}
			}
		}
	};

	protected AbstractFXClickPolicy getToolPolicy(IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(TOOL_POLICY_KEY);
	}

	@Override
	protected void registerListeners() {
		((IFXViewer) getDomain().getViewer()).getScene().addEventHandler(
				MouseEvent.MOUSE_PRESSED, pressedHandler);
	};

	@Override
	protected void unregisterListeners() {
		((IFXViewer) getDomain().getViewer()).getScene().removeEventHandler(
				MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

}
