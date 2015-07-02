/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * @author anyssen
 */
public class FXEditorActionBarContributor extends
		EditorActionBarContributor {

	@Override
	public void setActiveEditor(final IEditorPart targetEditor) {
		super.setActiveEditor(targetEditor);
		final UndoRedoActionGroup undoRedoActionGroup = targetEditor
				.getAdapter(UndoRedoActionGroup.class);
		if (undoRedoActionGroup != null) {
			undoRedoActionGroup.fillActionBars(getActionBars());
		}
	}
}
