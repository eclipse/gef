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

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;
import com.google.inject.Provider;

/**
 * A {@link FXSegmentHandlePart} is bound to one segment of a handle geometry.
 * The segmentIndex identifies that segment (0, 1, 2, ...). The segmentParameter
 * specifies the position of this handle part on the segment (0 = start, 0.5 =
 * mid, 1 = end).
 *
 * These parts are used for selection feedback per default.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXSegmentHandlePart extends AbstractFXHandlePart implements
Comparable<FXSegmentHandlePart> {

	public static final Color STROKE_DARK_BLUE = Color.web("#5a61af");

	private final static Color FILL_CONNECTED = Color.web("#ff0000");
	public static final Color FILL_UNCONNECTED = Color.web("#d5faff");
	public static final double SIZE = 5d;

	protected Shape visual;
	protected Provider<IGeometry> handleGeometryProvider;

	private int segmentIndex = -1;
	private double segmentParameter = 0.0;

	public FXSegmentHandlePart(Provider<IGeometry> handleGeometryProvider,
			int segmentIndex) {
		this(handleGeometryProvider, segmentIndex, 0);
	}

	public FXSegmentHandlePart(Provider<IGeometry> handleGeometryProvider,
			int segmentIndex, double segmentParameter) {
		super();
		this.handleGeometryProvider = handleGeometryProvider;
		this.segmentIndex = segmentIndex;
		this.segmentParameter = segmentParameter;

		visual = createHandleVisual(handleGeometryProvider.get());
	}

	@Override
	public int compareTo(FXSegmentHandlePart o) {
		// if we are bound to the same anchorages, we may compare segment
		// positions, otherwise we are not comparable
		if (!getAnchorages().equals(o.getAnchorages())) {
			throw new IllegalArgumentException(
					"Can only compare FXSegmentHandleParts that are bound to the same anchorages.");
		}
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
		// Shape shape = null;
		// // create shape dependent on passed in selection geometry
		// // TODO: this should not be done here but within the factory
		// if (handleGeometry instanceof IShape) {
		// shape = new Rectangle();
		// ((Rectangle) shape).setWidth(SIZE);
		// ((Rectangle) shape).setHeight(SIZE);
		// shape.setTranslateX(-SIZE / 2);
		// shape.setTranslateY(-SIZE / 2);
		// } else {
		// shape = new Circle(SIZE / 2d);
		// }
		Shape shape = new Circle(SIZE / 2d);

		// initialize invariant visual properties
		shape.setStroke(STROKE_DARK_BLUE);
		shape.setStrokeWidth(1);
		shape.setStrokeType(StrokeType.OUTSIDE);
		return shape;
	}

	@Override
	public void doRefreshVisual() {
		FXRootPart rootPart = (FXRootPart) getRoot();
		SetMultimap<IVisualPart<Node>, String> anchorages = getAnchorages();
		if (rootPart == null || anchorages.keySet().size() != 1) {
			return;
		}

		if (getSegmentIndex() == -1) {
			// hide those that have "invalid" index (this may happen during
			// life-feedback, when a waypoint is removed)
			visual.setVisible(false);
		} else {
			visual.setVisible(true);

			// get new position (in parent coordinate space)
			IGeometry handleGeometryInScene = handleGeometryProvider.get();
			IGeometry handleGeometryInParent = FXUtils.sceneToLocal(
					visual.getParent(), handleGeometryInScene);
			Point positionInParent = getPosition(handleGeometryInParent);

			// transform to handle space
			IVisualPart<Node> targetPart = anchorages.keySet().iterator()
					.next();

			// update visual layout position
			visual.setLayoutX(positionInParent.x);
			visual.setLayoutY(positionInParent.y);

			// update color
			if (segmentParameter != 0.0 && segmentParameter != 1.0) {
				visual.setFill(Color.WHITE);
			} else {
				// determine connected state for end point handles
				boolean connected = false;
				if (targetPart.getVisual() instanceof IFXConnection) {
					IFXConnection connection = (IFXConnection) targetPart
							.getVisual();
					if (segmentIndex == 0 && segmentParameter == 0.0) {
						connected = connection.isStartConnected();
					} else if (segmentParameter == 1.0) {
						IGeometry geom = handleGeometryInScene;
						if (geom instanceof ICurve) {
							BezierCurve[] beziers = ((ICurve) geom).toBezier();
							if (beziers.length - 1 == segmentIndex) {
								connected = connection.isEndConnected();
							}
						}
					}
				}
				if (connected) {
					visual.setFill(FILL_CONNECTED);
				} else {
					visual.setFill(FILL_UNCONNECTED);
				}
			}
		}

	}

	protected Point getPosition(IGeometry handleGeometry) {
		Point position = null;

		if (handleGeometry instanceof IShape) {
			IShape shape = (IShape) handleGeometry;
			// use the bounds to place the shape handles
			// TODO: we should be able to deal with an arbitrary shape here
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

	/**
	 * Sets the segment index. Refreshs the handle visual.
	 *
	 * @param segmentIndex
	 * @see #getSegmentIndex()
	 */
	public void setSegmentIndex(int segmentIndex) {
		int oldSegmentIndex = this.segmentIndex;
		this.segmentIndex = segmentIndex;
		if (oldSegmentIndex != segmentIndex) {
			refreshVisual();
		}
	}

	/**
	 * Sets the segment parameter. Refreshs the handle visual.
	 *
	 * @param segmentParameter
	 * @see #getSegmentParameter()
	 */
	public void setSegmentParameter(double segmentParameter) {
		double oldSegmentParameter = this.segmentParameter;
		this.segmentParameter = segmentParameter;
		if (oldSegmentParameter != segmentParameter) {
			refreshVisual();
		}
	}

}
