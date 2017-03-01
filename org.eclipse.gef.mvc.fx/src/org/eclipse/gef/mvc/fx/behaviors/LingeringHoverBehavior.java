/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * The {@link LingeringHoverBehavior} complements the {@link HoverBehavior}
 * w.r.t. feedback and handle generation in response to {@link HoverModel}
 * changes. While {@link HoverBehavior} is responsible for generating feedback
 * and handles for the {@link HoverModel#hoverProperty()}, the
 * {@link LingeringHoverBehavior} is responsible for generating feedback and
 * handles for the {@link HoverModel#lingeringHoverProperty()}.
 *
 * @author wienand
 *
 */
// FIXME: Support multiple different feedback and handle contexts within
// AbstractBehavior so that multiple factories can be used for feedback and
// handle generation from a single Behavior.
public class LingeringHoverBehavior extends AbstractBehavior {

	/**
	 * The adapter role for the {@link IFeedbackPartFactory} that is used to
	 * generate hover feedback parts.
	 */
	public static final String LINGERING_HOVER_FEEDBACK_PART_FACTORY = "LINGERING_HOVER_FEEDBACK_PART_FACTORY";

	/**
	 * The adapter role for the {@link IHandlePartFactory} that is used to
	 * generate hover handle parts.
	 */
	public static final String LINGERING_HOVER_HANDLE_PART_FACTORY = "LINGERING_HOVER_HANDLE_PART_FACTORY";

	private ChangeListener<IContentPart<? extends Node>> lingeringHoverObserver = new ChangeListener<IContentPart<? extends Node>>() {
		@Override
		public void changed(
				ObservableValue<? extends IContentPart<? extends Node>> observable,
				IContentPart<? extends Node> oldValue,
				IContentPart<? extends Node> newValue) {
			onLingeringHoverChange(oldValue, newValue);
		}
	};

	@Override
	protected void doActivate() {
		// register
		HoverModel hoverModel = getHoverModel();
		hoverModel.lingeringHoverProperty().addListener(lingeringHoverObserver);

		// create feedback and handles for a part that is already lingering
		// hovered, if any
		IContentPart<? extends Node> lingeringHover = hoverModel
				.getLingeringHover();
		if (lingeringHover != null) {
			onLingeringHoverChange(null, lingeringHover);
		}
	}

	@Override
	protected void doDeactivate() {
		// remove any pending feedback and handles
		HoverModel hoverModel = getHoverModel();
		IContentPart<? extends Node> lingeringHover = hoverModel
				.getLingeringHover();
		if (lingeringHover != null) {
			onLingeringHoverChange(lingeringHover, null);
		}

		// unregister
		hoverModel.lingeringHoverProperty()
				.removeListener(lingeringHoverObserver);

		// remove any pending feedback and handles
		clearFeedback();
		clearHandles();
	}

	@Override
	protected IFeedbackPartFactory getFeedbackPartFactory(IViewer viewer) {
		return getFeedbackPartFactory(viewer,
				LINGERING_HOVER_FEEDBACK_PART_FACTORY);
	}

	@Override
	protected IHandlePartFactory getHandlePartFactory(IViewer viewer) {
		return getHandlePartFactory(viewer,
				LINGERING_HOVER_HANDLE_PART_FACTORY);
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
	 * {@link HoverModel#lingeringHoverProperty()} changes. Triggers
	 * generation/removal of feedback and handles.
	 *
	 * @param oldLingeringHover
	 *            The previous lingering hovered {@link IContentPart}.
	 * @param newLingeringHover
	 *            The new lingering hovered {@link IContentPart}.
	 */
	protected void onLingeringHoverChange(
			IContentPart<? extends Node> oldLingeringHover,
			IContentPart<? extends Node> newLingeringHover) {
		// check if changed
		if (oldLingeringHover == newLingeringHover) {
			return;
		}

		// remove feedback/handles from the old lingering hovered part
		if (oldLingeringHover != null) {
			removeHandles(oldLingeringHover);
			removeFeedback(oldLingeringHover);
		}

		// add feedback/handles to the new lingering hovered part
		if (newLingeringHover != null) {
			addFeedback(newLingeringHover);
			addHandles(newLingeringHover);
		}
	}
}
