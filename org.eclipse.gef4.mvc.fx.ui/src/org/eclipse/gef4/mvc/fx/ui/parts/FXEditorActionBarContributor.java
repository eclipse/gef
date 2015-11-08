/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.parts;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * @author anyssen
 */
public class FXEditorActionBarContributor extends EditorActionBarContributor {

	private DeleteActionHandler deleteActionHandler = new DeleteActionHandler();

	@Override
	public void dispose() {
		super.dispose();
		deleteActionHandler.init(null);
	}

	@Override
	public void init(IActionBars actionBars) {
		super.init(actionBars);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				deleteActionHandler);
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
		final UndoRedoActionGroup undoRedoActionGroup = targetEditor
				.getAdapter(UndoRedoActionGroup.class);
		if (undoRedoActionGroup != null) {
			undoRedoActionGroup.fillActionBars(getActionBars());
		}
	}

	@Override
	public void setActiveEditor(final IEditorPart activeEditor) {
		registerUndoRedoActions(activeEditor);
		deleteActionHandler.init(((FXEditor) activeEditor).getViewer());
	}
}
