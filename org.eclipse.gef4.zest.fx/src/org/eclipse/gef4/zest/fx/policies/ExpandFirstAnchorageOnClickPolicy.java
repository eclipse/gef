/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import java.util.Set;

import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import com.google.common.collect.SetMultimap;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link ExpandFirstAnchorageOnClickPolicy} is an
 * {@link AbstractFXOnClickPolicy} that shows all hidden neighbors of its host
 * upon mouse click by removing them from the {@link HidingModel}.
 *
 * @author mwienand
 *
 */
public class ExpandFirstAnchorageOnClickPolicy extends AbstractFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getHost().getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		IVisualPart<Node, ? extends Node> anchorage = anchorages.keySet().iterator().next();
		IViewer<Node> viewer = anchorage.getRoot().getViewer();
		HidingModel hidingModel = viewer.getAdapter(HidingModel.class);
		Set<org.eclipse.gef4.graph.Node> hiddenNeighbors = hidingModel
				.getHiddenNeighbors(((NodeContentPart) anchorage).getContent());
		if (!hiddenNeighbors.isEmpty()) {
			for (org.eclipse.gef4.graph.Node node : hiddenNeighbors) {
				viewer.getContentPartMap().get(node).<HideNodePolicy> getAdapter(HideNodePolicy.class).show();
			}
		}
	}

}
