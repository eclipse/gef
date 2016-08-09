/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.common.reflect.Types;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * The {@link DeselectOperation} can be used to change the
 * {@link SelectionModel} of an {@link IViewer}.
 *
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit, e.g. javafx.scene.Node in
 *            case of JavaFX.
 */
public class DeselectOperation<VR> extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * <pre>
	 * &quot;change-selection&quot;
	 * </pre>
	 *
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "Deselect";

	private IViewer<VR> viewer;
	private List<IContentPart<VR, ? extends VR>> initialSelection;
	private List<IContentPart<VR, ? extends VR>> toBeDeselected;
	private ArrayList<IContentPart<VR, ? extends VR>> deselected;

	/**
	 * Creates a new {@link DeselectOperation} to change the selection within
	 * the given {@link IViewer} by removing the given {@link IContentPart}s.
	 * The {@link #DEFAULT_LABEL} is used.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which the selection is changed.
	 * @param toBeDeselected
	 *            The {@link IContentPart}s that are to be selected.
	 */
	public DeselectOperation(IViewer<VR> viewer,
			List<? extends IContentPart<VR, ? extends VR>> toBeDeselected) {
		this(DEFAULT_LABEL, viewer, toBeDeselected);
	}

	/**
	 * Creates a new {@link DeselectOperation} to change the selection within
	 * the given {@link IViewer} by removing the given {@link IContentPart}s.
	 * The given label is used.
	 *
	 * @param label
	 *            The operation's label.
	 * @param viewer
	 *            The {@link IViewer} for which the selection is changed.
	 * @param toBeDeselected
	 *            The {@link IContentPart}s that are to be selected.
	 */
	public DeselectOperation(String label, IViewer<VR> viewer,
			List<? extends IContentPart<VR, ? extends VR>> toBeDeselected) {
		super(label);
		this.viewer = viewer;
		this.toBeDeselected = new ArrayList<>(toBeDeselected);
		SelectionModel<VR> selectionModel = getSelectionModel();
		initialSelection = new ArrayList<>(
				selectionModel.getSelectionUnmodifiable());
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		SelectionModel<VR> selectionModel = getSelectionModel();
		deselected = new ArrayList<>(toBeDeselected);
		deselected.retainAll(
				new ArrayList<>(selectionModel.getSelectionUnmodifiable()));
		selectionModel.removeFromSelection(deselected);
		return Status.OK_STATUS;
	}

	/**
	 * Returns the {@link SelectionModel} adapted to the viewer.
	 *
	 * @return The {@link SelectionModel} adapter.
	 */
	@SuppressWarnings("serial")
	protected SelectionModel<VR> getSelectionModel() {
		SelectionModel<VR> selectionModel = viewer
				.getAdapter(new TypeToken<SelectionModel<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(viewer.getClass())));
		return selectionModel;
	}

	/**
	 * Returns the parts that are to be deleted.
	 *
	 * @return A reference to the to be deleted {@link IContentPart}s.
	 */
	public List<IContentPart<VR, ? extends VR>> getToBeDeselected() {
		return toBeDeselected;
	}

	@Override
	public boolean isContentRelevant() {
		return false;
	}

	@Override
	public boolean isNoOp() {
		return Collections.disjoint(initialSelection, toBeDeselected);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		SelectionModel<VR> selectionModel = getSelectionModel();
		selectionModel.setSelection(initialSelection);
		return Status.OK_STATUS;
	}

}
