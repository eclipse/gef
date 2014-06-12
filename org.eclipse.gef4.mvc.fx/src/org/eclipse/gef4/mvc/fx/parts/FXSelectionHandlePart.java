/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.bindings.IProvider;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * {@link IHandlePart} implementation used for selection handles. The individual
 * selection handles are created by the {@link FXSelectionBehavior} of the
 * selected {@link IVisualPart}. To exchange the default handle implementation,
 * the user has to override the corresponding method in
 * {@link FXSelectionBehavior}.
 *
 * @author mwienand
 *
 */
// TODO: rename to something more reasonable
public class FXSelectionHandlePart extends AbstractFXHandlePart implements
Comparable<FXSelectionHandlePart> {

	public static final Color STROKE_DARK_BLUE = Color.web("#5a61af");
	public static final Color FILL_BLUE = Color.web("#d5faff");
	public static final double SIZE = 5d;

	protected Shape visual;
	protected IContentPart<Node> targetPart;
	protected IProvider<IGeometry> handleGeometryProvider;

	/**
	 * See {@link #getSegmentIndex()}.
	 */
	protected int segmentIndex;

	/**
	 * See #getsegmentparameter().
	 */
	protected double segmentParameter;

	public FXSelectionHandlePart(IContentPart<Node> targetPart,
			IProvider<IGeometry> handleGeometryProvider, int segmentIndex) {
		this(targetPart, handleGeometryProvider, segmentIndex, 0);
	}

	public FXSelectionHandlePart(IContentPart<Node> targetPart,
			IProvider<IGeometry> handleGeometryProvider, int segmentIndex,
			double segmentParameter) {
		super();
		this.targetPart = targetPart;
		this.handleGeometryProvider = handleGeometryProvider;
		this.segmentIndex = segmentIndex;
		this.segmentParameter = segmentParameter;

		visual = createHandleVisual(handleGeometryProvider.get());
	}

	@Override
	public int compareTo(FXSelectionHandlePart o) {
		return (int) ((100 * getSegmentIndex() + 10 * getSegmentParameter()) - (100 * o
				.getSegmentIndex() + 10 * o.getSegmentParameter()));
	}

	/**
	 * Creates the visual representation of this selection handle depending on
	 * the given handle geometry. Per default, rectangular handles are created
	 * if the handle geometry is a {@link Rectangle}. Otherwise, round handles
	 * are created.
	 *
	 * @param handleGeometry
	 * @return {@link Shape} representing the handle visually
	 */
	protected Shape createHandleVisual(IGeometry handleGeometry) {
		Shape shape = null;
		// create shape dependent on passed in selection geometry
		if (handleGeometry instanceof org.eclipse.gef4.geometry.planar.Rectangle) {
			shape = new Rectangle();
			((Rectangle) shape).setWidth(SIZE);
			((Rectangle) shape).setHeight(SIZE);
			shape.setTranslateX(-SIZE / 2);
			shape.setTranslateY(-SIZE / 2);
		} else {
			shape = new Circle(SIZE / 2d);
		}

		// initialize invariant visual properties
		shape.setStroke(STROKE_DARK_BLUE);
		shape.setStrokeWidth(1);
		shape.setStrokeType(StrokeType.OUTSIDE);
		return shape;
	}

	protected Point getPosition(IGeometry handleGeometry) {
		Point position = null;

		if (handleGeometry instanceof IShape) {
			IShape shape = (IShape) handleGeometry;
			ICurve[] segments = shape.getOutlineSegments();
			position = segments[segmentIndex].toBezier()[0]
					.get(segmentParameter);
		} else if (handleGeometry instanceof ICurve) {
			ICurve curve = (ICurve) handleGeometry;
			BezierCurve[] beziers = curve.toBezier();
			if (beziers == null) {
				position = new Point();
			} else if (segmentIndex >= beziers.length) {
				position = new Point();
			} else {
				position = beziers[segmentIndex].get(segmentParameter);
			}
		} else {
			throw new IllegalStateException(
					"Unable to determine handle position: Expected IShape or ICurve but got: "
							+ handleGeometry);
		}

		return position;
	}

	/**
	 * The segmentIndex specifies the segment of the IGeometry provided by the
	 * handle geometry provider on which this selection handle part is
	 * positioned.
	 *
	 * For a shape geometry, segments are determined by the
	 * {@link IShape#getOutlineSegments()} method.
	 *
	 * For a curve geometry, segments are determined by the
	 * {@link ICurve#toBezier()} method.
	 *
	 * The exact position on the segment is specified by the
	 * {@link #getSegmentParameter() segmentParameter}.
	 *
	 * @return segmentIndex
	 */
	public int getSegmentIndex() {
		return segmentIndex;
	}

	/**
	 * The segmentParameter is a value between 0 and 1. It determines the final
	 * point on the segment which this selection handle part belongs to.
	 *
	 * @return segmentParameter
	 */
	public double getSegmentParameter() {
		return segmentParameter;
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		FXRootPart rootPart = (FXRootPart) getRoot();
		if (rootPart == null) {
			return;
		}

		if (getSegmentIndex() == -1) {
			// hide those that have "invalid" index (this may happen during
			// life-feedback, when a waypoint is removed)
			visual.setVisible(false);
		} else {
			visual.setVisible(true);

			// get new position (in local coordinate space)
			Point position = getPosition(handleGeometryProvider.get());

			// transform to handle space
			Node targetVisual = targetPart.getVisual();
			Pane handleLayer = rootPart.getHandleLayer();
			Point2D point2d = handleLayer.sceneToLocal(targetVisual
					.localToScene(position.x, position.y));

			// update visual layout position
			visual.setLayoutX(point2d.getX());
			visual.setLayoutY(point2d.getY());

			// update color
			if (getSegmentParameter() == 0.5) {
				// if (getSegmentIndex() == 0) {
				// visual.setFill(Color.RED);
				// } else if (getSegmentIndex() == 1) {
				// visual.setFill(Color.YELLOW);
				// } else if (getSegmentIndex() == 2) {
				// visual.setFill(Color.GREEN);
				// } else if (getSegmentIndex() == 3) {
				// visual.setFill(Color.PURPLE);
				// } else {
				visual.setFill(Color.WHITE);
				// }
			} else {
				visual.setFill(FILL_BLUE);
			}
		}
	}

	/**
	 * Sets the segment index. Refreshs the handle visual.
	 *
	 * @param segmentIndex
	 * @see #getSegmentIndex()
	 */
	public void setSegmentIndex(int segmentIndex) {
		this.segmentIndex = segmentIndex;
		refreshVisual();
	}

	/**
	 * Sets the segment parameter. Refreshs the handle visual.
	 *
	 * @param segmentParameter
	 * @see #getSegmentParameter()
	 */
	public void setSegmentParameter(double segmentParameter) {
		this.segmentParameter = segmentParameter;
		refreshVisual();
	}

}
