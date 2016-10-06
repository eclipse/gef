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

import org.eclipse.gef.geometry.planar.AffineTransform;

/**
 *
 *
 * @author wienand
 *
 * @param <VR>
 *            visual root type
 * @param <V>
 *            visual type
 */
public interface ITransformableVisualPart<VR, V extends VR>
		extends IVisualPart<VR, V> {

	/**
	 * Returns the visual of this {@link ITransformableContentPart} to which
	 * transformations should be applied.
	 *
	 * @return The visual of this {@link ITransformableContentPart} to which
	 *         transformations should be applied.
	 */
	public VR getTransformableVisual();

	/**
	 * Returns the current transform according to this
	 * {@link ITransformableVisualPart}'s visual.
	 *
	 * @return The current transform according to this
	 *         {@link ITransformableVisualPart}'s visual.
	 */
	public AffineTransform getVisualTransform();

	/**
	 * Applies the given {@link AffineTransform} to the visual of this
	 * {@link IResizableVisualPart}.
	 *
	 * @param transformation
	 *            The {@link AffineTransform} that is applied to the visual of
	 *            this {@link IResizableVisualPart}.
	 */
	public void transformVisual(AffineTransform transformation);

}
