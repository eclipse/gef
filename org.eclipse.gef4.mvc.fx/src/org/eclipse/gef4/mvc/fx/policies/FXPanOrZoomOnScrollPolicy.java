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
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.policies.AbstractInteractionPolicy;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;

/**
 * The {@link FXPanOrZoomOnScrollPolicy} is an {@link IFXOnScrollPolicy} that
 * pans (i.e. moves/scrolls) the viewport upon scrolling the mouse wheel.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class FXPanOrZoomOnScrollPolicy extends AbstractInteractionPolicy<Node>
		implements IFXOnScrollPolicy {

	private boolean stopped = false;
	private FXChangeViewportPolicy viewportPolicy;

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
	 * Returns the {@link FXChangeViewportPolicy} that is to be used for
	 * changing the viewport. This method is called within
	 * {@link #scrollStarted(ScrollEvent)} where the resulting policy is cached
	 * ({@link #setViewportPolicy(FXChangeViewportPolicy)}) for the scroll
	 * gesture.
	 *
	 * @return The {@link FXChangeViewportPolicy} that is to be used for
	 *         changing the viewport.
	 */
	protected FXChangeViewportPolicy determineViewportPolicy() {
		return getHost().getRoot().getAdapter(FXChangeViewportPolicy.class);
	}

	/**
	 * Returns the {@link FXChangeViewportPolicy} that is used for changing the
	 * viewport within the current scroll gesture. This policy is set within
	 * {@link #scrollStarted(ScrollEvent)} to the value determined by
	 * {@link #determineViewportPolicy()}.
	 *
	 * @return The {@link FXChangeViewportPolicy} that is used for changing the
	 *         viewport within the current scroll gesture.
	 */
	protected FXChangeViewportPolicy getViewportPolicy() {
		return viewportPolicy;
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
	protected boolean isPanning(ScrollEvent event) {
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
	protected boolean isZooming(ScrollEvent event) {
		return event.isControlDown() || event.isAltDown();
	}

	@Override
	public void scroll(ScrollEvent event) {
		// each event is tested for suitability so that you can switch between
		// multiple scroll actions instantly when pressing/releasing modifiers
		if (isPanning(event) && !isStopped()) {
			// Determine horizontal and vertical translation.
			Dimension delta = computeDelta(event);
			// Stop scrolling at the content-bounds.
			setStopped(stopAtContentBounds(delta));
			// change viewport via operation
			getViewportPolicy().scrollRelative(delta.width, delta.height);
		} else if (isZooming(event)) {
			getViewportPolicy().zoomRelative(
					event.getDeltaY() > 0 ? 1.05 : 1 / 1.05, event.getSceneX(),
					event.getSceneY());
		}
	}

	@Override
	public void scrollFinished() {
		commit(getViewportPolicy());
		setViewportPolicy(null);
		setStopped(false);
	}

	@Override
	public void scrollStarted(ScrollEvent event) {
		setViewportPolicy(determineViewportPolicy());
		init(getViewportPolicy());
		// delegate to scroll() to perform panning/zooming
		scroll(event);
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
	 * Sets the {@link FXChangeViewportPolicy} that is used to manipulate the
	 * viewport for the current scroll gesture to the given value.
	 *
	 * @param viewportPolicy
	 *            The new {@link FXChangeViewportPolicy} that is to be used to
	 *            manipulate the viewport for the current scroll gesture.
	 */
	protected void setViewportPolicy(FXChangeViewportPolicy viewportPolicy) {
		this.viewportPolicy = viewportPolicy;
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
		InfiniteCanvas infiniteCanvas = ((FXViewer) getHost().getRoot()
				.getViewer()).getCanvas();
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

}
