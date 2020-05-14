/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.zest.fx.parts.NodePart;

import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.collections.FXCollections;

/**
 * The {@link HidingModel} manages a {@link Set} of currently hidden
 * {@link org.eclipse.gef.graph.Node}s. The hidden neighbors of a
 * {@link org.eclipse.gef.graph.Node} can be identified using
 * {@link #getHiddenNeighbors(org.eclipse.gef.graph.Node)}.
 *
 * @author mwienand
 *
 */
public class HidingModel {

	/**
	 * Property name that is used when firing property change notifications when
	 * the {@link Set} of hidden {@link org.eclipse.gef.graph.Node}s changes.
	 */
	public static final String HIDDEN_PROPERTY = "hidden";

	private ReadOnlySetWrapper<org.eclipse.gef.graph.Node> hiddenProperty = new ReadOnlySetWrapper<>(this,
			HIDDEN_PROPERTY, FXCollections.observableSet(new HashSet<org.eclipse.gef.graph.Node>()));

	/**
	 * Returns a {@link Set} containing all {@link NodePart}s corresponding to
	 * the hidden neighbors of the content of the given {@link NodePart}.
	 *
	 * @param nodePart
	 *            The {@link NodePart} of which the hidden neighbors are
	 *            returned.
	 * @return A {@link Set} containing all hidden neighbors of the given
	 *         {@link NodePart}.
	 */
	public Set<NodePart> getHiddenNeighborParts(NodePart nodePart) {
		Set<Node> hiddenNeighbors = getHiddenNeighbors(nodePart.getContent());
		Set<NodePart> hiddenNeighborParts = Collections.newSetFromMap(new IdentityHashMap<NodePart, Boolean>());
		Map<Object, IContentPart<? extends javafx.scene.Node>> contentPartMap = nodePart.getRoot().getViewer()
				.getContentPartMap();
		for (org.eclipse.gef.graph.Node neighbor : hiddenNeighbors) {
			hiddenNeighborParts.add((NodePart) contentPartMap.get(neighbor));
		}
		return hiddenNeighborParts;
	}

	/**
	 * Returns a {@link Set} containing all hidden neighbors of the given
	 * {@link org.eclipse.gef.graph.Node}.
	 *
	 * @param node
	 *            The {@link org.eclipse.gef.graph.Node} of which the hidden
	 *            neighbors are returned.
	 * @return A {@link Set} containing all hidden neighbors of the given
	 *         {@link org.eclipse.gef.graph.Node}.
	 */
	public Set<org.eclipse.gef.graph.Node> getHiddenNeighbors(org.eclipse.gef.graph.Node node) {
		Set<org.eclipse.gef.graph.Node> neighbors = node.getNeighbors();
		Set<org.eclipse.gef.graph.Node> hiddenNeighbors = Collections
				.newSetFromMap(new IdentityHashMap<org.eclipse.gef.graph.Node, Boolean>());
		for (org.eclipse.gef.graph.Node neighbor : neighbors) {
			if (isHidden(neighbor)) {
				hiddenNeighbors.add(neighbor);
			}
		}
		return hiddenNeighbors;
	}

	/**
	 * Returns a copy of the {@link Set} that contains all hidden
	 * {@link org.eclipse.gef.graph.Node}s.
	 *
	 * @return A copy of the {@link Set} that contains all hidden
	 *         {@link org.eclipse.gef.graph.Node}s.
	 */
	public Set<org.eclipse.gef.graph.Node> getHiddenNodesUnmodifiable() {
		return FXCollections.unmodifiableObservableSet(hiddenProperty.get());
	}

	/**
	 * Returns <code>true</code> if at least one neighbor of the given
	 * {@link NodePart} is currently hidden. Otherwise returns
	 * <code>false</code>.
	 *
	 * @param nodePart
	 *            The {@link NodePart} that is tested for hidden neighbors.
	 * @return <code>true</code> if at least one neighbor of the given
	 *         {@link NodePart} is currently hidden, otherwise
	 *         <code>false</code>.
	 */
	public boolean hasHiddenNeighbors(NodePart nodePart) {
		return hasHiddenNeighbors(nodePart.getContent());
	}

