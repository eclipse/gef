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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.gef.common.adapt.IAdaptable;
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
 * The {@link ZoomComboContributionItem} is an
 * {@link AbstractViewerContributionItem} that contributes a zoom {@link Combo}
 * to the tool bar. The zoom combo displays the current zoom level/factor in
 * percent and provides a drop down menu that contains a number of predefined
 * zoom factors (see {@link #getZoomFactors()}). Additionally, you can specify
 * additional actions for which items should be added to the drop down menu when
 * constructing a {@link ZoomComboContributionItem} (see
 * {@link #ZoomComboContributionItem(IAction...)}).
 * <p>
 * If the user enters a custom value in the {@link Combo} and presses the return
 * key, the string is converted to a zoom factor, which is then restricted to
 * the range specified by {@link #getMinimumPermissibleZoomFactor()} and
 * {@link #getMaximumPermissibleZoomFactor()} before it is applied.
 *
 * @author mwienand
 *
 */
public class ZoomComboContributionItem extends AbstractViewerContributionItem {

	/**
	 * The ID (see {@link #setId(String)}) for this
	 * {@link ZoomComboContributionItem}.
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
	private AbstractZoomAction zoomAction = createZoomAction();

	private List<IAction> additionalActions = new ArrayList<>();

	/**
	 * Constructs a new {@link ZoomComboContributionItem}.
	 *
	 * @param additionalActions
	 *            Additional {@link IAction}s that should be shown in the
	 *            {@link Combo}, e.g. {@link FitToViewportAction}.
	 */
	public ZoomComboContributionItem(IAction... additionalActions) {
		setId(ZOOM_COMBO_CONTRIBUTION_ITEM_ID);
		this.additionalActions.addAll(Arrays.asList(additionalActions));
	}

	/**
	 * Returns an {@link AbstractZoomAction} that is used to carry out zooming
	 * when one of the predefined zoom factors is selected by the user. The zoom
	 * factor is passed on to the action using the {@link Event#data} field of
	 * the {@link Event} that is given to
	 * {@link AbstractZoomAction#determineZoomFactor(double, Event)}.
	 *
	 * @return The {@link AbstractZoomAction} that is used to apply predefined
	 *         zoom factors.
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
				int selectionIndex = zoomCombo.getSelectionIndex();
				if (selectionIndex < 0) {
					// no selection => nothing to do
					return;
				}
				List<Double> zoomFactors = getZoomFactors();
				if (selectionIndex >= zoomFactors.size()) {
					// additional action selected
					additionalActions.get(selectionIndex - zoomFactors.size())
							.runWithEvent(null);
				} else {
					// zoom factor selected
					Event event = new Event();
					event.data = zoomFactors.get(selectionIndex);
					zoomAction.runWithEvent(event);
				}
			}
		});

		updateComboText();
	}

	/**
	 * Returns a list of all strings that should be selectable in the
	 * {@link Combo} drop down menu.
	 *
	 * @return A list of all {@link Combo} items.
	 */
	protected List<String> getItems() {
		List<String> items = new ArrayList<>();
		// insert zoom factor items
		for (Double zoomFactor : getZoomFactors()) {
			items.add(toPercentText(zoomFactor));
		}
		// insert additional action items
		for (IAction a : additionalActions) {
			items.add(a.getText());
		}
		return items;
	}

	/**
	 * Returns the maximum zoom factor that is permissible when the user does
	 * not select a predefined value, but enters a custom zoom factor instead.
	 *
	 * @return The maximum zoom factor for custom input.
	 */
	protected double getMaximumPermissibleZoomFactor() {
		return 64d;
	}

	/**
	 * Returns the minimum zoom factor that is permissible when the user does
	 * not select a predefined value, but enters a custom zoom factor instead.
	 *
	 * @return The minimum zoom factor for custom input.
	 */
	protected double getMinimumPermissibleZoomFactor() {
		return 0.001d;
	}

	/**
	 * Returns a list containing all zoom factors in the order in which items
	 * should be created for them in the {@link Combo}.
	 *
	 * @return A list containing the predefined zoom factors.
	 */
	protected List<Double> getZoomFactors() {
		return Arrays.asList(0.125d, 0.25d, 1d / 3d, 0.5d, 2d / 3d, 0.75d, 1d,
				1.25d, 1.5d, 2d, 3d, 4d, 8d);
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
				showZoomFactor(n);
			};
			infiniteCanvas.getContentTransform().mxxProperty()
					.addListener(zoomListener);
			showZoomFactor(infiniteCanvas.getContentTransform().getMxx());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setAdaptable(IViewer viewer) {
		super.setAdaptable(viewer);
		zoomAction.setAdaptable(viewer);
		for (IAction a : additionalActions) {
			if (a instanceof IAdaptable.Bound) {
				((IAdaptable.Bound<IViewer>) a).setAdaptable(viewer);
			}
		}
	}

	/**
	 * Updates the zoom factor that is displayed by the {@link Combo}. Converts
	 * the given number to a percent text using {@link #toPercentText(double)}
	 * and sets it as the combo's display text.
	 *
	 * @param zoomFactor
	 *            The number to display in the {@link Combo}.
	 */
	protected void showZoomFactor(Number zoomFactor) {
		if (zoomCombo != null) {
			String text = toPercentText(zoomFactor.doubleValue());
			zoomCombo.setText(text);
		}
	}

	/**
	 * Converts the given zoom factor to a corresponding percent text which can
	 * be displayed in the {@link Combo}. The resulting percent text does
	 * neither have fraction digits nor grouping delimiters and contains a
	 * trailing <code>"%"</code>, e.g. the zoom factor <code>2.125</code> is
	 * converted to the text <code>213%</code>.
	 *
	 * @param zoomFactor
	 *            The zoom factor for which to determine the corresponding
	 *            percent text to display in the {@link Combo}.
	 * @return The corresponding percent text.
	 */
	protected String toPercentText(double zoomFactor) {
		return PERCENT_FORMAT.format(zoomFactor);
	}

	/**
	 * Converts the given percent text to a corresponding zoom factor,
	 * restricted to the range of permissible zoom factors that is specified by
	 * {@link #getMinimumPermissibleZoomFactor()} and
	 * {@link #getMaximumPermissibleZoomFactor()}.
	 * <p>
	 * This method is called to determine the zoom factor when the user does not
	 * select a predefined item in the {@link Combo}, but enters custom input
	 * instead.
	 *
	 * @param percentText
	 *            The percent text for which to determine the corresponding zoom
	 *            factor.
	 * @return The zoom factor corresponding to the given percent text,
	 *         restricted to the permissible range.
	 */
	protected double toZoomFactor(String percentText) {
		Matcher matcher = NUMBER_PATTERN.matcher(percentText);
		if (matcher.find()) {
			// user input is valid
			try {
				double zoom = PERCENT_FORMAT.parse(matcher.group(1))
						.doubleValue() / 100;
				// return restricted zoom level
				return Math.min(getMaximumPermissibleZoomFactor(),
						Math.max(getMinimumPermissibleZoomFactor(), zoom));
			} catch (ParseException e) {
				throw new IllegalStateException(e);
			}
		}
		// user input is invalid
		return -1;
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
	 * Updates the zoom factor that is displayed in the {@link Combo}. The
	 * current zoom factor is queried from the {@link #getViewer() viewer} and
	 * set as the display text for the {@link Combo} using
	 * {@link #showZoomFactor(Number)}.
	 */
	protected void updateComboText() {
		if (isEnabled()) {
			Parent canvas = getViewer().getCanvas();
			if (canvas instanceof InfiniteCanvas) {
				InfiniteCanvas infiniteCanvas = (InfiniteCanvas) canvas;
				showZoomFactor(infiniteCanvas.getContentTransform().getMxx());
			}
		}
	}
}
