/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/**
 * The {@link FocusFeedbackPart} visualizes focus feedback.
 */
public class FocusFeedbackPart
		extends AbstractFeedbackPart<GeometryNode<IGeometry>> {

	private static final double DEFAULT_STROKE_WIDTH = 1.5d;

	private Provider<? extends IGeometry> feedbackGeometryProvider;

	private ListChangeListener<IContentPart<? extends Node>> selectionModelObserver = new ListChangeListener<IContentPart<? extends Node>>() {
		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<? extends Node>> c) {
			refreshVisual();
		}
	};

	/**
	 * Default constructor.
	 */
	public FocusFeedbackPart() {
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		SelectionModel selectionModel = getViewer()
				.getAdapter(SelectionModel.class);
		selectionModel.selectionUnmodifiableProperty()
				.addListener(selectionModelObserver);
	}

	@Override
	protected GeometryNode<IGeometry> doCreateVisual() {
		GeometryNode<IGeometry> visual = new GeometryNode<>();
		visual.setFill(Color.TRANSPARENT);
		visual.setMouseTransparent(true);
		visual.setManaged(false);
		visual.setStrokeType(StrokeType.OUTSIDE);
		visual.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		visual.setStroke(getFocusStroke());
		return visual;
	}

	@Override
	protected void doDeactivate() {
		SelectionModel selectionModel = getViewer()
				.getAdapter(SelectionModel.class);
		selectionModel.selectionUnmodifiableProperty()
				.removeListener(selectionModelObserver);
		super.doDeactivate();
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

		IGeometry feedbackGeometry = getFeedbackGeometry();
		if (feedbackGeometry == null) {
			return;
		}

		// determine selection
		IViewer viewer = root.getViewer();
		List<IContentPart<? extends Node>> selected = viewer
				.getAdapter(SelectionModel.class).getSelectionUnmodifiable();

		// FIXME: Investigate why the StrokeType needs to be set before setting
		// the geometry in order to prevent a vertical offset.

		// adjust feedback depending on geometry
		if (feedbackGeometry instanceof ICurve) {
			// stroke centered
			visual.setStrokeType(StrokeType.CENTERED);
			// increase geometry size if selected
			if (selected.contains(anchorage)) {
				visual.setStrokeWidth(
						SelectionFeedbackPart.DEFAULT_STROKE_WIDTH * 2);
			} else {
				visual.setStrokeWidth(DEFAULT_STROKE_WIDTH);
			}
		} else {
			// stroke outside
			visual.setStrokeType(StrokeType.OUTSIDE);
		}

		// update geometry
		visual.setGeometry(feedbackGeometry);

		// adjust feedback depending on geometry
		if (!(feedbackGeometry instanceof ICurve)) {
			// increase geometry size if selected
			if (selected.contains(anchorage)) {
				Rectangle feedbackBounds = feedbackGeometry.getBounds();
				visual.resizeGeometry(feedbackBounds.getWidth()
						+ SelectionFeedbackPart.DEFAULT_STROKE_WIDTH * 2,
						feedbackBounds.getHeight()
								+ SelectionFeedbackPart.DEFAULT_STROKE_WIDTH
										* 2);
				visual.relocateGeometry(
						feedbackBounds.getX()
								- SelectionFeedbackPart.DEFAULT_STROKE_WIDTH,
						feedbackBounds.getY()
								- SelectionFeedbackPart.DEFAULT_STROKE_WIDTH);
			}
		}
	}

	/**
	 * Returns the {@link IGeometry} that is provided by this part's
	 * {@link #setGeometryProvider(Provider) geometry provider}.
	 *
	 * @return The {@link IGeometry} that is provided by this part's geometry
	 *         provider.
	 */
	protected IGeometry getFeedbackGeometry() {
		return NodeUtils.sceneToLocal(getVisual().getParent(),
				feedbackGeometryProvider.get());
	}

	/**
	 * Returns the {@link Color} that is used to stroke focus feedback.
	 *
	 * @return The {@link Color} that is used to stroke focus feedback.
	 */
	@SuppressWarnings("serial")
	protected Color getFocusStroke() {
		Provider<Color> focusFeedbackColorProvider = getViewer()
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Color>>() {
				}, DefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_COLOR_PROVIDER));
		return focusFeedbackColorProvider == null
				? DefaultFocusFeedbackPartFactory.DEFAULT_FOCUS_FEEDBACK_COLOR
				: focusFeedbackColorProvider.get();
	}

	/**
	 * Sets the <code>Provider&lt;IGeometry&gt;</code> of this part to the given
	 * value.
	 *
	 * @param geometryProvider
	 *            The new <code>Provider&lt;IGeometry&gt;</code> for this part.
	 */
	public void setGeometryProvider(
			Provider<? extends IGeometry> geometryProvider) {
		feedbackGeometryProvider = geometryProvider;
	}
}
