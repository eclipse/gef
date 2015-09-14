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
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * The {@link ChangeHoverOperation} can be used to change the {@link HoverModel}
 * of an {@link IViewer}.
 *
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit, e.g. javafx.scene.Node in
 *            case of JavaFX.
 */
public class ChangeHoverOperation<VR> extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * <pre>
	 * &quot;change-hover&quot;
	 * </pre>
	 *
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "Change Hover";

	private IViewer<VR> viewer;
	private IVisualPart<VR, ? extends VR> oldHovered;
	private IVisualPart<VR, ? extends VR> newHovered;

	/**
	 * Creates a new {@link ChangeHoverOperation} to set the given
	 * <i>newHovered</i> {@link IVisualPart} as the hovered part within the
	 * given {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which the {@link HoverModel} is to be
	 *            changed.
	 * @param newHovered
	 *            The {@link IVisualPart} which will be marked as hovered.
	 */
	public ChangeHoverOperation(IViewer<VR> viewer,
			IVisualPart<VR, ? extends VR> newHovered) {
		this(DEFAULT_LABEL, viewer,
				viewer.<HoverModel<VR>> getAdapter(HoverModel.class).getHover(),
				newHovered);
	}

	/**
	 * Creates a new {@link ChangeHoverOperation} to set the given
	 * <i>newHovered</i> {@link IVisualPart} as the hovered part within the
	 * given {@link IViewer}. When undoing this operation, the given
	 * <i>oldHovered</i> {@link IVisualPart} will be set as the hovered part.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which the {@link HoverModel} is to be
	 *            changed.
	 * @param oldHovered
	 *            The {@link IVisualPart} which will be marked as hovered when
	 *            undoing this operation.
	 * @param newHovered
	 *            The {@link IVisualPart} which will be marked as hovered.
	 */
	public ChangeHoverOperation(IViewer<VR> viewer,
			IVisualPart<VR, ? extends VR> oldHovered,
			IVisualPart<VR, ? extends VR> newHovered) {
		this(DEFAULT_LABEL, viewer, oldHovered, newHovered);
	}

	/**
	 * Creates a new {@link ChangeHoverOperation} to set the given
	 * <i>newHovered</i> {@link IVisualPart} as the hovered part within the
	 * given {@link IViewer}. When undoing this operation, the given
	 * <i>oldHovered</i> {@link IVisualPart} will be set as the hovered part.
	 * The given label is used as the label of the operation.
	 *
	 * @param label
	 *            The operation's label.
	 * @param viewer
	 *            The {@link IViewer} for which the {@link HoverModel} is to be
	 *            changed.
	 * @param oldHovered
	 *            The {@link IVisualPart} which will be marked as hovered when
	 *            undoing this operation.
	 * @param newHovered
	 *            The {@link IVisualPart} which will be marked as hovered.
	 */
	public ChangeHoverOperation(String label, IViewer<VR> viewer,
			IVisualPart<VR, ? extends VR> oldHovered,
			IVisualPart<VR, ? extends VR> newHovered) {
		super(label);
		this.viewer = viewer;
		this.oldHovered = oldHovered;
		this.newHovered = newHovered;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		viewer.<HoverModel<VR>> getAdapter(HoverModel.class)
				.setHover(newHovered);
		return Status.OK_STATUS;
	}

	@Override
	public boolean isNoOp() {
		return oldHovered == newHovered;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		viewer.<HoverModel<VR>> getAdapter(HoverModel.class)
				.setHover(oldHovered);
		return Status.OK_STATUS;
	}

}
