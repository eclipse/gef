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
package org.eclipse.gef.mvc.parts;

import org.eclipse.gef.geometry.planar.Dimension;

/**
 * The {@link IResizableVisualPart} defines methods that can be used to resize
 * an {@link IVisualPart}.
 *
 * @author wienand
 *
 * @param <VR>
 *            visual root type
 * @param <V>
 *            visual type
 */
public interface IResizableVisualPart<VR, V extends VR>
		extends IVisualPart<VR, V> {

	/**
	 * Returns the visual of this {@link IResizableVisualPart} that should be
	 * used for resizing.
	 *
	 * @return The visual of this {@link IResizableVisualPart} that should be
	 *         used for resizing.
	 */
	public VR getResizableVisual();

	/**
	 * Returns the current size according to this {@link IResizableVisualPart}'s
	 * visual.
	 *
	 * @return The current size according to this {@link IResizableVisualPart}'s
	 *         visual.
	 */
	public Dimension getVisualSize();

	/**
	 * Resizes the visual of this {@link IResizableVisualPart} to the given
	 * size.
	 *
	 * @param size
	 *            The new size for this {@link IResizableVisualPart}'s visual.
	 */
	public void resizeVisual(Dimension size);

}
