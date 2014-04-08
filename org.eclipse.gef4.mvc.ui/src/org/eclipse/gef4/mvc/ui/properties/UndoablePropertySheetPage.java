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

package org.eclipse.gef4.mvc.ui.properties;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * PropertySheetPage extension that allows to perform undo/redo of property
 * value changes also in case the editor is not active.
 * 
 * @author anyssen
 * @since 3.7
 */
public class UndoablePropertySheetPage extends PropertySheetPage {

	private final IOperationHistory operationHistory;
	private final IOperationHistoryListener operationHistoryListener;

	/**
	 * Constructs a new {@link UndoablePropertySheetPage}.
	 * 
	 * @param operationHistory
	 *            The {@link IOperationHistory} shared with the editor.
	 * @param undoAction
	 *            The global action handler to be registered for undo
	 *            operations.
	 * @param redoAction
	 *            The global action handler to be registered for redo
	 *            operations.
	 */
	public UndoablePropertySheetPage(IOperationHistory operationHistory) {
		this.operationHistory = operationHistory;
		this.operationHistoryListener = new IOperationHistoryListener() {

			@Override
			public void historyNotification(OperationHistoryEvent event) {
				if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_REDO
						|| event.getEventType() == OperationHistoryEvent.ABOUT_TO_UNDO) {
					refresh();
				}

			}
		};
		operationHistory.addOperationHistoryListener(operationHistoryListener);
		setRootEntry(new UndoablePropertySheetEntry(operationHistory));
	}

	/**
	 * Overwritten to unregister command stack listener.
	 * 
	 * @see org.eclipse.ui.views.properties.PropertySheetPage#dispose()
	 */
	public void dispose() {
		if (operationHistory != null)
			operationHistory
					.removeOperationHistoryListener(operationHistoryListener);
		super.dispose();
	}
}