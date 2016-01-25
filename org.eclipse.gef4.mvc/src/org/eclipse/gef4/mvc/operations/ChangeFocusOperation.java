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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.common.reflect.Types;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * The {@link ChangeFocusOperation} can be used to change the {@link FocusModel}
 * of an {@link IViewer}.
 *
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit, e.g. javafx.scene.Node in
 *            case of JavaFX.
 */
// TODO: split into focus and unfocus operations
public class ChangeFocusOperation<VR> extends AbstractOperation
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

	private IViewer<VR> viewer;
	private IContentPart<VR, ? extends VR> oldFocused;
	private IContentPart<VR, ? extends VR> newFocused;

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
	public ChangeFocusOperation(IViewer<VR> viewer,
			IContentPart<VR, ? extends VR> newFocused) {
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
	public ChangeFocusOperation(String label, IViewer<VR> viewer,
			IContentPart<VR, ? extends VR> newFocused) {
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
	@SuppressWarnings("serial")
	protected FocusModel<VR> getFocusModel() {
		FocusModel<VR> focusModel = viewer
				.getAdapter(new TypeToken<FocusModel<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(viewer.getClass())));
		return focusModel;
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
	public void setNewFocused(IContentPart<VR, ? extends VR> newFocused) {
		this.newFocused = newFocused;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		getFocusModel().setFocus(oldFocused);
		return Status.OK_STATUS;
	}

}
