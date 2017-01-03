/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
import java.util.List;

import org.eclipse.gef.mvc.fx.ui.actions.DeleteAction;
import org.eclipse.gef.mvc.fx.ui.actions.IViewerAction;
import org.eclipse.gef.mvc.fx.ui.actions.SelectAllAction;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.services.IDisposable;

/**
 * @author anyssen
 */
public class FXEditorActionBarContributor extends EditorActionBarContributor {

	private List<IAction> globalActions = new ArrayList<>();

	/**
	 * Returns a list containing the global {@link IAction}s that are to be
	 * registered by this {@link FXEditorActionBarContributor} as global action
	 * handlers for their respective IDs.
	 *
	 * @return A list containing the global {@link IAction}s.
	 */
	protected List<? extends IAction> createGlobalActionHandlers() {
		List<IAction> actions = new ArrayList<>();
		actions.add(new DeleteAction());
		actions.add(new SelectAllAction());
		return actions;
	}

	@Override
	public void dispose() {
		for (IAction action : globalActions) {
			if (action instanceof IDisposable) {
				IDisposable iDisposable = (IDisposable) action;
				iDisposable.dispose();
			}
		}
		globalActions.clear();
		super.dispose();
	}

	@Override
	public void init(IActionBars actionBars) {
		super.init(actionBars);
		globalActions.addAll(createGlobalActionHandlers());
		for (IAction action : globalActions) {
			actionBars.setGlobalActionHandler(action.getId(), action);
		}
	}

	/**
	 * Registers undo and redo action handlers for the given target editor. The
	 * target editor is adapted for an {@link UndoRedoActionGroup}, which is
	 * used to fill the action bars.
	 *
	 * @param targetEditor
	 *            The editor to register undo and redo action handlers for.
	 */
	protected void registerUndoRedoActions(final IEditorPart targetEditor) {
		// XXX: IAdaptable.getAdapter() has been 'generified' with Mars.
		// However, to maintain backwards compatibility with Luna, we need to
		// explicitly cast here.
		final UndoRedoActionGroup undoRedoActionGroup = (UndoRedoActionGroup) targetEditor
				.getAdapter(UndoRedoActionGroup.class);
		if (undoRedoActionGroup != null) {
			undoRedoActionGroup.fillActionBars(getActionBars());
		}
	}

	@Override
	public void setActiveEditor(final IEditorPart activeEditor) {
		registerUndoRedoActions(activeEditor);
		// XXX: We need to perform instance-of check here, even if
		// FXEditorActionBarContributor is bound to AbstractFXEditor alone.
		// This is because activeEditor may for instance also be of type
		// org.eclipse.ui.internal.ErrorEditorPart when the opened resource is
		// out of sync with the file system.
		if (activeEditor instanceof AbstractFXEditor) {
			IViewer viewer = ((AbstractFXEditor) activeEditor)
					.getContentViewer();
			for (IAction action : globalActions) {
				if (action instanceof IViewerAction) {
					((IViewerAction) action).init(viewer);
				}
			}
		}
	}
}
