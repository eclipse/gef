/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;

/**
 * The {@link AbstractViewerAction} provides an abstract implementation of
 * {@link IViewerAction}. It saves the {@link IViewer} for which the action is
 * {@link #init(IViewer) initialized}. Additionally, a mechanism
 * ({@link #createOperation()}) is provided for creating an
 * {@link ITransactionalOperation} that is executed on the {@link IDomain} of
 * the {@link IViewer}.
 *
 * @author mwienand
 *
 */
public abstract class AbstractViewerAction extends Action
		implements IViewerAction {

	private IViewer viewer;

	/**
	 * Creates a new {@link IViewerAction}.
	 *
	 * @param text
	 *            Text for the action.
	 */
	protected AbstractViewerAction(String text) {
		super(text);
	}

	/**
	 * Returns an {@link ITransactionalOperation} that performs the desired
	 * changes, or <code>null</code> if no changes should be performed. If
	 * <code>null</code> is returned, then the "doit" flag of the initiating
	 * {@link Event} is not altered. Otherwise, the "doit" flag is set to
	 * <code>false</code> so that no further event processing is done by SWT.
	 *
	 * @return An {@link ITransactionalOperation} that performs the desired
	 *         changes.
	 */
	protected abstract ITransactionalOperation createOperation();

	/**
	 * Returns the {@link IViewer} for which this {@link IViewerAction} was
	 * {@link #init(IViewer) initialized}.
	 *
	 * @return The {@link IViewer} for which this {@link IViewerAction} was
	 *         {@link #init(IViewer) initialized}.
	 */
	protected IViewer getViewer() {
		return viewer;
	}

	@Override
	public void init(IViewer viewer) {
		// save viewer
		this.viewer = viewer;
	}

	@Override
	public void runWithEvent(Event event) {
		ITransactionalOperation operation = createOperation();
		if (operation != null) {
			try {
				viewer.getDomain().execute(operation,
						new NullProgressMonitor());
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
			// cancel further event processing
			event.doit = false;
		}
	}
}
