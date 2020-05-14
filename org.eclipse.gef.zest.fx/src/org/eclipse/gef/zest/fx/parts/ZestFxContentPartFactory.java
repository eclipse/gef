/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.parts;

import java.util.Map;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.zest.fx.ZestProperties;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;
import javafx.util.Pair;

/**
 * The {@link ZestFxContentPartFactory} is a {@link Graph}-specific
 * {@link IContentPartFactory}. It creates {@link GraphPart}s,
 * {@link NodePart}s, and {@link EdgePart}s for the corresponding
 * {@link Graph}s, {@link Node}s, and {@link Edge}s.
 *
 * @author mwienand
 *
 */
public class ZestFxContentPartFactory implements IContentPartFactory {

	@Inject
	private Injector injector;

	@SuppressWarnings("rawtypes")
	@Override
	public IContentPart<? extends Node> createContentPart(Object content, Map<Object, Object> contextMap) {
		IContentPart<? extends Node> part = null;
		if (content instanceof Graph) {
			part = new GraphPart();
		} else if (content instanceof org.eclipse.gef.graph.Node) {
			part = new NodePart();
		} else if (content instanceof Edge) {
			part = new EdgePart();
		} else if (content instanceof Pair && ((Pair) content).getKey() instanceof Edge
				&& (ZestProperties.LABEL__NE.equals(((Pair) content).getValue())
						|| ZestProperties.EXTERNAL_LABEL__NE.equals(((Pair) content).getValue())
						|| ZestProperties.SOURCE_LABEL__E.equals(((Pair) content).getValue())
						|| ZestProperties.TARGET_LABEL__E.equals(((Pair) content).getValue()))) {
			part = new EdgeLabelPart();
		} else if (content instanceof Pair && ((Pair) content).getKey() instanceof org.eclipse.gef.graph.Node
				&& ZestProperties.EXTERNAL_LABEL__NE.equals(((Pair) content).getValue())) {
			part = new NodeLabelPart();
		}
		if (part != null) {
			injector.injectMembers(part);
		}
		return part;
	}

	/**
	 * Access to the injector that is used to initialize the result objects of
	 * {@link #createContentPart(Object, Map)}.
	 *
	 * @return the injector.
	 *
	 * @since 5.1
	 */
	protected Injector getInjector() {
		return injector;
	}

}
