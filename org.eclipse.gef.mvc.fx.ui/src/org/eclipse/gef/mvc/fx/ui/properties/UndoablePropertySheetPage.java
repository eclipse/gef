/*******************************************************************************
 * Copyright (c) 2011, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef.mvc.fx.ui.properties;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * PropertySheetPage extension that allows to perform undo/redo of property
 * value changes also in case the related {@link IWorkbenchPart} is not active.
 *
 * @author anyssen
 */
public class UndoablePropertySheetPage extends PropertySheetPage {

	private final IOperationHistory operationHistory;
	private IUndoContext undoContext;
	private IWorkbenchPart workbenchPart;

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
	 * @param workbenchPart
	 *            The {@link IWorkbenchPart} this
	 *            {@link UndoablePropertySheetPage} is related to. .
	 *
	 */
	@Inject
	public UndoablePropertySheetPage(@Assisted IWorkbenchPart workbenchPart,
			IOperationHistory operationHistory, IUndoContext undoContext) {
		this.workbenchPart = workbenchPart;
		this.operationHistory = operationHistory;
		this.undoContext = undoContext;
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
		setRootEntry(createRootEntry());
	}

	/**
	 * Creates the {@link IPropertySheetEntry} that is used as the root entry of
	 * this {@link UndoablePropertySheetPage}.
	 *
	 * @return A new {@link UndoablePropertySheetEntry}, bound to the
	 *         {@link IOperationHistory} and {@link IUndoContext} used by this
	 *         {@link UndoablePropertySheetPage}.
	 */
	protected UndoablePropertySheetEntry createRootEntry() {
		return new UndoablePropertySheetEntry(workbenchPart, operationHistory,
				undoContext);
	}

	/**
	 * Overwritten to unregister command stack listener.
	 *
	 * @see org.eclipse.ui.views.properties.PropertySheetPage#dispose()
	 */
	@Override
	public void dispose() {
		if (actionGroup != null) {
			actionGroup.dispose();
		}
		if (operationHistory != null) {
			operationHistory
					.removeOperationHistoryListener(operationHistoryListener);
		}
		super.dispose();
	}

	/**
	 * Returns the {@link IWorkbenchPart} this {@link UndoablePropertySheetPage}
	 * is related to.
	 *
	 * @return The {@link IWorkbenchPart} that was passed in upon creation.
	 */
	public IWorkbenchPart getWorkbenchPart() {
		return workbenchPart;
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
		super.setActionBars(actionBars);
		if (actionGroup == null) {
			actionGroup = new UndoRedoActionGroup(workbenchPart.getSite(),
					undoContext, true);
		}
		actionGroup.fillActionBars(actionBars);
	}
}