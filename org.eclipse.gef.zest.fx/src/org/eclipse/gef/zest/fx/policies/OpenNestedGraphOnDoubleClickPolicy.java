/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
import org.eclipse.gef.zest.fx.parts.NodePart;

import javafx.scene.input.MouseEvent;

/**
 * The {@link OpenNestedGraphOnDoubleClickPolicy} is an {@link IOnClickPolicy}
 * that can be installed on {@link NodePart}s (see {@link #getHost()}). It opens
 * the {@link Graph} that is nested inside of its {@link NodePart} when the
 * {@link NodePart} is double clicked.
 *
 * @author mwienand
 *
 */
public class OpenNestedGraphOnDoubleClickPolicy extends AbstractInteractionPolicy implements IOnClickPolicy {

	@Override
	public void click(MouseEvent event) {
		if (event.getClickCount() == 2) {
			// double click, so open nested graph, if it exists
			final Graph nestedGraph = getHost().getContent().getNestedGraph();
			if (nestedGraph != null) {
				IViewer viewer = getHost().getRoot().getViewer();
				try {
					// navigate to nested graph
					viewer.getDomain().execute(new NavigateOperation(viewer, nestedGraph, true),
							new NullProgressMonitor());
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public NodePart getHost() {
		return (NodePart) super.getHost();
	}
}
