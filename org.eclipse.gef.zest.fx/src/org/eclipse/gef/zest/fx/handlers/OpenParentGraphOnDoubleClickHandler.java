/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
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
package org.eclipse.gef.zest.fx.handlers;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.operations.NavigateOperation;
import org.eclipse.gef.zest.fx.parts.ZestFxRootPart;

import javafx.scene.input.MouseEvent;

/**
 * The {@link OpenParentGraphOnDoubleClickHandler} is an {@link IOnClickHandler}
 * that can be installed on {@link ZestFxRootPart}s (see {@link #getHost()}). It
 * opens the {@link Graph} that contains the node that contains the
 * {@link Graph} that is currently open when the background is double clicked.
 *
 * @author mwienand
 *
 */
public class OpenParentGraphOnDoubleClickHandler extends AbstractHandler implements IOnClickHandler {

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
					// navigate to parent graph
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
