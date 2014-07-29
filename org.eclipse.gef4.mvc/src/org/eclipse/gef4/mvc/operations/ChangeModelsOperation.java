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
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * Operation to change the default interaction models which store visual/content
 * parts, i.e. hover, focus, and selection model. When changes to those models
 * are needed in the context of a policy or operation, an instance of this
 * operation can be used to encapsulate the changes, so that they can be
 * reverted.
 * 
 * @author mwienand
 * 
 * @param <VR>
 *            Specifies the visual root type.
 */
public class ChangeModelsOperation<VR> extends AbstractOperation {

	/**
	 * <pre>
	 * &quot;Update hover/focus/selection&quot;
	 * </pre>
	 * 
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "Update hover/focus/selection";

	private IViewer<VR> viewer;
	private IVisualPart<VR> oldHovered;
	private IContentPart<VR> oldFocused;
	private ArrayList<IContentPart<VR>> oldSelected;
	private IVisualPart<VR> newHovered;
	private IContentPart<VR> newFocused;
	private ArrayList<IContentPart<VR>> newSelected;

	/**
	 * Constructs a new operation where the "old" values (old hovered, old
	 * focused, old selected) are retrieved from the given viewer. Uses the
	 * {@link #DEFAULT_LABEL default label}.
	 * 
	 * @param viewer
	 *            {@link IViewer} whose models are altered.
	 * @param newHovered
	 *            The {@link IVisualPart} which should be considered hovered
	 *            after executing this operation.
	 * @param newFocused
	 *            The {@link IContentPart} which should be considered focused
	 *            after executing this operation.
	 * @param newSelected
	 *            A {@link List} of {@link IContentPart}s which should be
	 *            considered selected after executing this operation.
	 */
	public ChangeModelsOperation(IViewer<VR> viewer,
			IVisualPart<VR> newHovered, IContentPart<VR> newFocused,
			List<IContentPart<VR>> newSelected) {
		this(DEFAULT_LABEL, viewer, viewer.getHoverModel().getHover(), viewer
				.getFocusModel().getFocused(), viewer.getSelectionModel()
				.getSelected(), newHovered, newFocused, newSelected);
	}

	/**
	 * Constructs a new operation from the given values. Uses the
	 * {@link #DEFAULT_LABEL default label}.
	 * 
	 * @param viewer
	 *            {@link IViewer} whose models are altered.
	 * @param oldHovered
	 *            The {@link IVisualPart} which should be considered hovered
	 *            after reverting this operation.
	 * @param oldFocused
	 *            The {@link IContentPart} which should be considered focused
	 *            after reverting this operation.
	 * @param oldSelected
	 *            A {@link List} of {@link IContentPart}s which should be
	 *            considered selected after reverting this operation.
	 * @param newHovered
	 *            The {@link IVisualPart} which should be considered hovered
	 *            after executing this operation.
	 * @param newFocused
	 *            The {@link IContentPart} which should be considered focused
	 *            after executing this operation.
	 * @param newSelected
	 *            A {@link List} of {@link IContentPart}s which should be
	 *            considered selected after executing this operation.
	 */
	public ChangeModelsOperation(IViewer<VR> viewer,
			IVisualPart<VR> oldHovered, IContentPart<VR> oldFocused,
			List<IContentPart<VR>> oldSelected, IVisualPart<VR> newHovered,
			IContentPart<VR> newFocused, List<IContentPart<VR>> newSelected) {
		this(DEFAULT_LABEL, viewer, oldHovered, oldFocused, oldSelected,
				newHovered, newFocused, newSelected);
	}

	/**
	 * Constructs a new operation where the "old" values (old hovered, old
	 * focused, old selected) are retrieved from the given viewer.
	 * 
	 * @param label
	 *            Description of this operation.
	 * @param viewer
	 *            {@link IViewer} whose models are altered.
	 * @param newHovered
	 *            The {@link IVisualPart} which should be considered hovered
	 *            after executing this operation.
	 * @param newFocused
	 *            The {@link IContentPart} which should be considered focused
	 *            after executing this operation.
	 * @param newSelected
	 *            A {@link List} of {@link IContentPart}s which should be
	 *            considered selected after executing this operation.
	 */
	public ChangeModelsOperation(String label, IViewer<VR> viewer,
			IVisualPart<VR> newHovered, IContentPart<VR> newFocused,
			List<IContentPart<VR>> newSelected) {
		this(label, viewer, viewer.getHoverModel().getHover(), viewer
				.getFocusModel().getFocused(), viewer.getSelectionModel()
				.getSelected(), newHovered, newFocused, newSelected);
	}

	/**
	 * Constructs a new operation from the given values.
	 * 
	 * @param label
	 *            Description of this operation.
	 * @param viewer
	 *            {@link IViewer} whose models are altered.
	 * @param oldHovered
	 *            The {@link IVisualPart} which should be considered hovered
	 *            after reverting this operation.
	 * @param oldFocused
	 *            The {@link IContentPart} which should be considered focused
	 *            after reverting this operation.
	 * @param oldSelected
	 *            A {@link List} of {@link IContentPart}s which should be
	 *            considered selected after reverting this operation.
	 * @param newHovered
	 *            The {@link IVisualPart} which should be considered hovered
	 *            after executing this operation.
	 * @param newFocused
	 *            The {@link IContentPart} which should be considered focused
	 *            after executing this operation.
	 * @param newSelected
	 *            A {@link List} of {@link IContentPart}s which should be
	 *            considered selected after executing this operation.
	 */
	public ChangeModelsOperation(String label, IViewer<VR> viewer,
			IVisualPart<VR> oldHovered, IContentPart<VR> oldFocused,
			List<IContentPart<VR>> oldSelected, IVisualPart<VR> newHovered,
			IContentPart<VR> newFocused, List<IContentPart<VR>> newSelected) {
		super(label);
		this.viewer = viewer;
		this.oldHovered = oldHovered;
		this.oldFocused = oldFocused;
		this.oldSelected = new ArrayList<IContentPart<VR>>(oldSelected);
		this.newHovered = newHovered;
		this.newFocused = newFocused;
		this.newSelected = new ArrayList<IContentPart<VR>>(newSelected);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		viewer.getHoverModel().setHover(newHovered);
		viewer.getFocusModel().setFocused(newFocused);
		viewer.getSelectionModel().deselectAll();
		viewer.getSelectionModel().select(newSelected);
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
		viewer.getHoverModel().setHover(oldHovered);
		viewer.getFocusModel().setFocused(oldFocused);
		viewer.getSelectionModel().deselectAll();
		viewer.getSelectionModel().select(oldSelected);
		return Status.OK_STATUS;
	}

}
