/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * Operation to change the anchorages of a specific anchored.
 * 
 * @author mwienand
 * @param <VR>
 *            Specifies the visual root type.
 * 
 */
public class ChangeAnchoragesOperation<VR> extends AbstractOperation {

	private IVisualPart<VR> anchored;
	private ArrayList<IVisualPart<VR>> toRemove;
	private ArrayList<IVisualPart<VR>> toAdd;

	/**
	 * Constructs a new operation from the given values.
	 * 
	 * @param label
	 *            Description of the operation.
	 * @param anchored
	 *            {@link IVisualPart} whose anchorages are altered.
	 * @param anchoragesToRemove
	 *            Anchorages which will be removed during execution.
	 * @param anchoragesToAdd
	 *            Anchorages which will be added during execution.
	 */
	public ChangeAnchoragesOperation(String label, IVisualPart<VR> anchored,
			List<IVisualPart<VR>> anchoragesToRemove,
			List<IVisualPart<VR>> anchoragesToAdd) {
		super(label);
		this.anchored = anchored;
		this.toRemove = new ArrayList<IVisualPart<VR>>(anchoragesToRemove);
		this.toAdd = new ArrayList<IVisualPart<VR>>(anchoragesToAdd);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// TODO: check if all removable
		anchored.removeAnchorages(toRemove);
		// TODO: check if all addable
		anchored.addAnchorages(toAdd);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// TODO: check if all removable
		anchored.removeAnchorages(toAdd);
		// TODO: check if all addable
		anchored.addAnchorages(toRemove);
		return Status.OK_STATUS;
	}

}
