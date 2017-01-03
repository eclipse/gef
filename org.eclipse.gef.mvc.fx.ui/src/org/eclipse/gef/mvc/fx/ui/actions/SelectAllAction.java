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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.mvc.fx.operations.SelectOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionFactory;

import javafx.scene.Node;

/**
 * The {@link SelectAllAction}
 *
 * @author wienand
 *
 */
public class SelectAllAction extends Action implements IViewerAction {

	private IViewer viewer = null;

	/**
	 * Creates a new {@link SelectAllAction}.
	 */
	public SelectAllAction() {
		super("Select All");
		setId(ActionFactory.SELECT_ALL.getId());
		setEnabled(true);
	}

	/**
	 * Returns a list containing the {@link IContentPart}s that should be
	 * selected by this action handler at the point of time this method is
	 * called.
	 * <p>
	 * Per default, all active and selectable parts within the content-part-map
	 * of the current viewer are returned.
	 *
	 * @return A list containing the {@link IContentPart}s that should be
	 *         selected by this action handler at the point of time this method
	 *         is called.
	 */
	protected List<? extends IContentPart<? extends Node>> getSelectableContentParts() {
		if (viewer == null) {
			return Collections.emptyList();
		}
		ArrayList<IContentPart<? extends Node>> parts = new ArrayList<>(
				viewer.getContentPartMap().values());
		parts.removeIf(p -> !p.isSelectable());
		return parts;
	}

	/**
	 * Returns the {@link IViewer} for which this {@link IViewerAction} was
	 * {@link #init(IViewer) initialized}, or <code>null</code>.
	 *
	 * @return The {@link IViewer} or <code>null</code>.
	 */
	protected IViewer getViewer() {
		return viewer;
	}

	/**
	 * Binds this {@link SelectAllAction} to the given viewer.
	 *
	 * @param viewer
	 *            The {@link IViewer} to bind this {@link Action} to. May be
	 *            <code>null</code> to unbind this action.
	 */
	@Override
	public void init(IViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void runWithEvent(Event event) {
		// select all parts
		SelectOperation selectOperation = new SelectOperation(viewer,
				getSelectableContentParts());
		if (selectOperation != null) {
			try {
				viewer.getDomain().execute(selectOperation,
						new NullProgressMonitor());
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
		// cancel further event processing
		event.doit = false;
	}
}
