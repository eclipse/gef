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

import org.eclipse.gef.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.FocusModel;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * The {@link FXFocusBehavior} can be registered on an {@link IVisualPart} to
 * transfer the focus information from the {@link FocusModel} to the part's
 * visualization. It will assign keyboard focus to the visualization if the part
 * is focused, and it will display focus feedback around the visualization to
 * indicate that the part has focus.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXFocusBehavior extends AbstractBehavior<Node> {

	/**
	 * The adapter role for the "focus" {@link IFeedbackPartFactory}.
	 */
	public static final String FOCUS_FEEDBACK_PART_FACTORY = "FOCUS_FEEDBACK_PART_FACTORY";

	private IContentPart<Node, ? extends Node> focusPart;
	private boolean isViewerFocused;

	private ChangeListener<IContentPart<Node, ? extends Node>> focusObserver = new ChangeListener<IContentPart<Node, ? extends Node>>() {
		@Override
		public void changed(
				ObservableValue<? extends IContentPart<Node, ? extends Node>> observable,
				IContentPart<Node, ? extends Node> oldValue,
				IContentPart<Node, ? extends Node> newValue) {
			if (oldValue != null && hasFeedback(oldValue)) {
				removeFeedback(oldValue);
			}
			focusPart = newValue;
			applyFocusToVisual();
			refreshFocusFeedback();
		}
	};

	private FocusModel<Node> focusModel;

	private ChangeListener<? super Boolean> viewerFocusedListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			isViewerFocused = newValue;
			refreshFocusFeedback();
		}
	};

	private IViewer<Node> viewer;
	private boolean hasViewerFocusedFeedback;

	/**
	 * Adds viewer focused feedback.
	 */
	protected void addViewerFocusedFeedback() {
		if (viewer instanceof FXViewer) {
			((FXViewer) viewer).getCanvas().setStyle(FXViewer.FOCUSED_STYLE);
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

	@SuppressWarnings("serial")
	@Override
	protected void doActivate() {
		super.doActivate();
		viewer = getHost().getRoot().getViewer();
		focusModel = viewer.getAdapter(new TypeToken<FocusModel<Node>>() {
		});
		if (focusModel == null) {
			throw new IllegalStateException(
					"Cannot obtain FocusModel<Node> from the IViewer of the host of this FXFocusBehavior.");
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
	protected IFeedbackPartFactory<Node> getFeedbackPartFactory(
			IViewer<Node> viewer) {
		return getFeedbackPartFactory(viewer, FOCUS_FEEDBACK_PART_FACTORY);
	}

	/**
	 * Returns the {@link FocusModel} at which this {@link FXFocusBehavior} is
	 * registered for changes.
	 *
	 * @return The {@link FocusModel} at which this {@link FXFocusBehavior} is
	 *         registered for changes.
	 */
	protected FocusModel<Node> getFocusModel() {
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
		if (viewer instanceof FXViewer) {
			((FXViewer) viewer).getCanvas().setStyle(FXViewer.DEFAULT_STYLE);
		}
		hasViewerFocusedFeedback = false;
	}

}
