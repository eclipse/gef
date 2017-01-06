/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import java.util.ArrayList;
import java.util.List;

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
 * @author mwienand
 *
 */
public class ZoomScaleContributionItem extends AbstractViewerContributionItem {

	/**
	 */
	protected static class Ranges {
		private int minSource = 0;
		private int maxSource = 1000;
		private double minTarget = 0.03125;
		private double maxTarget = 32.0;
		private List<Integer> stopsSource = new ArrayList<>();
		private List<Double> stopsTarget = new ArrayList<>();

		/**
		 */
		public Ranges() {
		}

		/**
		 * @param minSource
		 *            a
		 * @param maxSource
		 *            a
		 * @param minTarget
		 *            a
		 * @param maxTarget
		 *            a
		 */
		public Ranges(int minSource, int maxSource, double minTarget,
				double maxTarget) {
			this.minSource = minSource;
			this.maxSource = maxSource;
			this.minTarget = minTarget;
			this.maxTarget = maxTarget;
		}

		/**
		 * @param source
		 *            a
		 * @param target
		 *            a
		 * @return a
		 */
		public Ranges addStop(int source, double target) {
			stopsSource.add(source);
			stopsTarget.add(target);
			return this;
		}

		/**
		 * @param target
		 *            a
		 * @return a
		 */
		public int toSource(double target) {
			int mins = minSource;
			int maxs = maxSource;
			double mind = minTarget;
			double maxd = maxTarget;

			// iterate stops to find nearest min/max
			for (int i = 0; i < stopsTarget.size(); i++) {
				Double stopTarget = stopsTarget.get(i);
				Integer stopSource = stopsSource.get(i);
				if (target > stopTarget) {
					mind = stopTarget;
					mins = stopSource;
				}
				if (target < stopTarget) {
					if (stopTarget < maxd) {
						maxd = stopTarget;
					}
					if (stopSource < maxs) {
						maxs = stopSource;
					}
				}
			}

			// map from double range to int range
			return (int) (mins
					+ (maxs - mins) * (target - mind) / (maxd - mind));
		}

		/**
		 * @param source
		 *            a
		 * @return a
		 */
		public double toTarget(int source) {
			// System.out.println("toTarget(" + source + ")");

			int mins = minSource;
			int maxs = maxSource;
			double mind = minTarget;
			double maxd = maxTarget;

			// System.out.println(" Min/Max: " + mins + " : " + mind + " -> "
			// + maxs + " : " + maxd);

			// iterate stops to find nearest min/max
			for (int i = 0; i < stopsSource.size(); i++) {
				Integer stopSource = stopsSource.get(i);
				Double stopTarget = stopsTarget.get(i);

				// System.out.println(
				// " Stop " + i + ": " + stopSource + " : " + stopTarget);

				if (source > stopSource) {
					mins = stopSource;
					mind = stopTarget;
					// System.out.println(" -> Min: " + mins + " : " + mind);
				}
				if (source < stopSource) {
					if (stopSource < maxs) {
						maxs = stopSource;
					}
					if (stopTarget < maxd) {
						maxd = stopTarget;
					}
					// System.out.println(" -> Max: " + maxs + " : " + maxd);
				}
			}

			// System.out.println(" Result: " + mind + " + (" + maxd + " - " +
			// mind
			// + ") * (" + source + " - " + mins + ") / (" + maxs + " - "
			// + mins + ")");

			// map from int range to double range
			double target = mind
					+ (maxd - mind) * (source - mins) / (double) (maxs - mins);

			// System.out.println(" -> " + target);

			return target;
		}
	}

	/**
	 *
	 */
	public static final String ZOOM_SCALE_CONTRIBUTION_ITEM_ID = "ZoomScaleContributionItem";

	// controls
	private ToolItem toolItem;
	private Scale zoomScale;

	// listeners
	private ChangeListener<? super Number> zoomListener;

	// helpers
	private Ranges ranges;

	// zoom action
	private AbstractZoomAction zoomAction = new AbstractZoomAction("Zoom") {
		@Override
		protected double determineZoomFactor(double currentZoomFactor,
				Event event) {
			return ((double) event.data) * 1d / currentZoomFactor;
		}
	};

	/**
	 */
	public ZoomScaleContributionItem() {
		setId(ZOOM_SCALE_CONTRIBUTION_ITEM_ID);
	}

	@Override
	protected void activate() {
		if (zoomListener != null) {
			throw new IllegalStateException(
					"Zoom listener is already registered.");
		}

		ranges = new Ranges(0, getMaximumScaleValue(), getMinimumZoomFactor(),
				getMaximumZoomFactor());
		ranges.addStop(2500, 1d).addStop(4000, 2.5d).addStop(6000, 6d)
				.addStop(8000, 14d);

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
	protected void deactivate() {
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

	@Override
	public void dispose() {
		if (getViewer() != null && isActive()) {
			init(null);
		}
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
		zoomScale.setMinimum(0);
		zoomScale.setMaximum(getMaximumScaleValue());
		zoomScale.setSelection(getMaximumScaleValue() / 2);

		zoomScale.setSize(150, 100);
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
				double zoom = ranges.toTarget(sel);
				if (zoom > 0) {
					Event event = new Event();
					event.data = zoom;
					zoomAction.runWithEvent(event);
				} else {
					throw new IllegalStateException("Illegal zoom factor.");
				}
			}
		});

		double mxx = ((InfiniteCanvas) getViewer().getCanvas())
				.getContentTransform().getMxx();
		updateScaleValue(mxx);
	}

	/**
	 * @return a
	 */
	protected int getMaximumScaleValue() {
		return 10000;
	}

	/**
	 * @return a
	 */
	protected double getMaximumZoomFactor() {
		return 32d;
	}

	/**
	 * @return a
	 */
	protected double getMinimumZoomFactor() {
		return 1d / 32d;
	}

	@Override
	public void init(IViewer viewer) {
		super.init(viewer);
		zoomAction.init(viewer);
	}

	/**
	 * @param n
	 *            a
	 */
	protected void updateScaleValue(double n) {
		if (zoomScale != null) {
			zoomScale.setSelection(ranges.toSource(n));
		}
	}
}
