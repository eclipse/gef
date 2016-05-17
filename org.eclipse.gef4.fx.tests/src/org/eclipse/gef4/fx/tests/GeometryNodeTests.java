/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.junit.Test;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

public class GeometryNodeTests {

	/**
	 * Ensures setting/resizing the geometry will resize the visuals
	 */
	@Test
	public void resizeOnGeometryChange() {
		GeometryNode<RoundedRectangle> n = new GeometryNode<>();
		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);
		n.setGeometry(new RoundedRectangle(30, 40, 30, 40, 20, 20));
		
		assertEquals(n.getGeometry().getBounds().getWidth(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 40, 0);
		assertEquals(40.0, n.getWidth(), 0);
		assertEquals(50.0, n.getHeight(), 0);
		assertEquals(25, n.getLayoutX(), 0);
		assertEquals(35, n.getLayoutY(), 0);
		assertEquals(30, n.getGeometry().getBounds().getX(), 0);
		assertEquals(40, n.getGeometry().getBounds().getY(), 0);
		
		n.resizeGeometry(50, 60);
		assertEquals(n.getGeometry().getBounds().getWidth(), 50, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 60, 0);
		assertEquals(60.0, n.getWidth(), 0);
		assertEquals(70.0, n.getHeight(), 0);
		assertEquals(25, n.getLayoutX(), 0);
		assertEquals(35, n.getLayoutY(), 0);
		assertEquals(30, n.getGeometry().getBounds().getX(), 0);
		assertEquals(40, n.getGeometry().getBounds().getY(), 0);
	}
	
	/**
	 * Ensures setting/resizing the geometry will resize the visuals
	 */
	@Test
	public void resizeGeometryOnResize() {
		GeometryNode<RoundedRectangle> n = new GeometryNode<>();
		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);
		n.setGeometry(new RoundedRectangle(30, 40, 30, 40, 20, 20));
		
		n.resize(30, 40);
		assertEquals(n.getGeometry().getBounds().getWidth(), 20, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 30, 0);
		assertEquals(30.0, n.getWidth(), 0);
		assertEquals(40.0, n.getHeight(), 0);
		assertEquals(25, n.getLayoutX(), 0);
		assertEquals(35, n.getLayoutY(), 0);
		assertEquals(30, n.getGeometry().getBounds().getX(), 0);
		assertEquals(40, n.getGeometry().getBounds().getY(), 0);
	}
	
	@Test
	public void resizePolylineNode(){
		GeometryNode<Polyline> n = new GeometryNode<>();
		//Polyline: (19.02538299560547, 30.438175201416016) -> (19.02538299560547, 108.58389282226562)
		n.setGeometry(new Polyline(19.02538299560547, 30.438175201416016, 19.02538299560547, 108.58389282226562));
		n.setStrokeWidth(3.5);
		n.setStrokeType(StrokeType.CENTERED);
		System.out.println(n.getGeometry().getBounds());
	}

	@Test
	public void resizeRelocateOnStrokeWidthAndTypeChange() {
		GeometryNode<RoundedRectangle> n = new GeometryNode<>();
		n.setGeometry(new RoundedRectangle(30, 40, 30, 40, 20, 20));
		
		assertEquals(29.5, n.getLayoutX(), 0);
		assertEquals(39.5, n.getLayoutY(), 0);
		assertEquals(30, n.getGeometry().getBounds().getX(), 0);
		assertEquals(40, n.getGeometry().getBounds().getY(), 0);
		
		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);

		assertEquals(n.getGeometry().getBounds().getWidth(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 40, 0);
		assertEquals(40.0, n.getWidth(), 0);
		assertEquals(50.0, n.getHeight(), 0);
		assertEquals(25, n.getLayoutX(), 0);
		assertEquals(35, n.getLayoutY(), 0);
		assertEquals(30, n.getGeometry().getBounds().getX(), 0);
		assertEquals(40, n.getGeometry().getBounds().getY(), 0);
	}
	
	@Test
	public void relocateOnGeometryChange(){
		GeometryNode<RoundedRectangle> n = new GeometryNode<>();
		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);
		n.setGeometry(new RoundedRectangle(30, 40, 30, 40, 20, 20));
		
		assertEquals(n.getGeometry().getBounds().getX(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getY(), 40, 0);
		assertEquals(25.0, n.getLayoutX(), 0);
		assertEquals(35.0, n.getLayoutY(), 0);
		assertEquals(n.getGeometry().getBounds().getWidth(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 40, 0);
		assertEquals(40.0, n.getWidth(), 0);
		assertEquals(50.0, n.getHeight(), 0);
		
		n.relocateGeometry(50, 60);
		assertEquals(n.getGeometry().getBounds().getX(), 50, 0);
		assertEquals(n.getGeometry().getBounds().getY(), 60, 0);
		assertEquals(45.0, n.getLayoutX(), 0);
		assertEquals(55.0, n.getLayoutY(), 0);
		assertEquals(n.getGeometry().getBounds().getWidth(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 40, 0);
		assertEquals(40.0, n.getWidth(), 0);
		assertEquals(50.0, n.getHeight(), 0);
	}
	
	@Test
	public void relocateGeometryOnRelocate(){
		GeometryNode<RoundedRectangle> n = new GeometryNode<>();
		n.setFill(Color.RED);
		n.setStrokeWidth(5);
		n.setStrokeType(StrokeType.OUTSIDE);
		n.setGeometry(new RoundedRectangle(30, 40, 30, 40, 20, 20));
		n.relocate(30, 40);
		
		assertEquals(n.getGeometry().getBounds().getX(), 35, 0);
		assertEquals(n.getGeometry().getBounds().getY(), 45, 0);
		assertEquals(30.0, n.getLayoutX(), 0);
		assertEquals(40.0, n.getLayoutY(), 0);
		assertEquals(n.getGeometry().getBounds().getWidth(), 30, 0);
		assertEquals(n.getGeometry().getBounds().getHeight(), 40, 0);
		assertEquals(40.0, n.getWidth(), 0);
		assertEquals(50.0, n.getHeight(), 0);
	}
}
