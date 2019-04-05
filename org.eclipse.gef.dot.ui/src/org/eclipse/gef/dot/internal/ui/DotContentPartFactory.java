/************************************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #538226)
 *     Zoey Prigge    (itemis AG) - DotGraphView: FontName support (bug #541056)
 *
 ***********************************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import java.util.Map;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.EdgePart;
import org.eclipse.gef.zest.fx.parts.GraphPart;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;
import javafx.util.Pair;

/**
 * The implementation of this class is mainly taken from the
 * org.eclipse.gef.zest.fx.parts.ZestFxContentPartFactory java class.
 * 
 * Modification added: usage of DotEdgeLabelPart instead of EdgeLabelPart.
 */
public class DotContentPartFactory implements IContentPartFactory {

	@Inject
	private Injector injector;

	@SuppressWarnings("rawtypes")
	@Override
	public IContentPart<? extends Node> createContentPart(Object content,
			Map<Object, Object> contextMap) {
		IContentPart<? extends Node> part = null;
		if (content instanceof Graph) {
			part = new GraphPart();
		} else if (content instanceof org.eclipse.gef.graph.Node) {
			part = new DotNodePart();
		} else if (content instanceof Edge) {
			part = new EdgePart();
		} else if (content instanceof Pair
				&& ((Pair) content).getKey() instanceof Edge
				&& (ZestProperties.LABEL__NE.equals(((Pair) content).getValue())
						|| ZestProperties.EXTERNAL_LABEL__NE
								.equals(((Pair) content).getValue())
						|| ZestProperties.SOURCE_LABEL__E
								.equals(((Pair) content).getValue())
						|| ZestProperties.TARGET_LABEL__E
								.equals(((Pair) content).getValue()))) {
			part = new DotEdgeLabelPart();
		} else if (content instanceof Pair
				&& ((Pair) content)
						.getKey() instanceof org.eclipse.gef.graph.Node
				&& ZestProperties.EXTERNAL_LABEL__NE
						.equals(((Pair) content).getValue())) {
			part = new DotNodeLabelPart();
		}
		if (part != null) {
			// TODO: use injector to create parts
			injector.injectMembers(part);
		}
		return part;
	}
}
