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
 *******************************************************************************/
package org.eclipse.gef4.mvc.tools;

import org.eclipse.gef4.mvc.domain.IDomain;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public interface ICompositeTool<V> extends ITool<V> {

	/**
	 * Appends the given {@link ITool} to the list of sub-tools managed by this
	 * {@link ICompositeTool}. If this {@link ICompositeTool} is already
	 * registered on an {@link IDomain}, the added tool will be registered
	 * on the same {@link IDomain}.
	 * 
	 * @param tool
	 */
	public void add(ITool<V> tool);

	/**
	 * Inserts the given {@link ITool} into the list of sub-tools managed by
	 * this {@link ICompositeTool} at the given index. If this
	 * {@link ICompositeTool} is already registered on an {@link IDomain},
	 * the added tool will be registered on the same {@link IDomain}.
	 * 
	 * @param index
	 * @param tool
	 */
	public void add(int index, ITool<V> tool);

	/**
	 * Removes the given {@link ITool} from the list of sub-tools.
	 * 
	 * @param tool
	 */
	public void remove(ITool<V> tool);

	/**
	 * Removes the {@link ITool} at the given index from the list of sub-tools.
	 * 
	 * @param index
	 */
	public void remove(int index);

	/**
	 * Registers all sub-tools on the supplied {@link IDomain}. If the
	 * supplied {@link IDomain} is <code>null</code> all sub-tools are
	 * deactivated using the {@link #deactivate()} .
	 * 
	 * @param domain
	 */
	@Override
	public void setDomain(IDomain<V> domain);

}
