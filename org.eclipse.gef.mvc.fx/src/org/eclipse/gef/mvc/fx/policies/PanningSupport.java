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
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

/**
 * The {@link PanningSupport} can be used by an {@link IPolicy} to compute
 * panning translations that align the contents with the viewport.
 *
 * @author wienand
 *
 */
public class PanningSupport {

	private IPolicy hostPolicy;

	/**
	 * @param policy
	 *            The host {@link IPolicy} for which panning support is
	 *            provided.
	 */
	public PanningSupport(IPolicy policy) {
		this.hostPolicy = policy;
	}

	/**
	 * Returns a {@link Dimension} that stores the pan translation that needs to
	 * be applied in order to remove any free space within the viewport on the
	 * bottom and right sides of the contents (iff the contents do not fit into
	 * the viewport).
	 *
	 * @return A {@link Dimension} that stores the pan translation that needs to
	 *         be applied in order to remove any free space within the viewport
	 *         on the bottom and right sides of the contents.
	 */
	public Dimension computePanTranslationForBottomRightAlignment() {
		return computePanTranslationForBottomRightAlignment(getInfiniteCanvas(),
				false);
	}

	/**
	 * Returns a {@link Dimension} that stores the pan translation that needs to
	 * be applied in order to remove any free space within the viewport on the
	 * bottom and right sides of the contents.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} for which to compute the pan
	 *            translation for bottom/right alignment.
	 * @param contentsMayFit
	 *            <code>true</code> to indicate that bottom/right alignment
	 *            translation should also be computed if the contents fit into
	 *            the viewport, <code>false</code> otherwise.
	 * @return A {@link Dimension} storing the pan translation for bottom/right
	 *         alignment.
	 */
	protected Dimension computePanTranslationForBottomRightAlignment(
			InfiniteCanvas canvas, boolean contentsMayFit) {
		// compute free space at bottom and right sides depending on bounds
		Rectangle viewportBoundsInCanvasLocal = new Rectangle(0, 0,
				canvas.getWidth(), canvas.getHeight());
		Rectangle contentBoundsInCanvasLocal = FX2Geometry
				.toRectangle(canvas.getContentBounds());

		// compute delta tx and ty depending on free space and content size
		// relative to viewport size
		double freeSpaceRight = viewportBoundsInCanvasLocal.getRight().x
				- contentBoundsInCanvasLocal.getRight().x;

		double freeSpaceBottom = viewportBoundsInCanvasLocal.getBottom().y
				- contentBoundsInCanvasLocal.getBottom().y;

		double deltaTx = contentsMayFit
				&& contentBoundsInCanvasLocal
						.getWidth() <= viewportBoundsInCanvasLocal.getWidth()
				|| !contentsMayFit && contentBoundsInCanvasLocal
						.getWidth() > viewportBoundsInCanvasLocal.getWidth()
						&& freeSpaceRight > 0 ? freeSpaceRight : 0;

		double deltaTy = contentsMayFit
				&& contentBoundsInCanvasLocal
						.getHeight() <= viewportBoundsInCanvasLocal.getHeight()
				|| !contentsMayFit && contentBoundsInCanvasLocal
						.getHeight() > viewportBoundsInCanvasLocal.getHeight()
						&& freeSpaceBottom > 0 ? freeSpaceBottom : 0;
		return new Dimension(deltaTx, deltaTy);
	}

	/**
	 * Returns a {@link Dimension} that stores the pan translation that needs to
	 * be applied in order to remove any free space within the viewport on the
	 * top and left sides of the contents (iff the contents do fit into the
	 * viewport).
	 *
	 * @return A {@link Dimension} that stores the pan translation that needs to
	 *         be applied in order to remove any free space within the viewport
	 *         on the top and left sides of the contents.
	 */
	public Dimension computePanTranslationForTopLeftAlignment() {
		return computePanTranslationForTopLeftAlignment(getInfiniteCanvas(),
				true);
	}

	/**
	 * Returns a {@link Dimension} that stores the pan translation that needs to
	 * be applied in order to remove any free space within the viewport on the
	 * top and left sides of the contents.
	 *
	 * @param canvas
	 *            The {@link InfiniteCanvas} for which to compute the pan
	 *            translation for top/left alignment.
	 * @param contentsMayFit
	 *            <code>true</code> to indicate that top/left alignment
	 *            translation should also be computed if the contents fit into
	 *            the viewport, <code>false</code> otherwise.
	 * @return A {@link Dimension} storing the pan translation for top/left
	 *         alignment.
	 */
	protected Dimension computePanTranslationForTopLeftAlignment(
			InfiniteCanvas canvas, boolean contentsMayFit) {
		// compute free space at top and left sides depending on bounds
		Rectangle viewportBoundsInCanvasLocal = new Rectangle(0, 0,
				canvas.getWidth(), canvas.getHeight());

		Rectangle contentBoundsInCanvasLocal = FX2Geometry
				.toRectangle(canvas.getContentBounds());

		// compute delta tx and ty depending on free space and content size
		// relative to viewport size

		double freeSpaceLeft = contentBoundsInCanvasLocal.getLeft().x
				- viewportBoundsInCanvasLocal.getLeft().x;

		double freeSpaceTop = contentBoundsInCanvasLocal.getTop().y
				- viewportBoundsInCanvasLocal.getTop().y;

		double deltaTx = contentsMayFit
				&& contentBoundsInCanvasLocal
						.getWidth() <= viewportBoundsInCanvasLocal.getWidth()
				|| !contentsMayFit && contentBoundsInCanvasLocal
						.getWidth() > viewportBoundsInCanvasLocal.getWidth()
						&& freeSpaceLeft > 0 ? -freeSpaceLeft : 0;

		double deltaTy = contentsMayFit
				&& contentBoundsInCanvasLocal
						.getHeight() <= viewportBoundsInCanvasLocal.getHeight()
				|| !contentsMayFit && contentBoundsInCanvasLocal
						.getHeight() > viewportBoundsInCanvasLocal.getHeight()
						&& freeSpaceTop > 0 ? -freeSpaceTop : 0;

		return new Dimension(deltaTx, deltaTy);
	}

	/**
	 * Returns the host {@link IPolicy} for this {@link PanningSupport}.
	 *
	 * @return The host {@link IPolicy} for this {@link PanningSupport}.
	 */
	public IPolicy getHostPolicy() {
		return hostPolicy;
	}

	/**
	 * Returns the {@link InfiniteCanvas} for the {@link #getHostPolicy() host
	 * policy} of this {@link PanningSupport}.
	 *
	 * @return the {@link InfiniteCanvas} for the {@link #getHostPolicy() host
	 *         policy} of this {@link PanningSupport}.
	 */
	protected InfiniteCanvas getInfiniteCanvas() {
		return ((InfiniteCanvasViewer) hostPolicy.getAdaptable().getRoot()
				.getViewer()).getCanvas();
	}

}
