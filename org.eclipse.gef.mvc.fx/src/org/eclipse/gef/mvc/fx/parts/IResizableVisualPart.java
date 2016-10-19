/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.Dimension;

import javafx.scene.Node;

/**
 * The {@link IResizableVisualPart} defines methods that can be used to resize
 * an {@link IVisualPart}.
 *
 * @author wienand
 *
 * @param <V>
 *            visual type
 */
// TODO: Merge into IResizableContentPart.
public interface IResizableVisualPart<V extends Node> extends IVisualPart<V> {

	/**
	 * Returns the visual of this {@link IResizableVisualPart} that should be
	 * used for resizing.
	 *
	 * @return The visual of this {@link IResizableVisualPart} that should be
	 *         used for resizing.
	 */
	public default Node getResizableVisual() {
		return getVisual();
	}

	/**
	 * Returns the current size according to this {@link IResizableVisualPart}'s
	 * visual.
	 *
	 * @return The current size according to this {@link IResizableVisualPart}'s
	 *         visual.
	 */
	public default Dimension getVisualSize() {
		return NodeUtils.getShapeBounds(getResizableVisual()).getSize();
	}

	/**
	 * Resizes the visual of this {@link IResizableVisualPart} to the given
	 * size.
	 *
	 * @param size
	 *            The new size for this {@link IResizableVisualPart}'s visual.
	 */
	public default void resizeVisual(Dimension size) {
		getResizableVisual().resize(size.width, size.height);
	}

}
