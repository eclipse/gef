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
package org.eclipse.gef4.zest.fx.parts;

import java.util.Set;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.policies.HideNodePolicy;

import com.google.common.collect.SetMultimap;
import com.google.inject.Provider;

public class ZestFxExpandingHandlePart extends ZestFxHidingHandlePart {

	public static final String IMG_EXPAND = "/expandall.gif";
	public static final String IMG_EXPAND_DISABLED = "/expandall_disabled.gif";

	public ZestFxExpandingHandlePart(
			Provider<BezierCurve[]> segmentsInSceneProvider, int segmentIndex,
			double segmentParameter) {
		super(segmentsInSceneProvider, segmentIndex, segmentParameter);
	}

	@Override
	protected Image getHoverImage() {
		return new Image(IMG_EXPAND);
	}

	@Override
	protected Image getImage() {
		return new Image(IMG_EXPAND_DISABLED);
	}

	@Override
	protected void onClicked(MouseEvent event) {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		IVisualPart<Node, ? extends Node> anchorage = anchorages.keySet()
				.iterator().next();
		IViewer<Node> viewer = anchorage.getRoot().getViewer();
		HidingModel hidingModel = viewer.getAdapter(HidingModel.class);
		Set<org.eclipse.gef4.graph.Node> hiddenNeighbors = hidingModel
				.getHiddenNeighbors(((NodeContentPart) anchorage).getContent());
		if (!hiddenNeighbors.isEmpty()) {
			for (org.eclipse.gef4.graph.Node node : hiddenNeighbors) {
				viewer.getContentPartMap().get(node)
						.<HideNodePolicy> getAdapter(HideNodePolicy.class)
						.show();
			}
		}
	}

}
