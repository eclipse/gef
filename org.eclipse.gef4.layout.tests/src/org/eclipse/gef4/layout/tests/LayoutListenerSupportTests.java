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
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.ILayoutFilter;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.listeners.IContextListener;
import org.eclipse.gef4.layout.listeners.IGraphStructureListener;
import org.eclipse.gef4.layout.listeners.ILayoutListener;
import org.eclipse.gef4.layout.listeners.LayoutListenerSupport;
import org.junit.Before;
import org.junit.Test;

public class LayoutListenerSupportTests {

	private class StubContext implements ILayoutContext {
		private LayoutListenerSupport lls = new LayoutListenerSupport(this);

		@Override
		public void addContextListener(IContextListener listener) {
			lls.addContextListener(listener);
		}

		@Override
		public void addGraphStructureListener(
				IGraphStructureListener listener) {
			lls.addGraphStructureListener(listener);
		}

		@Override
		public void addLayoutFilter(ILayoutFilter layoutFilter) {
		}

		@Override
		public void addLayoutListener(ILayoutListener listener) {
			lls.addLayoutListener(listener);
		}

		@Override
		public void addPropertyChangeListener(PropertyChangeListener listener) {
		}

		@Override
		public void applyDynamicLayout(boolean clean) {
			assertTrue(expectedDynamicLayout);
		}

		@Override
		public void applyStaticLayout(boolean clean) {
		}

		@Override
		public void fireBackgroundEnableChangedEvent() {
			lls.fireBackgroundEnableChangedEvent();
		}

		@Override
		public void fireBoundsChangedEvent() {
			lls.fireBoundsChangedEvent();
		}

		@Override
		public void fireConnectionAddedEvent(IConnectionLayout connection) {
			lls.fireConnectionAddedEvent(connection);
		}

		@Override
		public void fireConnectionRemovedEvent(IConnectionLayout connection) {
			lls.fireConnectionRemovedEvent(connection);
		}

		@Override
		public void fireNodeAddedEvent(INodeLayout node) {
			lls.fireNodeAddedEvent(node);
		}

		@Override
		public void fireNodeMovedEvent(INodeLayout node) {
			lls.fireNodeMovedEvent(node);
		}

		@Override
		public void fireNodeRemovedEvent(INodeLayout node) {
			lls.fireNodeRemovedEvent(node);
		}

		@Override
		public void fireNodeResizedEvent(INodeLayout node) {
			lls.fireNodeResizedEvent(node);
		}

		@Override
		public void flushChanges() {
		}

		@Override
		public IConnectionLayout[] getConnections() {
			return null;
		}

		@Override
		public IConnectionLayout[] getConnections(INodeLayout layoutEntity1,
				INodeLayout layoutEntity2) {
			return null;
		}

		@Override
		public ILayoutAlgorithm getDynamicLayoutAlgorithm() {
			return null;
		}

		@Override
		public INodeLayout[] getNodes() {
			return null;
		}

		@Override
		public Object getProperty(String name) {
			return null;
		}

		@Override
		public ILayoutAlgorithm getStaticLayoutAlgorithm() {
			return null;
		}

		@Override
		public boolean isLayoutIrrelevant(IConnectionLayout connLayout) {
			return false;
		}

		@Override
		public boolean isLayoutIrrelevant(INodeLayout nodeLayout) {
			return false;
		}

		@Override
		public void removeContextListener(IContextListener listener) {
			lls.removeContextListener(listener);
		}

		@Override
		public void removeGraphStructureListener(
				IGraphStructureListener listener) {
			lls.removeGraphStructureListener(listener);
		}

		@Override
		public void removeLayoutFilter(ILayoutFilter layoutFilter) {
		}

		@Override
		public void removeLayoutListener(ILayoutListener listener) {
			lls.removeLayoutListener(listener);
		}

		@Override
		public void removePropertyChangeListener(
				PropertyChangeListener listener) {
		}

		@Override
		public void schedulePostLayoutPass(Runnable runnable) {
		}

		@Override
		public void schedulePreLayoutPass(Runnable runnable) {
		}

		@Override
		public void setDynamicLayoutAlgorithm(ILayoutAlgorithm algorithm) {
		}

		@Override
		public void setProperty(String name, Object value) {
		}

		@Override
		public void setStaticLayoutAlgorithm(ILayoutAlgorithm algorithm) {
		}

		@Override
		public void unschedulePostLayoutPass(Runnable runnable) {
		}

		@Override
		public void unschedulePreLayoutPass(Runnable runnable) {
		}
	}

