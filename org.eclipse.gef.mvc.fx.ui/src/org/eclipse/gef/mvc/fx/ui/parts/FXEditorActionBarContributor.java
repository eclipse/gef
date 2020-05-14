/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.ui.parts;

import org.eclipse.gef.mvc.fx.ui.actions.DeleteAction;
import org.eclipse.gef.mvc.fx.ui.actions.SelectAllAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * @author anyssen
 */
public class FXEditorActionBarContributor extends EditorActionBarContributor {

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
		// XXX: We need to perform instance-of check here, even if
		// FXEditorActionBarContributor is bound to AbstractFXEditor alone.
		// This is because activeEditor may for instance also be of type
		// org.eclipse.ui.internal.ErrorEditorPart when the opened resource is
		// out of sync with the file system.
		if (activeEditor instanceof AbstractFXEditor) {
			registerUndoRedoActions(activeEditor);

			DeleteAction deleteAction = (DeleteAction) activeEditor
					.getAdapter(DeleteAction.class);
			if (deleteAction != null) {
				getActionBars().setGlobalActionHandler(
						ActionFactory.DELETE.getId(), deleteAction);
			}

			IAction selectAllAction = (IAction) activeEditor
					.getAdapter(SelectAllAction.class);
			if (selectAllAction != null) {
				getActionBars().setGlobalActionHandler(
						ActionFactory.SELECT_ALL.getId(), selectAllAction);
			}
		}
	}
}
