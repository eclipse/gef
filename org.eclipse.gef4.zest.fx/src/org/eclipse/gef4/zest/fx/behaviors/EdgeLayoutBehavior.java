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
package org.eclipse.gef4.zest.fx.behaviors;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.parts.EdgePart;

import javafx.scene.Node;

/**
 * The {@link EdgeLayoutBehavior} is an {@link EdgePart}-specific
 * {@link AbstractLayoutBehavior} implementation.
 *
 * @author mwienand
 *
 */
// only applicable for EdgePart (see #getHost())
public class EdgeLayoutBehavior extends AbstractLayoutBehavior {

	@Override
	protected void adaptFromLayout() {
	}

	@Override
	protected GraphLayoutBehavior getGraphLayoutBehavior() {
		IContentPart<Node, ? extends Node> graphPart = getHost().getRoot().getViewer().getContentPartMap()
				.get(getHost().getContent().getGraph());
		return graphPart.getAdapter(GraphLayoutBehavior.class);
	}

	@Override
	public EdgePart getHost() {
		return (EdgePart) super.getHost();
	}

	@Override
	protected void provideToLayout() {
	}

}
