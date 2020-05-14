/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.ZoomEvent;

/**
 * An {@link IOnPinchSpreadHandler} that performs zooming.
 *
 * @author anyssen
 *
 */
public class ZoomOnPinchSpreadHandler extends AbstractHandler
		implements IOnPinchSpreadHandler {

	private PanningSupport panningSupport = new PanningSupport();

	// gesture validity
	private boolean invalidGesture = false;

	private ViewportPolicy viewportPolicy;

	@Override
	public void abortZoom() {
		if (invalidGesture) {
			return;
		}
		rollback(viewportPolicy);
		viewportPolicy = null;
	}

	/**
	 * Computes the zoom factor from the given {@link ZoomEvent}.
	 *
	 * @param event
	 *            The {@link ZoomEvent} from which to compute the zoom factor.
	 * @return The zoom factor according to the given {@link ZoomEvent}.
	 */
	protected double computeZoomFactor(ZoomEvent event) {
		return event.getZoomFactor();
	}

	/**
	 * Determines the {@link ViewportPolicy} that is used by this policy.
	 *
	 * @return The {@link ViewportPolicy} that is used by this policy.
	 */
	protected ViewportPolicy determineViewportPolicy() {
		return getHost().getRoot().getAdapter(ViewportPolicy.class);
	}

	@Override
	public void endZoom(ZoomEvent event) {
		if (invalidGesture) {
			return;
		}
		ITransactionalOperation commit = getViewportPolicy().commit();
		if (commit != null && !commit.isNoOp()) {
			try {
				getHost().getRoot().getViewer().getDomain().execute(commit,
						new NullProgressMonitor());
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Returns the {@link ViewportPolicy} that is used by this policy.
	 *
	 * @return The {@link ViewportPolicy} that is used by this policy.
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
	 * Returns whether the given {@link ZoomEvent} should trigger zooming. Per
	 * default, will always return <code>true</code>.
	 *
	 * @param event
	 *            The {@link ZoomEvent} in question.
	 * @return <code>true</code> if the given {@link ZoomEvent} should trigger
	 *         zoom, otherwise <code>false</code>.
	 */
	protected boolean isZoom(ZoomEvent event) {
		return true;
	}

	@Override
	public void startZoom(ZoomEvent event) {
		invalidGesture = !isZoom(event);
		if (invalidGesture) {
			return;
		}
		viewportPolicy = determineViewportPolicy();
		viewportPolicy.init();
	}

	@Override
	public void zoom(ZoomEvent event) {
		if (invalidGesture) {
			return;
		}
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
			InfiniteCanvas infiniteCanvas = ((InfiniteCanvasViewer) getHost()
					.getRoot().getViewer()).getCanvas();
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
