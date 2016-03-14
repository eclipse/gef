/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.common.attributes.IAttributeStore;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

/**
 * The {@link ChangeAttributeOperation} can be used to manipulate the position
 * of a {@link Node}.
 *
 * @author mwienand
 *
 */
public class ChangeAttributeOperation extends AbstractOperation implements ITransactionalOperation {

	private IAttributeStore element;
	private String attributeKey;
	private Object newAttributeValue;
	private Object oldAttributeValue;

	/**
	 * Constructs a new {@link ChangeAttributeOperation} that can be used to
	 * manipulate the position of the given {@link Node}.
	 *
	 * @param element
	 *            The {@link Node} or {@link Edge} that is manipulated by this
	 *            operation.
	 * @param attributeKey
	 *            The key of the attribute to set for the given Node.
	 * @param attributeValue
	 *            The value of the attribute to set for the given Node.
	 */
	public ChangeAttributeOperation(IAttributeStore element, String attributeKey, Object attributeValue) {
		super("Change attribute value");
		this.element = element;
		this.attributeKey = attributeKey;
		this.oldAttributeValue = element.attributesProperty().get(attributeKey);
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
