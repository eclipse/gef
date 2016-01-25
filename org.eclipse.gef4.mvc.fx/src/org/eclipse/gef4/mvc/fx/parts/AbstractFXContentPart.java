/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.scene.Node;

/**
 * The {@link AbstractFXContentPart} is an {@link IContentPart} implementation
 * that binds the VR type parameter (visual root type) to {@link Node}.
 * Furthermore, it implements the un-/registration of visuals at the visual part
 * map of the {@link IViewer}.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual {@link Node} used by this {@link AbstractFXContentPart}
 *            .
 */
public abstract class AbstractFXContentPart<V extends Node>
		extends AbstractContentPart<Node, V> {

	@Override
	protected void registerAtVisualPartMap(IViewer<Node> viewer, V visual) {
		// register "main" visual for this part
		super.registerAtVisualPartMap(viewer, visual);
		// register nested visuals that are not controlled by other parts
		FXPartUtils.registerNestedVisuals(this, viewer.getVisualPartMap(),
				visual);
	}

	@Override
	protected void unregisterFromVisualPartMap(IViewer<Node> viewer, V visual) {
		// unregister "main" visual for this part
		super.unregisterFromVisualPartMap(viewer, visual);
		// unregister all visuals for which we are registered
		FXPartUtils.unregisterVisuals(this, viewer.getVisualPartMap());
	}

}
