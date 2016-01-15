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

import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.AbstractRootPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.scene.Node;

/**
 * Abstract base implementation for a JavaFX-specific {@link IRootPart}.
 *
 * @author anyssen
 *
 * @param <N>
 *            The visual {@link Node} used by this {@link AbstractFXRootPart}.
 */
public abstract class AbstractFXRootPart<N extends Node>
		extends AbstractRootPart<Node, N> {

	/**
	 * Constructs a new {@link AbstractFXRootPart}.
	 */
	public AbstractFXRootPart() {
		super();
	}

	@Override
	public FXViewer getViewer() {
		return (FXViewer) super.getViewer();
	}

	@Override
	protected void registerAtVisualPartMap(IViewer<Node> viewer, N visual) {
		// register "main" visual for this part
		super.registerAtVisualPartMap(viewer, visual);
		// register nested visuals that are not controlled by other parts
		FXPartUtils.registerNestedVisuals(this, viewer.getVisualPartMap(),
				visual);
	}

	@Override
	public void setAdaptable(IViewer<Node> viewer) {
		if (viewer != null && !(viewer instanceof FXViewer)) {
			throw new IllegalArgumentException(
					"Adaptable needs to be of type FXViewer, but is of type "
							+ viewer.getClass().getName());
		}
		super.setAdaptable(viewer);
	}

	@Override
	protected void unregisterFromVisualPartMap(IViewer<Node> viewer, N visual) {
		// unregister "main" visual for this part
		super.unregisterFromVisualPartMap(viewer, visual);
		// unregister nested visuals that are not controlled by other parts
		FXPartUtils.unregisterNestedVisuals(this, viewer.getVisualPartMap(),
				visual);
	}

}