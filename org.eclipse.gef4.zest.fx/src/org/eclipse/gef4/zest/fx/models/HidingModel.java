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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.graph.Node;

public class HidingModel implements IPropertyChangeNotifier {

	public static final String HIDDEN_PROPERTY = "hidden";

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final Set<Node> hidden = Collections
			.newSetFromMap(new IdentityHashMap<Node, Boolean>());

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public Set<org.eclipse.gef4.graph.Node> getHiddenNeighbors(
			org.eclipse.gef4.graph.Node node) {
		Set<org.eclipse.gef4.graph.Node> neighbors = node.getLocalNeighbors();
		Set<org.eclipse.gef4.graph.Node> hiddenNeighbors = Collections
				.newSetFromMap(new IdentityHashMap<org.eclipse.gef4.graph.Node, Boolean>());
		for (org.eclipse.gef4.graph.Node n : neighbors) {
			if (isHidden(n)) {
				hiddenNeighbors.add(n);
			}
		}
		return hiddenNeighbors;
	}

	public Set<Node> getHiddenNodes() {
		Set<Node> copy = Collections
				.newSetFromMap(new IdentityHashMap<Node, Boolean>());
		copy.addAll(hidden);
		return copy;
	}

	public void hide(Node node) {
		Set<Node> oldHidden = getHiddenNodes();
		hidden.add(node);
		pcs.firePropertyChange(HIDDEN_PROPERTY, oldHidden, getHiddenNodes());
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
