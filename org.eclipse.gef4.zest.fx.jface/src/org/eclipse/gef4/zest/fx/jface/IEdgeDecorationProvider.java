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
package org.eclipse.gef4.zest.fx.jface;

import org.eclipse.gef4.fx.nodes.IFXDecoration;

/**
 * The {@link IEdgeDecorationProvider} can be used to decorate the start or end
 * of an edge with an {@link IFXDecoration} (e.g. arrow head, etc.).
 *
 * @author mwienand
 *
 */
public interface IEdgeDecorationProvider {

	/**
	 * Returns the {@link IFXDecoration} that should be used to decorate the
	 * start (source side) of the edge that is specified by the given
	 * <i>contentSoruceNode</i> and <i>contentTargetNode</i>.
	 *
	 * @param contentSourceNode
	 *            The content {@link Object} that represents the source node of
	 *            the decorated edge.
	 * @param contentTargetNode
	 *            The content {@link Object} that represents the target node of
	 *            the decorated edge.
	 * @return The {@link IFXDecoration} that should be used to decorate the
	 *         start of the specified edge.
	 */
	public IFXDecoration getSourceDecoration(Object contentSourceNode, Object contentTargetNode);

	/**
	 * Returns the {@link IFXDecoration} that should be used to decorate the end
	 * (target side) of the edge that is specified by the given
	 * <i>contentSoruceNode</i> and <i>contentTargetNode</i>.
	 *
	 * @param contentSourceNode
	 *            The content {@link Object} that represents the source node of
	 *            the decorated edge.
	 * @param contentTargetNode
	 *            The content {@link Object} that represents the target node of
	 *            the decorated edge.
	 * @return The {@link IFXDecoration} that should be used to decorate the end
	 *         of the specified edge.
	 */
	public IFXDecoration getTargetDecoration(Object contentSourceNode, Object contentTargetNode);

}
