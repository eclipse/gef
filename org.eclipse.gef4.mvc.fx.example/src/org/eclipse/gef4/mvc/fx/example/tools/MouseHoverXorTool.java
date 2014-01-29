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
package org.eclipse.gef4.mvc.fx.example.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.tools.FXToolUtils;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.CompositeXorTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class MouseHoverXorTool extends CompositeXorTool<Node> {

	private Map<Class<?>, ITool<Node>> tools = new HashMap<Class<?>, ITool<Node>>();

	private EventHandler<MouseEvent> hoverFilter = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			IVisualPart<Node> targetPart = FXToolUtils.getTargetPart(
					getDomain().getViewer(), event);
			if (targetPart == null) {
				hover(null);
			} else if (targetPart instanceof IRootVisualPart) {
				hover(null);
			} else if (targetPart instanceof IVisualPart) {
				hover((IVisualPart<Node>) targetPart);
			} else {
				throw new IllegalArgumentException("Unsupported part type.");
			}
		}
	};

	@Override
	protected void registerListeners() {
		super.registerListeners();
		getDomain().getViewer().getRootPart().getVisual().getScene()
				.addEventFilter(MouseEvent.MOUSE_MOVED, hoverFilter);
	}

	protected void hover(IVisualPart<Node> part) {
		ITool<Node> tool = findToolFor(part == null ? null : part.getClass());
		if (tool != null)
			selectTool(tool);
	}

	protected ITool<Node> findToolFor(Class<?> type) {
		while (type != null) {
			if (tools.containsKey(type)) {
				return tools.get(type);
			}
			type = type.getSuperclass();
		}
		return null;
	}

	public void bindToolToType(Class<?> type, ITool<Node> tool) {
		add(tool);
		tools.put(type, tool);
	}

	@Override
	protected void unregisterListeners() {
		getDomain().getViewer().getRootPart().getVisual().getScene()
				.removeEventFilter(MouseEvent.MOUSE_MOVED, hoverFilter);
		super.unregisterListeners();
	}

}