	/**
	 * Returns <code>true</code> if at least one neighbor of the given
	 * {@link org.eclipse.gef.graph.Node} is currently hidden. Otherwise returns
	 * <code>false</code>.
	 *
	 * @param node
	 *            The {@link org.eclipse.gef.graph.Node} that is tested for
	 *            hidden neighbors.
	 * @return <code>true</code> if at least one neighbor of the given
	 *         {@link org.eclipse.gef.graph.Node} is currently hidden, otherwise
	 *         <code>false</code>.
	 */
	public boolean hasHiddenNeighbors(org.eclipse.gef.graph.Node node) {
		return getHiddenNeighbors(node).size() > 0;
	}

	/**
	 * Returns a read-only property containing the hidden nodes.
	 *
	 * @return A read-only property named {@link #HIDDEN_PROPERTY}.
	 */
	public ReadOnlySetProperty<org.eclipse.gef.graph.Node> hiddenProperty() {
		return hiddenProperty.getReadOnlyProperty();
	}

	/**
	 * Adds the content of the given {@link NodePart} to the {@link Set} of
	 * hidden {@link org.eclipse.gef.graph.Node}s. Notifies all property change
	 * listeners about this change.
	 *
	 * @param nodePart
	 *            The {@link NodePart} that is added to the {@link Set} of
	 *            hidden {@link org.eclipse.gef.graph.Node}s.
	 */
	public void hide(NodePart nodePart) {
		hide(nodePart.getContent());
	}

	/**
	 * Adds the given {@link org.eclipse.gef.graph.Node} to the {@link Set} of
	 * hidden {@link org.eclipse.gef.graph.Node}s. Notifies all property change
	 * listeners about this change.
	 *
	 * @param node
	 *            The {@link org.eclipse.gef.graph.Node} that is added to the
	 *            {@link Set} of hidden {@link org.eclipse.gef.graph.Node}s.
	 */
	public void hide(org.eclipse.gef.graph.Node node) {
		hiddenProperty.add(node);
	}

	/**
	 * Returns <code>true</code> if the given {@link NodePart} is currently
	 * contained within the {@link Set} of hidden
	 * {@link org.eclipse.gef.graph.Node}s. Otherwise, returns
	 * <code>false</code>.
	 *
	 * @param nodePart
	 *            The {@link NodePart} in question.
	 * @return <code>true</code> if the given {@link org.eclipse.gef.graph.Node}
	 *         is currently contained within the {@link Set} of hidden
	 *         {@link org.eclipse.gef.graph.Node}s, otherwise
	 *         <code>false</code>.
	 */
	public boolean isHidden(NodePart nodePart) {
		return isHidden(nodePart.getContent());
	}

	/**
	 * Returns <code>true</code> if the given {@link org.eclipse.gef.graph.Node}
	 * is currently contained within the {@link Set} of hidden
	 * {@link org.eclipse.gef.graph.Node}s. Otherwise, returns
	 * <code>false</code>.
	 *
	 * @param node
	 *            The {@link org.eclipse.gef.graph.Node} in question.
	 * @return <code>true</code> if the content of the given {@link NodePart} is
	 *         currently contained within the {@link Set} of hidden
	 *         {@link org.eclipse.gef.graph.Node}s, otherwise
	 *         <code>false</code>.
	 */
	public boolean isHidden(org.eclipse.gef.graph.Node node) {
		return hiddenProperty.contains(node);
	}

	/**
	 * Remove the content of the given {@link NodePart} from the {@link Set} of
	 * hidden {@link org.eclipse.gef.graph.Node} s. Notifies all property change
	 * listeners about this change.
	 *
	 * @param nodePart
	 *            The {@link NodePart} of which the content is removed from the
	 *            {@link Set} of hidden {@link org.eclipse.gef.graph.Node} s.
	 */
	public void show(NodePart nodePart) {
		show(nodePart.getContent());
	}

	/**
	 * Remove the given {@link org.eclipse.gef.graph.Node} from the {@link Set}
	 * of hidden {@link org.eclipse.gef.graph.Node} s. Notifies all property
	 * change listeners about this change.
	 *
	 * @param node
	 *            The {@link org.eclipse.gef.graph.Node} that is removed from
	 *            the {@link Set} of hidden {@link org.eclipse.gef.graph.Node}
	 *            s.
	 */
	public void show(org.eclipse.gef.graph.Node node) {
		hiddenProperty.remove(node);
	}

}
