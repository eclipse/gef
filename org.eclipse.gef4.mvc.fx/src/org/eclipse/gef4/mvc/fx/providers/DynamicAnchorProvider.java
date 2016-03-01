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

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.scene.Node;

/**
 * The {@link DynamicAnchorProvider} is a <code>Provider&lt;IAnchor&gt;</code>
 * implementation that provides an {@link DynamicAnchor} for the host visual.
 *
 * @author anyssen
 *
 */
public class DynamicAnchorProvider implements Provider<IAnchor>,
		IAdaptable.Bound<IVisualPart<Node, ? extends Node>> {

	private IVisualPart<Node, ? extends Node> visualPart;
	private IAnchor anchor;

	@Override
	public IAnchor get() {
		if (anchor == null) {
			anchor = new DynamicAnchor(getAdaptable().getVisual());
		}
		return anchor;
	}

	@Override
	public IVisualPart<Node, ? extends Node> getAdaptable() {
		return visualPart;
	}

	@Override
	public void setAdaptable(IVisualPart<Node, ? extends Node> adaptable) {
		this.visualPart = adaptable;
	}
}