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
package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

/**
 * The {@link PanningSupport} can be used to compute panning translations that
 * align the contents with the viewport.
 */
public class PanningSupport {

	/**
	 * Removes free space between the contents and the viewport for the sides
	 * specified by the given {@link Pos}.
	 *
	 * @param viewportPolicy
	 *            The {@link ViewportPolicy} that is used to remove free space.
	 * @param orientation
	 *            The orientation {@link Pos} that specifies the sides where
	 *            free space is reduced, or the contents is aligned,
	 *            respectively.
	 * @param alignIfContentsFit
	 *            <code>true</code> to indicate that contents are aligned with
	 *            the given {@link Pos} if they completely fit into the
	 *            viewport, <code>false</code> to indicate that contents are not
	 *            aligned, but only free space is reduced if the contents do not
	 *            fit into the viewport.
	 */
	public void removeFreeSpace(ViewportPolicy viewportPolicy, Pos orientation,
			boolean alignIfContentsFit) {
		InfiniteCanvas canvas = (InfiniteCanvas) viewportPolicy.getAdaptable()
				.getRoot().getViewer().getCanvas();
		// determine contents and viewport bounds
		Rectangle viewportBoundsInCanvasLocal = new Rectangle(0, 0,
				canvas.getWidth(), canvas.getHeight());
		Rectangle contentBoundsInCanvasLocal = FX2Geometry
				.toRectangle(canvas.getContentBounds());

		// compute translation based on given alignment position, free space,
		// and contents-may-fit flag
		HPos hpos = orientation.getHpos();
		double deltaTx = 0;
		if (hpos != null) {
			if (HPos.RIGHT.equals(hpos)) {
				double freeSpaceRight = viewportBoundsInCanvasLocal.getRight().x
						- contentBoundsInCanvasLocal.getRight().x;
				deltaTx = alignIfContentsFit && contentBoundsInCanvasLocal
						.getWidth() <= viewportBoundsInCanvasLocal.getWidth()
						|| contentBoundsInCanvasLocal
								.getWidth() > viewportBoundsInCanvasLocal
										.getWidth()
								&& freeSpaceRight > 0 ? freeSpaceRight : 0;
			} else if (HPos.LEFT.equals(hpos)) {
				double freeSpaceLeft = contentBoundsInCanvasLocal.getLeft().x
						- viewportBoundsInCanvasLocal.getLeft().x;
				deltaTx = alignIfContentsFit && contentBoundsInCanvasLocal
						.getWidth() <= viewportBoundsInCanvasLocal.getWidth()
						|| contentBoundsInCanvasLocal
								.getWidth() > viewportBoundsInCanvasLocal
										.getWidth()
								&& freeSpaceLeft > 0 ? -freeSpaceLeft : 0;
			}
			// TODO: HPos.CENTER
		}

		VPos vpos = orientation.getVpos();
		double deltaTy = 0;
		if (vpos != null) {
			if (VPos.BOTTOM.equals(vpos)) {
				double freeSpaceBottom = viewportBoundsInCanvasLocal
						.getBottom().y
						- contentBoundsInCanvasLocal.getBottom().y;
				deltaTy = alignIfContentsFit && contentBoundsInCanvasLocal
						.getHeight() <= viewportBoundsInCanvasLocal.getHeight()
						|| contentBoundsInCanvasLocal
								.getHeight() > viewportBoundsInCanvasLocal
										.getHeight()
								&& freeSpaceBottom > 0 ? freeSpaceBottom : 0;
			} else if (VPos.TOP.equals(vpos)) {
				double freeSpaceTop = contentBoundsInCanvasLocal.getTop().y
						- viewportBoundsInCanvasLocal.getTop().y;
				deltaTy = alignIfContentsFit && contentBoundsInCanvasLocal
						.getHeight() <= viewportBoundsInCanvasLocal.getHeight()
						|| contentBoundsInCanvasLocal
								.getHeight() > viewportBoundsInCanvasLocal
										.getHeight()
								&& freeSpaceTop > 0 ? -freeSpaceTop : 0;
			}
			// TODO: VPos.CENTER
		}
		if (deltaTx != 0 || deltaTy != 0) {
			viewportPolicy.scroll(true, deltaTx, deltaTy);
		}
	}
}
