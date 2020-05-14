/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.AffineTransform;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class InifinteCanvasSnippet extends AbstractFxExample {

	public static void main(String[] args) {
		launch();
	}

	private InfiniteCanvas infiniteCanvas;

	public InifinteCanvasSnippet() {
		super("InfiniteCanvasSnippet");
	}

	@Override
	public Scene createScene() {
		BorderPane root = new BorderPane();

		infiniteCanvas = new InfiniteCanvas();
		root.setCenter(infiniteCanvas);
		infiniteCanvas.getContentGroup().getChildren().addAll(
				rect(25, 25, 100, 50, Color.BLUE),
				rect(25, 200, 25, 50, Color.BLUE),
				rect(150, 100, 75, 75, Color.BLUE),
				rect(-100, -100, 30, 60, Color.CYAN),
				rect(75, 75, 150, 150, Color.RED));

		// translate to top-left most content node
		Bounds canvasBounds = infiniteCanvas.getContentBounds();
		double minx = canvasBounds.getMinX();
		double miny = canvasBounds.getMinY();
		infiniteCanvas.setHorizontalScrollOffset(-minx);
		infiniteCanvas.setVerticalScrollOffset(-miny);

		infiniteCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (MouseButton.SECONDARY.equals(event.getButton())) {
					// determine pivot in content group
					Group contentGroup = infiniteCanvas.getContentGroup();
					Point2D contentPivot = contentGroup
							.sceneToLocal(event.getSceneX(), event.getSceneY());
					double zoomFactor = event.isControlDown() ? 4d / 5 : 5d / 4;

					// compute zoom transformation
					Affine tx = infiniteCanvas.getContentTransform();
					AffineTransform at = FX2Geometry.toAffineTransform(tx);
					at.concatenate(new AffineTransform()
							.translate(contentPivot.getX(), contentPivot.getY())
							.scale(zoomFactor, zoomFactor)
							.translate(-contentPivot.getX(),
									-contentPivot.getY()));
					Affine affine = Transform.affine(at.getM00(), at.getM01(),
							at.getM10(), at.getM11(), at.getTranslateX(),
							at.getTranslateY());
					infiniteCanvas.setContentTransform(affine);
				}
			}
		});

		return new Scene(root, 400, 300);
	}

	private Node rect(double layoutX, double layoutY, double width,
			double height, Paint fill) {
		final Rectangle rect = new Rectangle(width, height, fill);
		rect.setLayoutX(layoutX);
		rect.setLayoutY(layoutY);

		// register drag listeners
		final double[] initialLayout = new double[2];
		final double[] initialMouse = new double[2];
		rect.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				initialLayout[0] = rect.getLayoutX();
				initialLayout[1] = rect.getLayoutY();
				initialMouse[0] = event.getSceneX();
				initialMouse[1] = event.getSceneY();
			}
		});
		EventHandler<MouseEvent> dragHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				double dx = event.getSceneX() - initialMouse[0];
				double dy = event.getSceneY() - initialMouse[1];
				rect.setLayoutX(initialLayout[0] + dx);
				rect.setLayoutY(initialLayout[1] + dy);
				infiniteCanvas.reveal(rect);
			}
		};
		rect.setOnMouseDragged(dragHandler);
		rect.setOnMouseReleased(dragHandler);

		return rect;
	}

}
