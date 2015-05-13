/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.layout;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef4.common.properties.PropertyStoreSupport;
import org.eclipse.gef4.layout.IEntityLayout;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.ISubgraphLayout;

public class GraphSubgraphLayout implements ISubgraphLayout {

	private List<INodeLayout> nodes = new ArrayList<INodeLayout>();
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private PropertyStoreSupport pss = new PropertyStoreSupport(this, pcs);

	@Override
	public void addNodes(INodeLayout[] nodes) {
		if (nodes == null || nodes.length == 0) {
			return;
		}
		this.nodes.addAll(Arrays.asList(nodes));
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public int countNodes() {
		return nodes.size();
	}

	@Override
	public Object[] getItems() {
		List<Object> items = new ArrayList<Object>();
		for (INodeLayout node : nodes) {
			items.addAll(Arrays.asList(node.getItems()));
		}
		return items.toArray();
	}

	@Override
	public INodeLayout[] getNodes() {
		return nodes.toArray(new INodeLayout[] {});
	}

	@Override
	public IEntityLayout[] getPredecessingEntities() {
		List<IEntityLayout> predecessors = new ArrayList<IEntityLayout>();
		for (INodeLayout node : nodes) {
			predecessors.addAll(Arrays.asList(node.getPredecessingEntities()));
		}
		return predecessors.toArray(new IEntityLayout[] {});
	}

	@Override
	public Object getProperty(String name) {
		return pss.getProperty(name);
	}

	@Override
	public IEntityLayout[] getSuccessingEntities() {
		List<IEntityLayout> successors = new ArrayList<IEntityLayout>();
		for (INodeLayout node : nodes) {
			successors.addAll(Arrays.asList(node.getSuccessingEntities()));
		}
		return successors.toArray(new IEntityLayout[] {});
	}

	@Override
	public void removeNodes(INodeLayout[] nodes) {
		if (nodes == null || nodes.length == 0) {
			return;
		}
		this.nodes.removeAll(Arrays.asList(nodes));
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void setProperty(String name, Object value) {
		pss.setProperty(name, value);
	}

}
