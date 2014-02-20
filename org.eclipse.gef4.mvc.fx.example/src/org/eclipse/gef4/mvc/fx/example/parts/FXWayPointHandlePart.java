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
package org.eclipse.gef4.mvc.fx.example.parts;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractWayPointPolicy;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.policies.IDragPolicy;

public class FXWayPointHandlePart extends AbstractFXHandlePart {

	public static final double VISUAL_SIZE = 5;
	public static final Color VISUAL_STROKE = Color.web("#5a61af");
	public static final LinearGradient VISUAL_FILL = new LinearGradient(0, 0,
			0, 5, true, CycleMethod.NO_CYCLE, new Stop[] {
					new Stop(0.0, Color.web("#e4fbff")),
					new Stop(0.5, Color.web("#a5d3fb")),
					new Stop(1.0, Color.web("#d5faff")) });

	protected Shape visual;
	private Point startPoint;
	private Point currentPosition;
	private int wayPointIndex;
	
	public FXWayPointHandlePart(int index, Point wayPoint) {
		this(index, wayPoint, false);
	}

	protected FXWayPointHandlePart(int index, Point wayPoint, final boolean create) {
		// store attributes
		wayPointIndex = index;
		startPoint = wayPoint.getCopy();
		currentPosition = new Point(startPoint);
		createVisual();
		installPolicies(create);
	}

	protected void installPolicies(final boolean create) {
		installBound(IDragPolicy.class, new IDragPolicy.Impl<Node>() {
			@Override
			public void press(Point mouseLocation) {
				if (create) {
					getPolicy().createWayPoint(wayPointIndex, startPoint);
				} else {
					getPolicy().selectWayPoint(wayPointIndex);
				}
			}

			@Override
			public void drag(Point mouseLocation, Dimension delta) {
				currentPosition = startPoint.getTranslated(delta.width,
						delta.height);
				getPolicy().updateWayPoint(wayPointIndex, currentPosition);
			}

			@Override
			public void release(Point mouseLocation, Dimension delta) {
				currentPosition = startPoint.getTranslated(delta.width,
						delta.height);
				getPolicy().commitWayPoint(wayPointIndex, currentPosition);
			}
		});
	}

	protected void createVisual() {
		visual = new Rectangle(VISUAL_SIZE, VISUAL_SIZE);
		visual.setTranslateY(-0.5 * VISUAL_SIZE);
		visual.setFill(VISUAL_FILL);
		visual.setStroke(VISUAL_STROKE);
	}

	protected AbstractWayPointPolicy getPolicy() {
		return getAnchorages().get(0).getBound(AbstractWayPointPolicy.class);
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		visual.setLayoutX(currentPosition.x);
		visual.setLayoutY(currentPosition.y);
	}

}
