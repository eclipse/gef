/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.layout.tests;

import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;

import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.IEntityLayout;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.ILayoutFilter;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.ISubgraphLayout;
import org.eclipse.gef4.layout.listeners.IContextListener;
import org.eclipse.gef4.layout.listeners.IGraphStructureListener;
import org.eclipse.gef4.layout.listeners.ILayoutListener;
import org.eclipse.gef4.layout.listeners.IPruningListener;
import org.eclipse.gef4.layout.listeners.LayoutListenerSupport;
import org.junit.Before;
import org.junit.Test;

public class LayoutListenerSupportTests {

	private class StubContext implements ILayoutContext {
		private LayoutListenerSupport lls = new LayoutListenerSupport(this);

		public void addContextListener(IContextListener listener) {
			lls.addContextListener(listener);
		}

		public void addGraphStructureListener(IGraphStructureListener listener) {
			lls.addGraphStructureListener(listener);
		}

		public void addLayoutFilter(ILayoutFilter layoutFilter) {
		}

		public void addLayoutListener(ILayoutListener listener) {
			lls.addLayoutListener(listener);
		}

		public void addPropertyChangeListener(PropertyChangeListener listener) {
		}

		public void addPruningListener(IPruningListener listener) {
			lls.addPruningListener(listener);
		}

		public void applyDynamicLayout(boolean clean) {
			assertTrue(expectedDynamicLayout);
		}

		public void applyStaticLayout(boolean clean) {
		}

		public ISubgraphLayout createSubgraph(INodeLayout[] nodes) {
			return null;
		}

		public void fireBackgroundEnableChangedEvent() {
			lls.fireBackgroundEnableChangedEvent();
		}

		public void fireBoundsChangedEvent() {
			lls.fireBoundsChangedEvent();
		}

		public void fireConnectionAddedEvent(IConnectionLayout connection) {
			lls.fireConnectionAddedEvent(connection);
		}

		public void fireConnectionRemovedEvent(IConnectionLayout connection) {
			lls.fireConnectionRemovedEvent(connection);
		}

		public void fireNodeAddedEvent(INodeLayout node) {
			lls.fireNodeAddedEvent(node);
		}

		public void fireNodeMovedEvent(INodeLayout node) {
			lls.fireNodeMovedEvent(node);
		}

		public void fireNodeRemovedEvent(INodeLayout node) {
			lls.fireNodeRemovedEvent(node);
		}

		public void fireNodeResizedEvent(INodeLayout node) {
			lls.fireNodeResizedEvent(node);
		}

		public void firePruningEnableChangedEvent() {
			lls.firePruningEnableChangedEvent();
		}

		public void fireSubgraphMovedEvent(ISubgraphLayout subgraph) {
			lls.fireSubgraphMovedEvent(subgraph);
		}

		public void fireSubgraphResizedEvent(ISubgraphLayout subgraph) {
			lls.fireSubgraphResizedEvent(subgraph);
		}

		public void flushChanges(boolean animationHint) {
		}

		public IConnectionLayout[] getConnections() {
			return null;
		}

		public IConnectionLayout[] getConnections(IEntityLayout layoutEntity1,
				IEntityLayout layoutEntity2) {
			return null;
		}

		public ILayoutAlgorithm getDynamicLayoutAlgorithm() {
			return null;
		}

		public IEntityLayout[] getEntities() {
			return null;
		}

		public INodeLayout[] getNodes() {
			return null;
		}

		public Object getProperty(String name) {
			return null;
		}

		public ILayoutAlgorithm getStaticLayoutAlgorithm() {
			return null;
		}

		public ISubgraphLayout[] getSubgraphs() {
			return null;
		}

		public boolean isLayoutIrrelevant(IConnectionLayout connLayout) {
			return false;
		}

		public boolean isLayoutIrrelevant(INodeLayout nodeLayout) {
			return false;
		}

		public void removeContextListener(IContextListener listener) {
			lls.removeContextListener(listener);
		}

		public void removeGraphStructureListener(
				IGraphStructureListener listener) {
			lls.removeGraphStructureListener(listener);
		}

		public void removeLayoutFilter(ILayoutFilter layoutFilter) {
		}

