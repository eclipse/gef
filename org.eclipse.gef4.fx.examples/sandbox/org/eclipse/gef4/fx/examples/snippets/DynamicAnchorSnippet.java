/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.examples.snippets;

import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.examples.AbstractFxExample;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
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

		DynamicAnchor startAnchor = new DynamicAnchor(r1);
		DynamicAnchor endAnchor = new DynamicAnchor(r2);
		final AnchorKey startKey = new AnchorKey(l, "start");
		final AnchorKey endKey = new AnchorKey(l, "end");

		// update start and end point in case provided position values change
		ChangeListener<ObservableMap<AnchorKey, Point>> changeListener = new ChangeListener<ObservableMap<AnchorKey, Point>>() {
			@Override
			public void changed(
					ObservableValue<? extends ObservableMap<AnchorKey, Point>> observable,
					ObservableMap<AnchorKey, Point> oldValue,
					ObservableMap<AnchorKey, Point> newValue) {
				if (newValue.containsKey(startKey)) {
					l.setStartX(newValue.get(startKey).x);
					l.setStartY(newValue.get(startKey).y);
				}
				if (newValue.containsKey(endKey)) {
					l.setEndX(newValue.get(endKey).x);
					l.setEndY(newValue.get(endKey).y);
				}
			}
		};
		startAnchor.positionProperty().addListener(changeListener);
		endAnchor.positionProperty().addListener(changeListener);

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
		startAnchor.anchoredReferencePointsProperty().put(startKey, r2Center);
		startAnchor.attach(startKey);
		endAnchor.anchoredReferencePointsProperty().put(endKey, r1Center);
		endAnchor.attach(endKey);

		Group g = new Group(r1, r2, l);
		root.getChildren().add(g);

		return scene;
	}

}
