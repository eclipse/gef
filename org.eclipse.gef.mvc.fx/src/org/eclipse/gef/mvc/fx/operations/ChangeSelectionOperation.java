/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;

/**
 * The {@link ChangeSelectionOperation} can be used to change the
 * {@link SelectionModel} of an {@link IViewer}.
 *
 * @author mwienand
 *
 */
public class ChangeSelectionOperation extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * <pre>
	 * &quot;change-selection&quot;
	 * </pre>
	 *
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "Change selection";

	private IViewer viewer;
	private List<IContentPart<? extends Node>> initialSelection;
	private List<IContentPart<? extends Node>> finalSelection;

	/**
	 * Creates a new {@link ChangeSelectionOperation} to change the selection
	 * within the given {@link IViewer} by removing the given
	 * {@link IContentPart}s. The {@link #DEFAULT_LABEL} is used.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which the selection is changed.
	 * @param finalSelection
	 *            The {@link IContentPart}s that are to be selected.
	 */
	public ChangeSelectionOperation(IViewer viewer,
			List<? extends IContentPart<? extends Node>> finalSelection) {
		this(DEFAULT_LABEL, viewer, finalSelection);
	}

	/**
	 * Creates a new {@link ChangeSelectionOperation} to change the selection.
	 *
	 * @param label
	 *            The operation's label.
	 * @param viewer
	 *            The {@link IViewer} for which the selection is changed.
	 * @param finalSelection
	 *            The {@link IContentPart}s that are to be selected.
	 */
	public ChangeSelectionOperation(String label, IViewer viewer,
			List<? extends IContentPart<? extends Node>> finalSelection) {
		super(label);
		this.viewer = viewer;
		this.finalSelection = new ArrayList<>(finalSelection);
		SelectionModel selectionModel = getSelectionModel();
		initialSelection = new ArrayList<>(
				selectionModel.getSelectionUnmodifiable());
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		SelectionModel selectionModel = getSelectionModel();
		ArrayList<IContentPart<? extends Node>> currentSelection = new ArrayList<>(
				selectionModel.getSelectionUnmodifiable());
		if (!currentSelection.equals(finalSelection)) {
			selectionModel.setSelection(finalSelection);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Returns the {@link SelectionModel} adapted to the viewer.
	 *
	 * @return The {@link SelectionModel} adapter.
	 */
	protected SelectionModel getSelectionModel() {
		return viewer.getAdapter(SelectionModel.class);
	}

	/**
	 * Returns the parts that are to be deleted.
	 *
	 * @return A reference to the to be deleted {@link IContentPart}s.
	 */
	public List<IContentPart<? extends Node>> getToBeDeselected() {
		return finalSelection;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return initialSelection.equals(finalSelection);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		SelectionModel selectionModel = getSelectionModel();

		ArrayList<IContentPart<? extends Node>> currentSelection = new ArrayList<>(
				selectionModel.getSelectionUnmodifiable());
		if (!currentSelection.equals(initialSelection)) {
			selectionModel.setSelection(initialSelection);
		}

		return Status.OK_STATUS;
	}
}
