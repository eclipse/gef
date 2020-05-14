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
package org.eclipse.gef.mvc.fx.parts;

import java.util.Set;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

/**
 * The {@link SnappingFeedbackPart} visualizes a
 * {@link SnappingLocation} by drawing a red line at the
 * {@link SnappingLocation} through the whole viewport.
 */
public class SnappingFeedbackPart extends AbstractFeedbackPart<Line> {

	private SnappingLocation snappingLocation = null;

	private ChangeListener<? super Number> viewportSizeObserver = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			onViewportSizeChanged();
		}
	};

	private ChangeListener<? super Number> viewportTranslationObserver = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			onViewportTranslationChanged();
		}
	};

	@Override
	protected void doAttachToAnchorageVisual(
			IVisualPart<? extends Node> anchorage, String role) {
		super.doAttachToAnchorageVisual(anchorage, role);
		if (getAnchoragesUnmodifiable().size() == 1) {
			// add listeners
			InfiniteCanvas canvas = (InfiniteCanvas) anchorage.getRoot()
					.getViewer().getCanvas();
			canvas.widthProperty().addListener(viewportSizeObserver);
			canvas.heightProperty().addListener(viewportSizeObserver);
			canvas.horizontalScrollOffsetProperty()
					.addListener(viewportTranslationObserver);
			canvas.verticalScrollOffsetProperty()
					.addListener(viewportTranslationObserver);
		}
	}

	@Override
	protected Line doCreateVisual() {
		Line line = new Line();
		line.setStroke(Color.RED);
		line.setStrokeWidth(1);
		line.setStrokeType(StrokeType.CENTERED);
		line.setStrokeLineCap(StrokeLineCap.BUTT);
		line.setVisible(false);
		return line;
	}

	@Override
	protected void doDetachFromAnchorageVisual(
			IVisualPart<? extends Node> anchorage, String role) {
		super.doDetachFromAnchorageVisual(anchorage, role);
		if (getAnchoragesUnmodifiable().isEmpty()) {
			// remove listeners
			InfiniteCanvas canvas = (InfiniteCanvas) anchorage.getRoot()
					.getViewer().getCanvas();
			canvas.widthProperty().removeListener(viewportSizeObserver);
			canvas.heightProperty().removeListener(viewportSizeObserver);
			canvas.horizontalScrollOffsetProperty()
					.removeListener(viewportTranslationObserver);
			canvas.verticalScrollOffsetProperty()
					.removeListener(viewportTranslationObserver);
		}
	}

	@Override
	protected void doRefreshVisual(final Line visual) {
		Set<IVisualPart<? extends Node>> anchorages = getAnchoragesUnmodifiable()
				.keySet();
		if (anchorages.isEmpty()) {
			return;
		}

		IVisualPart<? extends Node> firstAnchorage = anchorages.iterator()
				.next();
		if (!(firstAnchorage instanceof IContentPart)) {
			throw new IllegalStateException(
					"SnapToLocationFeedbackPart can only be attached to IContentPart.");
		}

		// host: the context part for which feedback is rendered
		IContentPart<? extends Node> host = (IContentPart<? extends Node>) firstAnchorage;

		// determine scrollable bounds in scene
		InfiniteCanvas canvas = (InfiniteCanvas) host.getRoot().getViewer()
				.getCanvas();
		final Bounds canvasBoundsInScene = canvas
				.localToScene(canvas.getLayoutBounds());

		// hide visual
		canvas.widthProperty().removeListener(viewportSizeObserver);
		canvas.heightProperty().removeListener(viewportSizeObserver);
		canvas.horizontalScrollOffsetProperty()
				.removeListener(viewportTranslationObserver);
		canvas.verticalScrollOffsetProperty()
				.removeListener(viewportTranslationObserver);
		visual.setVisible(false);
		canvas.horizontalScrollOffsetProperty()
				.addListener(viewportTranslationObserver);
		canvas.verticalScrollOffsetProperty()
				.addListener(viewportTranslationObserver);
		canvas.widthProperty().addListener(viewportSizeObserver);
		canvas.heightProperty().addListener(viewportSizeObserver);

		// update visual
		if (getSnappingLocation().getOrientation() == Orientation.HORIZONTAL) {
			// x location saved in snapping location
			double xInScene = getSnappingLocation().getPositionInScene();

			// transform to local coordinates
			// XXX: an offset is added/subtracted from the scrollable bounds
			// min/max locations so that the feedback does not change the
			// scrollable bounds (which prevents a StackOverflowError)
			Point2D startLocal = visual.sceneToLocal(xInScene,
					canvasBoundsInScene.getMinY() + 3);
			Point2D endLocal = visual.sceneToLocal(xInScene,
					canvasBoundsInScene.getMaxY() - 3);

			// ensure pixel accuracy
			double xLocal = Math.floor(startLocal.getX()) + 0.5;
			visual.setStartX(xLocal);
			visual.setStartY(Math.floor(startLocal.getY() + 1) + 0.5);
			visual.setEndX(xLocal);
			visual.setEndY(Math.floor(endLocal.getY() - 1) - 0.5);
		} else {
			// y location saved in snapping location
			double yInScene = getSnappingLocation().getPositionInScene();

			// transform to local coordinates
			// XXX: an offset is added/subtracted from the scrollable bounds
			// min/max locations so that the feedback does not change the
			// scrollable bounds (which prevents a StackOverflowError)
			Point2D startLocal = visual
					.sceneToLocal(canvasBoundsInScene.getMinX() + 3, yInScene);
			Point2D endLocal = visual
					.sceneToLocal(canvasBoundsInScene.getMaxX() - 3, yInScene);

			// ensure pixel accuracy
			double yLocal = Math.floor(startLocal.getY()) + 0.5;
			visual.setStartX(Math.floor(startLocal.getX() + 1) + 0.5);
			visual.setStartY(yLocal);
			visual.setEndX(Math.floor(endLocal.getX() - 1) - 0.5);
			visual.setEndY(yLocal);
		}

		// XXX: ensure visual is inside scrollable bounds to prevent
		// a StackOverflowError (bounds change => refresh feedback
		// => bounds change => refresh feedback => ...)
		Bounds visualBoundsInScene = visual.getParent()
				.localToScene(visual.getBoundsInParent());
		visualBoundsInScene = Geometry2FX.toFXBounds(
				FX2Geometry.toRectangle(visualBoundsInScene).expand(2, 2));
		if (canvasBoundsInScene.contains(visualBoundsInScene)) {
			// show visual
			canvas.widthProperty().removeListener(viewportSizeObserver);
			canvas.heightProperty().removeListener(viewportSizeObserver);
			canvas.horizontalScrollOffsetProperty()
					.removeListener(viewportTranslationObserver);
			canvas.verticalScrollOffsetProperty()
					.removeListener(viewportTranslationObserver);
			visual.setVisible(true);
			canvas.horizontalScrollOffsetProperty()
					.addListener(viewportTranslationObserver);
			canvas.verticalScrollOffsetProperty()
					.addListener(viewportTranslationObserver);
			canvas.widthProperty().addListener(viewportSizeObserver);
			canvas.heightProperty().addListener(viewportSizeObserver);
		}
		// else {
		// System.err.println(
		// "[ERROR] Cannot show snapping feedback because it exceeds the
		// scrollable bounds.");
		// }
	}

	/**
	 * Returns the {@link SnappingLocation} for which feedback is visualized by
	 * this {@link SnappingFeedbackPart}.
	 *
	 * @return The {@link SnappingLocation} for which feedback is visualized by
	 *         this {@link SnappingFeedbackPart}.
	 */
	public SnappingLocation getSnappingLocation() {
		return snappingLocation;
	}

	/**
	 * Callback method that is invoked when the viewport size is changed.
	 */
	protected void onViewportSizeChanged() {
		refreshVisual();
	}

	/**
	 * Callback method that is invoked when the viewport translation (scrolling)
	 * is changed.
	 */
	protected void onViewportTranslationChanged() {
		refreshVisual();
	}

	/**
	 * Sets the {@link SnappingLocation} for this feedback part to the given
	 * value.
	 *
	 * @param snappingLocation
	 *            The new {@link SnappingLocation} for this feedback part.
	 */
	public void setSnappingLocation(SnappingLocation snappingLocation) {
		this.snappingLocation = snappingLocation;
	}
}
