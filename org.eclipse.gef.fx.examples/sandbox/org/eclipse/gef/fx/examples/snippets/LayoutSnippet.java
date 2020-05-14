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
package org.eclipse.gef.fx.examples.snippets;

import org.eclipse.gef.fx.examples.AbstractFxExample;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.AffineTransform;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;

public class LayoutSnippet extends AbstractFxExample {

	public static void main(String[] args) {
		launch();
	}

	private Point2D startPoint;

	private Point2D endPoint;
	private double initialTx;
	private double initialTy;
	private double initialWidth;
	private double initialHeight;
	private Affine affine;

	public LayoutSnippet() {
		super("LayoutSnippet");
	}

	private void applyTransform(Affine dst, Transform transform) {
		AffineTransform affineTransform = FX2Geometry
				.toAffineTransform(dst);
		AffineTransform result = affineTransform
				.concatenate(FX2Geometry.toAffineTransform(transform));
		setAffine(dst, result);
	}

	@Override
	public Scene createScene() {
		final BorderPane root = new BorderPane();
		root.setStyle("-fx-background-color: black;");

		final Pane contentPane = new Pane();
		root.setCenter(contentPane);
		final Scene scene = new Scene(root, 600, 400);

		// create rect
		final Rectangle rect = new Rectangle(0, 0, 0, 0);
		rect.setStroke(Color.WHITE);
		rect.setFill(Color.PINK);
		rect.setStrokeWidth(2.5);
		rect.setEffect(new DropShadow(10, 0, 0, Color.LIGHTBLUE));
		rect.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable,
					Bounds oldValue, Bounds newValue) {
				// JavaFX Bug
			}
		});
		affine = new Affine();
		rect.getTransforms().add(affine);

		// visualize rect's origin
		final Circle origin = new Circle(1.5, new Color(1, 0, 0, 0.5));
		origin.setStroke(Color.BLACK);
		origin.setStrokeWidth(1.5);
		origin.setEffect(new DropShadow(5, 0, 0, Color.RED));
		origin.layoutXProperty().bind(rect.layoutXProperty());
		origin.layoutYProperty().bind(rect.layoutYProperty());

		// create handle at top right corner
		final Rectangle handle = new Rectangle(0, 0, 10, 10);
		handle.setTranslateX(-5);
		handle.setTranslateY(-5);
		handle.setStroke(Color.CYAN);
		handle.setFill(Color.BLUE);
		handle.setStrokeWidth(1.5);
		handle.setStrokeType(StrokeType.INSIDE);
		handle.layoutXProperty().bind(new DoubleBinding() {
			{
				bind(rect.boundsInParentProperty());
			}

			@Override
			protected double computeValue() {
				Bounds bounds = rect.getLayoutBounds();
				return rect.localToParent(bounds.getMaxX(), bounds.getMinY())
						.getX();
			}
		});
		handle.layoutYProperty().bind(new DoubleBinding() {
			{
				bind(rect.boundsInParentProperty());
			}

			@Override
			protected double computeValue() {
				Bounds bounds = rect.getLayoutBounds();
				return rect.localToParent(bounds.getMaxX(), bounds.getMinY())
						.getY();
			}
		});
		handle.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				startPoint = new Point2D(event.getSceneX(), event.getSceneY());
				initialTx = affine.getTx();
				initialTy = affine.getTy();
				initialWidth = rect.getWidth();
				initialHeight = rect.getHeight();
			}
		});
		handle.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				endPoint = new Point2D(event.getSceneX(), event.getSceneY());
				Point2D start = rect.sceneToLocal(startPoint);
				Point2D end = rect.sceneToLocal(endPoint);
				double dx = end.getX() - start.getX();
				double dy = end.getY() - start.getY();

				Point2D layout = rect.parentToLocal(initialTx, initialTy);
				Point2D layoutParent = rect.localToParent(layout.getX(),
						layout.getY() + dy);

				rect.setWidth(initialWidth + dx);
				rect.setHeight(initialHeight - dy);
				affine.setTx(layoutParent.getX());
				affine.setTy(layoutParent.getY());
			}
		});

		// add nodes to the scene
		contentPane.getChildren().addAll(rect, origin, handle);

		// options
		VBox vbox = new VBox();
		TitledPane options = new TitledPane("Options:", vbox);
		root.setRight(options);

		HBox rowX = new HBox();
		rowX.getChildren().addAll(new Label("Layout-X: "),
				text(rect.layoutXProperty()));
		HBox rowY = new HBox();
		rowY.getChildren().addAll(new Label("Layout-Y: "),
				text(rect.layoutYProperty()));
		HBox rowW = new HBox();
		rowW.getChildren().addAll(new Label("Width: "),
				text(rect.widthProperty()));
		HBox rowH = new HBox();
		rowH.getChildren().addAll(new Label("Height: "),
				text(rect.heightProperty()));
		HBox rowR = new HBox();
		Button rotLeftButton = new Button("Rot Left");
		Button rotRightButton = new Button("Rot Right");
		rowR.getChildren().addAll(rotLeftButton, rotRightButton);
		HBox rowS = new HBox();
		Button scaleUpButton = new Button("Scale Up");
		Button scaleDownButton = new Button("Scale Down");
		rowS.getChildren().addAll(scaleUpButton, scaleDownButton);
		HBox rowShear = new HBox();
		Button shearRightButton = new Button("Shear Right");
		Button shearLeftButton = new Button("Shear Left");
		rowShear.getChildren().addAll(shearRightButton, shearLeftButton);
		HBox rowReset = new HBox();
		Button resetTxButton = new Button("Reset Transforms");
		rowReset.getChildren().addAll(resetTxButton);
		vbox.getChildren().addAll(rowX, rowY, rowW, rowH, rowR, rowS, rowShear,
				rowReset);
		rotLeftButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Bounds bounds = rect.getLayoutBounds();
				Rotate rotate = Transform.rotate(-30,
						bounds.getMinX() + bounds.getWidth() / 2,
						bounds.getMinY() + bounds.getHeight() / 2);
				applyTransform(affine, rotate);
			}

		});
		rotRightButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Bounds bounds = rect.getLayoutBounds();
				Rotate rotate = Transform.rotate(30,
						bounds.getMinX() + bounds.getWidth() / 2,
						bounds.getMinY() + bounds.getHeight() / 2);
				applyTransform(affine, rotate);
			}
		});
		scaleUpButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Bounds bounds = rect.getLayoutBounds();
				Scale scale = Transform.scale(1.25, 1.25,
						bounds.getMinX() + bounds.getWidth() / 2,
						bounds.getMinY() + bounds.getHeight() / 2);
				applyTransform(affine, scale);
			}
		});
		scaleDownButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Bounds bounds = rect.getLayoutBounds();
				Scale scale = Transform.scale(0.8, 0.8,
						bounds.getMinX() + bounds.getWidth() / 2,
						bounds.getMinY() + bounds.getHeight() / 2);
				applyTransform(affine, scale);
			}
		});
		shearRightButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Bounds bounds = rect.getLayoutBounds();
				Shear shear = Transform.shear(-0.25, 0,
						bounds.getMinX() + bounds.getWidth() / 2,
						bounds.getMinY() + bounds.getHeight() / 2);
				applyTransform(affine, shear);
			}
		});
		shearLeftButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Bounds bounds = rect.getLayoutBounds();
				Shear shear = Transform.shear(0.25, 0,
						bounds.getMinX() + bounds.getWidth() / 2,
						bounds.getMinY() + bounds.getHeight() / 2);
				applyTransform(affine, shear);
			}
		});
		resetTxButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setAffine(affine, new AffineTransform());
			}
		});

		// initial move
		rect.setLayoutX(100);
		rect.setLayoutY(100);

		// initial resize
		rect.setWidth(80);
		rect.setHeight(40);

		return scene;
	}

	private void setAffine(Affine dst, AffineTransform src) {
		dst.setMxx(src.getM00());
		dst.setMxy(src.getM01());
		dst.setMyx(src.getM10());
		dst.setMyy(src.getM11());
		dst.setTx(src.getTranslateX());
		dst.setTy(src.getTranslateY());
	}

	private Node text(final DoubleProperty property) {
		final TextField text = new TextField();
		text.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				try {
					double d = Double.parseDouble(newValue);
					property.set(d);
				} catch (Exception x) {
				}
			}
		});
		property.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				text.setText(newValue.toString());
			}
		});
		return text;
	}

}
