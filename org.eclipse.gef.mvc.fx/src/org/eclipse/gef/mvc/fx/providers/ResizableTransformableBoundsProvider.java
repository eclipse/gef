/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.providers;

import java.util.List;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint;
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
		Bounds boundsInParent = part.getVisual().getBoundsInLocal();// getBoundsInParent();

		// determine x and y offset
		double x, y;
		if (part instanceof IBendableContentPart) {
			// return null if there are no free bend points
			boolean isEmpty = true;
			List<BendPoint> bendPoints = ((IBendableContentPart<?>) part)
					.getVisualBendPoints();
			for (BendPoint bp : bendPoints) {
				if (!bp.isAttached()) {
					isEmpty = false;
					break;
				}
			}
			if (isEmpty) {
				return null;
			}

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
		return // FX2Geometry.toRectangle(part.getVisual().parentToLocal(
				// Geometry2FX.toFXBounds(
		new Rectangle(x, y, w, h);
		// )));
	}

}
