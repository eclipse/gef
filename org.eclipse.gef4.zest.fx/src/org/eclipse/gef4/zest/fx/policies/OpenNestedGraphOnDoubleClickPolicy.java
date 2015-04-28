/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

public class OpenNestedGraphOnDoubleClickPolicy extends AbstractFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		if (e.getClickCount() == 2) {
			// double click
			NodeContentPart nodePart = getHost();
			org.eclipse.gef4.graph.Node graphNode = nodePart.getContent();
			Graph nestedGraph = graphNode.getNestedGraph();
			if (nestedGraph != null) {
				// change contents
				ContentModel contentModel = getHost().getRoot().getViewer()
						.getAdapter(ContentModel.class);
				contentModel
						.setContents(Collections.singletonList(nestedGraph));
			}
		}
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

}
