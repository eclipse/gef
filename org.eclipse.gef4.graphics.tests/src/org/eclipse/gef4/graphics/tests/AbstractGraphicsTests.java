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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.IBlitProperties.InterpolationHint;
import org.eclipse.gef4.graphics.render.IDrawProperties.LineCap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractGraphicsTests<T extends IGraphics> {

	/**
	 * The specific {@link IGraphics} implementation to test.
	 */
	protected T graphics;

	@Test
	public void accessibles_not_null() {
		assertNotNull(graphics.blitProperties());
		assertNotNull(graphics.canvasProperties());
		assertNotNull(graphics.drawProperties());
		assertNotNull(graphics.fillProperties());
		assertNotNull(graphics.fontUtils());
		assertNotNull(graphics.writeProperties());
	}

	@After
	public void cleanUpGraphics() {
		graphics.cleanUp();
	}

	/**
	 * Returns a specific {@link IGraphics} implementation to test.
	 * 
	 * @return a specific {@link IGraphics} implementation
	 */
	public abstract T createGraphics();

	@Before
	public void initializeGraphics() {
		graphics = createGraphics();
	}

	@Test
	public void state_blitProperties() {
		InterpolationHint ih0 = graphics.blitProperties()
				.setInterpolationHint(InterpolationHint.QUALITY)
				.getInterpolationHint();

		graphics.pushState();

		InterpolationHint ih1 = graphics.blitProperties()
				.getInterpolationHint();
		assertEquals(ih0, ih1);

		graphics.blitProperties().setInterpolationHint(InterpolationHint.SPEED);
		ih1 = graphics.blitProperties().getInterpolationHint();
		assertFalse(ih0.equals(ih1));

		graphics.popState();

		ih1 = graphics.blitProperties().getInterpolationHint();
		assertEquals(ih0, ih1);
	}

	@Test
	public void state_canvasProperties() {
		AffineTransform at0 = graphics.canvasProperties()
				.setAffineTransform(new AffineTransform()).getAffineTransform();

		graphics.pushState();

		AffineTransform at1 = graphics.canvasProperties().getAffineTransform();
		assertEquals(at0, at1);
		assertNotSame(at0, at1);

		graphics.canvasProperties().setAffineTransform(
				new AffineTransform().translate(10, 10));
		at1 = graphics.canvasProperties().getAffineTransform();
		assertFalse(at0.equals(at1));

		graphics.popState();

		at1 = graphics.canvasProperties().getAffineTransform();
		assertEquals(at0, at1);
		assertNotSame(at0, at1);
	}

	@Test
	public void state_drawProperties() {
		LineCap lc0 = graphics.drawProperties().setLineCap(LineCap.FLAT)
				.getLineCap();

		graphics.pushState();

		LineCap lc1 = graphics.drawProperties().getLineCap();
		assertEquals(lc0, lc1);

		graphics.drawProperties().setLineCap(LineCap.ROUND);
		lc1 = graphics.drawProperties().getLineCap();
		assertFalse(lc0.equals(lc1));

		graphics.popState();

		lc1 = graphics.drawProperties().getLineCap();
		assertEquals(lc0, lc1);
	}

	@Test
	public void state_fillProperties() {
		Color c0 = graphics.fillProperties().setColor(new Color(0, 0, 0, 0))
				.getColor();

		graphics.pushState();

		Color c1 = graphics.fillProperties().getColor();
		assertEquals(c0, c1);
		assertNotSame(c0, c1);

		graphics.fillProperties().setColor(new Color(1, 1, 1, 1));
		c1 = graphics.fillProperties().getColor();
		assertFalse(c0.equals(c1));

		graphics.popState();

		c1 = graphics.fillProperties().getColor();
		assertEquals(c0, c1);
		assertNotSame(c0, c1);
	}

	@Test
	public void state_writeProperties() {
		Color c0 = graphics.writeProperties()
				.setForegroundColor(new Color(0, 0, 0, 0)).getForegroundColor();

		graphics.pushState();

		Color c1 = graphics.writeProperties().getForegroundColor();
		assertEquals(c0, c1);
		assertNotSame(c0, c1);

		graphics.writeProperties().setForegroundColor(new Color(1, 1, 1, 1));
		c1 = graphics.writeProperties().getForegroundColor();
		assertFalse(c0.equals(c1));

		graphics.popState();

		c1 = graphics.writeProperties().getForegroundColor();
		assertEquals(c0, c1);
		assertNotSame(c0, c1);
	}

}
