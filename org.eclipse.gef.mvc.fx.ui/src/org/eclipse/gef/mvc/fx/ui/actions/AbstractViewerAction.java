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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * The {@link AbstractViewerAction} provides an abstract implementation of
 * {@link IViewerAction}. It saves the {@link IViewer} for which the action is
 * {@link #init(IViewer) initialized}. Additionally, a mechanism
 * ({@link #createOperation(Event)}) is provided for creating an
 * {@link ITransactionalOperation} that is executed on the {@link IDomain} of
 * the {@link IViewer}.
 *
 * @author mwienand
 *
 */
public abstract class AbstractViewerAction extends Action
		implements IViewerAction {

	private IViewer viewer;
	private boolean isActive;
	private ChangeListener<Boolean> activationListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			if (newValue.booleanValue()) {
				setActive(true);
			} else {
				setActive(false);
			}
		}
	};

	/**
	 * Creates a new {@link IViewerAction}.
	 *
	 * @param text
	 *            Text for the action.
	 */
	protected AbstractViewerAction(String text) {
		super(text, IAction.AS_PUSH_BUTTON);
	}

	/**
	 * Creates a new {@link AbstractViewerAction} with the given text and style.
	 * Also sets the given {@link ImageDescriptor} for this action.
	 *
	 * @param text
	 *            Text for the action.
	 * @param style
	 *            Style for the action, see {@link IAction} for details.
	 * @param imageDescriptor
	 *            {@link ImageDescriptor} specifying the icon for the action.
	 */
	protected AbstractViewerAction(String text, int style,
			ImageDescriptor imageDescriptor) {
		super(text, style);
		setImageDescriptor(imageDescriptor);
	}

	/**
	 */
	protected void activate() {
		setEnabled(true);
	}

	/**
	 * Returns an {@link ITransactionalOperation} that performs the desired
	 * changes, or <code>null</code> if no changes should be performed. If
	 * <code>null</code> is returned, then the "doit" flag of the initiating
	 * {@link Event} is not altered. Otherwise, the "doit" flag is set to
	 * <code>false</code> so that no further event processing is done by SWT.
	 *
	 * @param event
	 *            The initiating {@link Event}.
	 *
	 * @return An {@link ITransactionalOperation} that performs the desired
	 *         changes.
	 */
	protected abstract ITransactionalOperation createOperation(Event event);

	/**
	 */
	protected void deactivate() {
		setEnabled(false);
	}

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
		if (this.viewer == viewer) {
			// nothing changed
			return;
		}

		// unregister listeners and clean up for the old viewer
		if (this.viewer != null) {
			this.viewer.activeProperty().removeListener(activationListener);
			setActive(false);
		}

		// save new viewer
		this.viewer = viewer;

		// register listeners and prepare for the new viewer
		if (this.viewer != null) {
			this.viewer.activeProperty().addListener(activationListener);
			setActive(this.viewer.isActive());
		}
	}

	/**
	 *
	 * @return a
	 */
	protected boolean isActive() {
		return isActive;
	}

	@Override
	public void run() {
		throw new UnsupportedOperationException(
				"Only runWithEvent(Event) supported.");
	}

	@Override
	public void runWithEvent(Event event) {
		if (!isActive()) {
			return;
		}

		ITransactionalOperation operation = createOperation(event);
		if (operation != null) {
			try {
				viewer.getDomain().execute(operation,
						new NullProgressMonitor());
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
			// cancel further event processing
			if (event != null) {
				event.doit = false;
			}
		}
	}

	/**
	 *
	 * @param isActive
	 *            a
	 */
	protected void setActive(boolean isActive) {
		if (this.isActive == isActive) {
			// nothing changed
			return;
		}
		if (isActive) {
			activate();
		} else {
			deactivate();
		}
		this.isActive = isActive;
	}
}
