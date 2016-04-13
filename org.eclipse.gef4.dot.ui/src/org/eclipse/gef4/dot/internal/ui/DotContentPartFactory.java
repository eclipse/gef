/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.zest.fx.parts.ZestFxContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;

/**
 * The specific {@link IContentPartFactory} used by the {@link DotGraphView}'s
 * viewer.
 * 
 * @author anyssen
 *
 */
public class DotContentPartFactory extends ZestFxContentPartFactory {

	@Inject
	private Injector injector;

	@Override
	public IContentPart<Node, ? extends Node> createContentPart(Object content,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
		if (content instanceof org.eclipse.gef4.graph.Node) {
			return injector.getInstance(DotNodePart.class);
		}
		return super.createContentPart(content, contextBehavior, contextMap);
	}
}
