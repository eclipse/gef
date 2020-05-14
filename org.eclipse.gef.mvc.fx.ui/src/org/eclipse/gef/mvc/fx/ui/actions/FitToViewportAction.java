/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
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
import org.eclipse.gef.mvc.fx.handlers.PanningSupport;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiBundle;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;

import javafx.geometry.Pos;
import javafx.scene.Parent;

/**
 * The {@link FitToViewportAction} is an {@link AbstractViewerAction} that will
 * zoom and scroll the {@link IViewer#getCanvas()} (provided that it is an
 * {@link InfiniteCanvas}) so that its contents are centered and fill the
 * viewport. However, the zoom factor is restricted to the range from 0.25 to
 * 4.0, which can be customized via subclassing ({@link #getMinZoom()},
 * {@link #getMaxZoom()}).
 *
 * @author mwienand
 *
 */
public class FitToViewportAction extends AbstractViewerAction {

	private PanningSupport panningSupport = new PanningSupport();

	/**
	 * Constructs a new {@link FitToViewportAction}.
	 */
	public FitToViewportAction() {
		this("Fit-To-Viewport", IAction.AS_PUSH_BUTTON,
				MvcFxUiBundle.getDefault().getImageRegistry().getDescriptor(
						MvcFxUiBundle.IMG_ICONS_FIT_TO_VIEWPORT));
	}

	/**
	 * Constructs a new {@link FitToViewportAction} with the given text and
	 * style. Also sets the given {@link ImageDescriptor} for this action.
	 *
	 * @param text
	 *            Text for the action.
	 * @param style
	 *            Style for the action, see {@link IAction} for details.
	 * @param imageDescriptor
	 *            {@link ImageDescriptor} specifying the icon for the action.
	 */
	protected FitToViewportAction(String text, int style,
			ImageDescriptor imageDescriptor) {
		super(text, style, imageDescriptor);
	}

	@Override
	protected ITransactionalOperation createOperation(Event event) {
		InfiniteCanvas infiniteCanvas = getInfiniteCanvas();
		if (infiniteCanvas == null) {
			throw new IllegalStateException(
					"Cannot perform FitToViewportAction, because no InfiniteCanvas can be determined.");
		}
		if (infiniteCanvas.getContentBounds().isEmpty()) {
			// nothing to do
			return null;
		}

		ViewportPolicy viewportPolicy = getViewer().getRootPart()
				.getAdapter(ViewportPolicy.class);
		if (viewportPolicy == null) {
			throw new IllegalStateException(
					"Cannot perform FitToViewportAction, because no ViewportPolicy can be determined for the root part.");
		}

		viewportPolicy.init();
		if (isContentRestricted()) {
			panningSupport.removeFreeSpace(viewportPolicy, Pos.TOP_LEFT, true);
		}
		viewportPolicy.fitToSize(getMinZoom(), getMaxZoom());
		if (isContentRestricted()) {
			panningSupport.removeFreeSpace(viewportPolicy, Pos.BOTTOM_RIGHT,
					false);
		}
		return viewportPolicy.commit();
	}

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
	 * Returns the maximum zoom level, which is <code>4.0</code> per default.
	 *
	 * @return The maximum zoom level.
	 */
	protected double getMaxZoom() {
		return 4;
	}

	/**
	 * Returns the minimum zoom level, which is <code>0.25</code> per default.
	 *
	 * @return The minimum zoom level.
	 */
	protected double getMinZoom() {
		return 0.25;
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
