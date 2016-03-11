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
package org.eclipse.gef4.zest.fx.parts;

import java.util.Map;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.zest.fx.ZestProperties;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;
import javafx.util.Pair;

/**
 * The {@link ZestFxContentPartFactory} is a {@link Graph}-specific
 * {@link IContentPartFactory}. It creates {@link GraphContentPart}s,
 * {@link NodeContentPart}s, and {@link EdgeContentPart}s for the corresponding
 * {@link Graph}s, {@link Node}s, and {@link Edge}s.
 *
 * @author mwienand
 *
 */
public class ZestFxContentPartFactory implements IContentPartFactory<Node> {

	@Inject
	private Injector injector;

	@SuppressWarnings("rawtypes")
	@Override
	public IContentPart<Node, ? extends Node> createContentPart(Object content, IBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {
		IContentPart<Node, ? extends Node> part = null;
		if (content instanceof Graph) {
			part = new GraphContentPart();
		} else if (content instanceof org.eclipse.gef4.graph.Node) {
			part = new NodeContentPart();
		} else if (content instanceof Edge) {
			part = new EdgeContentPart();
		} else if (content instanceof Pair && ((Pair) content).getKey() instanceof Edge
				&& (ZestProperties.ELEMENT_LABEL.equals(((Pair) content).getValue())
						|| ZestProperties.ELEMENT_EXTERNAL_LABEL.equals(((Pair) content).getValue())
						|| ZestProperties.EDGE_SOURCE_LABEL.equals(((Pair) content).getValue())
						|| ZestProperties.EDGE_TARGET_LABEL.equals(((Pair) content).getValue()))) {
			part = new EdgeLabelPart();
		} else if (content instanceof Pair && ((Pair) content).getKey() instanceof org.eclipse.gef4.graph.Node
				&& ZestProperties.ELEMENT_EXTERNAL_LABEL.equals(((Pair) content).getValue())) {
			part = new NodeExternalLabelPart();
		}
		if (part != null) {
			injector.injectMembers(part);
		}
		return part;
	}

}
