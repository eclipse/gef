/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzillas #449129 & #468780
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.ScrollEvent;

/**
 * The {@link PanOrZoomOnScrollHandler} is an {@link IOnScrollHandler} that pans
 * (i.e. moves/scrolls) the viewport upon scrolling the mouse wheel.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class PanOrZoomOnScrollHandler extends AbstractHandler
		implements IOnScrollHandler {

	private PanningSupport panningSupport = new PanningSupport();
	private ViewportPolicy viewportPolicy;

	@Override
	public void abortScroll() {
		rollback(viewportPolicy);
		this.viewportPolicy = null;
	}

	/**
	 * Computes the translation for the given {@link ScrollEvent}. The
	 * horizontal and vertical translation is inverted when
	 * {@link #isPanDirectionSwapped(ScrollEvent)} returns <code>true</code>.
	 *
	 * @param event
	 *            The original {@link ScrollEvent}.
	 * @return A {@link Dimension} storing the horizontal and vertical
	 *         translation.
	 */
	protected Dimension computePanTranslation(ScrollEvent event) {
		double dx = event.getDeltaX();
		double dy = event.getDeltaY();
		if (isPanDirectionSwapped(event)) {
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
	 * Returns the {@link ViewportPolicy} that is to be used for changing the
	 * viewport. This method is called within {@link #startScroll(ScrollEvent)}
	 * where the resulting policy is cached for the scroll gesture.
	 *
	 * @return The {@link ViewportPolicy} that is to be used for changing the
	 *         viewport.
	 */
	protected ViewportPolicy determineViewportPolicy() {
		return getHost().getRoot().getAdapter(ViewportPolicy.class);
	}

	@Override
	public void endScroll() {
		commit(viewportPolicy);
		this.viewportPolicy = null;
	}

	/**
	 * Returns the {@link ViewportPolicy} that is used for changing the viewport
	 * within the current scroll gesture. This policy is set within
	 * {@link #startScroll(ScrollEvent)} to the value determined by
	 * {@link #determineViewportPolicy()}.
	 *
	 * @return The {@link ViewportPolicy} that is used for changing the viewport
	 *         within the current scroll gesture.
	 */
	protected final ViewportPolicy getViewportPolicy() {
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
	 * Returns <code>true</code> if the pan direction should be inverted for the
	 * given {@link ScrollEvent}. Otherwise returns <code>false</code>.
	 *
	 * @param event
	 *            The {@link ScrollEvent} in question.
	 * @return <code>true</code> if the pan direction should be inverted,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isPanDirectionSwapped(ScrollEvent event) {
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
		Dimension delta = computePanTranslation(event);
		// change viewport via operation
		viewportPolicy.scroll(true, delta.width, delta.height);
		// restrict panning to contents
		if (isContentRestricted()) {
			panningSupport.removeFreeSpace(viewportPolicy, Pos.TOP_LEFT, true);
			panningSupport.removeFreeSpace(viewportPolicy, Pos.BOTTOM_RIGHT,
					false);
		}
	}

	@Override
	public void scroll(ScrollEvent event) {
		// each event is tested for suitability so that you can switch between
		// multiple scroll actions instantly when pressing/releasing modifiers
		if (isPan(event)) {
			pan(event);
		} else if (isZoom(event)) {
			zoom(event);
		}
	}

	@Override
	public void startScroll(ScrollEvent event) {
		this.viewportPolicy = determineViewportPolicy();
		init(viewportPolicy);
		// delegate to scroll() to perform panning/zooming
		scroll(event);
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
			panningSupport.removeFreeSpace(viewportPolicy, Pos.TOP_LEFT, true);
			// calculate a pivot points to achieve a zooming similar to that of
			// a text editor (fix absolute content left in x-direction, fix
			// visible content top in y-direction)
			InfiniteCanvas infiniteCanvas = (InfiniteCanvas) getHost().getRoot()
					.getViewer().getCanvas();
			// XXX: The pivot point computation needs to be done after free
			// space top/left is removed so that the content-bounds minX
			// coordinate is correct.
			Point2D pivotPointInScene = infiniteCanvas.localToScene(
					infiniteCanvas.getContentBounds().getMinX(), 0);
			// performing zooming
			viewportPolicy.zoom(true, true, zoomFactor,
					pivotPointInScene.getX(), pivotPointInScene.getY());
			// Ensure content is aligned with the viewport on the right and
			// bottom sides if there is free space on these sides and the
			// content does not fit into the viewport
			panningSupport.removeFreeSpace(viewportPolicy, Pos.BOTTOM_RIGHT,
					false);
		} else {
			// zoom into/out-of the event location
			viewportPolicy.zoom(true, true, zoomFactor, event.getSceneX(),
					event.getSceneY());
		}
	}

}
