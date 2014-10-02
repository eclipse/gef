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

import javafx.scene.Node;

import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.ILayoutModel;
import org.eclipse.gef4.zest.fx.models.SubgraphModel;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import com.google.common.collect.Multiset;

// TODO: only applicable for NodeContentPart
public class PruneNodePolicy extends AbstractPolicy<Node> {

	private boolean pruned = false;

	protected NodeLayout[] getPredecessors(NodeContentPart host,
			IViewer<Node> viewer) {
		ILayoutModel layoutModel = viewer.getDomain().getAdapter(
				ILayoutModel.class);
		GraphLayoutContext layoutContext = (GraphLayoutContext) layoutModel
				.getLayoutContext();
		NodeLayout[] predecessors = layoutContext.getNodeLayout(
				host.getContent()).getPredecessingNodes();
		return predecessors;
	}

	public boolean isPruned() {
		return pruned;
	}

	public void prune() {
		if (!isPruned()) {
			NodeContentPart host = (NodeContentPart) getHost();
			IViewer<Node> viewer = host.getRoot().getViewer();

			// add to predecessing subgraphs
			SubgraphModel subgraphModel = viewer.getDomain().getAdapter(
					SubgraphModel.class);
			for (NodeLayout p : getPredecessors(host, viewer)) {
				NodeContentPart pNodePart = (NodeContentPart) viewer
						.getContentPartMap().get(p.getItems()[0]);
				subgraphModel.addNodesToSubgraph(pNodePart, host);
			}

			// hide visual
			host.getVisual().setVisible(false);

			// hide connections
			Multiset<IVisualPart<Node>> anchoreds = host.getAnchoreds();
			for (IVisualPart<Node> anchored : anchoreds.elementSet()) {
				if (anchored instanceof EdgeContentPart) {
					anchored.getVisual().setVisible(false);
				}
			}

			pruned = true;
		}
	}

	public void unprune() {
		if (isPruned()) {
			NodeContentPart host = (NodeContentPart) getHost();
			IViewer<Node> viewer = host.getRoot().getViewer();
			SubgraphModel subgraphModel = viewer.getDomain().getAdapter(
					SubgraphModel.class);
			for (NodeLayout p : getPredecessors(host, viewer)) {
				NodeContentPart pNodePart = (NodeContentPart) viewer
						.getContentPartMap().get(p.getItems()[0]);
				subgraphModel.removeNodesFromSubgraph(pNodePart, host);
			}

			// show node
			host.getVisual().setVisible(true);

			// show connections
			Multiset<IVisualPart<Node>> anchoreds = host.getAnchoreds();
			for (IVisualPart<Node> anchored : anchoreds.elementSet()) {
				if (anchored instanceof EdgeContentPart) {
					anchored.getVisual().setVisible(true);
				}
			}

			pruned = false;
		}
	}

}
