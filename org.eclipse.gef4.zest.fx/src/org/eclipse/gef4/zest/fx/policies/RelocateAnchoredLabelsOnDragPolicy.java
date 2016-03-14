/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.PartUtils;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.parts.AbstractLabelPart;
import org.eclipse.gef4.zest.fx.parts.EdgeLabelPart;
import org.eclipse.gef4.zest.fx.parts.NodeLabelPart;

import javafx.scene.Node;

/**
 * A policy to relocate anchored {@link EdgeLabelPart} and {@link NodeLabelPart}
 * .
 * 
 * @author anyssen
 */
public class RelocateAnchoredLabelsOnDragPolicy extends FXTranslateSelectedOnDragPolicy {

	@Override
	public List<IContentPart<Node, ? extends Node>> getTargetParts() {
		List<IContentPart<Node, ? extends Node>> selected = super.getTargetParts();

		List<IContentPart<Node, ? extends Node>> linked = new ArrayList<>();
		for (IContentPart<Node, ? extends Node> cp : selected) {
			// ensure that linked parts are moved with us during dragging
			linked.addAll(new ArrayList<>(PartUtils
					.filterParts(PartUtils.getAnchoreds(cp, ZestProperties.ELEMENT_LABEL), AbstractLabelPart.class)));
			linked.addAll(new ArrayList<>(PartUtils.filterParts(
					PartUtils.getAnchoreds(cp, ZestProperties.ELEMENT_EXTERNAL_LABEL), AbstractLabelPart.class)));
			linked.addAll(new ArrayList<>(PartUtils
					.filterParts(PartUtils.getAnchoreds(cp, ZestProperties.EDGE_SOURCE_LABEL), EdgeLabelPart.class)));
			linked.addAll(new ArrayList<>(PartUtils
					.filterParts(PartUtils.getAnchoreds(cp, ZestProperties.EDGE_TARGET_LABEL), EdgeLabelPart.class)));
		}

		// remove all linked that are selected already (these will be translated
		// via the FXTranslateSelectedOnDragPolicy) already
		SelectionModel<?> selectionModel = getHost().getRoot().getViewer().getAdapter(SelectionModel.class);
		linked.removeAll(selectionModel.getSelectionUnmodifiable());
		return linked;
	}
}
