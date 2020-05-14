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
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;

/**
 * The {@link ZoomScaleContributionItem} is an
 * {@link AbstractViewerContributionItem} that contributes a zoom {@link Scale}
 * to the tool bar. The zoom scale can be used to set the zoom factor by
 * dragging its thumb. Per default, the minimal zoom factor is
 * <code>3.125%</code>
 *
 * @author mwienand
 *
 */
public class ZoomScaleContributionItem extends AbstractViewerContributionItem {

	/**
	 * The ID (see {@link #setId(String)}) for this
	 * {@link ZoomScaleContributionItem}.
	 */
	public static final String ZOOM_SCALE_CONTRIBUTION_ITEM_ID = "ZoomScaleContributionItem";

	private static final int SCALE_MINIMUM = 1; // 0.03125 zoom
	private static final int SCALE_MAXIMUM = 10000; // 32.0 zoom
	private static final double SCALE_TO_ZOOM_COEFF_BASE = 0.0312283;// 0.474357;
	private static final double SCALE_TO_ZOOM_COEFF_EXPO = 0.000693217; // 0.000421363;
	private static final double ZOOM_TO_SCALE_COEFF_BASE = 1442.55; // 2373.25;
	private static final double ZOOM_TO_SCALE_COEFF_EXPO = 32.0222; // 2.10812;

	// controls
	private ToolItem toolItem;
	private Scale zoomScale;

	// listeners
	private ChangeListener<? super Number> zoomListener;

	// zoom action
	private AbstractZoomAction zoomAction = createZoomAction();

	/**
	 * Constructs a new {@link ZoomScaleContributionItem}.
	 */
	public ZoomScaleContributionItem() {
		setId(ZOOM_SCALE_CONTRIBUTION_ITEM_ID);
	}

	/**
	 * Returns the scale value that corresponds to the given zoom factor.
	 *
	 * @param zoomFactor
	 *            The zoom factor that is converted.
	 * @return The corresponding scale value.
	 */
	protected int computeScaleValue(double zoomFactor) {
		return (int) Math.round(ZOOM_TO_SCALE_COEFF_BASE
				* Math.log(ZOOM_TO_SCALE_COEFF_EXPO * zoomFactor));
	}

	/**
	 * Returns the zoom factor that corresponds to the given scale value.
	 *
	 * @param scaleValue
	 *            The scale value that is converted.
	 * @return The corresponding zoom factor.
	 */
	protected double computeZoomFactor(int scaleValue) {
		return SCALE_TO_ZOOM_COEFF_BASE
				* Math.pow(Math.E, SCALE_TO_ZOOM_COEFF_EXPO * scaleValue);
	}

	/**
	 * Returns an {@link AbstractZoomAction} that is used to carry out zooming.
	 * The zoom factor is passed on to the action using the {@link Event#data}
	 * field of the {@link Event} that is given to
	 * {@link AbstractZoomAction#determineZoomFactor(double, Event)}.
	 *
	 * @return The {@link AbstractZoomAction} that is used to carry out zooming.
	 */
	protected AbstractZoomAction createZoomAction() {
		return new AbstractZoomAction("Zoom") {
			@Override
			protected double determineZoomFactor(double currentZoomFactor,
					Event event) {
				return ((double) event.data) * 1d / currentZoomFactor;
			}
		};
	}

	@Override
	public void dispose() {
		if (toolItem != null && !toolItem.isDisposed()) {
			toolItem.dispose();
		}
	}

	@Override
	public void fill(Composite parent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fill(CoolBar parent, int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fill(Menu menu, int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fill(ToolBar tb, int index) {
		toolItem = new ToolItem(tb, SWT.SEPARATOR, index);

		zoomScale = new Scale(tb, SWT.HORIZONTAL);
		zoomScale.setMinimum(SCALE_MINIMUM);
		zoomScale.setMaximum(SCALE_MAXIMUM);
		zoomScale.setSelection(SCALE_MAXIMUM / 2 + SCALE_MINIMUM / 2);

		zoomScale.setSize(75, 100);
		toolItem.setWidth(zoomScale.getSize().x);
		toolItem.setControl(zoomScale);

		zoomScale.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				int sel = zoomScale.getSelection();
				double zoom = computeZoomFactor(sel);
				if (zoom > 0) {
					Event event = new Event();
					event.data = zoom;
					zoomAction.runWithEvent(event);
				} else {
					throw new IllegalStateException("Illegal zoom factor.");
				}
			}
		});

		if (isEnabled()) {
			double mxx = ((InfiniteCanvas) getViewer().getCanvas())
					.getContentTransform().getMxx();
			updateScaleValue(mxx);
		}
	}

	@Override
	protected void register() {
		if (zoomListener != null) {
			throw new IllegalStateException(
					"Zoom listener is already registered.");
		}

		Parent canvas = getViewer().getCanvas();
		if (canvas instanceof InfiniteCanvas) {
			InfiniteCanvas infiniteCanvas = (InfiniteCanvas) canvas;
			zoomListener = (a, o, n) -> {
				updateScaleValue(n.doubleValue());
			};
			infiniteCanvas.getContentTransform().mxxProperty()
					.addListener(zoomListener);
			updateScaleValue(infiniteCanvas.getContentTransform().getMxx());
		}
	}

	@Override
	public void setAdaptable(IViewer viewer) {
		super.setAdaptable(viewer);
		zoomAction.setAdaptable(viewer);
	}

	@Override
	protected void unregister() {
		if (zoomListener == null) {
			throw new IllegalStateException(
					"Zoom listener not yet registered.");
		}
		Parent canvas = getViewer().getCanvas();
		if (canvas instanceof InfiniteCanvas) {
			((InfiniteCanvas) canvas).getContentTransform().mxxProperty()
					.removeListener(zoomListener);
			zoomListener = null;
		}
	}

	/**
	 * Updates the scale value that is shown in the GUI so that it corresponds
	 * to the given zoom factor.
	 *
	 * @param zoomFactor
	 *            The zoom factor to show in the scale.
	 */
	protected void updateScaleValue(double zoomFactor) {
		if (zoomScale != null && !zoomScale.isDisposed()) {
			zoomScale.setSelection(computeScaleValue(zoomFactor));
		}
	}
}
