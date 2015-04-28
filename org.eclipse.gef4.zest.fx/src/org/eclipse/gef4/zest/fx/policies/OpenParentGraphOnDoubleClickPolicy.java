/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import java.util.Collections;

import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.zest.fx.parts.GraphRootPart;

public class OpenParentGraphOnDoubleClickPolicy extends AbstractFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		if (e.getClickCount() == 2) {
			// double click
			ContentModel contentModel = getHost().getViewer().getAdapter(
					ContentModel.class);
			Graph graph = (Graph) contentModel.getContents().get(0);
			if (graph.getNestingNode() != null) {
				Graph parentGraph = graph.getNestingNode().getGraph();
				// change contents
				contentModel
						.setContents(Collections.singletonList(parentGraph));
			}
		}
	}

	@Override
	public GraphRootPart getHost() {
		return (GraphRootPart) super.getHost();
	}

}
