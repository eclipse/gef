/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.ICompositeOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.mvc.fx.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.ReverseUndoCompositeOperation;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheetEntry;

/**
 * <p>
 * UndoablePropertySheetEntry provides undo support for changes made to
 * IPropertySources by the PropertySheetViewer. Clients can construct a
 * {@link org.eclipse.ui.views.properties.PropertySheetPage} and use this class
 * as the root entry. All changes made to property sources displayed on that
 * page will be done using the provided {@link IOperationHistory}.
 * <p>
 * <b>NOTE:</b> If you intend to use an IPropertySourceProvider for a
 * PropertySheetPage whose root entry is an instance of of
 * UndoablePropertySheetEntry, you should set the IPropertySourceProvider on
 * that root entry, rather than the PropertySheetPage.
 */
public class UndoablePropertySheetEntry extends PropertySheetEntry {

	private IWorkbenchPart workbenchPart;
	private IOperationHistory operationHistory;
	private IOperationHistoryListener operationHistoryListener;
	private IUndoContext undoContext;

	/**
	 * Constructs a non-root, i.e. child entry, which may obtain the
	 * {@link IOperationHistory} from its parent.
	 *
	 */
	private UndoablePropertySheetEntry() {
	}

	/**
	 * Constructs a new root entry.
	 *
	 * @param workbenchPart
	 *            The {@link IWorkbenchPart} to adapt for an
	 *            {@link IPropertySource}, in case no values are provided.
	 * @param operationHistory
	 *            The {@link IOperationHistory} to use.
	 * @param undoContext
	 *            The {@link IUndoContext} to use.
	 */
	public UndoablePropertySheetEntry(IWorkbenchPart workbenchPart,
			IOperationHistory operationHistory, IUndoContext undoContext) {
		this.workbenchPart = workbenchPart;
		this.operationHistory = operationHistory;
		this.undoContext = undoContext;
		this.operationHistoryListener = new IOperationHistoryListener() {

			@Override
			public void historyNotification(OperationHistoryEvent event) {
				refreshFromRoot();
			}
		};
		this.operationHistory
				.addOperationHistoryListener(operationHistoryListener);
	}

	/**
	 * @see org.eclipse.ui.views.properties.PropertySheetEntry#createChildEntry()
	 */
	@Override
	protected PropertySheetEntry createChildEntry() {
		return new UndoablePropertySheetEntry();
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySheetEntry#dispose()
	 */
	@Override
	public void dispose() {
		if (operationHistory != null) {
			operationHistory
					.removeOperationHistoryListener(operationHistoryListener);
		}
		super.dispose();
	}

	/**
	 * Returns the {@link IOperationHistory} that is used by this entry. It is
	 * obtained from the parent in case the entry is not a root entry.
	 *
	 * @return the {@link IOperationHistory} to be used.
	 */
	protected IOperationHistory getOperationHistory() {
		// only the root has, and is listening too, the IOperationHistory
		if (getParent() != null) {
			return ((UndoablePropertySheetEntry) getParent())
					.getOperationHistory();
		}
		return operationHistory;
	}

	@Override
	protected IPropertySource getPropertySource(Object object) {
		if (object instanceof IPropertySource) {
			return (IPropertySource) object;
		}
		return super.getPropertySource(object);
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySheetEntry#resetPropertyValue()
	 */
	@Override
	public void resetPropertyValue() {
		ICompositeOperation cc = new ReverseUndoCompositeOperation("");
		if (getParent() == null) {
			// root does not have a default value
			return;
		}

		// Use our parent's values to reset our values.
		boolean change = false;
		Object[] objects = getParent().getValues();
		for (int i = 0; i < objects.length; i++) {
			IPropertySource source = getPropertySource(objects[i]);
			if (source.isPropertySet(getDescriptor().getId())) {
				SetPropertyValueOperation restoreCmd = new SetPropertyValueOperation(
						getDescriptor().getDisplayName(), source,
						getDescriptor().getId(),
						SetPropertyValueOperation.DEFAULT_VALUE);
				cc.add(restoreCmd);
				change = true;
			}
		}
		if (change) {
			try {
				getOperationHistory().execute(cc, new NullProgressMonitor(),
						null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			refreshFromRoot();
		}
	}

	@Override
	public void setValues(Object[] objects) {
		if (objects == null || objects.length == 0) {
			if (workbenchPart != null) {
				IPropertySource source = (IPropertySource) workbenchPart
						.getAdapter(IPropertySource.class);
				if (source != null) {
					// wrap source itself; it will be unwrapped by super
					// implementation and then passed to
					// #getPropertySource(Object), which will return it, so the
					// editable value can be retrieved from it
					objects = new Object[] { source };
				}
			}
		}
		super.setValues(objects);
	}

	/**
	 * @see PropertySheetEntry#valueChanged(PropertySheetEntry)
	 */
	@Override
	protected void valueChanged(PropertySheetEntry child) {
		// the update of values into a command and pass that to our parent (or
		// execute it on the operation history, if we have no parent)
		ForwardUndoCompositeOperation compositeOperation = new ForwardUndoCompositeOperation(
				"Update child property values"); // TODO: externalize string
		for (int i = 0; i < getValues().length; i++) {
			SetPropertyValueOperation setOperation = new SetPropertyValueOperation(
					child.getDisplayName(), getPropertySource(getValues()[i]),
					((UndoablePropertySheetEntry) child).getDescriptor()
							.getId(),
					child.getValues()[i]);
			compositeOperation.add(setOperation);
		}
		valueChanged((UndoablePropertySheetEntry) child,
				compositeOperation.unwrap(true));
	}

	/**
	 * Update parent entry about change, being encapsulated into the given
	 * operation.
	 *
	 * @param child
	 *            The child entry that changed.
	 * @param operation
	 *            An operation encapsulating the change.
	 */
	protected void valueChanged(UndoablePropertySheetEntry child,
			ITransactionalOperation operation) {
		// inform our parent
		if (getParent() != null) {
			((UndoablePropertySheetEntry) getParent()).valueChanged(this,
					operation);
		} else {
			// I am the root entry
			try {
				operation.addContext(undoContext);
				operationHistory.execute(operation, new NullProgressMonitor(),
						null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}
