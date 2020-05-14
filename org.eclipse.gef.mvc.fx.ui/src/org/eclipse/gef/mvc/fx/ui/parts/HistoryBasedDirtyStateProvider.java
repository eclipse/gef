/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.services.IDisposable;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

/**
 * A support class to handle the dirty state of a {@link WorkbenchPart} that
 * uses an {@link IOperationHistory} and an {@link IUndoContext}.
 *
 * @author anyssen
 */
public class HistoryBasedDirtyStateProvider
		implements IDirtyStateProvider, IDisposable {

	private ReadOnlyBooleanWrapper dirtyProperty = new ReadOnlyBooleanWrapper(
			false);
	private IOperationHistoryListener operationHistoryListener;
	private IOperationHistory operationHistory;
	private IUndoContext undoContext;
	private IUndoableOperation saveLocation = null;

	/**
	 * Creates a new {@link HistoryBasedDirtyStateProvider}.
	 *
	 * @param operationHistory
	 *            The {@link IOperationHistory} to use.
	 * @param undoContext
	 *            The {@link IUndoContext} to evaluate.
	 */
	public HistoryBasedDirtyStateProvider(IOperationHistory operationHistory,
			IUndoContext undoContext) {
		this.operationHistory = operationHistory;
		this.undoContext = undoContext;
		if (undoContext == null) {
			throw new IllegalArgumentException(
					"WorkbenchPart needs to be adaptable to IUndoContext");
		}

		operationHistoryListener = createOperationHistoryListener();
		operationHistory.addOperationHistoryListener(operationHistoryListener);
	}

	/**
	 * Returns the {@link IOperationHistoryListener} that is to be used to
	 * update the dirty state of this editor.
	 *
	 * @return The {@link IOperationHistoryListener} that is to be used to
	 *         update the dirty state of this editor.
	 */
	protected IOperationHistoryListener createOperationHistoryListener() {
		return new IOperationHistoryListener() {
			@Override
			public void historyNotification(final OperationHistoryEvent event) {
				// XXX: Only react to a subset of the history event
				// notifications. OPERATION_ADDED is issued when a transaction
				// is committed on the domain or an operation without a
				// transaction is executed on the domain; in the latter
				// case, we would also obtain a DONE notification (which we
				// ignore here). OPERATION_REMOVED is issued then flushing the
				// history
				IUndoableOperation[] undoHistory = event.getHistory()
						.getUndoHistory(getUndoContext());
				if (event.getEventType() == OperationHistoryEvent.UNDONE
						|| event.getEventType() == OperationHistoryEvent.REDONE
						|| event.getEventType() == OperationHistoryEvent.OPERATION_ADDED
						|| event.getEventType() == OperationHistoryEvent.OPERATION_REMOVED) {
					dirtyProperty.set(getMostRecentDirtyRelevantOperation(
							undoHistory) != saveLocation);
				}
			}
		};
	}

	@Override
	public ReadOnlyBooleanProperty dirtyProperty() {
		return dirtyProperty.getReadOnlyProperty();
	}

	@Override
	public void dispose() {
		// unregister operation history listener
		IOperationHistory operationHistory = getOperationHistory();
		if (operationHistory != null) {
			operationHistory
					.removeOperationHistoryListener(operationHistoryListener);
		}
		operationHistoryListener = null;
		saveLocation = null;
	}

	private IUndoableOperation getMostRecentDirtyRelevantOperation(
			IUndoableOperation[] undoHistory) {
		for (int i = undoHistory.length - 1; i >= 0; i--) {
			if (isContentsRelated(undoHistory[i])) {
				return undoHistory[i];
			}
		}
		return null;
	}

	private IOperationHistory getOperationHistory() {
		return operationHistory;
	}

	private IUndoContext getUndoContext() {
		return undoContext;
	}

	/**
	 * Tests whether the given {@link IUndoableOperation} is relevant for the
	 * dirty-state of the editor.
	 *
	 * @param operation
	 *            The {@link IUndoableOperation} to test.
	 * @return <code>true</code> if the operation encapsulates a dirty-state
	 *         relevant change, <code>false</code> otherwise.
	 */
	protected boolean isContentsRelated(IUndoableOperation operation) {
		return operation instanceof ITransactionalOperation
				&& !((ITransactionalOperation) operation).isNoOp()
				&& ((ITransactionalOperation) operation).isContentRelevant();
	}

	@Override
	public boolean isDirty() {
		return dirtyProperty.get();
	}

	@Override
	public void markNonDirty() {
		IUndoableOperation[] undoHistory = getOperationHistory()
				.getUndoHistory(getUndoContext());
		saveLocation = getMostRecentDirtyRelevantOperation(undoHistory);
		dirtyProperty.set(false);
	}
}