		public void removeLayoutListener(ILayoutListener listener) {
			lls.removeLayoutListener(listener);
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
		}

		public void removePruningListener(IPruningListener listener) {
			lls.removePruningListener(listener);
		}

		public void schedulePostLayoutPass(Runnable runnable) {
		}

		public void schedulePreLayoutPass(Runnable runnable) {
		}

		public void setDynamicLayoutAlgorithm(ILayoutAlgorithm algorithm) {
		}

		public void setProperty(String name, Object value) {
		}

		public void setStaticLayoutAlgorithm(ILayoutAlgorithm algorithm) {
		}

		public void unschedulePostLayoutPass(Runnable runnable) {
		}

		public void unschedulePreLayoutPass(Runnable runnable) {
		}
	}

	private static IContextListener nonInterceptingContextListener = new IContextListener() {
		public void backgroundEnableChanged(ILayoutContext context) {
		}

		public boolean boundsChanged(ILayoutContext context) {
			return false;
		}

		public boolean pruningEnablementChanged(ILayoutContext context) {
			return false;
		}
	};
	private static IContextListener interceptingContextListener = new IContextListener() {
		public void backgroundEnableChanged(ILayoutContext context) {
		}

		public boolean boundsChanged(ILayoutContext context) {
			return true;
		}

		public boolean pruningEnablementChanged(ILayoutContext context) {
			return true;
		}
	};

	private static IGraphStructureListener nonInterceptingGraphStructureListener = new IGraphStructureListener() {
		public boolean connectionAdded(ILayoutContext context,
				IConnectionLayout connection) {
			return false;
		}

		public boolean connectionRemoved(ILayoutContext context,
				IConnectionLayout connection) {
			return false;
		}

		public boolean nodeAdded(ILayoutContext context, INodeLayout node) {
			return false;
		}

		public boolean nodeRemoved(ILayoutContext context, INodeLayout node) {
			return false;
		}
	};
	private static IGraphStructureListener interceptingGraphStructureListener = new IGraphStructureListener() {
		public boolean connectionAdded(ILayoutContext context,
				IConnectionLayout connection) {
			return true;
		}

		public boolean connectionRemoved(ILayoutContext context,
				IConnectionLayout connection) {
			return true;
		}

		public boolean nodeAdded(ILayoutContext context, INodeLayout node) {
			return true;
		}

		public boolean nodeRemoved(ILayoutContext context, INodeLayout node) {
			return true;
		}
	};

	private static ILayoutListener nonInterceptingLayoutListener = new ILayoutListener() {
		public boolean nodeMoved(ILayoutContext context, INodeLayout node) {
			return false;
		}

		public boolean nodeResized(ILayoutContext context, INodeLayout node) {
			return false;
		}

		public boolean subgraphMoved(ILayoutContext context,
				ISubgraphLayout subgraph) {
			return false;
		}

		public boolean subgraphResized(ILayoutContext context,
				ISubgraphLayout subgraph) {
			return false;
		}
	};
	private static ILayoutListener interceptingLayoutListener = new ILayoutListener() {
		public boolean nodeMoved(ILayoutContext context, INodeLayout node) {
			return true;
		}

		public boolean nodeResized(ILayoutContext context, INodeLayout node) {
			return true;
		}

		public boolean subgraphMoved(ILayoutContext context,
				ISubgraphLayout subgraph) {
			return true;
		}

		public boolean subgraphResized(ILayoutContext context,
				ISubgraphLayout subgraph) {
			return true;
		}
	};

	/**
	 * When the {@link StubContext#applyDynamicLayout(boolean)} method is
	 * called, this variable is tested for being <code>true</code>.
	 */
	protected boolean expectedDynamicLayout;
	protected ILayoutContext context;

	@Before
	public void setUpLayoutContext() {
		expectedDynamicLayout = true;
		context = new StubContext();
	}

	@Test
	public void test_dynamic_onBoundsChange() {
		context.addContextListener(nonInterceptingContextListener);
		context.fireBoundsChangedEvent();
		expectedDynamicLayout = false;
		context.addContextListener(interceptingContextListener);
		context.fireBoundsChangedEvent();
		// add another non-intercepting listener and change bounds again to
		// verify that one intercepting listener prevents dynamic layout
		context.addContextListener(nonInterceptingContextListener);
		context.fireBoundsChangedEvent();
	}

