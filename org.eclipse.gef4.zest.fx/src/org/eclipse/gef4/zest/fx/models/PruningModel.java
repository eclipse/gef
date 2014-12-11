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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.graph.Node;

public class PruningModel implements IPropertyChangeNotifier {

	public static final String PRUNED_PROPERTY = "pruned";

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final Set<Node> pruned = new HashSet<Node>();

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public Set<Node> getPruned() {
		return new HashSet<Node>(pruned);
	}

	public Set<org.eclipse.gef4.graph.Node> getPrunedNeighbors(
			org.eclipse.gef4.graph.Node node) {
		Set<org.eclipse.gef4.graph.Node> neighbors = node.getLocalNeighbors();
		Set<org.eclipse.gef4.graph.Node> prunedNeighbors = new HashSet<org.eclipse.gef4.graph.Node>();
		for (org.eclipse.gef4.graph.Node n : neighbors) {
			if (isPruned(n)) {
				prunedNeighbors.add(n);
			}
		}
		return prunedNeighbors;
	}

	public boolean isPruned(Node node) {
		return pruned.contains(node);
	}

	public void prune(Node node) {
		Set<Node> oldPruned = getPruned();
		pruned.add(node);
		pcs.firePropertyChange("pruned", oldPruned, getPruned());
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void unprune(Node node) {
		Set<Node> oldPruned = getPruned();
		pruned.remove(node);
		pcs.firePropertyChange("pruned", oldPruned, getPruned());
	}

}
