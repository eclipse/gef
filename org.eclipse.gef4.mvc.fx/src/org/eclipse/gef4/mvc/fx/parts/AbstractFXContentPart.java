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

import java.util.Map;

import javafx.scene.Node;
import javafx.scene.Parent;

import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public abstract class AbstractFXContentPart<V extends Node> extends
		AbstractContentPart<Node, V> {

	@Override
	protected void registerAtVisualPartMap(IViewer<Node> viewer, V visual) {
		// register "main" visual for this part
		super.registerAtVisualPartMap(viewer, visual);
		// register nested visuals that are not controlled by other parts
		if (visual instanceof Parent) {
			registerNestedVisuals(viewer.getVisualPartMap(), (Parent) visual);
		}
	}

	/**
	 * Registers this {@link AbstractFXContentPart} for all visuals in the
	 * visual hierarchy of the given {@link Parent} at the given
	 * <i>visualPartMap</i>.
	 *
	 * @param visualPartMap
	 *            The map where the visuals are registered.
	 * @param parent
	 *            The {@link Parent} whose visual hierarchy is registered.
	 */
	protected void registerNestedVisuals(
			Map<Node, IVisualPart<Node, ? extends Node>> visualPartMap,
			Parent parent) {
		for (Node nestedVisual : parent.getChildrenUnmodifiable()) {
			if (!visualPartMap.containsKey(nestedVisual)) {
				visualPartMap.put(nestedVisual, this);
				if (nestedVisual instanceof Parent) {
					registerNestedVisuals(visualPartMap, (Parent) nestedVisual);
				}
			}
		}
	}

	@Override
	protected void unregisterFromVisualPartMap(
			org.eclipse.gef4.mvc.viewer.IViewer<Node> viewer, V visual) {
		// unregister "main" visual for this part
		super.unregisterFromVisualPartMap(viewer, visual);
		// unregister nested visuals that are not controlled by other parts
		if (visual instanceof Parent) {
			unregisterNestedVisuals(viewer.getVisualPartMap(), (Parent) visual);
		}
	}

	/**
	 * Removes all visuals in the visual hierarchy of the given {@link Parent}
	 * from the given <i>visualPartMap</i> where this
	 * {@link AbstractFXContentPart} is registered.
	 *
	 * @param visualPartMap
	 *            The map from which the visuals are removed.
	 * @param parent
	 *            The {@link Parent} whose visual hierarchy is unregistered.
	 */
	protected void unregisterNestedVisuals(
			Map<Node, IVisualPart<Node, ? extends Node>> visualPartMap,
			Parent parent) {
		for (Node nestedVisual : parent.getChildrenUnmodifiable()) {
			if (visualPartMap.containsKey(nestedVisual)
					&& visualPartMap.get(nestedVisual) == this) {
				visualPartMap.remove(nestedVisual);
				if (nestedVisual instanceof Parent) {
					unregisterNestedVisuals(visualPartMap,
							(Parent) nestedVisual);
				}
			}
		}
	}

}
