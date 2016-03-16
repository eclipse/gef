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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.parts.AbstractLabelPart;
import org.eclipse.gef4.zest.fx.parts.EdgePart;
import org.eclipse.gef4.zest.fx.parts.NodePart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * A specific {@link FXTranslateSelectedOnDragPolicy} that includes dragging of
 * unselected label parts.
 *
 * @author anyssen
 *
 */
public class TranslateSelectedAndRelocateLabelsOnDragPolicy extends FXTranslateSelectedOnDragPolicy {

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		super.drag(e, delta);
		for (AbstractLabelPart lp : getLabelParts()) {
			lp.getAdapter(TransformLabelPolicy.class).preserveLabelOffset();
		}
	}

	@Override
	public void dragAborted() {
		for (AbstractLabelPart lp : getLabelParts()) {
			rollback(lp.getAdapter(TransformLabelPolicy.class));
			restoreRefreshVisuals(lp);
		}
		super.dragAborted();
	}

	private List<AbstractLabelPart> getEdgeLabelParts(EdgePart edgePart) {
		List<AbstractLabelPart> linked = new ArrayList<>();
		linked.addAll(new ArrayList<>(PartUtils
				.filterParts(PartUtils.getAnchoreds(edgePart, ZestProperties.ELEMENT_LABEL), AbstractLabelPart.class)));
		linked.addAll(new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(edgePart, ZestProperties.ELEMENT_EXTERNAL_LABEL), AbstractLabelPart.class)));
		linked.addAll(new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(edgePart, ZestProperties.EDGE_SOURCE_LABEL), AbstractLabelPart.class)));
		linked.addAll(new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(edgePart, ZestProperties.EDGE_TARGET_LABEL), AbstractLabelPart.class)));
		return linked;
	}

	private List<AbstractLabelPart> getLabelParts() {
		Set<AbstractLabelPart> labelParts = Collections
				.newSetFromMap(new IdentityHashMap<AbstractLabelPart, Boolean>());
		// ensure that linked parts are moved with us during dragging
		List<IContentPart<Node, ? extends Node>> targetParts = getTargetParts();
		for (IVisualPart<Node, ? extends Node> tp : targetParts) {
			if (tp instanceof NodePart) {
				labelParts.addAll(getNodeLabelParts((NodePart) tp));
				for (IVisualPart<javafx.scene.Node, ? extends javafx.scene.Node> anchored : getHost()
						.getAnchoredsUnmodifiable()) {
					// add labels of edges if edge is not target part
					if (anchored instanceof EdgePart && !targetParts.contains(anchored)) {
						labelParts.addAll(getEdgeLabelParts((EdgePart) anchored));
					}
				}
			} else if (tp instanceof EdgePart) {
				labelParts.addAll(getEdgeLabelParts((EdgePart) tp));
			}
		}
		labelParts.removeAll(targetParts);
		return new ArrayList<>(labelParts);
	}

	private List<AbstractLabelPart> getNodeLabelParts(NodePart nodePart) {
		return new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(nodePart, ZestProperties.ELEMENT_EXTERNAL_LABEL), AbstractLabelPart.class));
	}

	@Override
	public void press(MouseEvent e) {
		super.press(e);
		// init label transform policies
		for (AbstractLabelPart lp : getLabelParts()) {
			storeAndDisableRefreshVisuals(lp);
			init(lp.getAdapter(TransformLabelPolicy.class));
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		for (AbstractLabelPart lp : getLabelParts()) {
			commit(lp.getAdapter(TransformLabelPolicy.class));
			restoreRefreshVisuals(lp);
		}
		super.release(e, delta);
	}
}
