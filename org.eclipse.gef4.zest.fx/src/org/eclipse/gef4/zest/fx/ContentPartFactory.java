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
package org.eclipse.gef4.zest.fx;

import java.util.Map;

import javafx.scene.Node;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class ContentPartFactory implements IContentPartFactory<Node> {

	@Inject
	private Injector injector;

	@Override
	public IContentPart<Node> createContentPart(Object content,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
		IContentPart<Node> part = null;
		if (content instanceof Graph) {
			part = new GraphContentPart();
		} else if (content instanceof org.eclipse.gef4.graph.Node) {
			part = new NodeContentPart();
		} else if (content instanceof Edge) {
			part = new EdgeContentPart();
		}
		if (part != null) {
			injector.injectMembers(part);
		}
		return part;
	}
}
