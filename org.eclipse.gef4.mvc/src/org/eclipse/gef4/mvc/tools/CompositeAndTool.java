/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.domain.IDomain;

/**
 * 
 * @author mwienand
 * 
 * @param <VR>
 */
public class CompositeAndTool<VR> extends AbstractTool<VR> {

	private List<ITool<VR>> subTools = null;

	@Override
	public void setDomain(IDomain<VR> domain) {
		super.setDomain(domain);
		// propagate the domain to all sub-tools
		for (ITool<VR> subTool : getSubTools()) {
			subTool.setDomain(domain);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ITool<VR>> getSubTools() {
		return (List<ITool<VR>>) (subTools == null ? Collections.emptyList()
				: Collections.unmodifiableList(subTools));
	}

	public void add(int index, ITool<VR> tool) {
		assertNotActive();
		initSubToolsList();
		subTools.add(index, tool);
	}

	public void add(ITool<VR> tool) {
		assertNotActive();
		initSubToolsList();
		subTools.add(tool);
	}

	public void remove(int index) {
		assertNotActive();
		if (subTools != null) {
			subTools.remove(index);
			cleanSubToolsList();
		}
	}

	public void remove(ITool<VR> tool) {
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
			subTools = new ArrayList<ITool<VR>>(1);
		}
	}

	private void assertNotActive() {
		if (isActive()) {
			throw new IllegalArgumentException(
					"May not manipulate nested tools while being active.");
		}
	}

	@Override
	public void activate() {
		super.activate();
		for (ITool<VR> tool : getSubTools()) {
			tool.activate();
		}
	}

	@Override
	public void deactivate() {
		for (ITool<VR> tool : getSubTools()) {
			tool.deactivate();
		}
		super.deactivate();
	}

}
