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
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class OpenNestedGraphOnDoubleClickPolicy extends AbstractFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		if (e.getClickCount() == 2) {
			// double click, so open nested graph, if it exists
			final Graph nestedGraph = getHost().getContent().getNestedGraph();
			if (nestedGraph != null) {
				IDomain<Node> domain = getHost().getRoot().getViewer().getDomain();
				NavigationPolicy navigationPolicy = getHost().getRoot().getAdapter(NavigationPolicy.class);
				navigationPolicy.init();
				navigationPolicy.openNestedGraph(nestedGraph);
				domain.execute(navigationPolicy.commit());
			}
		}
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

}
