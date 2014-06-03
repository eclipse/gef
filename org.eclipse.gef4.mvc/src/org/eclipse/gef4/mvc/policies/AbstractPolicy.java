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
 * Note: Parts of this class have been transferred from org.eclipse.gef.editpolicies.AbstractEditPolicy.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public abstract class AbstractPolicy<V> implements IPolicy<V> {

	private IVisualPart<V> host;

	@Override
	public void setAdaptable(IVisualPart<V> adaptable){
		setHost(adaptable);
	}
	
	public void setHost(IVisualPart<V> host) {
		this.host = host;
	}

	@Override
	public IVisualPart<V> getAdaptable() {
		return getHost();
	}
	
	public IVisualPart<V> getHost() {
		return host;
	}
	
	protected void executeOperation(IUndoableOperation operation) {
		IDomain<V> domain = getHost().getRoot().getViewer().getDomain();
		IOperationHistory operationHistory = domain.getOperationHistory();
		operation.addContext(domain.getUndoContext());
		try {
			operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}