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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;

/**
 * @author wienand
 *
 */
public class ZoomComboContributionItem extends AbstractViewerContributionItem {

	/**
	 * ID
	 */
	public static final String ZOOM_COMBO_CONTRIBUTION_ITEM_ID = "ZoomComboContributionItem";

	private static final Pattern NUMBER_PATTERN = Pattern
			.compile("(\\d+\\.\\d+|\\.\\d+|\\d+)");

	private static final NumberFormat PERCENT_FORMAT = NumberFormat
			.getPercentInstance();

	static {
		PERCENT_FORMAT.setGroupingUsed(false);
		PERCENT_FORMAT.setMinimumFractionDigits(0);
		PERCENT_FORMAT.setMaximumFractionDigits(0);
	}

	// controls
	private ToolItem toolItem;
	private Combo zoomCombo;
	private ChangeListener<? super Number> zoomListener;

	// actions
	private AbstractZoomAction zoomAction = new AbstractZoomAction("Zoom") {
		@Override
		protected double determineZoomFactor(double currentZoomFactor,
				Event event) {
			return ((double) event.data) * 1d / currentZoomFactor;
		}
	};
	private List<IAction> additionalActions = new ArrayList<>();

	/**
	 * @param additionalActions
	 *            a
	 */
	public ZoomComboContributionItem(IAction... additionalActions) {
		setId(ZOOM_COMBO_CONTRIBUTION_ITEM_ID);
		this.additionalActions.addAll(Arrays.asList(additionalActions));
	}

	@Override
	protected void activate() {
		if (zoomListener != null) {
			throw new IllegalStateException(
					"Zoom listener is already registered.");
		}
		Parent canvas = getViewer().getCanvas();
		if (canvas instanceof InfiniteCanvas) {
			InfiniteCanvas infiniteCanvas = (InfiniteCanvas) canvas;
			zoomListener = (a, o, n) -> {
				showZoomFactor(n);
			};
			infiniteCanvas.getContentTransform().mxxProperty()
					.addListener(zoomListener);
			showZoomFactor(infiniteCanvas.getContentTransform().getMxx());
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

		zoomCombo = new Combo(tb, SWT.DROP_DOWN);
		zoomCombo.setItems(getItems().toArray(new String[0]));
		toolItem.setWidth(
				zoomCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		toolItem.setControl(zoomCombo);

		zoomCombo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == '\n' || e.keyCode == '\r') {
					double zoom = toZoomFactor(zoomCombo.getText());
					if (zoom > 0) {
						Event event = new Event();
						event.data = zoom;
						zoomAction.runWithEvent(event);
					} else {
						updateComboText();
					}
				}
			}

		});

		zoomCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = zoomCombo.getText();

				IAction action = null;
				for (IAction a : additionalActions) {
					if (a.getText().equals(text)) {
						action = a;
						break;
					}
				}

				if (action != null) {
					action.runWithEvent(null);
				} else {
					double zoom = getItemToZoomFactorMap().containsKey(text)
							? getItemToZoomFactorMap().get(text)
							: toZoomFactor(text);
					if (zoom > 0) {
						Event event = new Event();
						event.data = zoom;
						zoomAction.runWithEvent(event);
					} else {
						throw new IllegalStateException(
								"Illegal zoom factor for selected zoom item.");
					}
				}
			}
		});

		updateComboText();
	}

	/**
	 * @return a
	 */
	protected List<String> getItems() {
		List<String> list = new ArrayList<>(getZoomItems());
		for (IAction a : additionalActions) {
			list.add(a.getText());
		}
		return list;
	}

	/**
	 * @return a
	 */
	protected Map<String, Double> getItemToZoomFactorMap() {
		List<String> zoomItems = getZoomItems();
		List<Double> zoomFactors = getZoomFactors();
		if (zoomItems.size() != zoomFactors.size()) {
			throw new IllegalStateException(
					"Number of zoom factors differs from number of zoom items.");
		}
		HashMap<String, Double> map = new HashMap<>();
		for (int i = 0; i < zoomFactors.size(); i++) {
			String item = zoomItems.get(i);
			Double factor = zoomFactors.get(i);
			map.put(item, factor);
		}
		return map;
	}

	/**
	 *
	 * @return a
	 */
	protected double getMinimumZoomFactor() {
		return 0.001;
	}

	/**
	 *
	 * @return a
	 */
	protected List<Double> getZoomFactors() {
		return Arrays.asList(0.125d, 0.25d, 1d / 3d, 0.5d, 2d / 3d, 0.75d, 1d,
				1.25d, 1.5d, 2d, 3d, 4d, 8d);
	}

	/**
	 *
	 * @return a
	 */
	protected List<String> getZoomItems() {
		List<String> items = new ArrayList<>();
		for (Double zoomFactor : getZoomFactors()) {
			items.add(toPercentText(zoomFactor));
		}
		return items;
	}

	/**
	 *
	 * @param viewer
	 *            a
	 */
	@Override
	public void init(IViewer viewer) {
		super.init(viewer);
		// initialize delegate actions
		if (zoomAction instanceof IViewerAction) {
			((IViewerAction) zoomAction).init(viewer);
		}
		for (IAction a : additionalActions) {
			if (a instanceof IViewerAction) {
				((IViewerAction) a).init(viewer);
			}
		}
	}

	/**
	 *
	 * @param n
	 *            a
	 */
	protected void showZoomFactor(Number n) {
		if (zoomCombo != null) {
			String text = toPercentText(n.doubleValue());
			zoomCombo.setText(text);
		}
	}

	/**
	 *
	 * @param zoomFactor
	 *            a
	 * @return a
	 */
	protected String toPercentText(double zoomFactor) {
		return PERCENT_FORMAT.format(zoomFactor);
	}

	/**
	 *
	 * @param percentText
	 *            a
	 * @return a
	 */
	protected double toZoomFactor(String percentText) {
		Matcher matcher = NUMBER_PATTERN.matcher(percentText);
		double zoom = -1;
		if (matcher.find()) {
			try {
				zoom = PERCENT_FORMAT.parse(matcher.group(1)).doubleValue()
						/ 100;
			} catch (ParseException e) {
				throw new IllegalStateException(e);
			}
		}
		// minimum zoom level
		if (zoom < getMinimumZoomFactor()) {
			zoom = getMinimumZoomFactor();
		}
		return zoom;
	}

	/**
	 *
	 */
	protected void updateComboText() {
		if (isActive()) {
			Parent canvas = getViewer().getCanvas();
			if (canvas instanceof InfiniteCanvas) {
				InfiniteCanvas infiniteCanvas = (InfiniteCanvas) canvas;
				showZoomFactor(infiniteCanvas.getContentTransform().getMxx());
			}
		}
	}
}
