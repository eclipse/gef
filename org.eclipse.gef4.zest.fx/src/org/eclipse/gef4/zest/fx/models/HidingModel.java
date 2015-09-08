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
package org.eclipse.gef4.zest.fx.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.graph.Node;

/**
 * The {@link HidingModel} manages a {@link Set} of currently hidden
 * {@link Node}s. It does also provide a method to retrieve the hidden neighbors
 * of a {@link Node}.
 *
 * @author mwienand
 *
 */
public class HidingModel implements IPropertyChangeNotifier {

	/**
	 * Property name that is used when firing property change notifications when
	 * the {@link Set} of hidden {@link Node}s changes.
	 */
	public static final String HIDDEN_PROPERTY = "hidden";

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final Set<Node> hidden = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Returns a {@link Set} containing all hidden neighbors of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the hidden neighbors are returned.
	 * @return A {@link Set} containing all hidden neighbors of the given
	 *         {@link Node}.
	 */
	public Set<org.eclipse.gef4.graph.Node> getHiddenNeighbors(org.eclipse.gef4.graph.Node node) {
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

	/**
	 * Returns a copy of the {@link Set} that contains all hidden {@link Node}s.
	 *
	 * @return A copy of the {@link Set} that contains all hidden {@link Node}s.
	 */
	public Set<Node> getHiddenNodes() {
		Set<Node> copy = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
		copy.addAll(hidden);
		return copy;
	}

	/**
	 * Adds the given {@link Node} to the {@link Set} of hidden {@link Node}s.
	 * Notifies all property change listeners about this change.
	 *
	 * @param node
	 *            The {@link Node} that is added to the {@link Set} of hidden
	 *            {@link Node}s.
	 */
	public void hide(Node node) {
		Set<Node> oldHidden = getHiddenNodes();
		hidden.add(node);
		pcs.firePropertyChange(HIDDEN_PROPERTY, oldHidden, getHiddenNodes());
	}

	/**
	 * Returns <code>true</code> if the given {@link Node} is currently
	 * contained within the {@link Set} of hidden {@link Node}s. Otherwise,
	 * returns <code>false</code>.
	 *
	 * @param node
	 *            The {@link Node} in question.
	 * @return <code>true</code> if the given {@link Node} is currently
	 *         contained within the {@link Set} of hidden {@link Node}s,
	 *         otherwise <code>false</code>.
	 */
	public boolean isHidden(Node node) {
		return hidden.contains(node);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Remove the given {@link Node} from the {@link Set} of hidden {@link Node}
	 * s. Notifies all property change listeners about this change.
	 *
	 * @param node
	 *            The {@link Node} that is removed from the {@link Set} of
	 *            hidden {@link Node}s.
	 */
	public void show(Node node) {
		Set<Node> oldHidden = getHiddenNodes();
		hidden.remove(node);
		pcs.firePropertyChange(HIDDEN_PROPERTY, oldHidden, getHiddenNodes());
	}

}
