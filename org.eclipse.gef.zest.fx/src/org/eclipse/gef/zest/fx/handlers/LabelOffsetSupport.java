/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.AbstractLabelPart;
import org.eclipse.gef.zest.fx.parts.EdgePart;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.gef.zest.fx.policies.TransformLabelPolicy;

import javafx.scene.Node;

/**
 * The {@link LabelOffsetSupport} can be used to preserve label positions during
 * interaction w.r.t. their reference positions.
 */
public class LabelOffsetSupport extends IAdaptable.Bound.Impl<IViewer> {

	private AbstractLabelPart[] labelParts;
	private TransformLabelPolicy[] policies;
	private boolean[] wasRefresh;

	/**
	 * Aborts preserving of label offsets by calling
	 * {@link TransformLabelPolicy#rollback()} on the individual
	 * {@link TransformLabelPolicy TransformLabelPolicies}.
	 */
	public void abort() {
		if (labelParts != null) {
			// abort policies
			for (int i = 0; i < labelParts.length; i++) {
				policies[i].rollback();
				labelParts[i].setRefreshVisual(wasRefresh[i]);
			}
			labelParts = null;
			policies = null;
			wasRefresh = null;
		}
	}

	/**
	 * Commits preserving of label offsets by calling
	 * {@link TransformLabelPolicy#commit()} on the individual
	 * {@link TransformLabelPolicy TransformLabelPolicies} and executing the
	 * resulting operations on the {@link IDomain} of the {@link #getAdaptable()
	 * viewer}.
	 */
	public void commit() {
		if (labelParts != null) {
			// commit policies and execute on domain
			for (int i = 0; i < labelParts.length; i++) {
				try {
					getAdaptable().getDomain().execute(policies[i].commit(), null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				labelParts[i].setRefreshVisual(wasRefresh[i]);
			}
			labelParts = null;
			policies = null;
			wasRefresh = null;
		}
	}

	/**
	 * Returns the {@link AbstractLabelPart}s attached to the given
	 * {@link EdgePart}.
	 *
	 * @param edgePart
	 *            The {@link EdgePart} for which to determine the label parts.
	 * @return The {@link AbstractLabelPart}s attached to the given
	 *         {@link EdgePart}.
	 */
	protected List<AbstractLabelPart> getEdgeLabelParts(EdgePart edgePart) {
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
	 * Computes the {@link AbstractLabelPart}s that are attached to the targets
	 * of this {@link LabelOffsetSupport}. Ingores label parts that are already
	 * contained in the targets.
	 *
	 * @param targets
	 *            {@link List} of target {@link IVisualPart}s.
	 * @return The {@link AbstractLabelPart}s that should be relocated.
	 */
	protected List<AbstractLabelPart> getLabelParts(List<? extends IVisualPart<? extends Node>> targets) {
		Set<AbstractLabelPart> labelParts = Collections
				.newSetFromMap(new IdentityHashMap<AbstractLabelPart, Boolean>());

		// find label parts
		for (IVisualPart<? extends Node> target : targets) {
			if (target instanceof NodePart) {
				labelParts.addAll(getNodeLabelParts((NodePart) target));
				for (IVisualPart<? extends javafx.scene.Node> anchored : target.getAnchoredsUnmodifiable()) {
					// add labels for connected edges
					if (anchored instanceof EdgePart) {
						labelParts.addAll(getEdgeLabelParts((EdgePart) anchored));
					}
				}
			} else if (target instanceof EdgePart) {
				labelParts.addAll(getEdgeLabelParts((EdgePart) target));
			}
		}

		// filter out those that do not have a stored position
		for (Iterator<AbstractLabelPart> iterator = labelParts.iterator(); iterator.hasNext();) {
			if (iterator.next().getLabelPosition() == null) {
				iterator.remove();
			}
		}

		labelParts.removeAll(targets);
		return new ArrayList<>(labelParts);
	}

	/**
	 * Returns the {@link AbstractLabelPart}s attached to the given
	 * {@link NodePart}.
	 *
	 * @param nodePart
	 *            The {@link NodePart} for which to determine the label parts.
	 * @return The {@link AbstractLabelPart}s attached to the given
	 *         {@link NodePart}.
	 */
	protected List<AbstractLabelPart> getNodeLabelParts(NodePart nodePart) {
		return new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(nodePart, ZestProperties.EXTERNAL_LABEL__NE), AbstractLabelPart.class));
	}

	/**
	 * @param targets
	 *            The {@link IVisualPart}s for which to relocate the attached
	 *            {@link AbstractLabelPart}s.
	 */
	public void init(List<? extends IVisualPart<? extends Node>> targets) {
		// find labels that support preserving their offset
		List<AbstractLabelPart> labels = getLabelParts(targets);
		Iterator<AbstractLabelPart> it = labels.iterator();
		while (it.hasNext()) {
			if (it.next().getAdapter(TransformLabelPolicy.class) == null) {
				it.remove();
			}
		}
		if (!labels.isEmpty()) {
			// query and initialize policies
			labelParts = labels.toArray(new AbstractLabelPart[] {});
			policies = new TransformLabelPolicy[labelParts.length];
			wasRefresh = new boolean[labelParts.length];
			for (int i = 0; i < labelParts.length; i++) {
				policies[i] = labelParts[i].getAdapter(TransformLabelPolicy.class);
				wasRefresh[i] = labelParts[i].isRefreshVisual();
				labelParts[i].setRefreshVisual(false);
				policies[i].init();
			}
		}
	}

	/**
	 * Preserves label offsets by calling
	 * {@link TransformLabelPolicy#preserveLabelOffset()} on the individual
	 * {@link TransformLabelPolicy TransformLabelPolicies}.
	 */
	public void preserveLabelOffsets() {
		if (labelParts != null) {
			// use policies to relocate by given delta
			for (int i = 0; i < labelParts.length; i++) {
				policies[i].preserveLabelOffset();
			}
		}
	}
}
