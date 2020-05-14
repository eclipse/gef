/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
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
 * The {@link SelectOperation} can be used to change the {@link SelectionModel}
 * of an {@link IViewer}.
 *
 * @author mwienand
 *
 */
public class SelectOperation extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * <pre>
	 * &quot;change-selection&quot;
	 * </pre>
	 *
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "Select";

	private IViewer viewer;
	private List<IContentPart<? extends Node>> initialSelection;
	private List<IContentPart<? extends Node>> toBeSelected;
	private List<IContentPart<? extends Node>> selected;

	/**
	 * Creates a new {@link SelectOperation} to change the selection within the
	 * given {@link IViewer} to prepend the given content parts. It uses the
	 * {@link #DEFAULT_LABEL}.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which the selection is changed.
	 * @param toBeSelected
	 *            The {@link IContentPart}s that are to be selected.
	 */
	public SelectOperation(IViewer viewer,
			List<? extends IContentPart<? extends Node>> toBeSelected) {
		this(DEFAULT_LABEL, viewer, toBeSelected);
	}

	/**
	 * * Creates a new {@link SelectOperation} to change the selection within
	 * the given {@link IViewer} to prepend the given content parts. The given
	 * label is used.
	 *
	 * @param label
	 *            The operation's label.
	 * @param viewer
	 *            The {@link IViewer} for which the selection is changed.
	 * @param toBeSelected
	 *            The {@link IContentPart}s that are to be selected.
	 */
	public SelectOperation(String label, IViewer viewer,
			List<? extends IContentPart<? extends Node>> toBeSelected) {
		super(label);
		this.viewer = viewer;
		this.toBeSelected = new ArrayList<>(toBeSelected);
		SelectionModel selectionModel = getSelectionModel();
		initialSelection = new ArrayList<>(
				selectionModel.getSelectionUnmodifiable());
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		SelectionModel selectionModel = getSelectionModel();
		selected = new ArrayList<>(toBeSelected);
		selected.removeAll(
				new ArrayList<>(selectionModel.getSelectionUnmodifiable()));
		selectionModel.prependToSelection(selected);
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

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return initialSelection.containsAll(toBeSelected);
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
		selectionModel.setSelection(initialSelection);
		return Status.OK_STATUS;
	}

}
