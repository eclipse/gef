/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - contribution for Bugzilla #450231
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.behaviors;

import java.util.Collection;
import java.util.List;

import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.Viewer;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * The {@link FocusBehavior} can be registered on an {@link IVisualPart} to
 * transfer the focus information from the {@link FocusModel} to the part's
 * visualization. It will assign keyboard focus to the visualization if the part
 * is focused, and it will display focus feedback around the visualization to
 * indicate that the part has focus.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FocusBehavior extends AbstractBehavior {

	/**
	 * The adapter role for the "focus" {@link IFeedbackPartFactory}.
	 */
	public static final String FOCUS_FEEDBACK_PART_FACTORY = "FOCUS_FEEDBACK_PART_FACTORY";

	private IContentPart<? extends Node> focusPart;
	private boolean isViewerFocused;

	private ChangeListener<IContentPart<? extends Node>> focusObserver = new ChangeListener<IContentPart<? extends Node>>() {
		@Override
		public void changed(
				ObservableValue<? extends IContentPart<? extends Node>> observable,
				IContentPart<? extends Node> oldValue,
				IContentPart<? extends Node> newValue) {
			if (oldValue != null && hasFeedback(oldValue)) {
				removeFeedback(oldValue);
			}
			focusPart = newValue;
			applyFocusToVisual();
			refreshFocusFeedback();
		}
	};

	private FocusModel focusModel;

	private ChangeListener<? super Boolean> viewerFocusedListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			isViewerFocused = newValue;
			refreshFocusFeedback();
		}
	};

	private IViewer viewer;
	private boolean hasViewerFocusedFeedback;

	@Override
	protected void addAnchoreds(
			Collection<? extends IVisualPart<? extends Node>> targets,
			List<? extends IVisualPart<? extends Node>> anchoreds) {
		super.addAnchoreds(targets, anchoreds, 0);
	}

	/**
	 * Adds viewer focused feedback.
	 */
	protected void addViewerFocusedFeedback() {
		if (viewer instanceof Viewer) {
			((IViewer) viewer).getCanvas().setStyle(Viewer.FOCUSED_STYLE);
		}
		hasViewerFocusedFeedback = true;
	}

	/**
	 * Transfers the keyboard focus to JavaFX, i.e. calls
	 * {@link Node#requestFocus()} on the visual of the focus part, or on the
	 * root visual if no part is focused.
	 */
	protected void applyFocusToVisual() {
		if (focusPart == null) {
			viewer.getRootPart().getVisual().requestFocus();
		} else {
			focusPart.getVisual().requestFocus();
		}
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		viewer = getHost().getRoot().getViewer();
		focusModel = viewer.getAdapter(FocusModel.class);
		if (focusModel == null) {
			throw new IllegalStateException(
					"Unable to retrieve FocusModel viewer adapter. Please check your adapter bindings.");
		}

		viewer.viewerFocusedProperty().addListener(viewerFocusedListener);
		focusModel.focusProperty().addListener(focusObserver);

		focusPart = focusModel.getFocus();
		isViewerFocused = viewer.isViewerFocused();
		refreshFocusFeedback();
	}

	@Override
	protected void doDeactivate() {
		focusModel.focusProperty().removeListener(focusObserver);
		viewer.viewerFocusedProperty().removeListener(viewerFocusedListener);
		focusPart = null;
		isViewerFocused = false;
		refreshFocusFeedback();
		super.doDeactivate();
	}

	@Override
	protected IFeedbackPartFactory getFeedbackPartFactory(IViewer viewer) {
		return getFeedbackPartFactory(viewer, FOCUS_FEEDBACK_PART_FACTORY);
	}

	/**
	 * Returns the {@link FocusModel} at which this {@link FocusBehavior} is
	 * registered for changes.
	 *
	 * @return The {@link FocusModel} at which this {@link FocusBehavior} is
	 *         registered for changes.
	 */
	protected FocusModel getFocusModel() {
		return focusModel;
	}

	/**
	 * Refreshes focus feedback, i.e. adds or removes feedback.
	 */
	protected void refreshFocusFeedback() {
		// refresh viewer focus feedback
		boolean showViewerFocusFeedback = isViewerFocused && focusPart == null;
		if (hasViewerFocusedFeedback && !showViewerFocusFeedback) {
			removeViewerFocusedFeedback();
		} else if (!hasViewerFocusedFeedback && showViewerFocusFeedback) {
			addViewerFocusedFeedback();
		}

		// refresh focused part focus feedback
		if (focusPart != null) {
			boolean hasFeedback = hasFeedback(focusPart);
			if (hasFeedback && !isViewerFocused) {
				removeFeedback(focusPart);
			} else if (!hasFeedback && isViewerFocused) {
				addFeedback(focusPart);
			}
		}
	}

	/**
	 * Removes viewer focused feedback.
	 */
	protected void removeViewerFocusedFeedback() {
		if (viewer instanceof Viewer) {
			((IViewer) viewer).getCanvas().setStyle(Viewer.DEFAULT_STYLE);
		}
		hasViewerFocusedFeedback = false;
	}

}
