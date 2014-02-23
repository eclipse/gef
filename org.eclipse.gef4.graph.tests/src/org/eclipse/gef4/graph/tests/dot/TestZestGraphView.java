/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.graph.tests.dot;

import java.io.IOException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.gef4.graph.internal.dot.ZestGraphView;
import org.eclipse.gef4.zest.core.widgets.GraphWidget;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the Zest Graph View.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestZestGraphView {

	private static final String DOT = "digraph{1->2}"; //$NON-NLS-1$

	@BeforeClass
	public static void setup() {
		if (!Platform.isRunning()) {
			Assert.fail("Please run as JUnit Plug-in test"); //$NON-NLS-1$
		}
	}

	@Test
	public void loadGraphFromDot() throws PartInitException,
			InterruptedException, IOException {
		GraphWidget g = loadGraphInView(DOT);
		Assert.assertEquals(2, g.getNodes().size());
		Assert.assertEquals(1, g.getConnections().size());
	}

	private GraphWidget loadGraphInView(String dot) throws PartInitException {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		ZestGraphView graphView = (ZestGraphView) page
				.showView(ZestGraphView.ID);
		graphView.setGraph(dot, false);
		GraphWidget g = graphView.getGraph();
		return g;
	}
}
