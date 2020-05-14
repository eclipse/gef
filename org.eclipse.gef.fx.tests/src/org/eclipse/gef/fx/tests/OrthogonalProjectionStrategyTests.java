/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef.fx.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef.fx.anchors.AnchorKey;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.OrthogonalProjectionStrategy;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchorageReferenceGeometry;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef.fx.anchors.DynamicAnchor.PreferredOrientation;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.junit.Test;

import javafx.geometry.Orientation;

public class OrthogonalProjectionStrategyTests {

	@Test
	public void projectionOnRectangleWithoutOrientationPreference() {
		javafx.scene.shape.Rectangle anchorage = new javafx.scene.shape.Rectangle(0, 0, 50, 50);
		DynamicAnchor a = new DynamicAnchor(anchorage, new OrthogonalProjectionStrategy());
		AnchorageReferenceGeometry computationParameter = a.getComputationParameter(AnchorageReferenceGeometry.class);
		if(computationParameter.isBound()){
			computationParameter.unbind();
		}
		computationParameter.set(new Rectangle(0, 0, 50, 50));

		javafx.scene.shape.Rectangle anchored = new javafx.scene.shape.Rectangle(100, 0, 50, 50);
		AnchorKey anchorKey = new AnchorKey(anchored, "role");
		a.attach(anchorKey);

		a.getComputationParameter(anchorKey, AnchoredReferencePoint.class).set(new Point(125, 0));
		assertEquals(new Point(50, 0), a.getPosition(anchorKey));

		a.getComputationParameter(anchorKey, AnchoredReferencePoint.class).set(new Point(125, 25));
		assertEquals(new Point(50, 25), a.getPosition(anchorKey));

		a.getComputationParameter(anchorKey, AnchoredReferencePoint.class).set(new Point(125, 50));
		assertEquals(new Point(50, 50), a.getPosition(anchorKey));
	}
	
	@Test
	public void projectionOnDiamondWithOrientationPreference() {
		javafx.scene.shape.Polygon anchorage = new javafx.scene.shape.Polygon(0, 25, 25, 0, 50, 25, 25, 50);
		DynamicAnchor a = new DynamicAnchor(anchorage, new OrthogonalProjectionStrategy());
		AnchorageReferenceGeometry computationParameter = a.getComputationParameter(AnchorageReferenceGeometry.class);
		if(computationParameter.isBound()){
			computationParameter.unbind();
		}
		computationParameter.set(new Polygon(0, 25, 25, 0, 50, 25, 25, 50));

		javafx.scene.shape.Rectangle anchored = new javafx.scene.shape.Rectangle(100, 0, 50, 50);
		AnchorKey anchorKey = new AnchorKey(anchored, "role");
		a.attach(anchorKey);

		a.getComputationParameter(anchorKey, PreferredOrientation.class).set(Orientation.HORIZONTAL);
		
		a.getComputationParameter(anchorKey, AnchoredReferencePoint.class).set(new Point(125, 25));
		assertEquals(new Point(50, 25), a.getPosition(anchorKey));

		a.getComputationParameter(anchorKey, AnchoredReferencePoint.class).set(new Point(125, 37.5));
		assertEquals(new Point(37.5, 37.5), a.getPosition(anchorKey));

		a.getComputationParameter(anchorKey, AnchoredReferencePoint.class).set(new Point(125, 12.5));
		assertEquals(new Point(37.5, 12.5), a.getPosition(anchorKey));
	}
	
	

}
