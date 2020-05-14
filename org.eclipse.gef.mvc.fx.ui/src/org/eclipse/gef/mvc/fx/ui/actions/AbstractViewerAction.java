/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
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
package org.eclipse.gef.mvc.fx.ui.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * The {@link AbstractViewerAction} provides an extension to {@link Action} that
 * is bound to an {@link IViewer}. Additionally, a mechanism
 * ({@link #createOperation(Event)}) is provided for creating an
 * {@link ITransactionalOperation} that is executed on the {@link IDomain} of
 * the {@link IViewer}.
 *
 * @author mwienand
 *
 */
public abstract class AbstractViewerAction extends Action
		implements IAdaptable.Bound<IViewer> {

	private ReadOnlyObjectWrapper<IViewer> viewerProperty = new ReadOnlyObjectWrapper<>();

	private ChangeListener<Boolean> activationListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			if (newValue.booleanValue()) {
				register();
			} else {
				unregister();
			}
		}
	};

	// private ObjectProperty<IViewer> viewerProperty;
	// private BooleanProperty checked = new BooleanProperty(false);
	// private BooleanProperty enabled = new BooleanProperty(false);
	// @Override public BooleanProperty enabledProperty() { return enabled; }
	// @Override public BooleanProperty checkedProperty() { return checked; }

	/**
	 * Creates a new {@link AbstractViewerAction}.
	 *
	 * @param text
	 *            Text for the action.
	 */
	protected AbstractViewerAction(String text) {
		this(text, IAction.AS_PUSH_BUTTON, null);
	}

	/**
	 * Constructs a new {@link AbstractViewerAction} with the given text and
	 * style. Also sets the given {@link ImageDescriptor} for this action.
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
		setEnabled(false);
	}

	@Override
	public ReadOnlyObjectProperty<IViewer> adaptableProperty() {
		return viewerProperty;
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

	@Override
	public IViewer getAdaptable() {
		return viewerProperty.get();
	}

	/**
	 * Returns the {@link IViewer} for which this {@link AbstractViewerAction}
	 * is bound.
	 *
	 * @return The {@link IViewer} to which this {@link AbstractViewerAction} is
	 *         bound.
	 */
	protected IViewer getViewer() {
		return getAdaptable();
	}

	/**
	 * This method is called when this action obtains an {@link IViewer} which
	 * is {@link IViewer#activeProperty() active} or when a previously obtained
	 * viewer is activated. Per default, this method {@link #setEnabled(boolean)
	 * enables} this action.
	 */
	protected void register() {
		setEnabled(true);
	}

	@Override
	public void run() {
		throw new UnsupportedOperationException(
				"Only runWithEvent(Event) supported.");
	}

	@Override
	public void runWithEvent(Event event) {
		if (!isEnabled()) {
			return;
		}

		ITransactionalOperation operation = createOperation(event);
		if (operation != null) {
			try {
				getViewer().getDomain().execute(operation,
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

	@Override
	public void setAdaptable(IViewer viewer) {
		if (this.viewerProperty.get() == viewer) {
			// nothing changed
			return;
		}

		// unregister listeners and clean up for the old viewer
		if (this.viewerProperty.get() != null) {
			this.viewerProperty.get().activeProperty()
					.removeListener(activationListener);
			if (this.viewerProperty.get().isActive()) {
				unregister();
			}
		}

		// save new viewer
		this.viewerProperty.set(viewer);

		// register listeners and prepare for the new viewer
		if (this.viewerProperty.get() != null) {
			this.viewerProperty.get().activeProperty()
					.addListener(activationListener);
			if (this.viewerProperty.get().isActive()) {
				register();
			}
		}
	}

	/**
	 * This method is called when this action loses its {@link IViewer} or when
	 * its {@link #getViewer() viewer} is deactivated. Per default, this method
	 * {@link #setEnabled(boolean) disables} this action.
	 */
	protected void unregister() {
		setEnabled(false);
	}
}
