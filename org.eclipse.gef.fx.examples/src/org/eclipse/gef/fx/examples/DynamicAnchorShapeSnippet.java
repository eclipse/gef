/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
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
package org.eclipse.gef.fx.examples;

import org.eclipse.gef.fx.anchors.AnchorKey;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.planar.Point;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class DynamicAnchorShapeSnippet extends AbstractFxExample {

	public static void main(String[] args) {
		launch();
	}

	private Circle refPoint;
	private Line anchorLine;
	private DynamicAnchor anchor;
	private AnchorKey anchorKey;

	private MapChangeListener<AnchorKey, Point> anchorPositionChangeListener = new MapChangeListener<AnchorKey, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
			if (change.getKey() == anchorKey) {
				Point anchorPoint = change.getValueAdded();
				if (anchorPoint != null) {
					anchorLine.setStartX(anchorPoint.x);
					anchorLine.setStartY(anchorPoint.y);
				}
			}
		}
	};

	public DynamicAnchorShapeSnippet() {
		super("DynamicAnchor for JavaFX Shape");
	}

	@Override
	public Scene createScene() {
		InfiniteCanvas root = new InfiniteCanvas();
		Scene scene = new Scene(root, 500, 400);

		// create choice box to select shape
		ChoiceBox<String> shapeChoiceBox = new ChoiceBox<>();
		shapeChoiceBox.getItems().addAll("Arc", "Circle", "CubicCurve",
				"Ellipse", "Line", "Path", "Polygon", "Polyline", "QuadCurve",
				"Rectangle", "Star Polygon");
		shapeChoiceBox.setLayoutX(10);
		shapeChoiceBox.setLayoutY(10);
		root.getContentGroup().getChildren().add(shapeChoiceBox);

		// create group containing the selected shape
		final Group selectedShapeGroup = new Group();
		selectedShapeGroup.setLayoutX(150);
		selectedShapeGroup.setLayoutY(150);
		selectedShapeGroup.setScaleX(3);
		selectedShapeGroup.setScaleY(3);
		root.getContentGroup().getChildren().add(selectedShapeGroup);

		// create reference point
		refPoint = new Circle(3.5);
		refPoint.setStroke(Color.BLACK);
		refPoint.setFill(Color.RED);
		root.getContentGroup().getChildren().add(refPoint);

		// create group containing the anchor line
		anchorLine = new Line();
		anchorLine.setStroke(Color.RED);
		anchorLine.setStrokeWidth(1.5);
		anchorLine.setFill(null);
		root.getContentGroup().getChildren().add(anchorLine);

		// register for ref point changes
		anchorKey = new AnchorKey(anchorLine, "ref");
		refPoint.boundsInParentProperty()
				.addListener(new ChangeListener<Bounds>() {
					@Override
					public void changed(
							javafx.beans.value.ObservableValue<? extends Bounds> observable,
							Bounds oldValue, Bounds newValue) {
						Point2D refInScene = refPoint.localToScene(
								refPoint.getCenterX(), refPoint.getCenterY());
						Point2D refInLocal = anchorLine
								.sceneToLocal(refInScene);
						if (anchor != null) {
							anchor.getComputationParameter(anchorKey,
									AnchoredReferencePoint.class)
									.set(new Point(refInLocal.getX(),
											refInLocal.getY()));
						}
						anchorLine.setEndX(refInLocal.getX());
						anchorLine.setEndY(refInLocal.getY());
					}
				});
		refPoint.relocate(300, 300);

		// register relocate on drag for refPoint
		refPoint.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Point2D positionInParent = refPoint.getParent()
						.sceneToLocal(event.getSceneX(), event.getSceneY());
				refPoint.relocate(positionInParent.getX(),
						positionInParent.getY());
			}
		});

		// register for choice changes
		shapeChoiceBox.valueProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldValue, String newValue) {
						showShape(selectedShapeGroup, newValue);
					}
				});

		// display initial shape
		shapeChoiceBox.setValue("Star Polygon");

		return scene;
	}

	protected void showShape(Group selectedShapeGroup, String item) {
		// unregister anchor
		if (anchor != null) {
			anchor.positionsUnmodifiableProperty()
					.removeListener(anchorPositionChangeListener);
			anchor.detach(anchorKey);
			anchor = null;
		}

		// clear shape box
		selectedShapeGroup.getChildren().clear();

		// determine shape geometry
		Shape shape = null;
		if ("Arc".equals(item)) {
			shape = new Arc(50, 50, 20, 20, 15, 135);
		} else if ("Circle".equals(item)) {
			shape = new Circle(50, 50, 20);
		} else if ("CubicCurve".equals(item)) {
			shape = new CubicCurve(10, 80, 10, 10, 80, 10, 80, 80);
		} else if ("Ellipse".equals(item)) {
			shape = new Ellipse(50, 50, 30, 15);
		} else if ("Line".equals(item)) {
			shape = new Line(10, 10, 80, 80);
		} else if ("Path".equals(item)) {
			shape = new Path(new MoveTo(10, 10), new LineTo(80, 10),
					new QuadCurveTo(50, 50, 80, 80),
					new CubicCurveTo(50, 50, 30, 100, 10, 80), new ClosePath());
		} else if ("Polygon".equals(item)) {
			shape = new Polygon(10, 10, 80, 50, 10, 80);
		} else if ("Polyline".equals(item)) {
			shape = new Polyline(10, 10, 80, 50, 10, 80);
		} else if ("QuadCurve".equals(item)) {
			shape = new QuadCurve(10, 80, 50, 10, 80, 80);
		} else if ("Rectangle".equals(item)) {
			shape = new Rectangle(10, 10, 70, 40);
		} else if ("Star Polygon".equals(item)) {
			shape = new Polygon(50, 10, 60, 40, 90, 50, 60, 60, 50, 90, 40, 60,
					10, 50, 40, 40);
		} else {
			throw new IllegalStateException(
					"Unsupported selection: <" + item + ">.");
		}

		// apply shape style
		shape.setStrokeWidth(3.5);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.GREY);

		// add to content group
		selectedShapeGroup.getChildren().add(shape);

		// create anchor
		anchor = new DynamicAnchor(shape);

		Point2D refInScene = refPoint.localToScene(refPoint.getCenterX(),
				refPoint.getCenterY());
		Point2D refInLocal = anchorLine.sceneToLocal(refInScene);
		anchor.getComputationParameter(anchorKey,
				AnchoredReferencePoint.class)
				.set(new Point(refInLocal.getX(), refInLocal.getY()));
		anchor.positionsUnmodifiableProperty()
				.addListener(anchorPositionChangeListener);
		anchor.attach(anchorKey);
	}

}
