/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.policies;

import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.eclipse.gef4.zest.fx.parts.ShowHiddenNeighboursHandlePart;
import org.eclipse.gef4.zest.fx.parts.HideHandlePart;

import com.google.common.collect.SetMultimap;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link HoverFirstAnchorageOnHoverPolicy} is an extension to the
 * {@link FXHoverOnHoverPolicy} that hovers its first anchorage instead of its
 * {@link #getHost() host}. By default, this policy is installed on
 * {@link ShowHiddenNeighboursHandlePart}s and {@link HideHandlePart}s, so
 * that the corresponding {@link NodeContentPart} is hovered instead of the
 * handle parts.
 *
 * @author mwienand
 *
 */
public class HoverFirstAnchorageOnHoverPolicy extends FXHoverOnHoverPolicy {

	@Override
	public void hover(MouseEvent e) {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getHost().getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		getHost().getRoot().getViewer().<HoverModel<Node>> getAdapter(HoverModel.class)
				.setHover(anchorages.keySet().iterator().next());
	}

}
