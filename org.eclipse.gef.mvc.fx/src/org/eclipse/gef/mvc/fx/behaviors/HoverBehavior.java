/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.parts.AbstractFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.AbstractHandlePart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IHandlePart;
import org.eclipse.gef.mvc.fx.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;

/**
 * The {@link HoverBehavior} can be registered on an {@link IVisualPart} in
 * order to react to {@link HoverModel} changes. It generates
 * {@link AbstractFeedbackPart}s and {@link AbstractHandlePart}s.
 *
 * @author mwienand
 *
 */
public class HoverBehavior extends AbstractBehavior {

	/**
	 * The adapter role for the {@link IFeedbackPartFactory} that is used to
	 * generate hover feedback parts.
	 */
	public static final String HOVER_FEEDBACK_PART_FACTORY = "HOVER_FEEDBACK_PART_FACTORY";

	/**
	 * The adapter role for the {@link IHandlePartFactory} that is used to
	 * generate hover handle parts.
	 */
	public static final String HOVER_HANDLE_PART_FACTORY = "HOVER_HANDLE_PART_FACTORY";

	private ChangeListener<IVisualPart<? extends Node>> hoverObserver = new ChangeListener<IVisualPart<? extends Node>>() {
		@Override
		public void changed(
				ObservableValue<? extends IVisualPart<? extends Node>> observable,
				IVisualPart<? extends Node> oldValue,
				IVisualPart<? extends Node> newValue) {
			onHoverChange(oldValue, newValue);
		}
	};

	private final Map<IVisualPart<? extends Node>, Effect> effects = new HashMap<>();

	@Override
	protected void doActivate() {
		// create feedback and handles if we are already hovered
		HoverModel hoverModel = getHoverModel();
		IVisualPart<? extends Node> hover = hoverModel.getHover();
		if (hover != null) {
			onHoverChange(null, hover);
		}

		// register HoverModel observer
		hoverModel.hoverProperty().addListener(hoverObserver);
	}

	@Override
	protected void doDeactivate() {
		// unregister HoverModel observer
		HoverModel hoverModel = getHoverModel();
		hoverModel.hoverProperty().removeListener(hoverObserver);

		// remove any pending feedback and handles
		IVisualPart<? extends Node> hover = hoverModel.getHover();
		if (hover != null) {
			onHoverChange(hover, null);
		}
	}

	@Override
	protected IFeedbackPartFactory getFeedbackPartFactory(IViewer viewer) {
		return getFeedbackPartFactory(viewer, HOVER_FEEDBACK_PART_FACTORY);
	}

	/**
	 * Returns the {@link Effect} that is applied to {@link IHandlePart}s as a
	 * replacement for {@link IFeedbackPart}s which are created for normal
	 * parts.
	 *
	 * @param contextMap
	 *            A map with context information that might be needed to
	 *            identify the concrete creation context.
	 * @return The {@link Effect} that is applied to {@link IHandlePart}s as a
	 *         replacement for {@link IFeedbackPart}s which are created for
	 *         normal parts.
	 */
	public Effect getHandleHoverFeedbackEffect(Map<Object, Object> contextMap) {
		DropShadow effect = new DropShadow();
		effect.setRadius(5);
		return effect;
	}

	@Override
	protected IHandlePartFactory getHandlePartFactory(IViewer viewer) {
		return getHandlePartFactory(viewer, HOVER_HANDLE_PART_FACTORY);
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

	private void onHoverChange(IVisualPart<? extends Node> oldHovered,
			IVisualPart<? extends Node> newHovered) {
		if (oldHovered != null) {
			if (oldHovered instanceof IHandlePart) {
				// unhovering a handle part
				// remove feedback effect
				if (effects.containsKey(oldHovered)) {
					oldHovered.getVisual()
							.setEffect(effects.remove(oldHovered));
				} else {
					throw new IllegalStateException(
							"Cannot unhover/restore effect <" + oldHovered
									+ ">.");
				}
			} else {
				removeHandles(oldHovered);
				removeFeedback(oldHovered);
			}
		}
		if (newHovered != null) {
			if (newHovered instanceof IHandlePart) {
				// hovering a handle part
				// add feedback effect
				effects.put(newHovered, newHovered.getVisual().getEffect());
				newHovered.getVisual().setEffect(
						getHandleHoverFeedbackEffect(Collections.emptyMap()));
			} else {
				addFeedback(newHovered);
				addHandles(newHovered);
			}
		}
	}
}
