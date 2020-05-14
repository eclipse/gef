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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.mvc.fx.models.SnappingModel;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;

/**
 * The {@link SnappingBehavior} is responsible for creating and removing
 * feedback and handles in response to {@link SnappingModel} changes.
 */
public class SnappingBehavior extends AbstractBehavior {

	/**
	 * Defines the role for the {@link IFeedbackPartFactory} that is used to
	 * generate snap-to feedback.
	 */
	public static final String SNAPPING_FEEDBACK_PART_FACTORY = "snappingFeedbackPartFactory";

	private ListChangeListener<SnappingLocation> snappingLocationsObserver = new ListChangeListener<SnappingLocation>() {
		@Override
		public void onChanged(
				javafx.collections.ListChangeListener.Change<? extends SnappingLocation> c) {
			List<? extends SnappingLocation> previousContents = CollectionUtils
					.getPreviousContents(c);
			onSnappingLocationsChanged(previousContents, c.getList());
		}
	};

	@Override
	protected void doActivate() {
		super.doActivate();
		SnappingModel snappingModel = getHost().getRoot().getViewer()
				.getAdapter(SnappingModel.class);
		snappingModel.snappingLocationsProperty()
				.addListener(snappingLocationsObserver);
	}

	@Override
	protected void doDeactivate() {
		SnappingModel snappingModel = getHost().getRoot().getViewer()
				.getAdapter(SnappingModel.class);
		snappingModel.snappingLocationsProperty()
				.removeListener(snappingLocationsObserver);
		super.doDeactivate();
	}

	/**
	 * Returns the {@link IFeedbackPartFactory} for selection feedback.
	 *
	 * @return The {@link IFeedbackPartFactory} for selection feedback.
	 */
	@Override
	protected IFeedbackPartFactory getFeedbackPartFactory(IViewer viewer) {
		return getFeedbackPartFactory(viewer, SNAPPING_FEEDBACK_PART_FACTORY);
	}

	/**
	 * Callback method that is called in response to {@link SnappingModel}
	 * changes.
	 *
	 * @param oldValue
	 *            A {@link List} containing the {@link SnappingLocation}s
	 *            previously stored in the {@link SnappingModel}.
	 * @param newValue
	 *            A {@link List} containing the {@link SnappingLocation}s
	 *            currently stored in the {@link SnappingModel}.
	 */
	protected void onSnappingLocationsChanged(
			List<? extends SnappingLocation> oldValue,
			List<? extends SnappingLocation> newValue) {
		// extract parts from both lists
		Set<IVisualPart<? extends Node>> oldParts = new HashSet<>();
		for (SnappingLocation sl : oldValue) {
			oldParts.add(sl.getPart());
		}
		Set<IVisualPart<? extends Node>> newParts = new HashSet<>();
		for (SnappingLocation sl : newValue) {
			if (sl.getPart().getRoot() == null) {
				throw new IllegalStateException(
						"ContentPart has no link to the RootPart!");
			}
			newParts.add(sl.getPart());
		}
		// remove feedback for all parts that were previously present
		for (IVisualPart<? extends Node> part : oldParts) {
			removeFeedback(part);
		}
		// add feedback for all parts that are currently present
		for (IVisualPart<? extends Node> part : newParts) {
			addFeedback(part);
		}
	}
}
