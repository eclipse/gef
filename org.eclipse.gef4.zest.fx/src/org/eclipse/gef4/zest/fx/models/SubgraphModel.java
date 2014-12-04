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
package org.eclipse.gef4.zest.fx.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

public class SubgraphModel implements IPropertyChangeNotifier {

	public static final String SUBGRAPHS_PROPERTY = "subgraphs";

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private Map<NodeContentPart, Set<NodeContentPart>> subgraphs = new HashMap<NodeContentPart, Set<NodeContentPart>>();

	public SubgraphModel() {
	}

	public void addNodesToSubgraph(NodeContentPart sourceNode,
			NodeContentPart... toAdd) {
		if (toAdd == null || toAdd.length == 0) {
			return;
		}

		Map.Entry<NodeContentPart, Set<NodeContentPart>> oldEntry;
		Set<NodeContentPart> contained;

		if (subgraphs.containsKey(sourceNode)) {
			contained = subgraphs.get(sourceNode);
			oldEntry = new AbstractMap.SimpleEntry<NodeContentPart, Set<NodeContentPart>>(
					sourceNode, new HashSet<NodeContentPart>(contained));
		} else {
			contained = new HashSet<NodeContentPart>();
			oldEntry = null;
		}

		List<NodeContentPart> addList = Arrays.asList(toAdd);
		contained.addAll(addList);
		subgraphs.put(sourceNode, contained);

		pcs.firePropertyChange(
				SUBGRAPHS_PROPERTY,
				oldEntry,
				new AbstractMap.SimpleEntry<NodeContentPart, Set<NodeContentPart>>(
						sourceNode, contained));
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public Set<NodeContentPart> getContainedNodes(NodeContentPart sourceNode) {
		Set<NodeContentPart> contained = subgraphs.get(sourceNode);
		if (contained == null || contained.isEmpty()) {
			return Collections.emptySet();
		}
		return new HashSet<NodeContentPart>(contained);
	}

	public Set<NodeContentPart> getSubgraphNodes(NodeContentPart pruned) {
		Set<NodeContentPart> subgraphNodes = new HashSet<NodeContentPart>();
		Set<Entry<NodeContentPart, Set<NodeContentPart>>> entries = subgraphs
				.entrySet();
		for (Entry<NodeContentPart, Set<NodeContentPart>> e : entries) {
			if (e.getValue() == pruned) {
				subgraphNodes.add(e.getKey());
			}
		}
		return subgraphNodes;
	}

	public boolean isPruned(NodeContentPart node) {
		for (NodeContentPart subgraphNode : subgraphs.keySet()) {
			Set<NodeContentPart> pruned = subgraphs.get(subgraphNode);
			if (pruned != null && pruned.contains(node)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSubgraphAssociated(NodeContentPart sourceNode) {
		return subgraphs.containsKey(sourceNode);
	}

	public void removeNodesFromSubgraph(NodeContentPart sourceNode,
			NodeContentPart... toRemove) {
		if (toRemove == null || toRemove.length == 0) {
			return;
		}
		if (!subgraphs.containsKey(sourceNode)) {
			throw new IllegalArgumentException(
					"No subgraph exists for the given source node.");
		}

		Set<NodeContentPart> contained = subgraphs.get(sourceNode);
		Map.Entry<NodeContentPart, Set<NodeContentPart>> oldEntry = new AbstractMap.SimpleEntry<NodeContentPart, Set<NodeContentPart>>(
				sourceNode, new HashSet<NodeContentPart>(contained));

		contained.removeAll(Arrays.asList(toRemove));

		if (contained.isEmpty()) {
			subgraphs.remove(sourceNode);
			pcs.firePropertyChange(SUBGRAPHS_PROPERTY, oldEntry, null);
		} else {
			pcs.firePropertyChange(
					SUBGRAPHS_PROPERTY,
					oldEntry,
					new AbstractMap.SimpleEntry<NodeContentPart, Set<NodeContentPart>>(
							sourceNode, contained));
		}
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

}
