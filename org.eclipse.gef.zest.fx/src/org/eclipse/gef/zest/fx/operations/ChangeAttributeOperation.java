/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     Alexander Nyßen (itemis AG)  - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.common.attributes.IAttributeStore;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;

/**
 * The {@link ChangeAttributeOperation} can be used to manipulate the value of
 * an attribute of an {@link IAttributeStore}.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class ChangeAttributeOperation extends AbstractOperation implements ITransactionalOperation {

	private IAttributeStore element;
	private String attributeKey;
	private Object newAttributeValue;
	private Object oldAttributeValue;

	/**
	 * Constructs a new {@link ChangeAttributeOperation} to manipulate the value
	 * of an attribute for the given {@link IAttributeStore}.
	 *
	 * @param attributeStore
	 *            The {@link IAttributeStore} that is manipulated by this
	 *            operation.
	 * @param attributeKey
	 *            The key that identifies the attribute to change.
	 * @param attributeValue
	 *            The new value of the attribute.
	 */
	public ChangeAttributeOperation(IAttributeStore attributeStore, String attributeKey, Object attributeValue) {
		super("Change attribute value");
		this.element = attributeStore;
		this.attributeKey = attributeKey;
		this.oldAttributeValue = attributeStore.attributesProperty().get(attributeKey);
		this.newAttributeValue = attributeValue;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Object currentValue = element.attributesProperty().get(attributeKey);
		if (newAttributeValue != currentValue
				&& (newAttributeValue == null || !newAttributeValue.equals(currentValue))) {
			element.attributesProperty().put(attributeKey, newAttributeValue);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return oldAttributeValue == newAttributeValue
				|| (oldAttributeValue != null && oldAttributeValue.equals(newAttributeValue));
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Object currentValue = element.attributesProperty().get(attributeKey);
		if (oldAttributeValue != currentValue
				&& (oldAttributeValue == null || !oldAttributeValue.equals(currentValue))) {
			element.attributesProperty().put(attributeKey, oldAttributeValue);
		}
		return Status.OK_STATUS;
	}

}
