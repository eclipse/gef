/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
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
package org.eclipse.gef.mvc.fx.ui.actions;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.handlers.PanningSupport;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;

/**
 * The {@link AbstractScrollAction} is an {@link AbstractViewerAction} that
 * scrolls the viewer based on a pivot point computation, so that the computed
 * position within the content area will be visible at the computed position
 * within the viewport. I.e. the pivot point computation
 * ({@link #determinePivotPoint(Bounds)}) is done twice, once for the content
 * bounds, and once for the viewport bounds. From these computed pivot points,
 * an operation is constructed that scrolls the viewer so that the content pivot
 * point is moved to the viewport pivot point.
 *
 * @author mwienand
 *
 */
public abstract class AbstractScrollAction extends AbstractViewerAction {

	private PanningSupport panningSupport = new PanningSupport();

	/**
	 * Constructs a new {@link AbstractScrollAction}.
	 *
	 * @param text
	 *            Text for the action.
	 */
	protected AbstractScrollAction(String text) {
		super(text, IAction.AS_PUSH_BUTTON, null);
	}

	/**
	 * Constructs a new {@link AbstractScrollAction} with the given text and
	 * style. Also sets the given {@link ImageDescriptor} for this action.
	 *
	 * @param text
	 *            Text for the action.
	 * @param style
	 *            Style for the action, see {@link IAction} for details.
	 * @param imageDescriptor
	 *            {@link ImageDescriptor} specifying the icon for the action.
	 */
	protected AbstractScrollAction(String text, int style,
			ImageDescriptor imageDescriptor) {
		super(text, style, imageDescriptor);
	}

	@Override
	protected ITransactionalOperation createOperation(Event event) {
		InfiniteCanvas infiniteCanvas = getInfiniteCanvas();
		if (infiniteCanvas == null) {
			throw new IllegalStateException(
					"Cannot perform ResetZoomAction, because no InfiniteCanvas can be determiend.");
		}

		// compute content pivot based on content bounds
		Bounds contentBounds = infiniteCanvas.getContentBounds();
		Point contentPivot = determinePivotPoint(contentBounds);

		// compute viewport pivot based on viewport bounds
		Bounds viewportBounds = Geometry2FX.toFXBounds(new Rectangle(0, 0,
				infiniteCanvas.getWidth(), infiniteCanvas.getHeight()));
		Point viewportPivot = determinePivotPoint(viewportBounds);

		// query ViewportPolicy
		ViewportPolicy viewportPolicy = getViewer().getRootPart()
				.getAdapter(ViewportPolicy.class);
		if (viewportPolicy == null) {
			throw new IllegalStateException(
					"Cannot perform AbstractScrollAction, because no ViewportPolicy can be determined for the root part.");
		}

		viewportPolicy.init();
		if (isContentRestricted()) {
			panningSupport.removeFreeSpace(viewportPolicy, Pos.TOP_LEFT, true);
		}
		viewportPolicy.scroll(false, viewportPivot.x - contentPivot.x,
				viewportPivot.y - contentPivot.y);
		if (isContentRestricted()) {
			panningSupport.removeFreeSpace(viewportPolicy, Pos.BOTTOM_RIGHT,
					false);
		}
		return viewportPolicy.commit();
	}

	/**
	 * Computes the pivot {@link Point} within the given {@link Bounds}.
	 *
	 * @param bounds
	 *            The {@link Bounds} for which to return the pivot
	 *            {@link Point}.
	 * @return The pivot {@link Point} within the given {@link Bounds}.
	 */
	protected abstract Point determinePivotPoint(Bounds bounds);

	/**
	 * Returns the {@link InfiniteCanvas} of the viewer where this action is
	 * installed.
	 *
	 * @return The {@link InfiniteCanvas} of the viewer.
	 */
	protected InfiniteCanvas getInfiniteCanvas() {
		Parent canvas = getViewer().getCanvas();
		if (canvas instanceof InfiniteCanvas) {
			return (InfiniteCanvas) canvas;
		}
		return null;
	}

	/**
	 * Returns <code>true</code> to signify that scrolling and zooming is
	 * restricted to the content bounds, <code>false</code> otherwise.
	 * <p>
	 * When content-restricted, the policy behaves texteditor-like, i.e. the
	 * pivot point for zooming is at the top of the viewport and at the left of
	 * the contents, and free space is only allowed to the right and to the
	 * bottom of the contents. Therefore, the action does not allow scrolling
	 * and zooming if it would result in free space within the viewport at the
	 * top or left sides of the contents.
	 *
	 * @return <code>true</code> to signify that scrolling and zooming is
	 *         restricted to the content bounds, <code>false</code> otherwise.
	 */
	protected boolean isContentRestricted() {
		return false;
	}
}
