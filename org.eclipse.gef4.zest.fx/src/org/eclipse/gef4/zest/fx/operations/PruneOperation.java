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
package org.eclipse.gef4.zest.fx.operations;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.ILayoutModel;
import org.eclipse.gef4.zest.fx.models.SubgraphModel;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

public class PruneOperation extends AbstractOperation {

	public static PruneOperation prune(NodeContentPart toPrune) {
		return new PruneOperation(toPrune, false);
	}

	public static PruneOperation unprune(NodeContentPart toUnprune) {
		return new PruneOperation(toUnprune, true);
	}

	private NodeContentPart node;
	private boolean isPruned;

	public PruneOperation(NodeContentPart node, boolean isPruned) {
		super("prune");
		this.node = node;
		this.isPruned = isPruned;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (isPruned) {
			unprune();
		} else {
			prune();
		}
		return Status.OK_STATUS;
	}

	protected NodeLayout[] getNeighbors(NodeContentPart host,
			IViewer<Node> viewer) {
		ILayoutModel layoutModel = viewer.getDomain().getAdapter(
				ILayoutModel.class);
		GraphLayoutContext layoutContext = (GraphLayoutContext) layoutModel
				.getLayoutContext();
		NodeLayout[] predecessors = layoutContext.getNodeLayout(
				host.getContent()).getPredecessingNodes();
		NodeLayout[] successors = layoutContext
				.getNodeLayout(host.getContent()).getSuccessingNodes();
		NodeLayout[] neighbors = new NodeLayout[predecessors.length
				+ successors.length];
		for (int i = 0; i < predecessors.length; i++) {
			neighbors[i] = predecessors[i];
		}
		for (int i = 0; i < successors.length; i++) {
			neighbors[predecessors.length + i] = successors[i];
		}
		return neighbors;
	}

	protected void prune() {
		IViewer<Node> viewer = node.getRoot().getViewer();
		// add to neighboring subgraphs
		SubgraphModel subgraphModel = viewer.getDomain().getAdapter(
				SubgraphModel.class);
		for (NodeLayout p : getNeighbors(node, viewer)) {
			NodeContentPart pNodePart = (NodeContentPart) viewer
					.getContentPartMap().get(p.getItems()[0]);
			subgraphModel.addNodesToSubgraph(pNodePart, node);
		}
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (isPruned) {
			prune();
		} else {
			unprune();
		}
		return Status.OK_STATUS;
	}

	protected void unprune() {
		IViewer<Node> viewer = node.getRoot().getViewer();
		SubgraphModel subgraphModel = viewer.getDomain().getAdapter(
				SubgraphModel.class);
		// remove from neighboring subgraphs
		for (NodeLayout p : getNeighbors(node, viewer)) {
			NodeContentPart pNodePart = (NodeContentPart) viewer
					.getContentPartMap().get(p.getItems()[0]);
			subgraphModel.removeNodesFromSubgraph(pNodePart, node);
		}
	}

}
