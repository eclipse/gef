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
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class ChangeHoverOperation<VR> extends AbstractOperation {

	/**
	 * <pre>
	 * &quot;change-hover&quot;
	 * </pre>
	 * 
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "change-hover";

	private IViewer<VR> viewer;
	private IVisualPart<VR> oldHovered;
	private IVisualPart<VR> newHovered;

	public ChangeHoverOperation(IViewer<VR> viewer, IVisualPart<VR> newHovered) {
		this(DEFAULT_LABEL, viewer, viewer.getHoverModel().getHover(),
				newHovered);
	}

	public ChangeHoverOperation(IViewer<VR> viewer, IVisualPart<VR> oldHovered,
			IVisualPart<VR> newHovered) {
		this(DEFAULT_LABEL, viewer, oldHovered, newHovered);
	}

	public ChangeHoverOperation(String label, IViewer<VR> viewer,
			IVisualPart<VR> oldHovered, IVisualPart<VR> newHovered) {
		super(label);
		this.viewer = viewer;
		this.oldHovered = oldHovered;
		this.newHovered = newHovered;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		viewer.getHoverModel().setHover(newHovered);
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
		return Status.OK_STATUS;
	}

}
