/*******************************************************************************
 * Copyright (c) 2009-2010 Mateusz Matela and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.layout.interfaces;

import org.eclipse.gef4.common.properties.IPropertyStore;
import org.eclipse.gef4.layout.LayoutAlgorithm;

/**
 * Objects implementing LayoutContext interface are used for exchanging of
 * information between layout algorithms and graphical objects displaying
 * graphs.
 */
public interface LayoutContext extends IPropertyStore {

	public static final String STATIC_LAYOUT_ALGORITHM_PROPERTY = "staticLayoutAlgorithm";
	public static final String DYNAMIC_LAYOUT_ALGORITHM_PROPERTY = "dynamicLayoutAlgorithm";

	/**
	 * Applies the background layout algorithm of this LayoutContext. The clean
	 * flag is passed-in to the background layout algorithm to indicate whether
	 * the context changed significantly since the last layout pass.
	 */
	public void applyDynamicLayout(boolean clean);

	/**
	 * Applies the static layout algorithm of this LayoutContext. The clean flag
	 * is passed-in to the static layout algorithm to indicate whether the
	 * context changed significantly since the last layout pass.
	 */
	public void applyStaticLayout(boolean clean);

	/**
	 * Returns all the nodes that should be laid out. Replacing elements in the
	 * returned array does not affect this context.
	 * 
	 * @return array of nodes to lay out
	 */
	public NodeLayout[] getNodes();

	/**
	 * Returns all the connections between nodes that should be laid out.
	 * Replacing elements in the returned array does not affect this context.
	 * 
	 * @return array of connections between nodes
	 */
	public ConnectionLayout[] getConnections();

	/**
	 * Returns all entities that are currently placed on the graph, that is
	 * subgraphs and unpruned nodes. Replacing elements in the returned array
	 * does not affect this context.
	 * 
	 * @return array of entities to layout
	 */
	public EntityLayout[] getEntities();

	/**
	 * Returns all the connections between given source and target entities. If
	 * given entity is a subgraph, connections adjacent to each of its nodes
	 * will be included in the result. All the undirected nodes connecting the
	 * two nodes will be also included in the result. Replacing elements in the
	 * returned array does not affect this context.
	 * 
	 * @param layoutEntity1
	 * @param layoutEntity2
	 * @return the connections between layoutEntitity1 and layoutEntity2
	 */
	public ConnectionLayout[] getConnections(EntityLayout layoutEntity1,
			EntityLayout layoutEntity2);

	/**
	 * Returns all the subgraphs this context's nodes were pruned to. Replacing
	 * elements in the returned array does not affect this context.
	 * 
	 * @return array of subgraphs (may be empty)
	 */
	public SubgraphLayout[] getSubgraphs();

	/**
	 * Creates a subgraph containing given nodes and adds it to this context. If
	 * given nodes already belong to another subgraphs, they are removed from
	 * them prior to adding to the new subgraph.
	 * 
	 * @param nodes
	 *            nodes to add to the new subgraph
	 */
	public SubgraphLayout createSubgraph(NodeLayout[] nodes);

	/**
	 * Sets the dynamic layout algorithm for this context. This algorithm will
	 * be used to relayout graph items using
	 * {@link LayoutAlgorithm#applyLayout(boolean)} after every event that is
	 * not intercepted by any listener when currently changes are not being
	 * flushed and background layout is enabled. The clean flag for the
	 * background layout algorithm can be set to <code>false</code> by the
	 * context when reacting to events.
	 * 
	 * @param algorithm
	 */
	public void setDynamicLayoutAlgorithm(LayoutAlgorithm algorithm);

	/**
	 * Sets the static layout algorithm for this context. The static algorithm
	 * has to be manually invoked, for example, after significant changes to the
	 * context.
	 * 
	 * @param algorithm
	 */
	public void setStaticLayoutAlgorithm(LayoutAlgorithm algorithm);

	/**
	 * Returns the static layout algorithm used to layout a newly initialized
	 * graph or after heavy changes to it.
	 * 
	 * @return the static layout algorithm
	 */
	public LayoutAlgorithm getStaticLayoutAlgorithm();

	/**
	 * @return the background algorithm of this context (see
	 *         {@link #setDynamicLayoutAlgorithm(LayoutAlgorithm)} for details)
	 */
	public LayoutAlgorithm getDynamicLayoutAlgorithm();

