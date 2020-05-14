/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;

/**
 * The {@link ChangeFocusOperation} can be used to change the {@link FocusModel}
 * of an {@link IViewer}.
 *
 * @author mwienand
 *
 */
// TODO: split into focus and unfocus operations
public class ChangeFocusOperation extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * <pre>
	 * &quot;change-focus&quot;
	 * </pre>
	 *
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "Change Focus";

	private IViewer viewer;
	private IContentPart<? extends Node> oldFocused;
	private IContentPart<? extends Node> newFocused;

	/**
	 * Creates a new {@link ChangeFocusOperation} to assign focus to the given
	 * <i>newFocused</i> {@link IContentPart} within the given {@link IViewer}.
	 * When the operation is undone, focus is assigned to the given
	 * <i>oldFocused</i> {@link IContentPart}.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which the {@link FocusModel} is to be
	 *            changed.
	 * @param newFocused
	 *            The {@link IContentPart} to which focus will be assigned.
	 */
	public ChangeFocusOperation(IViewer viewer,
			IContentPart<? extends Node> newFocused) {
		this(DEFAULT_LABEL, viewer, newFocused);
	}

	/**
	 * Creates a new {@link ChangeFocusOperation} to assign focus to the given
	 * <i>newFocused</i> {@link IContentPart} within the given {@link IViewer}.
	 * When the operation is undone, focus is assigned to the given
	 * <i>oldFocused</i> {@link IContentPart}. The given <i>label</i> is used as
	 * the label for the operation.
	 *
	 * @param label
	 *            The operation's label.
	 * @param viewer
	 *            The {@link IViewer} for which the {@link FocusModel} is to be
	 *            changed.
	 * @param newFocused
	 *            The {@link IContentPart} to which focus will be assigned.
	 */
	public ChangeFocusOperation(String label, IViewer viewer,
			IContentPart<? extends Node> newFocused) {
		super(label);
		this.viewer = viewer;
		this.oldFocused = getFocusModel().getFocus();
		this.newFocused = newFocused;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		getFocusModel().setFocus(newFocused);
		return Status.OK_STATUS;
	}

	/**
	 * Returns the {@link FocusModel} adapted to the viewer.
	 *
	 * @return The {@link FocusModel} adapter.
	 */
	protected FocusModel getFocusModel() {
		return viewer.getAdapter(FocusModel.class);
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return oldFocused == newFocused;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the new focussed part to the given one.
	 *
	 * @param newFocused
	 *            The new focus part.
	 */
	public void setNewFocused(IContentPart<? extends Node> newFocused) {
		this.newFocused = newFocused;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		getFocusModel().setFocus(oldFocused);
		return Status.OK_STATUS;
	}

}
