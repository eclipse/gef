/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.Dimension;

import javafx.scene.Node;

/**
 * An {@link IContentPart} that supports content related resize.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual node used by this {@link IResizableContentPart}.
 *
 */
public interface IResizableContentPart<V extends Node> extends IContentPart<V> {

	// TODO: Refresh
	// Returns the current size according to this part's content.
	// public Dimension getContentSize();

	/**
	 * Returns the visual of this {@link IResizableContentPart} that should be
	 * used for resizing.
	 *
	 * @return The visual of this {@link IResizableContentPart} that should be
	 *         used for resizing.
	 */
	public default Node getResizableVisual() {
		return getVisual();
	}

	/**
	 * Returns the current size according to this
	 * {@link IResizableContentPart}'s visual.
	 *
	 * @return The current size according to this
	 *         {@link IResizableContentPart}'s visual.
	 */
	public default Dimension getVisualSize() {
		return NodeUtils.getShapeBounds(getResizableVisual()).getSize();
	}

	/**
	 * Resizes the content element as specified by the given {@link Dimension}.
	 *
	 * @param totalSize
	 *            The new size.
	 */
	public void resizeContent(Dimension totalSize);

	/**
	 * Resizes the visual of this {@link IResizableContentPart} to the given
	 * size.
	 *
	 * @param totalSize
	 *            The new size for this {@link IResizableContentPart}'s visual.
	 */
	public default void resizeVisual(Dimension totalSize) {
		getResizableVisual().resize(totalSize.width, totalSize.height);
	}

}
