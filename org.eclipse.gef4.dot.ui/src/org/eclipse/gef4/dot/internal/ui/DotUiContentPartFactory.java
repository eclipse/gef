/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #321775
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import java.util.Map;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.parts.ZestFxContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;
import javafx.util.Pair;

public class DotUiContentPartFactory extends ZestFxContentPartFactory {

	@Inject
	private Injector injector;

	@SuppressWarnings("rawtypes")
	@Override
	public IContentPart<Node, ? extends Node> createContentPart(Object content,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
		IContentPart<Node, ? extends Node> part = null;
		if (content instanceof org.eclipse.gef4.graph.Node) {
			part = new DotNodeContentPart();
		} else if (content instanceof Pair
				&& ((Pair) content).getKey() instanceof Edge) {
			part = new DotEdgeLabelPart();
		}
		if (part != null) {
			injector.injectMembers(part);
			return part;
		}
		return super.createContentPart(content, contextBehavior, contextMap);
	}

}
