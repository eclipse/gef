/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import javafx.event.EventHandler;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.fx.parts.FXSegmentHandlePart;
import org.eclipse.gef4.mvc.policies.HoverPolicy;
import org.eclipse.gef4.zest.fx.policies.HoverFirstAnchoragePolicy;

import com.google.inject.Provider;

public class ZestFxPruningHandlePart extends FXSegmentHandlePart {

	private Circle shape;
	private Polygon icon;

	public ZestFxPruningHandlePart(
			Provider<BezierCurve[]> segmentsInSceneProvider, int segmentIndex,
			double segmentParameter) {
		super(segmentsInSceneProvider, segmentIndex, segmentParameter);
		setAdapter(AdapterKey.get(HoverPolicy.class),
				new HoverFirstAnchoragePolicy());
	}

	protected Polygon createIcon(double size, double width) {
		// minus shape
		Polygon icon = new Polygon(-size, -width, -size, width, size, width,
				size, -width);
		icon.setStroke(Color.TRANSPARENT);
		icon.setFill(Color.RED);
		return icon;
	}

	@Override
	protected StackPane createVisual() {
		StackPane stackPane = new StackPane();
		stackPane.setTranslateX(-5);
		stackPane.setTranslateY(-5);
		stackPane.setPickOnBounds(false);
		icon = createIcon(5, 1);
		shape = new Circle(6);
		shape.setStroke(Color.BLACK);
		shape.setFill(Color.WHITE);
		stackPane.getChildren().addAll(shape, icon);

		// register click action
		stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// TODO: prune this node
			}
		});

		// TODO: allow hierarchical hover
		stackPane.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				DropShadow effect = new DropShadow();
				effect.setRadius(5);
				shape.setEffect(effect);
			}
		});
		stackPane.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				shape.setEffect(null);
			}
		});

		return stackPane;
	}

	@Override
	public void doRefreshVisual() {
		// TODO: animate visibility by fading in/out
		super.doRefreshVisual();
	}

	@Override
	public StackPane getVisual() {
		return (StackPane) super.getVisual();
	}

	@Override
	protected void registerAtVisualPartMap() {
		super.registerAtVisualPartMap();
		getViewer().getVisualPartMap().put(shape, this);
		getViewer().getVisualPartMap().put(icon, this);
	}

	@Override
	protected void unregisterFromVisualPartMap() {
		super.unregisterFromVisualPartMap();
		getViewer().getVisualPartMap().remove(shape);
		getViewer().getVisualPartMap().remove(icon);
	}

}