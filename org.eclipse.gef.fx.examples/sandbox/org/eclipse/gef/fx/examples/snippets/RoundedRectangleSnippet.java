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

import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.planar.RoundedRectangle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RoundedRectangleSnippet extends Application {

	static class RectData {
		public double arcWidthPercentage;
		public double arcHeightPercentage;

		public RectData(double aw, double ah) {
			arcWidthPercentage = aw;
			arcHeightPercentage = ah;
		}
	}

	public static void main(String[] args) {
		launch();
	}

	private List<RectData> getRectData() {
		return Arrays.asList(
				new RectData[] { new RectData(0, 0), new RectData(0.125, 0),
						new RectData(0, 0.125), new RectData(0.125, 0.125),
						new RectData(0.25, 0.25), new RectData(0.5, 0.5),
						new RectData(0.75, 0.75), new RectData(1, 1),
						new RectData(1.5, 1.5), new RectData(2, 2) });
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		InfiniteCanvas root = new InfiniteCanvas();
		Scene scene = new Scene(root, 600, 500);
		final int PAD = 20;
		final int HEIGHT = 100;

		// create width slider
		Slider widthSlider = new Slider(50, 200, 100);
		widthSlider.setPrefWidth(200);

		// create entry for width slider value
		TextField widthSliderText = new TextField();
		widthSliderText.textProperty()
				.bind(widthSlider.valueProperty().asString());
		widthSliderText.setPrefWidth(50);

		// layout slider and textfield in a box
		HBox hBox = new HBox();
		hBox.setStyle(
				"-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px;");
		hBox.getChildren().addAll(new Text("Width: "), widthSlider,
				widthSliderText);
		root.getOverlayGroup().getChildren().add(hBox);

		final double x = PAD;
		double y = PAD;
		ObservableList<Node> contents = root.getContentGroup().getChildren();
		for (RectData rd : getRectData()) {
			// create half transparent background rect
			Rectangle bgRect = new Rectangle(x, y, 100, HEIGHT);
			bgRect.widthProperty().bind(widthSlider.valueProperty());
			bgRect.setStroke(null);
			bgRect.setFill(new Color(1.0, 0, 0, 0.5));
			contents.add(bgRect);

			// create rectangle with arcs and inside stroke
			final Rectangle rect = new Rectangle(x, y, 100, HEIGHT);
			rect.widthProperty().bind(widthSlider.valueProperty());
			rect.arcWidthProperty().bind(widthSlider.valueProperty()
					.multiply(rd.arcWidthPercentage));
			rect.setArcHeight(rd.arcHeightPercentage * HEIGHT);
			rect.setStroke(Color.BLACK);
			rect.setStrokeType(StrokeType.INSIDE);
			rect.setFill(Color.BROWN);
			contents.add(rect);

			// create vertices
			if (rd.arcHeightPercentage > 0 && rd.arcWidthPercentage > 0) {
				double awp = rd.arcWidthPercentage > 1 ? 1
						: rd.arcWidthPercentage;
				double ah = rect.getArcHeight() > rect.getHeight()
						? rect.getHeight() : rect.getArcHeight();
				contents.add(new Circle(x, y + rect.getHeight() - ah / 2, 2.5,
						Color.CYAN));
				contents.add(new Circle(x, y + rect.getHeight() / 2, 2.5,
						Color.CYAN));
				contents.add(new Circle(x, y + ah / 2, 2.5, Color.CYAN));
				Circle dot = new Circle(x + rect.getWidth() / 2, y, 2.5,
						Color.CYAN);
				dot.centerXProperty()
						.bind(widthSlider.valueProperty().divide(2).add(x));
				contents.add(dot);
				dot = new Circle(x, y, 2.5, Color.CYAN);
				dot.centerXProperty().bind(widthSlider.valueProperty()
						.multiply(awp).divide(2).add(x));
				contents.add(dot);
				dot = new Circle(x, y, 2.5, Color.CYAN);
				dot.centerXProperty()
						.bind(widthSlider.valueProperty().add(x)
								.subtract(widthSlider.valueProperty()
										.multiply(awp).divide(2)));
				contents.add(dot);
			}

			// create geometry node containing a rounded rectangle for
			// comparison
			RoundedRectangle rr = new RoundedRectangle(
					rect.getX() + rect.getWidth() + PAD * 2, rect.getY(),
					rect.getWidth(), rect.getHeight(), rect.getArcWidth(),
					rect.getArcHeight());
			final GeometryNode<RoundedRectangle> geometryNode = new GeometryNode<>(
					rr);
			geometryNode.setStrokeType(StrokeType.INSIDE);
			geometryNode.setStroke(Color.BLACK);
			geometryNode.setFill(Color.BROWN);
			contents.add(geometryNode);

			rect.layoutBoundsProperty()
					.addListener(new ChangeListener<Bounds>() {
						@Override
						public void changed(
								ObservableValue<? extends Bounds> observable,
								Bounds oldValue, Bounds newValue) {
							RoundedRectangle newGeometry = geometryNode
									.getGeometry().getCopy()
									.setX(rect.getX() + rect.getWidth()
											+ PAD * 2)
									.setWidth(rect.getWidth())
									.setHeight(rect.getHeight())
									.setArcWidth(rect.getArcWidth())
									.setArcHeight(rect.getArcHeight());
							geometryNode.setGeometry(newGeometry);
						}
					});

			// create labels showing the stats
			Text arcWidthLabel = new Text(
					"arc-width: " + (rd.arcWidthPercentage * 100) + "%");
			Text arcHeightLabel = new Text(
					"arc-height: " + (rd.arcHeightPercentage * 100) + "%");
			VBox vBox = new VBox();
			vBox.getChildren().addAll(arcWidthLabel, arcHeightLabel);
			vBox.layoutXProperty()
					.bind(widthSlider.valueProperty().multiply(2).add(PAD * 4));
			vBox.setLayoutY(y);
			contents.add(vBox);

			// increase y coord
			y += HEIGHT + PAD;
		}

		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.setTitle(
				"JavaFX Rectangle vs. GeometryNode<RoundedRectangle>");
		primaryStage.show();
	}

}
