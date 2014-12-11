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

import java.util.HashSet;
import java.util.Set;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.models.PruningModel;
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

	protected org.eclipse.gef4.graph.Node[] getNeighbors(
			org.eclipse.gef4.graph.Node node) {
		Set<org.eclipse.gef4.graph.Node> neighbors = new HashSet<org.eclipse.gef4.graph.Node>();
		neighbors.addAll(node.getLocalPredecessorNodes());
		neighbors.addAll(node.getLocalSuccessorNodes());
		return neighbors.toArray(new org.eclipse.gef4.graph.Node[] {});
	}

	protected void prune() {
		IViewer<Node> viewer = node.getRoot().getViewer();
		// add to neighboring subgraphs
		PruningModel pruningModel = viewer.getDomain().getAdapter(
				PruningModel.class);
		pruningModel.prune(node.getContent());
		node.deactivate();
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
		node.activate();
		IViewer<Node> viewer = node.getRoot().getViewer();
		PruningModel pruningModel = viewer.getDomain().getAdapter(
				PruningModel.class);
		pruningModel.unprune(node.getContent());
	}

}
