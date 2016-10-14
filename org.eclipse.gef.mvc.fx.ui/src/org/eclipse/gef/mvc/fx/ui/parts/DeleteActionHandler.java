/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.parts;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.fx.swt.canvas.FXCanvasEx;
import org.eclipse.gef.mvc.fx.domain.Domain;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.policies.DeletionPolicy;
import org.eclipse.gef.mvc.fx.tools.TypeTool;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.viewer.Viewer;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionFactory;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * An {@link Action} to handle deletion of selection elements in an
 * {@link Viewer}
 * <P>
 * IMPORTANT: Usually, an action handler will only be executed in case the
 * widget that currently has focus does not already consume the triggering key
 * event. However, in case of an {@link FXCanvas} the triggering SWT key event
 * is never consumed, because it is forwarded to the embedded JavaFX
 * {@link Scene}, while a consumption of the mapping JavaFX event is not
 * propagated back.
 * <p>
 * Additionally, the JavaFX event handler (i.e. the {@link TypeTool}, in case
 * its registered at the {@link Domain}) will be notified after the execution of
 * the action handler, because {@link FXCanvasEx} wraps the event forwarding in
 * an {@link Platform#runLater(Runnable)} call.
 *
 * @author anyssen
 *
 */
public class DeleteActionHandler extends Action {

	private IViewer viewer = null;
	private ListChangeListener<IContentPart<? extends Node>> selectionListener = new ListChangeListener<IContentPart<? extends Node>>() {
		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<? extends Node>> c) {
			updateEnabledState(getSelectionModel());
		}
	};

	/**
	 * Creates a new {@link DeleteActionHandler}.
	 */
	public DeleteActionHandler() {
		super("Delete");
		setId(ActionFactory.DELETE.getId());
		setEnabled(false);
	}

	private SelectionModel getSelectionModel() {
		if (viewer == null) {
			return null;
		}
		return viewer.getAdapter(SelectionModel.class);
	}

	/**
	 * Binds this {@link DeleteActionHandler} to the given viewer.
	 *
	 * @param viewer
	 *            The {@link IViewer} to bind this {@link Action} to. May be
	 *            <code>null</code> to unbind this action.
	 */
	public void init(IViewer viewer) {
		SelectionModel oldSelectionModel = getSelectionModel();
		SelectionModel newSelectionModel = null;
		this.viewer = viewer;
		if (viewer != null) {
			newSelectionModel = viewer.getAdapter(SelectionModel.class);
		}
		// register listeners to update enabled state
		if (oldSelectionModel != null
				&& oldSelectionModel != newSelectionModel) {
			oldSelectionModel.getSelectionUnmodifiable()
					.removeListener(selectionListener);
		}
		if (newSelectionModel != null
				&& oldSelectionModel != newSelectionModel) {
			newSelectionModel.getSelectionUnmodifiable()
					.addListener(selectionListener);
		}
		updateEnabledState(newSelectionModel);
	}

	@Override
	public void runWithEvent(Event event) {
		// delete selected parts
		DeletionPolicy deletionPolicy = viewer.getRootPart()
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
		if (deleteOperation != null) {
			try {
				viewer.getDomain().execute(deleteOperation,
						new NullProgressMonitor());
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
		// mark as been handled
		event.doit = false;
	}

	/**
	 * Updates the enabled state of this {@link Action} dependent on the
	 * selection state of the {@link SelectionModel}.
	 *
	 * @param selectionModel
	 *            The {@link SelectionModel} to obtain the selection from.
	 */
	protected void updateEnabledState(SelectionModel selectionModel) {
		if (selectionModel == null) {
			setEnabled(false);
		} else {
			setEnabled(!selectionModel.getSelectionUnmodifiable().isEmpty());
		}
	}
}