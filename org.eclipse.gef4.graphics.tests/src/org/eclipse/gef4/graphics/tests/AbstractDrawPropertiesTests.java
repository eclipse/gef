/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IDrawProperties;
import org.eclipse.gef4.graphics.IDrawProperties.LineCap;
import org.eclipse.gef4.graphics.IDrawProperties.LineJoin;
import org.eclipse.gef4.graphics.IGraphics;
import org.junit.Test;

public abstract class AbstractDrawPropertiesTests extends
		AbstractGraphicsPropertiesTests<IDrawProperties> {

	private static final double DELTA = 0.000001d;

	@Test
	public void getColor() {
		assertEquals(properties.getColor(), propertiesCopy.getColor());
		properties.setColor(new Color(0, 0, 0, 0));
		propertiesCopy.setColor(properties.getColor());
		assertEquals(properties.getColor(), propertiesCopy.getColor());
		properties.setColor(new Color(255, 0, 0, 255));
		assertTrue(!properties.getColor().equals(propertiesCopy.getColor()));
		propertiesCopy.setColor(new Color(0, 255, 0, 255));
		assertTrue(!properties.getColor().equals(propertiesCopy.getColor()));
		properties.setColor(propertiesCopy.getColor());
		assertEquals(propertiesCopy.getColor(), properties.getColor());
	}

	@Test
	public void getDashArray() {
		assertNull(properties.getDashArray());
		assertNull(propertiesCopy.getDashArray());
		properties.setDashArray(1, 2);
		propertiesCopy.setDashArray(1, 2);
		assertArrayEquals(properties.getDashArray(),
				propertiesCopy.getDashArray(), DELTA);
		properties.setDashArray(2, 1);
		propertiesCopy.setDashArray(1, 2);
		assertFalse(Arrays.equals(properties.getDashArray(),
				propertiesCopy.getDashArray()));
		properties.setDashArray(1, 1, 2);
		assertFalse(Arrays.equals(properties.getDashArray(),
				propertiesCopy.getDashArray()));
	}

	@Test
	public void getLineCap() {
		assertEquals(properties.getLineCap(), propertiesCopy.getLineCap());
		properties.setLineCap(LineCap.FLAT);
		propertiesCopy.setLineCap(LineCap.ROUND);
		assertFalse(properties.getLineCap().equals(propertiesCopy.getLineCap()));
		properties.setLineCap(LineCap.SQUARE);
		assertFalse(properties.getLineCap().equals(propertiesCopy.getLineCap()));
	}

	@Test
	public void getLineJoin() {
		assertEquals(properties.getLineJoin(), propertiesCopy.getLineJoin());
		properties.setLineJoin(LineJoin.BEVEL);
		propertiesCopy.setLineJoin(LineJoin.ROUND);
		assertFalse(properties.getLineJoin().equals(
				propertiesCopy.getLineJoin()));
		properties.setLineJoin(LineJoin.MITER);
		assertFalse(properties.getLineJoin().equals(
				propertiesCopy.getLineJoin()));
	}

	@Test
	public void getLineWidth() {
		assertEquals(properties.getLineWidth(), propertiesCopy.getLineWidth(),
				DELTA);
		properties.setLineWidth(1);
		propertiesCopy.setLineWidth(10);
		assertFalse(properties.getLineWidth() == propertiesCopy.getLineWidth());
		properties.setLineWidth(5);
		assertFalse(properties.getLineWidth() == propertiesCopy.getLineWidth());
		properties.setLineWidth(10);
		assertEquals(properties.getLineWidth(), propertiesCopy.getLineWidth(),
				DELTA);
	}

	@Test
	public void getMiterLimit() {
		assertEquals(properties.getMiterLimit(),
				propertiesCopy.getMiterLimit(), DELTA);
		properties.setMiterLimit(1);
		propertiesCopy.setMiterLimit(10);
		assertFalse(properties.getMiterLimit() == propertiesCopy
				.getMiterLimit());
		properties.setMiterLimit(5);
		assertFalse(properties.getMiterLimit() == propertiesCopy
				.getMiterLimit());
		properties.setMiterLimit(10);
		assertEquals(properties.getMiterLimit(),
				propertiesCopy.getMiterLimit(), DELTA);
	}

	@Override
	public IDrawProperties getProperties(IGraphics g) {
		return g.drawProperties();
	}

	@Test
	public void isAntialiasing() {
		assertEquals(properties.isAntialiasing(),
				propertiesCopy.isAntialiasing());
		properties.setAntialiasing(true);
		propertiesCopy.setAntialiasing(false);
		assertTrue(properties.isAntialiasing());
		properties.setAntialiasing(true);
		assertFalse(propertiesCopy.isAntialiasing());
		assertFalse(properties.isAntialiasing() == propertiesCopy
				.isAntialiasing());
		properties.setAntialiasing(false);
		assertEquals(properties.isAntialiasing(),
				propertiesCopy.isAntialiasing());
	}

}
