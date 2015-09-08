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
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/**
 * The {@link HideOperation} can be used to hide/show a {@link NodeContentPart}
 * by changing the {@link HidingModel} accordingly.
 *
 * @author mwienand
 *
 */
public class HideOperation extends AbstractOperation {

	/**
	 * Constructs a new {@link HideOperation} that will hide the given
	 * {@link NodeContentPart} upon execution.
	 *
	 * @param toHide
	 *            The {@link NodeContentPart} to hide.
	 * @return The new {@link HideOperation} that will hide the given
	 *         {@link NodeContentPart} upon execution.
	 */
	public static HideOperation hide(NodeContentPart toHide) {
		return new HideOperation(toHide, false);
	}

	/**
	 * Constructs a new {@link HideOperation} that will show the given
	 * {@link NodeContentPart} upon execution.
	 *
	 * @param toShow
	 *            The {@link NodeContentPart} to show.
	 * @return The new {@link HideOperation} that will show the given
	 *         {@link NodeContentPart} upon execution.
	 */
	public static HideOperation show(NodeContentPart toShow) {
		return new HideOperation(toShow, true);
	}

	private NodeContentPart node;
	private boolean isHidden;

	/**
	 * Constructs a new {@link HideOperation} that will show or hide the given
	 * {@link NodeContentPart} depending on the <i>isHidden</i> flag. If the
	 * node is currently hidden (as indicated by the flag being set to
	 * <code>true</code>), then the node will be shown, otherwise it will be
	 * hidden upon execution.
	 *
	 * @param node
	 *            The {@link NodeContentPart} to show/hide.
	 * @param isHidden
	 *            <code>true</code> if the {@link NodeContentPart} should be
	 *            shown, otherwise <code>false</code>.
	 */
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
		node.getRoot().getViewer().<HidingModel> getAdapter(HidingModel.class).hide(node.getContent());
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