	@Test
	public void test_dynamic_onConnectionAdded() {
		context.addGraphStructureListener(nonInterceptingGraphStructureListener);
		context.fireConnectionAddedEvent(null);
		expectedDynamicLayout = false;
		context.addGraphStructureListener(interceptingGraphStructureListener);
		context.fireConnectionAddedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addGraphStructureListener(nonInterceptingGraphStructureListener);
		context.fireConnectionAddedEvent(null);
	}

	@Test
	public void test_dynamic_onConnectionRemoved() {
		context.addGraphStructureListener(nonInterceptingGraphStructureListener);
		context.fireConnectionRemovedEvent(null);
		expectedDynamicLayout = false;
		context.addGraphStructureListener(interceptingGraphStructureListener);
		context.fireConnectionRemovedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addGraphStructureListener(nonInterceptingGraphStructureListener);
		context.fireConnectionRemovedEvent(null);
	}

	@Test
	public void test_dynamic_onNodeAdded() {
		context.addGraphStructureListener(nonInterceptingGraphStructureListener);
		context.fireNodeAddedEvent(null);
		expectedDynamicLayout = false;
		context.addGraphStructureListener(interceptingGraphStructureListener);
		context.fireNodeAddedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addGraphStructureListener(nonInterceptingGraphStructureListener);
		context.fireNodeAddedEvent(null);
	}

	@Test
	public void test_dynamic_onNodeMoved() {
		context.addLayoutListener(nonInterceptingLayoutListener);
		context.fireNodeMovedEvent(null);
		expectedDynamicLayout = false;
		context.addLayoutListener(interceptingLayoutListener);
		context.fireNodeMovedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addLayoutListener(nonInterceptingLayoutListener);
		context.fireNodeMovedEvent(null);
	}

	@Test
	public void test_dynamic_onNodeRemoved() {
		context.addGraphStructureListener(nonInterceptingGraphStructureListener);
		context.fireNodeRemovedEvent(null);
		expectedDynamicLayout = false;
		context.addGraphStructureListener(interceptingGraphStructureListener);
		context.fireNodeRemovedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addGraphStructureListener(nonInterceptingGraphStructureListener);
		context.fireNodeRemovedEvent(null);
	}

	@Test
	public void test_dynamic_onNodeResized() {
		context.addLayoutListener(nonInterceptingLayoutListener);
		context.fireNodeResizedEvent(null);
		expectedDynamicLayout = false;
		context.addLayoutListener(interceptingLayoutListener);
		context.fireNodeResizedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addLayoutListener(nonInterceptingLayoutListener);
		context.fireNodeResizedEvent(null);
	}

	@Test
	public void test_dynamic_onPruningEnableChange() {
		context.addContextListener(nonInterceptingContextListener);
		context.firePruningEnableChangedEvent();
		expectedDynamicLayout = false;
		context.addContextListener(interceptingContextListener);
		context.firePruningEnableChangedEvent();
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addContextListener(nonInterceptingContextListener);
		context.firePruningEnableChangedEvent();
	}

	@Test
	public void test_dynamic_onSubgraphMoved() {
		context.addLayoutListener(nonInterceptingLayoutListener);
		context.fireSubgraphMovedEvent(null);
		expectedDynamicLayout = false;
		context.addLayoutListener(interceptingLayoutListener);
		context.fireSubgraphMovedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addLayoutListener(nonInterceptingLayoutListener);
		context.fireSubgraphMovedEvent(null);
	}

	@Test
	public void test_dynamic_onSubgraphResized() {
		context.addLayoutListener(nonInterceptingLayoutListener);
		context.fireSubgraphResizedEvent(null);
		expectedDynamicLayout = false;
		context.addLayoutListener(interceptingLayoutListener);
		context.fireSubgraphResizedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addLayoutListener(nonInterceptingLayoutListener);
		context.fireSubgraphResizedEvent(null);
	}

	@Test
	public void test_noDynamic_onBackgroundEnableChange() {
		expectedDynamicLayout = false;
		context.addContextListener(interceptingContextListener);
		context.fireBackgroundEnableChangedEvent();
	}

}
