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

import org.eclipse.gef.geometry.planar.AffineTransform;

import javafx.scene.Node;

/**
 * An {@link IContentPart} that supports content related transformations.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual node used by this {@link ITransformableContentPart}.
 *
 */
public interface ITransformableContentPart<V extends Node>
		extends ITransformableVisualPart<V> {

	/**
	 * Transform the content element as specified by the given
	 * {@link AffineTransform}.
	 *
	 * @param transform
	 *            The {@link AffineTransform} to apply.
	 */
	public void transformContent(AffineTransform transform);

}
