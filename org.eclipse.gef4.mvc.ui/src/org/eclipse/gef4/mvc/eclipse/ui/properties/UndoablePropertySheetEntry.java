/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.eclipse.ui.properties;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.ICompositeOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheetEntry;

/**
 * <p>
 * UndoablePropertySheetEntry provides undo support for changes made to
 * IPropertySources by the PropertySheetViewer. Clients can construct a
 * {@link org.eclipse.ui.views.properties.PropertySheetPage} and use this class
 * as the root entry. All changes made to property sources displayed on that
 * page will be done using the provided command stack.
 * <p>
 * <b>NOTE:</b> If you intend to use an IPropertySourceProvider for a
 * PropertySheetPage whose root entry is an instance of of
 * UndoablePropertySheetEntry, you should set the IPropertySourceProvider on
 * that root entry, rather than the PropertySheetPage.
 */
public class UndoablePropertySheetEntry extends PropertySheetEntry {

	private IOperationHistory operationHistory;
	private IOperationHistoryListener operationHistoryListener;

	/**
	 * Constructs a non-root, i.e. child entry, which may obtain the command
	 * stack from its parent.
	 * 
	 * @since 3.1
	 */
	private UndoablePropertySheetEntry() {
	}

	/**
	 * Constructs the root entry using the given command stack.
	 * 
	 * @param operationHistory
	 *            the command stack to use
	 * @since 3.1
	 */
	public UndoablePropertySheetEntry(IOperationHistory operationHistory) {
		this.operationHistory = operationHistory;
		this.operationHistoryListener = new IOperationHistoryListener() {

			@Override
			public void historyNotification(OperationHistoryEvent event) {
				refreshFromRoot();
			}
		};
		this.operationHistory.addOperationHistoryListener(operationHistoryListener);
	}

	/**
	 * @see org.eclipse.ui.views.properties.PropertySheetEntry#createChildEntry()
	 */
	protected PropertySheetEntry createChildEntry() {
		return new UndoablePropertySheetEntry();
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySheetEntry#dispose()
	 */
	public void dispose() {
		if (operationHistory != null)
			operationHistory.removeOperationHistoryListener(operationHistoryListener);
		super.dispose();
	}

	/**
	 * Returns the {@link CommandStack} that is used by this entry. It is
	 * obtained from the parent in case the entry is not a root entry.
	 * 
	 * @return the {@link CommandStack} to be used.
	 * @since 3.7
	 */
	protected IOperationHistory getOperationHistory() {
		// only the root has, and is listening too, the command stack
		if (getParent() != null)
			return ((UndoablePropertySheetEntry) getParent()).getOperationHistory();
		return operationHistory;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySheetEntry#resetPropertyValue()
	 */
	public void resetPropertyValue() {
		ICompositeOperation cc = new ReverseUndoCompositeOperation("");
		if (getParent() == null)
			// root does not have a default value
			return;

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
				getOperationHistory().execute(cc, new NullProgressMonitor(), null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			refreshFromRoot();
		}
	}

	/**
	 * @see PropertySheetEntry#valueChanged(PropertySheetEntry)
	 */
	protected void valueChanged(PropertySheetEntry child) {
		valueChanged((UndoablePropertySheetEntry) child,
				new ForwardUndoCompositeOperation(""));
	}

	private void valueChanged(UndoablePropertySheetEntry child,
			ICompositeOperation command) {
		ICompositeOperation cc = new ReverseUndoCompositeOperation("");
		command.add(cc);

		SetPropertyValueOperation setCommand;
		for (int i = 0; i < getValues().length; i++) {
			setCommand = new SetPropertyValueOperation(child.getDisplayName(),
					getPropertySource(getValues()[i]), child.getDescriptor()
							.getId(), child.getValues()[i]);
			cc.add(setCommand);
		}

		// inform our parent
		if (getParent() != null)
			((UndoablePropertySheetEntry) getParent()).valueChanged(this,
					command);
		else {
			// I am the root entry
			try {
				operationHistory.execute(command, new NullProgressMonitor(), null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}
