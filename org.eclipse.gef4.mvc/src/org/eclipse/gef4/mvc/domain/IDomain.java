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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditDomain.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.domain;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewerBound;

/**
 * 
 * @author anyssen
 * 
 * @param <V>
 */
// TODO: it seems to be not nice that the domain is bound directly to the viewer.
public interface IDomain<V> extends IViewerBound<V> {

	/**
	 * Returns the {@link IOperationHistory} that is used by this domain.
	 * 
	 * @return The {@link IOperationHistory}.
	 */
	public abstract IOperationHistory getOperationHistory();

	public abstract <P extends Object> P getProperty(Class<P> key);

	/**
	 * Returns the active Tool
	 * 
	 * @return the active Tool
	 */
	public abstract ITool<V> peekTool();

	public abstract ITool<V> popTool();

	/**
	 * Sets the active Tool for this EditDomain. If a current Tool is active, it
	 * is deactivated. The new Tool is told its EditDomain, and is activated.
	 * 
	 * @param tool
	 *            the Tool
	 */
	public abstract void pushTool(ITool<V> tool);

	/**
	 * Sets the {@link IOperationHistory}, which can later be requested via
	 * {@link #getOperationHistory()}.
	 * 
	 * @param operationHistory
	 *            The new {@link IOperationHistory} to be used.
	 */
	public abstract void setOperationHistory(IOperationHistory operationHistory);

	public abstract <P extends Object> void setProperty(Class<P> key, P property);

}