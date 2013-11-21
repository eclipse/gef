/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef4.mvc.eclipse.ui.properties;

import org.eclipse.gef4.mvc.commands.CommandStack;
import org.eclipse.gef4.mvc.commands.CommandStackEvent;
import org.eclipse.gef4.mvc.commands.ICommandStackEventListener;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * PropertySheetPage extension that allows to perform undo/redo of property
 * value changes also in case the editor is not active.
 * 
 * @author anyssen
 * @since 3.7
 */
public class UndoablePropertySheetPage extends PropertySheetPage {

	private final CommandStack commandStack;
	private final ICommandStackEventListener commandStackEventListener;

	/**
	 * Constructs a new {@link UndoablePropertySheetPage}.
	 * 
	 * @param commandStack
	 *            The {@link CommandStack} shared with the editor.
	 * @param undoAction
	 *            The global action handler to be registered for undo
	 *            operations.
	 * @param redoAction
	 *            The global action handler to be registered for redo
	 *            operations.
	 */
	public UndoablePropertySheetPage(final CommandStack commandStack) {
		this.commandStack = commandStack;
		this.commandStackEventListener = new ICommandStackEventListener() {

			public void stackChanged(CommandStackEvent event) {
				if (event.getDetail() == CommandStack.PRE_UNDO
						|| event.getDetail() == CommandStack.PRE_REDO) {
					// ensure the property sheet entry looses its current edit
					// state, otherwise it may revert the undo/redo operation
					// within valueChanged when the editor is activated again.
					refresh();
				}
			}
		};
		commandStack.addCommandStackEventListener(commandStackEventListener);
		setRootEntry(new UndoablePropertySheetEntry(commandStack));
	}

	/**
	 * Overwritten to unregister command stack listener.
	 * 
	 * @see org.eclipse.ui.views.properties.PropertySheetPage#dispose()
	 */
	public void dispose() {
		if (commandStack != null)
			commandStack
					.removeCommandStackEventListener(commandStackEventListener);
		super.dispose();
	}
}