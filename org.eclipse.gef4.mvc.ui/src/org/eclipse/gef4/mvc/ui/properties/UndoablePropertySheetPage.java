/*******************************************************************************
 * Copyright (c) 2011, 2014 IBM Corporation and others.
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
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * PropertySheetPage extension that allows to perform undo/redo of property
 * value changes also in case the editor is not active.
 * 
 * @author anyssen
 */
public class UndoablePropertySheetPage extends PropertySheetPage {

	private final IOperationHistory operationHistory;
	private final IOperationHistoryListener operationHistoryListener;
	private UndoRedoActionGroup actionGroup;

	/**
	 * Constructs a new {@link UndoablePropertySheetPage} using the provided
	 * {@link IOperationHistory}.
	 * 
	 * @param operationHistory
	 *            The {@link IOperationHistory} shared with the editor/view.
	 * @param undoContext
	 *            The {@link IUndoContext} shared with the editor/view.
	 * @param actionGroup
	 *            The {@link UndoRedoActionGroup} shared with the editor/view,
	 *            used to contribute UNDO/REDO actions. May be <code>null</code>
	 *            .
	 * 
	 */
	public UndoablePropertySheetPage(IOperationHistory operationHistory,
			IUndoContext undoContext, UndoRedoActionGroup actionGroup) {
		this.operationHistory = operationHistory;
		this.actionGroup = actionGroup;
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
		setRootEntry(
				new UndoablePropertySheetEntry(operationHistory, undoContext));
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
		super.setActionBars(actionBars);
		if (actionGroup != null) {
			actionGroup.fillActionBars(actionBars);
		}
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