	private static IContextListener nonInterceptingContextListener = new IContextListener() {
		@Override
		public void backgroundEnableChanged(ILayoutContext context) {
		}

		@Override
		public boolean boundsChanged(ILayoutContext context) {
			return false;
		}

	};
	private static IContextListener interceptingContextListener = new IContextListener() {
		@Override
		public void backgroundEnableChanged(ILayoutContext context) {
		}

		@Override
		public boolean boundsChanged(ILayoutContext context) {
			return true;
		}

	};

	private static IGraphStructureListener nonInterceptingGraphStructureListener = new IGraphStructureListener() {
		@Override
		public boolean connectionAdded(ILayoutContext context,
				IConnectionLayout connection) {
			return false;
		}

		@Override
		public boolean connectionRemoved(ILayoutContext context,
				IConnectionLayout connection) {
			return false;
		}

		@Override
		public boolean nodeAdded(ILayoutContext context, INodeLayout node) {
			return false;
		}

		@Override
		public boolean nodeRemoved(ILayoutContext context, INodeLayout node) {
			return false;
		}
	};
	private static IGraphStructureListener interceptingGraphStructureListener = new IGraphStructureListener() {
		@Override
		public boolean connectionAdded(ILayoutContext context,
				IConnectionLayout connection) {
			return true;
		}

		@Override
		public boolean connectionRemoved(ILayoutContext context,
				IConnectionLayout connection) {
			return true;
		}

		@Override
		public boolean nodeAdded(ILayoutContext context, INodeLayout node) {
			return true;
		}

		@Override
		public boolean nodeRemoved(ILayoutContext context, INodeLayout node) {
			return true;
		}
	};

	private static ILayoutListener nonInterceptingLayoutListener = new ILayoutListener() {
		@Override
		public boolean nodeMoved(ILayoutContext context, INodeLayout node) {
			return false;
		}

		@Override
		public boolean nodeResized(ILayoutContext context, INodeLayout node) {
			return false;
		}

	};
	private static ILayoutListener interceptingLayoutListener = new ILayoutListener() {
		@Override
		public boolean nodeMoved(ILayoutContext context, INodeLayout node) {
			return true;
		}

		@Override
		public boolean nodeResized(ILayoutContext context, INodeLayout node) {
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
		context.addGraphStructureListener(
				nonInterceptingGraphStructureListener);
		context.fireConnectionAddedEvent(null);
		expectedDynamicLayout = false;
		context.addGraphStructureListener(interceptingGraphStructureListener);
		context.fireConnectionAddedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addGraphStructureListener(
				nonInterceptingGraphStructureListener);
		context.fireConnectionAddedEvent(null);
	}

	@Test
	public void test_dynamic_onConnectionRemoved() {
		context.addGraphStructureListener(
				nonInterceptingGraphStructureListener);
		context.fireConnectionRemovedEvent(null);
		expectedDynamicLayout = false;
		context.addGraphStructureListener(interceptingGraphStructureListener);
		context.fireConnectionRemovedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addGraphStructureListener(
				nonInterceptingGraphStructureListener);
		context.fireConnectionRemovedEvent(null);
	}

	@Test
	public void test_dynamic_onNodeAdded() {
		context.addGraphStructureListener(
				nonInterceptingGraphStructureListener);
		context.fireNodeAddedEvent(null);
		expectedDynamicLayout = false;
		context.addGraphStructureListener(interceptingGraphStructureListener);
		context.fireNodeAddedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addGraphStructureListener(
				nonInterceptingGraphStructureListener);
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
		context.addGraphStructureListener(
				nonInterceptingGraphStructureListener);
		context.fireNodeRemovedEvent(null);
		expectedDynamicLayout = false;
		context.addGraphStructureListener(interceptingGraphStructureListener);
		context.fireNodeRemovedEvent(null);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addGraphStructureListener(
				nonInterceptingGraphStructureListener);
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
	public void test_dynamic_onSubgraphMoved() {
		context.addLayoutListener(nonInterceptingLayoutListener);
		expectedDynamicLayout = false;
		context.addLayoutListener(interceptingLayoutListener);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addLayoutListener(nonInterceptingLayoutListener);
	}

	@Test
	public void test_dynamic_onSubgraphResized() {
		context.addLayoutListener(nonInterceptingLayoutListener);
		expectedDynamicLayout = false;
		context.addLayoutListener(interceptingLayoutListener);
		// add another non-intercepting listener and change again to
		// verify that one intercepting listener prevents dynamic layout
		context.addLayoutListener(nonInterceptingLayoutListener);
	}

	@Test
	public void test_noDynamic_onBackgroundEnableChange() {
		expectedDynamicLayout = false;
		context.addContextListener(interceptingContextListener);
		context.fireBackgroundEnableChangedEvent();
	}

}
