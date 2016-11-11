/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzillas #449129 & #468780
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.ScrollEvent;

/**
 * The {@link PanOrZoomOnScrollPolicy} is an {@link IOnScrollPolicy} that pans
 * (i.e. moves/scrolls) the viewport upon scrolling the mouse wheel.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class PanOrZoomOnScrollPolicy extends AbstractInteractionPolicy
		implements IOnScrollPolicy {

	private boolean stopped = false;
	private ChangeViewportPolicy viewportPolicy;

	@Override
	public void abortScroll() {
		rollback(getViewportPolicy());
		setViewportPolicy(null);
		setStopped(false);
	}

	/**
	 * Computes the translation for the given {@link ScrollEvent}. The
	 * horizontal and vertical translation is inverted when
	 * {@link #isSwapDirection(ScrollEvent)} returns <code>true</code>.
	 *
	 * @param event
	 *            The original {@link ScrollEvent}.
	 * @return A {@link Dimension} storing the horizontal and vertical
	 *         translation.
	 */
	protected Dimension computeDelta(ScrollEvent event) {
		double dx = event.getDeltaX();
		double dy = event.getDeltaY();
		if (isSwapDirection(event)) {
			double t = dx;
			dx = dy;
			dy = t;
		}
		return new Dimension(dx, dy);
	}

	/**
	 * Computes the zoom factor from the given {@link ScrollEvent}.
	 *
	 * @param event
	 *            The {@link ScrollEvent} from which to compute the zoom factor.
	 * @return The zoom factor according to the given {@link ScrollEvent}.
	 */
	protected double computeZoomFactor(ScrollEvent event) {
		return event.getDeltaY() > 0 ? 1.05 : 1 / 1.05;
	}

	/**
	 * Returns the {@link ChangeViewportPolicy} that is to be used for changing
	 * the viewport. This method is called within
	 * {@link #startScroll(ScrollEvent)} where the resulting policy is cached
	 * ({@link #setViewportPolicy(ChangeViewportPolicy)}) for the scroll
	 * gesture.
	 *
	 * @return The {@link ChangeViewportPolicy} that is to be used for changing
	 *         the viewport.
	 */
	protected ChangeViewportPolicy determineViewportPolicy() {
		return getHost().getRoot().getAdapter(ChangeViewportPolicy.class);
	}

	@Override
	public void endScroll() {
		commit(getViewportPolicy());
		setViewportPolicy(null);
		setStopped(false);
	}

	/**
	 * Returns the {@link ChangeViewportPolicy} that is used for changing the
	 * viewport within the current scroll gesture. This policy is set within
	 * {@link #startScroll(ScrollEvent)} to the value determined by
	 * {@link #determineViewportPolicy()}.
	 *
	 * @return The {@link ChangeViewportPolicy} that is used for changing the
	 *         viewport within the current scroll gesture.
	 */
	protected ChangeViewportPolicy getViewportPolicy() {
		return viewportPolicy;
	}

	/**
	 * Returns <code>true</code> to signify that scrolling and zooming is
	 * restricted to the content bounds, <code>false</code> otherwise.
	 * <p>
	 * When content-restricted, the policy behaves texteditor-like, i.e. the
	 * pivot point for zooming is at the top of the viewport and at the left of
	 * the contents, and free space is only allowed to the right and to the
	 * bottom of the contents. Therefore, the policy does not allow panning or
	 * zooming if it would result in free space within the viewport at the top
	 * or left sides of the contents.
	 *
	 * @return <code>true</code> to signify that scrolling and zooming is
	 *         restricted to the content bounds, <code>false</code> otherwise.
	 */
	protected boolean isContentRestricted() {
		return false;
	}

	/**
	 * Returns <code>true</code> if the given {@link ScrollEvent} should trigger
	 * panning. Otherwise returns <code>false</code>.
	 *
	 * @param event
	 *            The {@link ScrollEvent} in question.
	 * @return <code>true</code> to indicate that the given {@link ScrollEvent}
	 *         should trigger panning, otherwise <code>false</code>.
	 */
	protected boolean isPan(ScrollEvent event) {
		// Do not scroll when a modifier key (<Alt>, <Control>, <Meta>) is
		// pressed.
		return !(event.isAltDown() || event.isControlDown()
				|| event.isMetaDown());
	}

	/**
	 * Returns <code>true</code> if panning was stopped for the current scroll
	 * gesture, because further panning would move past the content bounds.
	 * Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if panning was stopped for the current scroll
	 *         gesture, otherwise <code>false</code>.
	 */
	protected boolean isStopped() {
		return stopped;
	}

	/**
	 * Returns <code>true</code> to signify that panning should stop once the
	 * content bounds are hit, <code>false</code> otherwise.
	 *
	 * @return <code>true</code> to signify that panning should stop once the
	 *         content bounds are hit, <code>false</code> otherwise.
	 */
	protected boolean isStoppingAtContentBounds() {
		return !isContentRestricted();
	}

	/**
	 * Returns <code>true</code> if the pan direction should be inverted for the
	 * given {@link ScrollEvent}. Otherwise returns <code>false</code>.
	 *
	 * @param event
	 *            The {@link ScrollEvent} in question.
	 * @return <code>true</code> if the pan direction should be inverted,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isSwapDirection(ScrollEvent event) {
		// Swap horizontal/vertical when the <Shift> key is pressed.
		return event.isShiftDown();
	}

	/**
	 * Returns <code>true</code> if the given {@link ScrollEvent} should trigger
	 * zooming. Otherwise returns <code>false</code>. Per default, either
	 * <code>&lt;Control&gt;</code> or <code>&lt;Alt&gt;</code> has to be
	 * pressed so that <code>true</code> is returned.
	 *
	 * @param event
	 *            The {@link ScrollEvent} in question.
	 * @return <code>true</code> if the given {@link ScrollEvent} should trigger
	 *         zooming, otherwise <code>false</code>.
	 */
	protected boolean isZoom(ScrollEvent event) {
		return event.isControlDown() || event.isAltDown();
	}

	/**
	 * Performs panning according to the given {@link ScrollEvent}.
	 *
	 * @param event
	 *            The {@link ScrollEvent} according to which panning is
	 *            performed.
	 */
	protected void pan(ScrollEvent event) {
		// Determine horizontal and vertical translation.
		Dimension delta = computeDelta(event);
		// stop scrolling at the content-bounds
		if (isStoppingAtContentBounds()) {
			setStopped(stopAtContentBounds(delta));
		}
		// change viewport via operation
		getViewportPolicy().scroll(true, delta.width, delta.height);
		// restrict panning to contents
		if (isContentRestricted()) {
			removeFreeSpaceTopLeft();
			removeFreeSpaceBottomRight();
		}
	}

	/**
	 * Removes any free space within the viewport on the bottom and right sides
	 * of the contents if the contents do not fit into the viewport.
	 */
	protected void removeFreeSpaceBottomRight() {
		// compute free space at top and left sides depending on bounds
		InfiniteCanvas canvas = ((InfiniteCanvasViewer) getHost().getRoot()
				.getViewer()).getCanvas();
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
		double deltaTx = contentBoundsInCanvasLocal
				.getWidth() > viewportBoundsInCanvasLocal.getWidth()
				&& freeSpaceRight > 0 ? freeSpaceRight : 0;
		double deltaTy = contentBoundsInCanvasLocal
				.getHeight() > viewportBoundsInCanvasLocal.getHeight()
				&& freeSpaceBottom > 0 ? freeSpaceBottom : 0;

		// scroll to align content with viewport (if necessary)
		if (deltaTx != 0 || deltaTy != 0) {
			getViewportPolicy().scroll(true, deltaTx, deltaTy);
		}
	}

	/**
	 * Removes any free space within the viewport on the top and left sides of
	 * the contents.
	 */
	protected void removeFreeSpaceTopLeft() {
		// compute free space at top and left sides depending on bounds
		InfiniteCanvas canvas = ((InfiniteCanvasViewer) getHost().getRoot()
				.getViewer()).getCanvas();
		Rectangle viewportBoundsInCanvasLocal = new Rectangle(0, 0,
				canvas.getWidth(), canvas.getHeight());
		Rectangle contentBoundsInCanvasLocal = FX2Geometry
				.toRectangle(canvas.getContentBounds());
		double freeSpaceLeft = contentBoundsInCanvasLocal.getLeft().x
				- viewportBoundsInCanvasLocal.getLeft().x;
		double freeSpaceTop = contentBoundsInCanvasLocal.getTop().y
				- viewportBoundsInCanvasLocal.getTop().y;

		// compute delta tx and ty depending on free space and content size
		// relative to viewport size
		double deltaTx = contentBoundsInCanvasLocal
				.getWidth() <= viewportBoundsInCanvasLocal.getWidth()
				|| contentBoundsInCanvasLocal
						.getWidth() > viewportBoundsInCanvasLocal.getWidth()
						&& freeSpaceLeft > 0 ? -freeSpaceLeft : 0;
		double deltaTy = contentBoundsInCanvasLocal
				.getHeight() <= viewportBoundsInCanvasLocal.getHeight()
				|| contentBoundsInCanvasLocal
						.getHeight() > viewportBoundsInCanvasLocal.getHeight()
						&& freeSpaceTop > 0 ? -freeSpaceTop : 0;

		// scroll to align content with viewport (if necessary)
		if (deltaTx != 0 || deltaTy != 0) {
			getViewportPolicy().scroll(true, deltaTx, deltaTy);
		}
	}

	@Override
	public void scroll(ScrollEvent event) {
		// each event is tested for suitability so that you can switch between
		// multiple scroll actions instantly when pressing/releasing modifiers
		if (isPan(event) && !isStopped()) {
			pan(event);
		} else if (isZoom(event)) {
			zoom(event);
		}
	}

	/**
	 * Sets the stopped flag to the given value. If stopped, this policy will
	 * not perform panning.
	 *
	 * @param stopped
	 *            The new value for the stopped flag.
	 */
	protected void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	/**
	 * Sets the {@link ChangeViewportPolicy} that is used to manipulate the
	 * viewport for the current scroll gesture to the given value.
	 *
	 * @param viewportPolicy
	 *            The new {@link ChangeViewportPolicy} that is to be used to
	 *            manipulate the viewport for the current scroll gesture.
	 */
	protected void setViewportPolicy(ChangeViewportPolicy viewportPolicy) {
		this.viewportPolicy = viewportPolicy;
	}

	@Override
	public void startScroll(ScrollEvent event) {
		setViewportPolicy(determineViewportPolicy());
		init(getViewportPolicy());
		// delegate to scroll() to perform panning/zooming
		scroll(event);
	}

	/**
	 * Determines if the given panning {@link Dimension} would result in panning
	 * past the contents. In this case, the panning {@link Dimension} is
	 * adjusted so that it pans exactly to the border of the contents. Returns
	 * <code>true</code> if the panning {@link Dimension} was adjusted.
	 * Otherwise returns <code>false</code>.
	 *
	 * @param delta
	 *            The panning {@link Dimension}.
	 * @return <code>true</code> if the given panning {@link Dimension} was
	 *         adjusted, otherwise <code>false</code>.
	 */
	protected boolean stopAtContentBounds(Dimension delta) {
		InfiniteCanvas infiniteCanvas = ((InfiniteCanvasViewer) getHost()
				.getRoot().getViewer()).getCanvas();
		Bounds contentBounds = infiniteCanvas.getContentBounds();
		boolean stopped = false;
		if (contentBounds.getMinX() < 0
				&& contentBounds.getMinX() + delta.width >= 0) {
			// If the left side of the content-bounds was left-of the viewport
			// before scrolling and will not be left-of the viewport after
			// scrolling, then the left side of the content-bounds was reached
			// by scrolling. Therefore, scrolling should stop at the left side
			// of the content-bounds now.
			delta.width = -contentBounds.getMinX();
			stopped = true;
		} else if (contentBounds.getMaxX() > infiniteCanvas.getWidth()
				&& contentBounds.getMaxX() + delta.width <= infiniteCanvas
						.getWidth()) {
			// If the right side of the content-bounds was right-of the viewport
			// before scrolling and will not be right-of the viewport after
			// scrolling, then the right side of the content-bounds was reached
			// by scrolling. Therefore, scrolling should stop at the right side
			// of the content-bounds now.
			delta.width = infiniteCanvas.getWidth() - contentBounds.getMaxX();
			stopped = true;
		}
		if (contentBounds.getMinY() < 0
				&& contentBounds.getMinY() + delta.height >= 0) {
			// If the top side of the content-bounds was top-of the
			// viewport before scrolling and will not be top-of the viewport
			// after scrolling, then the top side of the content-bounds was
			// reached by scrolling. Therefore, scrolling should stop at the
			// top side of the content-bounds now.
			delta.height = -contentBounds.getMinY();
			stopped = true;
		} else if (contentBounds.getMaxY() > infiniteCanvas.getHeight()
				&& contentBounds.getMaxY() + delta.height <= infiniteCanvas
						.getHeight()) {
			// If the bottom side of the content-bounds was bottom-of the
			// viewport before scrolling and will not be top-of the viewport
			// after scrolling, then the bottom side of the content-bounds was
			// reached by scrolling. Therefore, scrolling should stop at the
			// bottom side of the content-bounds now.
			delta.height = infiniteCanvas.getHeight() - contentBounds.getMaxY();
			stopped = true;
		}
		return stopped;
	}

	/**
	 * Performs zooming according to the given {@link ScrollEvent}.
	 *
	 * @param event
	 *            The {@link ScrollEvent} according to which zooming is
	 *            performed.
	 */
	protected void zoom(ScrollEvent event) {
		// compute zoom factor from the given event
		double zoomFactor = computeZoomFactor(event);

		if (isContentRestricted()) {
			// Ensure content is aligned with the viewport on the left and top
			// sides if there is free space on these sides and the content fits
			// into the viewport
			removeFreeSpaceTopLeft();

			// calculate a pivot points to achieve a zooming similar to that of
			// a text editor (fix absolute content left in x-direction, fix
			// visible content top in y-direction)
			InfiniteCanvas infiniteCanvas = ((InfiniteCanvasViewer) getHost()
					.getRoot().getViewer()).getCanvas();
			Point2D pivotPointInScene = infiniteCanvas.localToScene(
					infiniteCanvas.getContentBounds().getMinX(), 0);

			// performing zooming
			getViewportPolicy().zoom(true, true, zoomFactor,
					pivotPointInScene.getX(), pivotPointInScene.getY());

			// Ensure content is aligned with the viewport on the right and
			// bottom sides if there is free space on these sides and the
			// content does not fit into the viewport
			removeFreeSpaceBottomRight();
		} else {
			// zoom into/out-of the event location
			getViewportPolicy().zoom(true, true, zoomFactor, event.getSceneX(),
					event.getSceneY());
		}
	}

}
