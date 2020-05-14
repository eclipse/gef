/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
import org.eclipse.gef.mvc.fx.viewer.IViewer;

/**
 * The {@link ChangeContentsOperation} can be used to change the content objects
 * stored in the {@link IViewer#contentsProperty()}.
 *
 * @author anyssen
 *
 */
public class ChangeContentsOperation extends AbstractOperation
		implements ITransactionalOperation {

	/**
	 * <pre>
	 * &quot;Change Contents&quot;
	 * </pre>
	 *
	 * The default label for this operation (i.e. used if no label is
	 * specified).
	 */
	public static final String DEFAULT_LABEL = "Change Contents";

	private IViewer viewer;
	private List<? extends Object> newContents;
	private List<? extends Object> initialContents;

	/**
	 * Constructs a new {@link ChangeContentsOperation} that can be used to
	 * change the contents of the given {@link IViewer}.
	 * <p>
	 * The operation is initialized as a no-op, i.e. the initial viewer contents
	 * are also used as the final viewer contents.
	 * <p>
	 * The final contents can later be set using the
	 * {@link #setNewContents(List)} method.
	 *
	 * @param viewer
	 *            The {@link IViewer} of which the
	 *            {@link IViewer#contentsProperty()} is to be changed.
	 */
	public ChangeContentsOperation(IViewer viewer) {
		this(DEFAULT_LABEL, viewer, new ArrayList<>(viewer.getContents()));
	}

	/**
	 * Creates a new {@link ChangeContentsOperation} for changing the contents
	 * of the given {@link IViewer} to the specified list of objects.
	 *
	 * @param viewer
	 *            The {@link IViewer} of which the
	 *            {@link IViewer#contentsProperty()} is to be changed.
	 * @param contents
	 *            The new content objects to store in the
	 *            {@link IViewer#contentsProperty()}.
	 */
	public ChangeContentsOperation(IViewer viewer,
			List<? extends Object> contents) {
		this(DEFAULT_LABEL, viewer, contents);
	}

	/**
	 * Creates a new {@link ChangeContentsOperation} for changing the contents
	 * of the given {@link IViewer} to the specified list of objects. The given
	 * <i>label</i> is used as the label of the operation.
	 *
	 * @param label
	 *            The label of the operation.
	 * @param viewer
	 *            The {@link IViewer} of which the
	 *            {@link IViewer#contentsProperty()} is to be changed.
	 * @param contents
	 *            The new content objects to store in the
	 *            {@link IViewer#contentsProperty()}.
	 */
	// TODO: pass in content model instead of viewer
	public ChangeContentsOperation(String label, IViewer viewer,
			List<? extends Object> contents) {
		super(label);
		this.viewer = viewer;
		this.newContents = new ArrayList<>(contents);
		this.initialContents = new ArrayList<>(viewer.getContents());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.
	 * eclipse.core.runtime.IProgressMonitor,
	 * org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (!viewer.getContents().equals(newContents)) {
			viewer.getContents().setAll(newContents);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Returns the list containing the initial contents by reference.
	 *
	 * @return the list containing the initial contents by reference.
	 */
	protected List<? extends Object> getInitialContents() {
		return initialContents;
	}

	/**
	 * Returns the list containing the new contents by reference.
	 *
	 * @return the list containing the new contents by reference.
	 */
	protected List<? extends Object> getNewContents() {
		return newContents;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return initialContents == newContents || (initialContents != null
				&& initialContents.equals(newContents));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse.
	 * core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the final contents to the given list.
	 *
	 * @param newContents
	 *            The new final contents.
	 */
	public void setNewContents(List<? extends Object> newContents) {
		this.newContents = new ArrayList<>(newContents);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse.
	 * core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		viewer.getContents().setAll(initialContents);
		return Status.OK_STATUS;
	}

}
