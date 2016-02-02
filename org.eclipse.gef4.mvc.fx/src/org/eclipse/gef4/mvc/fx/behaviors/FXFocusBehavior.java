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
package org.eclipse.gef4.mvc.fx.behaviors;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;

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
	 * The name of the named injection that is used within this
	 * {@link FXFocusBehavior} to obtain an {@link IFeedbackPartFactory} or
	 * {@link IHandlePartFactory}.
	 */
	public static final String PART_FACTORIES_BINDING_NAME = "focus";

	private IContentPart<Node, ? extends Node> focusPart;
	private boolean isViewerFocused;

	private ChangeListener<IContentPart<Node, ? extends Node>> focusObserver = new ChangeListener<IContentPart<Node, ? extends Node>>() {
		@Override
		public void changed(
				ObservableValue<? extends IContentPart<Node, ? extends Node>> observable,
				IContentPart<Node, ? extends Node> oldValue,
				IContentPart<Node, ? extends Node> newValue) {
			focusPart = newValue;
			applyFocusToVisual();
			refreshFocusFeedback();
		}
	};

	@Inject
	@Named(PART_FACTORIES_BINDING_NAME)
	private IFeedbackPartFactory<Node> feedbackPartFactory;

	// @Inject
	// @Named(PART_FACTORIES_BINDING_NAME)
	// private IHandlePartFactory<Node> handlePartFactory;

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
		if (getHost() == viewer.getRootPart()) {
			boolean showFeedback = isViewerFocused && focusPart == null;
			if (hasViewerFocusedFeedback && !showFeedback) {
				removeViewerFocusedFeedback();
			} else if (!hasViewerFocusedFeedback && showFeedback) {
				addViewerFocusedFeedback();
			}
		} else {
			List<IVisualPart<Node, ? extends Node>> targets = Collections
					.<IVisualPart<Node, ? extends Node>> singletonList(
							getHost());
			boolean hasFeedback = getFeedbackParts() != null
					&& !getFeedbackParts().isEmpty();
			boolean isFocused = isViewerFocused && getHost() == focusPart;
			if (hasFeedback && !isFocused) {
				removeFeedback(targets);
			} else if (!hasFeedback && isFocused) {
				List<IFeedbackPart<Node, ? extends Node>> feedbackParts = feedbackPartFactory
						.createFeedbackParts(targets, FXFocusBehavior.this,
								Collections.emptyMap());
				addFeedback(targets, feedbackParts);
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

}
