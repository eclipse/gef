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
package org.eclipse.gef4.mvc.fx.parts;

import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

public class ChopBoxAnchorProvider implements Provider<IFXAnchor>,
		IAdaptable.Bound<IVisualPart<Node, ? extends Node>> {

	private IVisualPart<Node, ? extends Node> visualPart;
	private IFXAnchor anchor;

	@Override
	public IFXAnchor get() {
		if (anchor == null) {
			anchor = new FXChopBoxAnchor(getAdaptable().getVisual());
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