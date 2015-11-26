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
package org.eclipse.gef4.zest.fx.policies;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.zest.fx.operations.NavigateOperation;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.scene.input.MouseEvent;

/**
 * The {@link OpenNestedGraphOnDoubleClickPolicy} is an
 * {@link AbstractFXOnClickPolicy} that can be installed on
 * {@link NodeContentPart}s (see {@link #getHost()}). It opens the {@link Graph}
 * that is nested inside of its {@link NodeContentPart} when the
 * {@link NodeContentPart} is double clicked.
 *
 * @author mwienand
 *
 */
public class OpenNestedGraphOnDoubleClickPolicy extends AbstractFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		if (e.getClickCount() == 2) {
			// double click, so open nested graph, if it exists
			final Graph nestedGraph = getHost().getContent().getNestedGraph();
			if (nestedGraph != null) {
				FXViewer viewer = (FXViewer) getHost().getRoot().getViewer();
				viewer.getDomain().execute(NavigateOperation.openNestedGraph(viewer, nestedGraph));
			}
		}
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

}
