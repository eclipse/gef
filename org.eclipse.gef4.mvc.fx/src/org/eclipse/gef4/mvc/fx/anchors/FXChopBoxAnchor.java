/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.anchors;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;

public class FXChopBoxAnchor extends AbstractFXAnchor {

	public FXChopBoxAnchor(Node anchorage){
		super(anchorage);
	}
	
	@Override
	public Point getPosition(Node anchored, Point referencePoint) {
		// compute intersection point between outline of anchorage reference
		// shape and line through anchorage and anchor reference points.
		Line referenceLine = new Line(getAnchorageReferencePoint(), getAnchorReferencePoint(anchored, referencePoint));
		IShape anchorageReferenceShape = getAnchorageReferenceShape();
		Point[] intersectionPoints = anchorageReferenceShape.getOutline().getIntersections(referenceLine);
		if(intersectionPoints.length > 0){
			return JavaFX2Geometry.toPoint(anchored.sceneToLocal(Geometry2JavaFX.toFXPoint(intersectionPoints[0])));
		}
		throw new IllegalArgumentException("Invalid reference point " + referencePoint);
	}

	protected IShape getAnchorageReferenceShape() {
		return JavaFX2Geometry.toRectangle(getAnchorage().localToScene(
				getAnchorage().getLayoutBounds()));
	}

	protected Point getAnchorageReferencePoint() {
		return getAnchorageReferenceShape().getBounds().getCenter();
	}

	protected Point getAnchorReferencePoint(Node anchored, Point referencePoint) {
		// this is the line...
		return JavaFX2Geometry.toPoint(
				anchored.localToScene(Geometry2JavaFX.toFXPoint(referencePoint)));
	}

}
