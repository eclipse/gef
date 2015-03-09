/*******************************************************************************
 * Copyright (c) 2009-2010 Mateusz Matela and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.zest.core.widgets;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.layout.algorithms.SpaceTreeLayoutAlgorithm.ExpandCollapseManager;
import org.eclipse.gef4.layout.listeners.IContextListener;
import org.eclipse.gef4.layout.listeners.IGraphStructureListener;

/**
 * <p>
 * An {@link ExpandCollapseManager} specialized for Directed Acyclic Graphs. It
 * works correctly only when all connections are directed (and of course nodes
 * form an acyclic graph). It's supposed to be used with
 * {@link InternalLayoutContext}.
 * </p>
 * <p>
 * When a node is collapsed, all its outgoing connections are hidden and these
 * successors that have no visible incoming nodes are pruned. When a node is
 * expanded, all its successors are unpruned and connections pointing to them
 * are shown.
 * </p>
 * <p>
 * <b>NOTE:</b> A <code>Graph</code> using this manager should use
 * {@link DefaultSubgraph}, which doesn't show any information about subgraphs
 * in the graph. That's because for this manager it doesn't matter which
 * subgraph a node belongs to (each pruning creates a new subgraph). Also, this
 * manager adds a label to each collapsed node showing number of its successors.
 * </p>
 * One instance of this class can serve only one instance of <code>Graph</code>.
 * 
 */
public class DAGExpandCollapseManager implements ExpandCollapseManager {

	private HashSet<INodeLayout> expandedNodes = new HashSet<INodeLayout>();

	private HashSet<INodeLayout> nodesToPrune = new HashSet<INodeLayout>();

	private HashSet<INodeLayout> nodesToUnprune = new HashSet<INodeLayout>();

	private HashSet<INodeLayout> nodesToUpdate = new HashSet<INodeLayout>();

	private boolean cleanLayoutScheduled = false;

	private boolean animate = true;

	private ILayoutContext context;

	/**
	 * @param animate
	 *            if true, implicit animations are enabled (e.g. on layout
	 *            changes)
	 */
	public DAGExpandCollapseManager(boolean animate) {
		this.animate = animate;
	}

	public void initExpansion(final ILayoutContext context) {
		this.context = context;
		context.addGraphStructureListener(new IGraphStructureListener() {
			public boolean nodeRemoved(ILayoutContext context, INodeLayout node) {
				if (isExpanded(node)) {
					collapse(node);
				}
				flushChanges(false, true);
				return false;
			}

			public boolean nodeAdded(ILayoutContext context, INodeLayout node) {
				resetState(node);
				flushChanges(false, true);
				return false;
			}

			public boolean connectionRemoved(ILayoutContext context,
					IConnectionLayout connection) {
				INodeLayout target = connection.getTarget();
				if (!isExpanded(target)
						&& target.getIncomingConnections().length == 0) {
					expand(target);
				}
				flushChanges(false, true);
				return false;
			}

			public boolean connectionAdded(ILayoutContext context,
					IConnectionLayout connection) {
				resetState(connection.getTarget());
				updateNodeLabel(connection.getSource());
				flushChanges(false, true);
				return false;
			}

		});

		context.addContextListener(new IContextListener.Stub() {
			public void backgroundEnableChanged(ILayoutContext context) {
				flushChanges(false, false);
			}
		});
	}

	public boolean canCollapse(ILayoutContext context, INodeLayout node) {
		return isExpanded(node) && !LayoutProperties.isPruned(node)
				&& node.getOutgoingConnections().length > 0;
	}

	public boolean canExpand(ILayoutContext context, INodeLayout node) {
		return !isExpanded(node) && !LayoutProperties.isPruned(node)
				&& node.getOutgoingConnections().length > 0;
	}

	private void collapseAllConnections(INodeLayout node) {
		IConnectionLayout[] outgoingConnections = node.getOutgoingConnections();
		for (int i = 0; i < outgoingConnections.length; i++) {
			LayoutProperties.setVisible(outgoingConnections[i], false);
		}
		flushChanges(true, true);
	}

	private void expandAllConnections(INodeLayout node) {
		IConnectionLayout[] outgoingConnections = node.getOutgoingConnections();
		for (int i = 0; i < outgoingConnections.length; i++) {
			LayoutProperties.setVisible(outgoingConnections[i], true);
		}
		flushChanges(true, true);
	}

