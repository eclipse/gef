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
package org.eclipse.gef.zest.fx.policies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.policies.TranslateSelectedOnDragPolicy;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.AbstractLabelPart;
import org.eclipse.gef.zest.fx.parts.EdgePart;
import org.eclipse.gef.zest.fx.parts.NodePart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * A specific {@link TranslateSelectedOnDragPolicy} that includes dragging of
 * unselected label parts.
 *
 * @author anyssen
 *
 */
public class TranslateSelectedAndRelocateLabelsOnDragPolicy extends TranslateSelectedOnDragPolicy {

	private List<AbstractLabelPart> labelParts;

	@Override
	public void abortDrag() {
		for (AbstractLabelPart lp : getLabelParts()) {
			rollback(lp.getAdapter(TransformLabelPolicy.class));
			restoreRefreshVisuals(lp);
		}
		super.abortDrag();
		labelParts = null;
	}

	/**
	 * Computes the {@link AbstractLabelPart}s that are anchored to the
	 * {@link #getHost()} of this policy and need to be relocated together with
	 * the host.
	 *
	 * @return The {@link AbstractLabelPart}s that need to be relocated together
	 *         with the host.
	 */
	protected List<AbstractLabelPart> computeLabelParts() {
		Set<AbstractLabelPart> labelParts = Collections
				.newSetFromMap(new IdentityHashMap<AbstractLabelPart, Boolean>());
		// ensure that linked parts are moved with us during dragging
		List<IContentPart<? extends Node>> targetParts = getTargetParts();
		for (IVisualPart<? extends Node> tp : targetParts) {
			if (tp instanceof NodePart) {
				labelParts.addAll(getNodeLabelParts((NodePart) tp));
				for (IVisualPart<? extends javafx.scene.Node> anchored : getHost().getAnchoredsUnmodifiable()) {
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

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		super.drag(e, delta);
		for (AbstractLabelPart lp : getLabelParts()) {
			lp.getAdapter(TransformLabelPolicy.class).preserveLabelOffset();
		}
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		for (AbstractLabelPart lp : getLabelParts()) {
			commit(lp.getAdapter(TransformLabelPolicy.class));
			restoreRefreshVisuals(lp);
		}
		super.endDrag(e, delta);
		labelParts = null;
	}

	private List<AbstractLabelPart> getEdgeLabelParts(EdgePart edgePart) {
		List<AbstractLabelPart> linked = new ArrayList<>();
		linked.addAll(new ArrayList<>(PartUtils.filterParts(PartUtils.getAnchoreds(edgePart, ZestProperties.LABEL__NE),
				AbstractLabelPart.class)));
		linked.addAll(new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(edgePart, ZestProperties.EXTERNAL_LABEL__NE), AbstractLabelPart.class)));
		linked.addAll(new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(edgePart, ZestProperties.SOURCE_LABEL__E), AbstractLabelPart.class)));
		linked.addAll(new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(edgePart, ZestProperties.TARGET_LABEL__E), AbstractLabelPart.class)));
		return linked;
	}

	/**
	 * Returns the {@link AbstractLabelPart}s that were previously determined if
	 * available, otherwise the label parts are {@link #computeLabelParts()
	 * computed} and saved.
	 *
	 * @return The {@link AbstractLabelPart}s that were previously determined by
	 *         {@link #computeLabelParts()}.
	 */
	protected List<AbstractLabelPart> getLabelParts() {
		if (labelParts == null) {
			labelParts = computeLabelParts();
		}
		return labelParts;
	}

	private List<AbstractLabelPart> getNodeLabelParts(NodePart nodePart) {
		return new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(nodePart, ZestProperties.EXTERNAL_LABEL__NE), AbstractLabelPart.class));
	}

	@Override
	public void startDrag(MouseEvent e) {
		super.startDrag(e);
		// init label transform policies
		for (AbstractLabelPart lp : getLabelParts()) {
			storeAndDisableRefreshVisuals(lp);
			init(lp.getAdapter(TransformLabelPolicy.class));
		}
	}

}
