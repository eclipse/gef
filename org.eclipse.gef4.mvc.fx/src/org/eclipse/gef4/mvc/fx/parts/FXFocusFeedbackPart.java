/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import java.util.List;
import java.util.Set;

import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/**
 * The {@link FXFocusFeedbackPart} visualizes focus feedback.
 */
public class FXFocusFeedbackPart
		extends AbstractFXFeedbackPart<GeometryNode<IGeometry>> {

	private static final double STROKE_WIDTH = 1.5d;

	/**
	 * The default stroke color for this part's visualization.
	 */
	public static final Color DEFAULT_STROKE = Color.web("#8ec0fc");

	private Provider<? extends IGeometry> feedbackGeometryProvider;

	private ListChangeListener<IContentPart<Node, ? extends Node>> selectionModelObserver = new ListChangeListener<IContentPart<Node, ? extends Node>>() {
		@Override
		public void onChanged(
				ListChangeListener.Change<? extends IContentPart<Node, ? extends Node>> c) {
			refreshVisual();
		}
	};

	/**
	 * Default constructor.
	 */
	public FXFocusFeedbackPart() {
	}

	@Override
	protected GeometryNode<IGeometry> createVisual() {
		GeometryNode<IGeometry> visual = new GeometryNode<>();
		visual.setFill(Color.TRANSPARENT);
		visual.setMouseTransparent(true);
		visual.setManaged(false);
		visual.setStrokeType(StrokeType.OUTSIDE);
		visual.setStrokeWidth(STROKE_WIDTH);
		visual.setStroke(DEFAULT_STROKE);
		return visual;
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		@SuppressWarnings("serial")
		SelectionModel<Node> selectionModel = getViewer()
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				});
		selectionModel.selectionUnmodifiableProperty()
				.addListener(selectionModelObserver);
	}

	@Override
	protected void doDeactivate() {
		@SuppressWarnings("serial")
		SelectionModel<Node> selectionModel = getViewer()
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				});
		selectionModel.selectionUnmodifiableProperty()
				.removeListener(selectionModelObserver);
		super.doDeactivate();
	}

	@Override
	public void doRefreshVisual(GeometryNode<IGeometry> visual) {
		Set<IVisualPart<Node, ? extends Node>> anchorages = getAnchoragesUnmodifiable()
				.keySet();
		if (anchorages.isEmpty()) {
			return;
		}
		IGeometry feedbackGeometry = getFeedbackGeometry();
		if (feedbackGeometry == null) {
			return;
		}

		// update geometry
		visual.setGeometry(feedbackGeometry);

		// determine selection
		IVisualPart<Node, ? extends Node> anchorage = anchorages.iterator()
				.next();
		IViewer<Node> viewer = anchorage.getRoot().getViewer();
		@SuppressWarnings("serial")
		List<IContentPart<Node, ? extends Node>> selected = viewer
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				}).getSelectionUnmodifiable();

		// adjust feedback depending on geometry
		if (feedbackGeometry instanceof ICurve) {
			// stroke centered
			visual.setStrokeType(StrokeType.CENTERED);
			if (selected.contains(anchorage)) {
				// place behind selection feedback
				visual.setStrokeWidth(FXSelectionFeedbackPart.STROKE_WIDTH * 3);
				visual.toBack();
			} else {
				visual.setStrokeWidth(STROKE_WIDTH);
			}
		} else {
			// stroke outside
			visual.setStrokeType(StrokeType.OUTSIDE);
			// TODO: adjust stroke width to get hair lines
			// increase geometry size if selected
			if (selected.contains(anchorage)) {
				visual.resizeGeometry(
						feedbackGeometry.getBounds().getWidth()
								+ FXSelectionFeedbackPart.STROKE_WIDTH * 2,
						feedbackGeometry.getBounds().getHeight()
								+ FXSelectionFeedbackPart.STROKE_WIDTH * 2);
				visual.setGeometry(
						feedbackGeometry.getTransformed(new AffineTransform(1,
								0, 0, 1, -FXSelectionFeedbackPart.STROKE_WIDTH,
								-FXSelectionFeedbackPart.STROKE_WIDTH)));
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
		return NodeUtils.sceneToLocal(getVisual(),
				feedbackGeometryProvider.get());
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
