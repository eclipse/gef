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

import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef4.mvc.bindings.IAdaptable;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link AbstractDomain} should be subclassed.
 * 
 * @author anyssen
 * 
 * @param <VR>
 */
// TODO: it seems to be not nice that the domain is bound directly to the
// viewer.-> we should support multiple viewers (and provide the active one)
public interface IDomain<VR> extends IAdaptable {

	public Map<Class<? extends ITool<VR>>, ITool<VR>> getTools();

	/**
	 * Returns the {@link IOperationHistory} that is used by this domain.
	 * 
	 * @return The {@link IOperationHistory}.
	 */
	// replace by binding
	public IOperationHistory getOperationHistory();

	// replace by binding
	public IUndoContext getUndoContext();

	/**
	 * Sets the {@link IOperationHistory}, which can later be requested via
	 * {@link #getOperationHistory()}.
	 * 
	 * @param operationHistory
	 *            The new {@link IOperationHistory} to be used.
	 */
	// replace by binding (using adapters)?
	public void setOperationHistory(IOperationHistory operationHistory);

	// replace by binding
	public void setUndoContext(IUndoContext undoContext);
	
	public void addViewer(IViewer<VR> viewer);
	
	public void removeViewer(IViewer<VR> viewer);
	
	public Set<IViewer<VR>> getViewers();

}