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

public class HidingModel implements IPropertyChangeNotifier {

	public static final String HIDDEN_PROPERTY = "hidden";

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final Set<Node> hidden = new HashSet<Node>();

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public Set<org.eclipse.gef4.graph.Node> getHiddenNeighbors(
			org.eclipse.gef4.graph.Node node) {
		Set<org.eclipse.gef4.graph.Node> neighbors = node.getLocalNeighbors();
		Set<org.eclipse.gef4.graph.Node> prunedNeighbors = new HashSet<org.eclipse.gef4.graph.Node>();
		for (org.eclipse.gef4.graph.Node n : neighbors) {
			if (isHidden(n)) {
				prunedNeighbors.add(n);
			}
		}
		return prunedNeighbors;
	}

	public Set<Node> getHiddenNodes() {
		return new HashSet<Node>(hidden);
	}

	public void hide(Node node) {
		Set<Node> oldPruned = getHiddenNodes();
		hidden.add(node);
		pcs.firePropertyChange(HIDDEN_PROPERTY, oldPruned, getHiddenNodes());
	}

	public boolean isHidden(Node node) {
		return hidden.contains(node);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void show(Node node) {
		Set<Node> oldHidden = getHiddenNodes();
		hidden.remove(node);
		pcs.firePropertyChange(HIDDEN_PROPERTY, oldHidden, getHiddenNodes());
	}

}
