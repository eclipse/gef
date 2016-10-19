/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
package org.eclipse.gef.zest.fx.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.fx.policies.IOnClickPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.operations.NavigateOperation;
import org.eclipse.gef.zest.fx.parts.ZestFxRootPart;

import javafx.scene.input.MouseEvent;

/**
 * The {@link OpenParentGraphOnDoubleClickPolicy} is an {@link IOnClickPolicy}
 * that can be installed on {@link ZestFxRootPart}s (see {@link #getHost()}). It
 * opens the {@link Graph} that contains the node that contains the
 * {@link Graph} that is currently open when the background is double clicked.
 *
 * @author mwienand
 *
 */
public class OpenParentGraphOnDoubleClickPolicy extends AbstractInteractionPolicy implements IOnClickPolicy {

	@Override
	public void click(MouseEvent event) {
		if (event.getClickCount() == 2) {
			// do nothing in case there is an explicit event target
			if (isRegistered(event.getTarget()) && !isRegisteredForHost(event.getTarget())) {
				return;
			}

			// double click, so open nesting graph, if it exists
			IViewer viewer = getHost().getRoot().getViewer();
			final Graph currentGraph = (Graph) viewer.getContents().get(0);
			final Graph nestingGraph = currentGraph.getNestingNode() != null ? currentGraph.getNestingNode().getGraph()
					: null;
			if (nestingGraph != null) {
				try {
					viewer.getDomain().execute(new NavigateOperation(viewer, nestingGraph, false),
							new NullProgressMonitor());
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public ZestFxRootPart getHost() {
		return (ZestFxRootPart) super.getHost();
	}

}
