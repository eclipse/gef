/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #504480
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.Dimension;

import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * An {@link IContentPart} that supports content related resize.
 *
 * @author anyssen
 * @author mwienand
 *
 * @param <V>
 *            The visual node used by this {@link IResizableContentPart}.
 *
 */
public interface IResizableContentPart<V extends Node> extends IContentPart<V> {

	/**
	 * Returns the current size according to this part's content.
	 *
	 * @return The current size according to this part's content.
	 */
	public Dimension getContentSize();

	/**
	 * Returns the current size according to this {@link IResizableContentPart}
	 * 's visual.
	 *
	 * @return The current size according to this {@link IResizableContentPart}
	 *         's visual.
	 */
	public default Dimension getVisualSize() {
		return NodeUtils.getShapeBounds(getVisual()).getSize();
	}

	/**
	 * Resizes the content element as specified by the given {@link Dimension}.
	 *
	 * @param totalSize
	 *            The new size.
	 */
	public void setContentSize(Dimension totalSize);

	/**
	 * Resizes the visual of this {@link IResizableContentPart} to the given
	 * size.
	 *
	 * @param totalSize
	 *            The new size for this {@link IResizableContentPart}'s visual.
	 */
	public default void setVisualSize(Dimension totalSize) {
		if (getVisual() instanceof Region) {
			// A Region should not be resized directly. Instead, its size
			// constraints should be adjusted so that it will be resized to the
			// desired size during the next layout pass.
			((Region) getVisual()).setPrefSize(totalSize.width,
					totalSize.height);
			((Region) getVisual()).autosize();
		} else {
			// resize manually
			getVisual().resize(totalSize.width, totalSize.height);
		}
	}
}
