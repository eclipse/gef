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
package org.eclipse.gef.mvc.fx.providers;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IResizableContentPart;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link ResizableTransformableBoundsProvider} returns bounds according to
 * the size and transformation as returned by the part API, i.e.
 * {@link IResizableContentPart} and {@link ITransformableContentPart}.
 *
 * @author wienand
 *
 */
public class ResizableTransformableBoundsProvider
		extends IAdaptable.Bound.Impl<IVisualPart<? extends Node>>
		implements Provider<IGeometry> {

	@Override
	public IGeometry get() {
		IVisualPart<? extends Node> part = getAdaptable();
		Bounds boundsInParent = part.getVisual().getBoundsInParent();

		// determine x and y offset
		double x, y;
		if (part instanceof IBendableContentPart) {
			// TODO: generalize for ITransformableContentPart (transform corner
			// points of local bounds to scene and take axis parallel bounds
			// around that)
			Affine visualTransform = ((ITransformableContentPart<? extends Node>) part)
					.getVisualTransform();
			x = visualTransform.getTx();
			y = visualTransform.getTy();
		} else {
			x = boundsInParent.getMinX();
			y = boundsInParent.getMinY();
		}

		// determine width and height
		double w, h;
		if (part instanceof IBendableContentPart) {
			// TODO: generalize for IResizableContentPart (transform corner
			// points of local bounds to scene and take axis parallel bounds
			// around that)
			Dimension visualSize = ((IResizableContentPart<? extends Node>) part)
					.getVisualSize();
			w = visualSize.width;
			h = visualSize.height;
		} else {
			w = boundsInParent.getWidth();
			h = boundsInParent.getHeight();
		}

		// construct bounds and transform to local
		return FX2Geometry.toRectangle(part.getVisual().parentToLocal(
				Geometry2FX.toFXBounds(new Rectangle(x, y, w, h))));
	}

}
