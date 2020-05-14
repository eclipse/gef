/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
 * The {@link HideOperation} can be used to add a {@link NodePart} to the
 * {@link HidingModel}.
 *
 * @author mwienand
 *
 */
public class HideOperation extends AbstractOperation implements ITransactionalOperation {

	private NodePart nodePart;
	private IViewer viewer;
	private HidingModel hidingModel;
	private boolean initialHiddenStatus;

	/**
	 * Constructs a new {@link HideOperation} that will hide the given
	 * {@link NodePart} upon execution.
	 *
	 * @param viewer
	 *            The viewer from which to retrieve the {@link HidingModel}.
	 * @param nodePart
	 *            The {@link NodePart} to show/hide.
	 */
	public HideOperation(IViewer viewer, NodePart nodePart) {
		super("Hide");
		this.viewer = viewer;
		this.nodePart = nodePart;
		hidingModel = viewer.<HidingModel>getAdapter(HidingModel.class);
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
	public boolean isContentRelevant() {
		return false;
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
