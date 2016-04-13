/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.providers;

import org.eclipse.gef4.common.adapt.AbstractBoundProvider;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.scene.Node;

/**
 * The {@link DynamicAnchorProvider} is a <code>Provider&lt;IAnchor&gt;</code>
 * implementation that provides an {@link DynamicAnchor} for the host visual.
 *
 * @author anyssen
 *
 */
public class DynamicAnchorProvider extends
		AbstractBoundProvider<IAnchor, IVisualPart<Node, ? extends Node>> {

	private IAnchor anchor;

	/**
	 * Creates a new dynamic anchor to be provided.
	 *
	 * @return A new {@link DynamicAnchor}.
	 */
	protected DynamicAnchor createAnchor() {
		return new DynamicAnchor(getAdaptable().getVisual());
	}

	@Override
	public IAnchor get() {
		if (anchor == null) {
			anchor = createAnchor();
		}
		return anchor;
	}

}