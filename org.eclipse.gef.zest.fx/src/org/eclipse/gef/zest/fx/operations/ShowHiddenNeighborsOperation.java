/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
package org.eclipse.gef.zest.fx.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.models.HidingModel;
import org.eclipse.gef.zest.fx.parts.NodePart;

/**
 * The {@link ShowHiddenNeighborsOperation} can be used to remove the neighbors
 * of a given {@link NodePart} from the {@link HidingModel} of a given
 * {@link IViewer}.
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighborsOperation extends AbstractOperation implements ITransactionalOperation {

	private NodePart nodePart;
	private HidingModel hidingModel;
	private List<NodePart> shownNeighbors = new ArrayList<>();

	/**
	 * Constructs a new {@link ShowHiddenNeighborsOperation} that will show all
	 * hidden neighbors of the given {@link NodePart} by removing them from the
	 * {@link HidingModel} of the given {@link IViewer} upon execution.
	 *
	 * @param viewer
	 *            The viewer from which to retrieve the {@link HidingModel}.
	 * @param nodePart
	 *            The {@link NodePart} of which the hidden neighbors are to be
	 *            shown.
	 */
	public ShowHiddenNeighborsOperation(IViewer viewer, NodePart nodePart) {
		super("ShowHiddenNeighbors");
		this.nodePart = nodePart;
		hidingModel = viewer.getAdapter(HidingModel.class);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		// save the graph nodes that are removed from the hiding model
		shownNeighbors.clear();
		Set<NodePart> hiddenNeighbors = hidingModel.getHiddenNeighborParts(nodePart);
		if (hiddenNeighbors != null && !hiddenNeighbors.isEmpty()) {
			for (NodePart neighborPart : hiddenNeighbors) {
				neighborPart.activate();
				hidingModel.show(neighborPart);
				shownNeighbors.add(neighborPart);
			}
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		Set<NodePart> hiddenNeighbors = hidingModel.getHiddenNeighborParts(nodePart);
		return hiddenNeighbors != null && !hiddenNeighbors.isEmpty();
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		for (NodePart neighborPart : shownNeighbors) {
			hidingModel.hide(neighborPart);
			neighborPart.deactivate();
		}
		return Status.OK_STATUS;
	}

}
