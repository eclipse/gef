/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - refactoring for Bugzilla #480959
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import java.util.ArrayList;

import org.eclipse.gef.fx.swt.canvas.FXCanvasEx;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.gestures.TypeStrokeGesture;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.policies.DeletionPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionFactory;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * An {@link Action} to handle deletion of selection elements in an
 * {@link IViewer}
 * <P>
 * IMPORTANT: Usually, an action handler will only be executed in case the
 * widget that currently has focus does not already consume the triggering key
 * event. However, in case of an {@link FXCanvas} the triggering SWT key event
 * is never consumed, because it is forwarded to the embedded JavaFX
 * {@link Scene}, while a consumption of the mapping JavaFX event is not
 * propagated back.
 * <p>
 * Additionally, the JavaFX event handler (i.e. the {@link TypeStrokeGesture}, in case it
 * is registered at the {@link IDomain}), will be notified after the execution
 * of the action handler, because {@link FXCanvasEx} wraps the event forwarding
 * in a {@link Platform#runLater(Runnable)} call.
 *
 * @author anyssen
 *
 */
public class DeleteAction extends AbstractViewerAction {

	private ListChangeListener<IContentPart<? extends Node>> selectionListener = new ListChangeListener<IContentPart<? extends Node>>() {
		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<? extends Node>> c) {
			setEnabled(!c.getList().isEmpty());
		}
	};

	/**
	 * Constructs a new {@link DeleteAction}.
	 */
	public DeleteAction() {
		this("Delete", IAction.AS_PUSH_BUTTON, null);
		setId(ActionFactory.DELETE.getId());
	}

	/**
	 * Constructs a new {@link DeleteAction} with the given text and style. Also
	 * sets the given {@link ImageDescriptor} for this action.
	 *
	 * @param text
	 *            Text for the action.
	 * @param style
	 *            Style for the action, see {@link IAction} for details.
	 * @param imageDescriptor
	 *            {@link ImageDescriptor} specifying the icon for the action.
	 */
	protected DeleteAction(String text, int style,
			ImageDescriptor imageDescriptor) {
		super(text, style, imageDescriptor);
	}

	/**
	 * Computes an {@link ITransactionalOperation} that performs the desired
	 * changes, or <code>null</code> if no changes should be performed.
	 *
	 * @return An {@link ITransactionalOperation} that performs the desired
	 *         changes.
	 */
	@Override
	protected ITransactionalOperation createOperation(Event event) {
		// delete selected parts
		DeletionPolicy deletionPolicy = getViewer().getRootPart()
				.getAdapter(DeletionPolicy.class);
		if (deletionPolicy == null) {
			throw new IllegalStateException(
					"DeleteActionHandler requires a DeletionPolicy to be registered at the viewer's root part.");
		}
		deletionPolicy.init();
		for (IContentPart<? extends Node> s : new ArrayList<>(
				getSelectionModel().getSelectionUnmodifiable())) {
			deletionPolicy.delete(s);
		}
		ITransactionalOperation deleteOperation = deletionPolicy.commit();
		return deleteOperation;
	}

	/**
	 * Returns the {@link SelectionModel} for the currently bound
	 * {@link IViewer} or <code>null</code> if this action handler is either not
	 * bound or the viewer does not provide a {@link SelectionModel}.
	 *
	 * @return The {@link SelectionModel} or <code>null</code>.
	 */
	protected SelectionModel getSelectionModel() {
		return getViewer() == null ? null
				: getViewer().getAdapter(SelectionModel.class);
	}

	@Override
	protected void register() {
		SelectionModel newSelectionModel = getSelectionModel();
		if (newSelectionModel != null) {
			newSelectionModel.getSelectionUnmodifiable()
					.addListener(selectionListener);
		}
		setEnabled(newSelectionModel != null
				&& !newSelectionModel.getSelectionUnmodifiable().isEmpty());
	}

	@Override
	protected void unregister() {
		setEnabled(false);
		SelectionModel oldSelectionModel = getSelectionModel();
		if (oldSelectionModel != null) {
			oldSelectionModel.getSelectionUnmodifiable()
					.removeListener(selectionListener);
		}
	}
}