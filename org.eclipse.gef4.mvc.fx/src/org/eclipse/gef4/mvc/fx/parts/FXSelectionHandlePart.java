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
import org.eclipse.gef4.mvc.IProvider;
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
public class FXSelectionHandlePart extends AbstractFXHandlePart {

	public static final double SIZE = 5d;

	private Shape visual;
	private IContentPart<Node> targetPart;
	private IProvider<IGeometry> handleGeometryProvider;
	private int vertexIndex;
	private boolean isEndPoint;

	public FXSelectionHandlePart(IContentPart<Node> targetPart,
			IProvider<IGeometry> handleGeometryProvider, int vertexIndex) {
		this(targetPart, handleGeometryProvider, vertexIndex, false);
	}

	public FXSelectionHandlePart(IContentPart<Node> targetPart,
			IProvider<IGeometry> handleGeometryProvider, int vertexIndex,
			boolean isEndPoint) {
		super();
		this.targetPart = targetPart;
		this.handleGeometryProvider = handleGeometryProvider;
		this.vertexIndex = vertexIndex;
		this.isEndPoint = isEndPoint;

		visual = createHandleVisual(handleGeometryProvider.get());

		visual.setFill(Color.web("#d5faff"));
		visual.setStroke(Color.web("#5a61af"));
		visual.setStrokeWidth(1);
		visual.setStrokeType(StrokeType.OUTSIDE);
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
		if (handleGeometry instanceof org.eclipse.gef4.geometry.planar.Rectangle) {
			shape = new Rectangle();
			((Rectangle) shape).setWidth(SIZE);
			((Rectangle) shape).setHeight(SIZE);
			shape.setTranslateX(-SIZE / 2);
			shape.setTranslateY(-SIZE / 2);
		} else {
			shape = new Circle(SIZE / 2d);
		}
		return shape;
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		IGeometry handleGeometry = handleGeometryProvider.get();

		Point position = null;

		if (handleGeometry instanceof IShape) {
			IShape shape = (IShape) handleGeometry;
			ICurve[] segments = shape.getOutlineSegments();
			position = segments[vertexIndex].getP1();
		} else if (handleGeometry instanceof ICurve) {
			ICurve curve = (ICurve) handleGeometry;
			BezierCurve[] beziers = curve.toBezier();
			BezierCurve bc = beziers[vertexIndex];
			position = isEndPoint ? bc.getP2() : bc.getP1();
		} else {
			throw new IllegalStateException(
					"Unable to determine handle position: Expected IShape or ICurve but got: "
							+ handleGeometry);
		}

		Node targetVisual = targetPart.getVisual();
		Point2D point2d = visual.getParent().sceneToLocal(
				targetVisual.localToScene(position.x, position.y));

		visual.setLayoutX(point2d.getX());
		visual.setLayoutY(point2d.getY());
	}

}
