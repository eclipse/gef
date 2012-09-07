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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.Region;
import org.eclipse.gef4.geometry.planar.Ring;
import org.eclipse.gef4.graphics.ICanvasProperties;
import org.eclipse.gef4.graphics.IGraphics;
import org.junit.Test;

public abstract class AbstractCanvasPropertiesTests extends
		AbstractGraphicsPropertiesTests<ICanvasProperties> {

	@Test
	public void getAffineTransform() {
		assertEquals(properties.getAffineTransform(),
				propertiesCopy.getAffineTransform());
		properties.setAffineTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
		propertiesCopy.setAffineTransform(new AffineTransform(1, 0, 0, 1, 10,
				10));
		assertFalse(properties.getAffineTransform().equals(
				propertiesCopy.getAffineTransform()));
		properties.setAffineTransform(new AffineTransform(1, 0, 0, 1, 5, 5));
		assertFalse(properties.getAffineTransform().equals(
				propertiesCopy.getAffineTransform()));
		properties.setAffineTransform(new AffineTransform(1, 0, 0, 1, 10, 10));
		assertEquals(properties.getAffineTransform(),
				propertiesCopy.getAffineTransform());
	}

	@Test
	public void getClippingArea_with_Region() {
		assertEquals(properties.getClippingArea(),
				propertiesCopy.getClippingArea());
		properties.setClippingArea(new Region(new Rectangle(0, 0, 100, 100)));
		propertiesCopy
				.setClippingArea(new Region(new Rectangle(20, 20, 60, 60)));
		assertFalse(properties.getClippingArea().equals(
				propertiesCopy.getClippingArea()));
		properties.setClippingArea(new Region(new Rectangle(10, 10, 80, 80)));
		assertFalse(properties.getClippingArea().equals(
				propertiesCopy.getClippingArea()));
		properties.setClippingArea(new Region(new Rectangle(20, 20, 60, 60)));
		assertEquals(properties.getClippingArea(),
				propertiesCopy.getClippingArea());
		properties.setClippingArea((Region) null);
		propertiesCopy.setClippingArea((Ring) null);
		assertNull(properties.getClippingArea());
		assertNull(propertiesCopy.getClippingArea());
	}

	@Test
	public void getClippingArea_with_Ring() {
		assertEquals(properties.getClippingArea(),
				propertiesCopy.getClippingArea());
		properties.setClippingArea(new Ring(new Rectangle(0, 0, 100, 100)
				.toPolygon()));
		propertiesCopy.setClippingArea(new Ring(new Rectangle(20, 20, 60, 60)
				.toPolygon()));
		assertFalse(properties.getClippingArea().equals(
				propertiesCopy.getClippingArea()));
		properties.setClippingArea(new Ring(new Rectangle(10, 10, 80, 80)
				.toPolygon()));
		assertFalse(properties.getClippingArea().equals(
				propertiesCopy.getClippingArea()));
		properties.setClippingArea(new Ring(new Rectangle(20, 20, 60, 60)
				.toPolygon()));
		assertEquals(properties.getClippingArea(),
				propertiesCopy.getClippingArea());
	}

	@Override
	public ICanvasProperties getProperties(IGraphics g) {
		return g.canvasProperties();
	}

}
