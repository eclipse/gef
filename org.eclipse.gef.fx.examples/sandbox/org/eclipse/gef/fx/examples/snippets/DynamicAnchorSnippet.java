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

import org.eclipse.gef.fx.anchors.AnchorKey;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef.fx.examples.AbstractFxExample;
import org.eclipse.gef.geometry.planar.Point;

import javafx.collections.MapChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class DynamicAnchorSnippet extends AbstractFxExample {

	public static void main(String[] args) {
		launch();
	}

	private Rectangle r1;
	private Rectangle r2;

	public DynamicAnchorSnippet() {
		super("DynamicAnchorSnippet");
	}

	@Override
	public Scene createScene() {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 400, 400);

		r1 = new Rectangle(50, 50);
		r1.setFill(Color.RED);
		r1.relocate(100, 100);
		r2 = new Rectangle(50, 50);
		r2.setFill(Color.BLUE);
		r2.relocate(200, 200);
		final Line l = new Line();
		l.setStroke(Color.BLACK);

		DynamicAnchor startAnchor = new DynamicAnchor(r1);
		DynamicAnchor endAnchor = new DynamicAnchor(r2);
		final AnchorKey startKey = new AnchorKey(l, "start");
		final AnchorKey endKey = new AnchorKey(l, "end");

		// update start and end point in case provided position values change
		MapChangeListener<AnchorKey, Point> changeListener = new MapChangeListener<AnchorKey, Point>() {

			@Override
			public void onChanged(
					MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
				if (change.getKey().equals(startKey)) {
					l.setStartX(change.getMap().get(startKey).x);
					l.setStartY(change.getMap().get(startKey).y);
				}
				if (change.getKey().equals(endKey)) {
					l.setEndX(change.getMap().get(endKey).x);
					l.setEndY(change.getMap().get(endKey).y);
				}
			}
		};

		startAnchor.positionsUnmodifiableProperty().addListener(changeListener);
		endAnchor.positionsUnmodifiableProperty().addListener(changeListener);

		Point r1Center = new Point(
				r1.getLayoutBounds().getMinX() + r1.getLayoutX()
						+ r1.getWidth() / 2,
				r1.getLayoutBounds().getMinY() + r1.getLayoutY()
						+ r1.getHeight() / 2);
		Point r2Center = new Point(
				r2.getLayoutBounds().getMinX() + r2.getLayoutX()
						+ r2.getWidth() / 2,
				r2.getLayoutBounds().getMinY() + r2.getLayoutY()
						+ r2.getHeight() / 2);

		// use static values for dynamic anchor reference points
		startAnchor.getComputationParameter(startKey,
				AnchoredReferencePoint.class).set(r2Center);
		startAnchor.attach(startKey);
		endAnchor.getComputationParameter(endKey,
				AnchoredReferencePoint.class).set(r1Center);
		endAnchor.attach(endKey);

		Group g = new Group(r1, r2, l);
		root.getChildren().add(g);

		return scene;
	}

}