	/**
	 * Adds a listener to the context that will be notified about changes in
	 * this context's layout, that is movement and resizing of nodes /
	 * subgraphs. The notifications will not include changes made with API
	 * included in layout related interfaces, so that layout algorithms won't be
	 * notified about changes they invoke. Only internal changes of the system
	 * will fire events.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addLayoutListener(LayoutListener listener);

	/**
	 * Removes a layout listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeLayoutListener(LayoutListener listener);

	/**
	 * Adds a listener to the context that will be notified about changes in
	 * graph structure, that is addition and removal of nodes and connections.
	 * The notifications will not include changes made with API included in
	 * layout related interfaces, so that layout algorithms won't be notified
	 * about changes they invoke. Only internal changes of the system will fire
	 * events.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addGraphStructureListener(GraphStructureListener listener);

	/**
	 * Removes a graph structure listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeGraphStructureListener(GraphStructureListener listener);

	/**
	 * Adds a listener to the context that will be notified about changes
	 * related to its configuration.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addContextListener(ContextListener listener);

	/**
	 * Removes a context listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeContextListener(ContextListener listener);

	/**
	 * Adds a listener to the context that will be notified about changes in
	 * graph pruning, that is hiding and showing of nodes. The notifications
	 * will not include changes made with API included in layout related
	 * interfaces, so that layout algorithms won't be notified about changes
	 * they invoke. Only internal changes of the system will fire events.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addPruningListener(PruningListener listener);

	/**
	 * Removes a pruning structure listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removePruningListener(PruningListener listener);

	/**
	 * Causes all the changes made to elements in this context to affect the
	 * display. Called from layout algorithms on finish of layout.
	 * 
	 * @param animationHint
	 *            a hint for display mechanism indicating whether changes are
	 *            major and should be animated (if true) or not.
	 */
	public void flushChanges(boolean animationHint);

	/**
	 * Notifies all previously registered {@link GraphStructureListener}s about
	 * the newly added node.
	 * 
	 * @param node
	 *            NodeLayout of newly added node
	 */
	public void fireNodeAddedEvent(NodeLayout node);

	/**
	 * Notifies all previously registered {@link GraphStructureListener}s about
	 * the removed node.
	 * 
	 * @param node
	 *            NodeLayout of removed node
	 */
	public void fireNodeRemovedEvent(NodeLayout node);

	/**
	 * Notifies all previously registered {@link GraphStructureListener}s about
	 * the newly added connection.
	 * 
	 * @param connection
	 *            ConnectionLayout of newly added connection
	 */
	public void fireConnectionAddedEvent(ConnectionLayout connection);

	/**
	 * Notifies all previously registered {@link GraphStructureListener}s about
	 * the removed connection.
	 * 
	 * @param connection
	 *            ConnectionLayout of removed connection
	 */
	public void fireConnectionRemovedEvent(ConnectionLayout connection);

	/**
	 * Notifies all previously registered {@link ContextListener}s about the
	 * bounds change.
	 */
	public void fireBoundsChangedEvent();

	/**
	 * Notifies all previously registered {@link ContextListener}s about the
	 * state of the background layout flag.
	 */
	public void fireBackgroundEnableChangedEvent();

	/**
	 * Notifies all previously registered {@link LayoutListener}s about the
	 * moved node.
	 * 
	 * @param node
	 *            NodeLayout of moved node
	 */
	public void fireNodeMovedEvent(NodeLayout node);

	/**
	 * Notifies all previously registered {@link LayoutListener}s about the
	 * resized node.
	 * 
	 * @param node
	 *            NodeLayout of resized node
	 */
	public void fireNodeResizedEvent(NodeLayout node);

	/**
	 * Notifies all previously registered {@link LayoutListener}s about the
	 * moved subgraph.
	 * 
	 * @param subgraph
	 *            SubgraphLayout of moved subgraph
	 */
	public void fireSubgraphMovedEvent(SubgraphLayout subgraph);

	/**
	 * Notifies all previously registered {@link LayoutListener}s about the
	 * resized subgraph.
	 * 
	 * @param subgraph
	 *            SubgraphLayout of resized subgraph
	 */
	public void fireSubgraphResizedEvent(SubgraphLayout subgraph);

	/**
	 * Notifies all previously registered {@link ContextListener}s about the
	 * state of the pruning flag.
	 */
	public void firePruningEnableChangedEvent();

}
