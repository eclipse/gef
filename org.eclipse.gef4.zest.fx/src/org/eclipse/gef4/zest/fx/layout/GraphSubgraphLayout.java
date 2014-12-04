/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef4.common.properties.PropertyStoreSupport;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

public class GraphSubgraphLayout implements SubgraphLayout {

	private List<NodeLayout> nodes = new ArrayList<NodeLayout>();
	private PropertyStoreSupport ps = new PropertyStoreSupport(this);

	@Override
	public void addNodes(NodeLayout[] nodes) {
		if (nodes == null || nodes.length == 0) {
			return;
		}
		this.nodes.addAll(Arrays.asList(nodes));
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		ps.addPropertyChangeListener(listener);
	}

	@Override
	public int countNodes() {
		return nodes.size();
	}

	@Override
	public Object[] getItems() {
		List<Object> items = new ArrayList<Object>();
		for (NodeLayout node : nodes) {
			items.addAll(Arrays.asList(node.getItems()));
		}
		return items.toArray();
	}

	@Override
	public NodeLayout[] getNodes() {
		return nodes.toArray(new NodeLayout[] {});
	}

	@Override
	public EntityLayout[] getPredecessingEntities() {
		List<EntityLayout> predecessors = new ArrayList<EntityLayout>();
		for (NodeLayout node : nodes) {
			predecessors.addAll(Arrays.asList(node.getPredecessingEntities()));
		}
		return predecessors.toArray(new EntityLayout[] {});
	}

	@Override
	public Object getProperty(String name) {
		return ps.getProperty(name);
	}

	@Override
	public EntityLayout[] getSuccessingEntities() {
		List<EntityLayout> successors = new ArrayList<EntityLayout>();
		for (NodeLayout node : nodes) {
			successors.addAll(Arrays.asList(node.getSuccessingEntities()));
		}
		return successors.toArray(new EntityLayout[] {});
	}

	@Override
	public void removeNodes(NodeLayout[] nodes) {
		if (nodes == null || nodes.length == 0) {
			return;
		}
		this.nodes.removeAll(Arrays.asList(nodes));
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		ps.removePropertyChangeListener(listener);
	}

	@Override
	public void setProperty(String name, Object value) {
		ps.setProperty(name, value);
	}

}
