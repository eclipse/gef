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
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class ChangeSelectionOperation<VR> extends AbstractOperation {

	/**
	 * <pre>
	 * &quot;change-selection&quot;
	 * </pre>
	 *
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "change-selection";

	private IViewer<VR> viewer;
	private List<IContentPart<VR>> oldSelection;
	private List<IContentPart<VR>> newSelection;

	public ChangeSelectionOperation(IViewer<VR> viewer,
			List<IContentPart<VR>> newSelection) {
		this(DEFAULT_LABEL, viewer, viewer.<SelectionModel<VR>> getAdapter(
				SelectionModel.class).getSelected(), newSelection);
	}

	public ChangeSelectionOperation(IViewer<VR> viewer,
			List<IContentPart<VR>> oldSelection,
			List<IContentPart<VR>> newSelection) {
		this(DEFAULT_LABEL, viewer, oldSelection, newSelection);
	}

	public ChangeSelectionOperation(String label, IViewer<VR> viewer,
			List<IContentPart<VR>> oldSelection,
			List<IContentPart<VR>> newSelection) {
		super(label);
		this.viewer = viewer;
		this.oldSelection = new ArrayList<IContentPart<VR>>(oldSelection);
		this.newSelection = new ArrayList<IContentPart<VR>>(newSelection);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		SelectionModel selectionModel = viewer.getAdapter(SelectionModel.class);
		selectionModel.deselectAll();
		selectionModel.select(newSelection);
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
		SelectionModel<VR> selectionModel = viewer
				.<SelectionModel<VR>> getAdapter(SelectionModel.class);
		selectionModel.deselectAll();
		selectionModel.select(oldSelection);
		return Status.OK_STATUS;
	}

}
