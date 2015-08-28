/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

public class HideOperation extends AbstractOperation {

	public static HideOperation hide(NodeContentPart toHide) {
		return new HideOperation(toHide, false);
	}

	public static HideOperation show(NodeContentPart toShow) {
		return new HideOperation(toShow, true);
	}

	private NodeContentPart node;
	private boolean isHidden;

	public HideOperation(NodeContentPart node, boolean isHidden) {
		super("hide/show");
		this.node = node;
		this.isHidden = isHidden;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (isHidden) {
			show();
		} else {
			hide();
		}
		return Status.OK_STATUS;
	}

	protected org.eclipse.gef4.graph.Node[] getNeighbors(org.eclipse.gef4.graph.Node node) {
		Set<org.eclipse.gef4.graph.Node> neighbors = new HashSet<org.eclipse.gef4.graph.Node>();
		neighbors.addAll(node.getLocalPredecessorNodes());
		neighbors.addAll(node.getLocalSuccessorNodes());
		return neighbors.toArray(new org.eclipse.gef4.graph.Node[] {});
	}

	protected void hide() {
		node.getRoot().getViewer().<HidingModel> getAdapter(HidingModel.class).hide(node.getContent());
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	protected void show() {
		node.getRoot().getViewer().<HidingModel> getAdapter(HidingModel.class).show(node.getContent());
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (isHidden) {
			hide();
		} else {
			show();
		}
		return Status.OK_STATUS;
	}

}
