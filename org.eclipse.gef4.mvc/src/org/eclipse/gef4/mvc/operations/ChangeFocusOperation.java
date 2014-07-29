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
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class ChangeFocusOperation<VR> extends AbstractOperation {

	/**
	 * <pre>
	 * &quot;change-focus&quot;
	 * </pre>
	 * 
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "change-focus";

	private IViewer<VR> viewer;
	private IContentPart<VR> oldFocused;
	private IContentPart<VR> newFocused;

	public ChangeFocusOperation(IViewer<VR> viewer, IContentPart<VR> newFocused) {
		this(DEFAULT_LABEL, viewer, viewer.getFocusModel().getFocused(),
				newFocused);
	}

	public ChangeFocusOperation(IViewer<VR> viewer,
			IContentPart<VR> oldFocused, IContentPart<VR> newFocused) {
		this(DEFAULT_LABEL, viewer, oldFocused, newFocused);
	}

	public ChangeFocusOperation(String label, IViewer<VR> viewer,
			IContentPart<VR> oldFocused, IContentPart<VR> newFocused) {
		super(label);
		this.viewer = viewer;
		this.oldFocused = oldFocused;
		this.newFocused = newFocused;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		viewer.getFocusModel().setFocused(newFocused);
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
		viewer.getFocusModel().setFocused(oldFocused);
		return Status.OK_STATUS;
	}

}
