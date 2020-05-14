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
package org.eclipse.gef.mvc.fx.parts;

import java.util.List;
import java.util.Set;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/**
 * The {@link SelectionFeedbackPart} is an {@link AbstractFeedbackPart} that is
 * parameterized by <code>GeometryNode&lt;IGeometry&gt;</code>.
 *
 * @author mwienand
 *
 */
public class SelectionFeedbackPart
		extends AbstractFeedbackPart<GeometryNode<IGeometry>> {

	/**
	 * The stroke width for selection feedback.
	 */
	protected static final double DEFAULT_STROKE_WIDTH = 1.5d;

	private Provider<? extends IGeometry> feedbackGeometryProvider;

	/**
	 * Default constructor.
	 */
	public SelectionFeedbackPart() {
	}

	@Override
	protected GeometryNode<IGeometry> doCreateVisual() {
		GeometryNode<IGeometry> feedbackVisual = new GeometryNode<>();
		feedbackVisual.setFill(Color.TRANSPARENT);
		feedbackVisual.setMouseTransparent(true);
		feedbackVisual.setManaged(false);
		feedbackVisual.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		return feedbackVisual;
	}

	@Override
	public void doRefreshVisual(GeometryNode<IGeometry> visual) {
		Set<IVisualPart<? extends Node>> anchorages = getAnchoragesUnmodifiable()
				.keySet();
		if (anchorages.isEmpty()) {
			return;
		}
		IVisualPart<? extends Node> anchorage = anchorages.iterator().next();
		IRootPart<? extends Node> root = anchorage.getRoot();
		if (root == null) {
			return;
		}
		IViewer viewer = anchorage.getRoot().getViewer();
		if (viewer == null) {
			return;
		}
		IGeometry feedbackGeometry = getFeedbackGeometry();
		if (feedbackGeometry == null) {
			return;
		}

		// FIXME: Investigate why the StrokeType needs to be set before setting
		// the geometry in order to prevent a vertical offset.

		// update stroke type
		if (feedbackGeometry instanceof ICurve) {
			// stroke centered
			visual.setStrokeType(StrokeType.CENTERED);
		} else {
			// stroke outside
			visual.setStrokeType(StrokeType.OUTSIDE);
			// TODO: adjust stroke width to get hair lines
		}

		// update geometry
		visual.setGeometry(feedbackGeometry);

		// update color
		List<IContentPart<? extends Node>> selected = viewer
				.getAdapter(SelectionModel.class).getSelectionUnmodifiable();
		if (selected != null && selected.size() > 0
				&& selected.get(0) == anchorage) {
			// primary selection
			visual.setStroke(getPrimarySelectionColor());
		} else {
			// secondary selection
			visual.setStroke(getSecondarySelectionColor());
		}
	}

	/**
	 * Returns the {@link IGeometry} that is provided by this part's
	 * {@link #setGeometryProvider(Provider) feedback geometry provider}.
	 *
	 * @return The {@link IGeometry} that is provided by this part's
	 *         {@link #setGeometryProvider(Provider) feedback geometry provider}
	 *         .
	 */
	protected IGeometry getFeedbackGeometry() {
		return NodeUtils.sceneToLocal(getVisual().getParent(),
				feedbackGeometryProvider.get());
	}

	/**
	 * Returns the primary selection {@link Color}.
	 *
	 * @return The primary selection {@link Color}.
	 */
	protected Color getPrimarySelectionColor() {
		@SuppressWarnings("serial")
		Provider<Color> connectedColorProvider = getViewer()
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Color>>() {
				}, DefaultSelectionFeedbackPartFactory.PRIMARY_SELECTION_FEEDBACK_COLOR_PROVIDER));
		return connectedColorProvider == null
				? DefaultSelectionFeedbackPartFactory.DEFAULT_PRIMARY_SELECTION_FEEDBACK_COLOR
				: connectedColorProvider.get();
	}

	/**
	 * Returns the primary selection {@link Color}.
	 *
	 * @return The primary selection {@link Color}.
	 */
	protected Color getSecondarySelectionColor() {
		@SuppressWarnings("serial")
		Provider<Color> connectedColorProvider = getViewer()
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Color>>() {
				}, DefaultSelectionFeedbackPartFactory.SECONDARY_SELECTION_FEEDBACK_COLOR_PROVIDER));
		return connectedColorProvider == null
				? DefaultSelectionFeedbackPartFactory.DEFAULT_SECONDARY_SELECTION_FEEDBACK_COLOR
				: connectedColorProvider.get();
	}

	/**
	 * Sets the feedback geometry provider (
	 * <code>Provider&lt;IGeometry&gt;</code>) of this part to the given value.
	 *
	 * @param geometryProvider
	 *            The new feedback geometry provider for this part.
	 */
	public void setGeometryProvider(
			Provider<? extends IGeometry> geometryProvider) {
		feedbackGeometryProvider = geometryProvider;
	}
}
