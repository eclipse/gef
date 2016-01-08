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
 * The {@link HideOperation} can be used to add a {@link NodeContentPart} to the
 * {@link HidingModel}.
 *
 * @author mwienand
 *
 */
public class HideOperation extends AbstractOperation implements ITransactionalOperation {

	private NodeContentPart nodePart;
	private IViewer<javafx.scene.Node> viewer;
	private HidingModel hidingModel;
	private boolean initialHiddenStatus;

	/**
	 * Constructs a new {@link HideOperation} that will hide the given
	 * {@link NodeContentPart} upon execution.
	 *
	 * @param viewer
	 *            The viewer from which to retrieve the {@link HidingModel}.
	 * @param nodePart
	 *            The {@link NodeContentPart} to show/hide.
	 */
	public HideOperation(IViewer<javafx.scene.Node> viewer, NodeContentPart nodePart) {
		super("Hide");
		this.viewer = viewer;
		this.nodePart = nodePart;
		hidingModel = viewer.<HidingModel> getAdapter(HidingModel.class);
		initialHiddenStatus = hidingModel.isHidden(nodePart);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (!viewer.getAdapter(HidingModel.class).isHidden(nodePart)) {
			hidingModel.hide(nodePart);
			nodePart.deactivate();
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isNoOp() {
		return initialHiddenStatus;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (viewer.getAdapter(HidingModel.class).isHidden(nodePart)) {
			nodePart.activate();
			hidingModel.show(nodePart);
		}
		return Status.OK_STATUS;
	}

}
