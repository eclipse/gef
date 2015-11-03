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

/**
 * The {@link ChangeSelectionOperation} can be used to change the
 * {@link SelectionModel} of an {@link IViewer}.
 *
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit, e.g. javafx.scene.Node in
 *            case of JavaFX.
 */
public class ChangeSelectionOperation<VR> extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * <pre>
	 * &quot;change-selection&quot;
	 * </pre>
	 *
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "Change Selection";

	private IViewer<VR> viewer;
	private List<IContentPart<VR, ? extends VR>> oldSelection;
	private List<IContentPart<VR, ? extends VR>> newSelection;

	/**
	 * Creates a new {@link ChangeSelectionOperation} to change the selection
	 * within the given {@link IViewer} to the given list of
	 * {@link IContentPart}s.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which the selection is changed.
	 * @param newSelection
	 *            The new selection.
	 */
	public ChangeSelectionOperation(IViewer<VR> viewer,
			List<? extends IContentPart<VR, ? extends VR>> newSelection) {
		this(DEFAULT_LABEL, viewer,
				viewer.<SelectionModel<VR>> getAdapter(SelectionModel.class)
						.getSelection(),
				newSelection);
	}

	/**
	 * Creates a new {@link ChangeSelectionOperation} to change the selection
	 * within the given {@link IViewer} to the given <i>newSelection</i>. When
	 * undoing this operation, the given <i>oldSelection</i> is restored.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which the selection is changed.
	 * @param oldSelection
	 *            The old selection.
	 * @param newSelection
	 *            The new selection.
	 */
	public ChangeSelectionOperation(IViewer<VR> viewer,
			List<? extends IContentPart<VR, ? extends VR>> oldSelection,
			List<? extends IContentPart<VR, ? extends VR>> newSelection) {
		this(DEFAULT_LABEL, viewer, oldSelection, newSelection);
	}

	/**
	 * Creates a new {@link ChangeSelectionOperation} to change the selection
	 * within the given {@link IViewer} to the given <i>newSelection</i>. When
	 * undoing this operation, the given <i>oldSelection</i> is restored. The
	 * given label is used as the label for the operation.
	 *
	 * @param label
	 *            The operation's label.
	 * @param viewer
	 *            The {@link IViewer} for which the selection is changed.
	 * @param oldSelection
	 *            The old selection.
	 * @param newSelection
	 *            The new selection.
	 */
	public ChangeSelectionOperation(String label, IViewer<VR> viewer,
			List<? extends IContentPart<VR, ? extends VR>> oldSelection,
			List<? extends IContentPart<VR, ? extends VR>> newSelection) {
		super(label);
		this.viewer = viewer;
		this.oldSelection = new ArrayList<IContentPart<VR, ? extends VR>>(
				oldSelection);
		this.newSelection = new ArrayList<IContentPart<VR, ? extends VR>>(
				newSelection);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		SelectionModel<VR> selectionModel = viewer
				.<SelectionModel<VR>> getAdapter(SelectionModel.class);
		selectionModel.setSelection(newSelection);
		return Status.OK_STATUS;
	}

	@Override
	public boolean isNoOp() {
		return oldSelection == newSelection
				|| (oldSelection != null && oldSelection.equals(newSelection));
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
		selectionModel.setSelection(oldSelection);
		return Status.OK_STATUS;
	}

}
