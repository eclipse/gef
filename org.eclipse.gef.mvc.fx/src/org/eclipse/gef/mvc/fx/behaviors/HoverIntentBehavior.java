/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.behaviors;

import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * The {@link HoverIntentBehavior} complements the {@link HoverBehavior} w.r.t.
 * feedback and handle generation in response to {@link HoverModel} changes.
 * While {@link HoverBehavior} is responsible for generating feedback and
 * handles for the {@link HoverModel#hoverProperty()}, the
 * {@link HoverIntentBehavior} is responsible for generating feedback and
 * handles for the {@link HoverModel#hoverIntentProperty()}.
 *
 * @author wienand
 *
 */
// FIXME: Support multiple different feedback and handle contexts within
// AbstractBehavior so that multiple factories can be used for feedback and
// handle generation from a single Behavior.
public class HoverIntentBehavior extends AbstractBehavior {

	/**
	 * The adapter role for the {@link IFeedbackPartFactory} that is used to
	 * generate hover feedback parts.
	 */
	public static final String HOVER_INTENT_FEEDBACK_PART_FACTORY = "HOVER_INTENT_FEEDBACK_PART_FACTORY";

	/**
	 * The adapter role for the {@link IHandlePartFactory} that is used to
	 * generate hover handle parts.
	 */
	public static final String HOVER_INTENT_HANDLE_PART_FACTORY = "HOVER_INTENT_HANDLE_PART_FACTORY";

	private ChangeListener<IContentPart<? extends Node>> hoverIntentObserver = new ChangeListener<IContentPart<? extends Node>>() {
		@Override
		public void changed(
				ObservableValue<? extends IContentPart<? extends Node>> observable,
				IContentPart<? extends Node> oldValue,
				IContentPart<? extends Node> newValue) {
			onHoverIntentChange(oldValue, newValue);
		}
	};

	@Override
	protected void doActivate() {
		// register
		HoverModel hoverModel = getHoverModel();
		hoverModel.hoverIntentProperty().addListener(hoverIntentObserver);

		// create feedback and handles for a part that is already the hover
		// intent
		IContentPart<? extends Node> hoverIntent = hoverModel.getHoverIntent();
		if (hoverIntent != null) {
			onHoverIntentChange(null, hoverIntent);
		}
	}

	@Override
	protected void doDeactivate() {
		// remove any pending feedback and handles
		HoverModel hoverModel = getHoverModel();
		IContentPart<? extends Node> hoverIntent = hoverModel.getHoverIntent();
		if (hoverIntent != null) {
			onHoverIntentChange(hoverIntent, null);
		}

		// unregister
		hoverModel.hoverIntentProperty().removeListener(hoverIntentObserver);

		// remove any pending feedback and handles
		clearFeedback();
		clearHandles();
	}

	@Override
	protected IFeedbackPartFactory getFeedbackPartFactory(IViewer viewer) {
		return getFeedbackPartFactory(viewer,
				HOVER_INTENT_FEEDBACK_PART_FACTORY);
	}

	@Override
	protected IHandlePartFactory getHandlePartFactory(IViewer viewer) {
		return getHandlePartFactory(viewer, HOVER_INTENT_HANDLE_PART_FACTORY);
	}

	/**
	 * Returns the {@link HoverModel} in the context of the {@link #getHost()
	 * host}.
	 *
	 * @return The {@link HoverModel} in the context of the {@link #getHost()
	 *         host}.
	 */
	protected HoverModel getHoverModel() {
		IViewer viewer = getHost().getRoot().getViewer();
		HoverModel hoverModel = viewer.getAdapter(HoverModel.class);
		return hoverModel;
	}

	/**
	 * Callback method that is invoked when the
	 * {@link HoverModel#hoverIntentProperty()} changes. Triggers
	 * generation/removal of feedback and handles.
	 *
	 * @param oldHoverIntent
	 *            The previous hover intent {@link IContentPart}.
	 * @param newHoverIntent
	 *            The new hover intent {@link IContentPart}.
	 */
	protected void onHoverIntentChange(
			IContentPart<? extends Node> oldHoverIntent,
			IContentPart<? extends Node> newHoverIntent) {
		// check if changed
		if (oldHoverIntent == newHoverIntent) {
			return;
		}

		// remove feedback/handles from the old hover intent part
		if (oldHoverIntent != null) {
			removeHandles(oldHoverIntent);
			removeFeedback(oldHoverIntent);
		}

		// add feedback/handles to the new hover intent part
		if (newHoverIntent != null) {
			addFeedback(newHoverIntent);
			addHandles(newHoverIntent);
		}
	}
}
