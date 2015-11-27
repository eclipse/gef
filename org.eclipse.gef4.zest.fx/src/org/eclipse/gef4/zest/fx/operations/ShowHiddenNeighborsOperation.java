/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/**
 * The {@link ShowHiddenNeighborsOperation} can be used to remove the neighbors
 * of a given {@link NodeContentPart} from the {@link HidingModel} of a given
 * {@link IViewer}.
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighborsOperation extends AbstractOperation implements ITransactionalOperation {

	private NodeContentPart nodePart;
	private HidingModel hidingModel;
	private List<NodeContentPart> shownNeighbors = new ArrayList<NodeContentPart>();

	/**
	 * Constructs a new {@link ShowHiddenNeighborsOperation} that will show all
	 * hidden neighbors of the given {@link NodeContentPart} by removing them
	 * from the {@link HidingModel} of the given {@link IViewer} upon execution.
	 *
	 * @param viewer
	 *            The viewer from which to retrieve the {@link HidingModel}.
	 * @param nodePart
	 *            The {@link NodeContentPart} of which the hidden neighbors are
	 *            to be shown.
	 */
	public ShowHiddenNeighborsOperation(IViewer<javafx.scene.Node> viewer, NodeContentPart nodePart) {
		super("ShowHiddenNeighbors");
		this.nodePart = nodePart;
		hidingModel = viewer.getAdapter(HidingModel.class);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		// save the graph nodes that are removed from the hiding model
		shownNeighbors.clear();
		Set<NodeContentPart> hiddenNeighbors = hidingModel.getHiddenNeighborParts(nodePart);
		if (hiddenNeighbors != null && !hiddenNeighbors.isEmpty()) {
			for (NodeContentPart neighborPart : hiddenNeighbors) {
				hidingModel.show(neighborPart);
				shownNeighbors.add(neighborPart);
			}
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isNoOp() {
		Set<NodeContentPart> hiddenNeighbors = hidingModel.getHiddenNeighborParts(nodePart);
		return hiddenNeighbors != null && !hiddenNeighbors.isEmpty();
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		for (NodeContentPart neighborPart : shownNeighbors) {
			hidingModel.hide(neighborPart);
		}
		return Status.OK_STATUS;
	}

}