	public void setExpanded(ILayoutContext context, INodeLayout node,
			boolean expanded) {

		// if (isExpanded(node) == expanded)
		// return;
		if (expanded) {
			if (canExpand(context, node)) {
				expand(node);
			}
			expandAllConnections(node);
		} else {
			if (canCollapse(context, node)) {
				collapse(node);
			}
			collapseAllConnections(node);
		}
		flushChanges(true, true);
	}

	private void expand(INodeLayout node) {
		setExpanded(node, true);
		INodeLayout[] successingNodes = node.getSuccessingNodes();
		for (int i = 0; i < successingNodes.length; i++) {
			unpruneNode(successingNodes[i]);
		}
		updateNodeLabel(node);
	}

	private void collapse(INodeLayout node) {
		if (isExpanded(node)) {
			setExpanded(node, false);
		} else {
			return;
		}
		INodeLayout[] successors = node.getSuccessingNodes();
		for (int i = 0; i < successors.length; i++) {
			checkPruning(successors[i]);
			if (isPruned(successors[i])) {
				collapse(successors[i]);
			}
		}
		updateNodeLabel(node);
	}

	private void checkPruning(INodeLayout node) {
		boolean prune = true;
		INodeLayout[] predecessors = node.getPredecessingNodes();
		for (int j = 0; j < predecessors.length; j++) {
			if (isExpanded(predecessors[j])) {
				prune = false;
				break;
			}
		}
		if (prune) {
			pruneNode(node);
		} else {
			unpruneNode(node);
		}
	}

	/**
	 * By default nodes at the top (having no predecessors) are expanded. The
	 * rest are collapsed and pruned if they don't have any expanded
	 * predecessors
	 * 
	 * @param target
	 */
	private void resetState(INodeLayout node) {
		INodeLayout[] predecessors = node.getPredecessingNodes();
		if (predecessors.length == 0) {
			expand(node);
		} else {
			collapse(node);
			checkPruning(node);
		}
	}

	/**
	 * If given node belongs to a layout context using
	 * {@link PrunedSuccessorsSubgraph}, update of the nodes's label is forced.
	 * 
	 * @param node
	 *            node to update
	 */
	private void updateNodeLabel(INodeLayout node) {
		nodesToUpdate.add(node);
	}

	private void updateNodeLabel2(InternalNodeLayout node) {
		SubgraphFactory subgraphFactory = node.getOwnerLayoutContext()
				.getSubgraphFactory();
		if (subgraphFactory instanceof DefaultSubgraph.PrunedSuccessorsSubgraphFactory) {
			((DefaultSubgraph.PrunedSuccessorsSubgraphFactory) subgraphFactory)
					.updateLabelForNode(node);
		}
	}

	private void pruneNode(INodeLayout node) {
		if (isPruned(node)) {
			return;
		}
		nodesToUnprune.remove(node);
		nodesToPrune.add(node);
	}

	private void unpruneNode(INodeLayout node) {
		if (!isPruned(node)) {
			return;
		}
		nodesToPrune.remove(node);
		nodesToUnprune.add(node);
	}

	private boolean isPruned(INodeLayout node) {
		if (nodesToUnprune.contains(node)) {
			return false;
		}
		if (nodesToPrune.contains(node)) {
			return true;
		}
		return LayoutProperties.isPruned(node);
	}

	private void flushChanges(boolean force, boolean clean) {
		cleanLayoutScheduled = cleanLayoutScheduled || clean;
		if (!force && !LayoutProperties.isDynamicLayoutEnables(context)) {
			return;
		}

		for (Iterator<INodeLayout> iterator = nodesToUnprune.iterator(); iterator
				.hasNext();) {
			INodeLayout node = iterator.next();
			node.prune(null);
		}
		nodesToUnprune.clear();

		if (!nodesToPrune.isEmpty()) {
			context.createSubgraph(nodesToPrune
					.toArray(new INodeLayout[nodesToPrune.size()]));
			nodesToPrune.clear();
		}

		for (Iterator<INodeLayout> iterator = nodesToUpdate.iterator(); iterator
				.hasNext();) {
			InternalNodeLayout node = (InternalNodeLayout) iterator.next();
			updateNodeLabel2(node);
		}
		nodesToUpdate.clear();

		(context).applyStaticLayout(cleanLayoutScheduled);
		cleanLayoutScheduled = false;
		context.flushChanges(animate);
	}

	private boolean isExpanded(INodeLayout node) {
		// return !node.isPruned();
		return expandedNodes.contains(node);
	}

	private void setExpanded(INodeLayout node, boolean expanded) {
		if (expanded) {
			expandedNodes.add(node);
		} else {
			expandedNodes.remove(node);
		}
	}
}
