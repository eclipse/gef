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
package org.eclipse.gef.mvc.parts;

import org.eclipse.gef.geometry.planar.Dimension;

/**
 * An {@link IContentPart} that supports content related resize.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this
 *            {@link IResizableContentPart} is used in, e.g. javafx.scene.Node
 *            in case of JavaFX.
 *
 * @param <V>
 *            The visual node used by this {@link IResizableContentPart}.
 *
 */
public interface IResizableContentPart<VR, V extends VR>
		extends IResizableVisualPart<VR, V> {

	// TODO: Refresh
	// Returns the current size according to this part's content.
	// public Dimension getContentSize();

	/**
	 * Resizes the content element as specified by the given {@link Dimension}.
	 *
	 * @param size
	 *            The new size.
	 */
	public void resizeContent(Dimension size);

}
