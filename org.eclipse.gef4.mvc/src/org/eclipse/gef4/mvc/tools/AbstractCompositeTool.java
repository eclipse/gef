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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.domain.IEditDomain;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public abstract class AbstractCompositeTool<V> extends AbstractTool<V>
		implements ICompositeTool<V> {

	private List<ITool<V>> subTools = null;

	@Override
	public void setDomain(IEditDomain<V> domain) {
		super.setDomain(domain);
		// propagate the domain to all sub-tools
		for (ITool<V> subTool : getSubTools()) {
			subTool.setDomain(domain);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ITool<V>> getSubTools() {
		return (List<ITool<V>>) (subTools == null ? Collections.emptyList()
				: Collections.unmodifiableList(subTools));
	}

	@Override
	public void add(int index, ITool<V> tool) {
		assertNotActive(); 
		initSubToolsList();
		subTools.add(index, tool);
	}

	@Override
	public void add(ITool<V> tool) {
		assertNotActive();
		initSubToolsList();
		subTools.add(tool);
	}

	@Override
	public void remove(int index) {
		assertNotActive();
		if (subTools != null) {
			subTools.remove(index);
			cleanSubToolsList();
		}
	}

	@Override
	public void remove(ITool<V> tool) {
		assertNotActive();
		if (subTools != null) {
			subTools.remove(tool);
			cleanSubToolsList();
		}
	}

	private void cleanSubToolsList() {
		if (subTools.isEmpty()) {
			subTools = null;
		}
	}

	private void initSubToolsList() {
		if (subTools == null) {
			subTools = new ArrayList<ITool<V>>(1);
		}
	}
	
	private void assertNotActive() {
		if (isActive()) {
			throw new IllegalArgumentException(
					"May not manipulate nested tools while being active.");
		}
	}

}
