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
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/**
 * The {@link HideOperation} can be used to hide/show a {@link NodeContentPart}
 * by changing the {@link HidingModel} accordingly.
 *
 * @author mwienand
 *
 */
// TODO: split into hide and show operation
public class HideOperation extends AbstractOperation implements ITransactionalOperation {

	private NodeContentPart nodePart;
	private boolean show;
	private boolean initiallyHidden;

	/**
	 * Constructs a new {@link HideOperation} that will show or hide the given
	 * {@link NodeContentPart} depending on the <i>show</i> flag. If the
	 * nodePart is currently hidden (as indicated by the flag being set to
	 * <code>true</code> ), then the nodePart will be shown, otherwise it will
	 * be hidden upon execution.
	 *
	 * @param viewer
	 *            The viewer from which to retrieve the {@link HidingModel}.
	 *
	 * @param nodePart
	 *            The {@link NodeContentPart} to show/hide.
	 * @param show
	 *            <code>true</code> if the {@link NodeContentPart} should be
	 *            shown, otherwise <code>false</code>.
	 */
	public HideOperation(IViewer<javafx.scene.Node> viewer, NodeContentPart nodePart, boolean show) {
		super("hide/show");
		this.nodePart = nodePart;
		this.show = show;
		initiallyHidden = viewer.getAdapter(HidingModel.class).isHidden(nodePart.getContent());
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (show) {
			show();
		} else {
			hide();
		}
		return Status.OK_STATUS;
	}

	/**
	 * Returns the neighbors of the given {@link Node}, i.e. all of its
	 * {@link Node#getLocalPredecessorNodes() predecessors} and
	 * {@link Node#getLocalSuccessorNodes() successors}.
	 *
	 * @param node
	 *            The {@link Node} of which the neighbors are returned.
	 * @return An array containing all neighbors of the given {@link Node}.
	 */
	protected org.eclipse.gef4.graph.Node[] getNeighbors(org.eclipse.gef4.graph.Node node) {
		Set<org.eclipse.gef4.graph.Node> neighbors = new HashSet<org.eclipse.gef4.graph.Node>();
		neighbors.addAll(node.getLocalPredecessorNodes());
		neighbors.addAll(node.getLocalSuccessorNodes());
		return neighbors.toArray(new org.eclipse.gef4.graph.Node[] {});
	}

	/**
	 * Adjusts the {@link HidingModel} so that the {@link NodeContentPart} of
	 * this {@link HideOperation} will be hidden.
	 */
	protected void hide() {
		nodePart.getRoot().getViewer().<HidingModel> getAdapter(HidingModel.class).hide(nodePart.getContent());
	}

	@Override
	public boolean isNoOp() {
		return show ? !initiallyHidden : initiallyHidden;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Adjusts the {@link HidingModel} so that the {@link NodeContentPart} of
	 * this {@link HideOperation} will be shown.
	 */
	protected void show() {
		nodePart.getRoot().getViewer().<HidingModel> getAdapter(HidingModel.class).show(nodePart.getContent());
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (show) {
			hide();
		} else {
			show();
		}
		return Status.OK_STATUS;
	}

}
