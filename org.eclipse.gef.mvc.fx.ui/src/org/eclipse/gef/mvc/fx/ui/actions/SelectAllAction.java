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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.SelectOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionFactory;

import javafx.scene.Node;

/**
 * The {@link SelectAllAction} is an {@link AbstractViewerAction} that executes
 * a {@link SelectOperation} for selecting all
 * {@link #getSelectableContentParts() selectable parts}.
 *
 * @author mwienand
 *
 */
public class SelectAllAction extends AbstractViewerAction {

	/**
	 * Constructs a new {@link SelectAllAction}.
	 */
	public SelectAllAction() {
		this("Select All", IAction.AS_PUSH_BUTTON, null);
		setId(ActionFactory.SELECT_ALL.getId());
	}

	/**
	 * Constructs a new {@link SelectAllAction} with the given text and style.
	 * Also sets the given {@link ImageDescriptor} for this action.
	 *
	 * @param text
	 *            Text for the action.
	 * @param style
	 *            Style for the action, see {@link IAction} for details.
	 * @param imageDescriptor
	 *            {@link ImageDescriptor} specifying the icon for the action.
	 */
	protected SelectAllAction(String text, int style,
			ImageDescriptor imageDescriptor) {
		super(text, style, imageDescriptor);
	}

	@Override
	protected ITransactionalOperation createOperation(Event event) {
		return new SelectOperation(getViewer(), getSelectableContentParts());
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
		if (getViewer() == null) {
			return Collections.emptyList();
		}
		ArrayList<IContentPart<? extends Node>> parts = new ArrayList<>(
				getViewer().getContentPartMap().values());
		parts.removeIf(p -> !p.isSelectable());
		return parts;
	}
}
