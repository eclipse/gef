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

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.graphics.ICanvasProperties;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public abstract class AbstractCanvasPropertiesTests extends
		AbstractGraphicsTests {

	public static class getCopy {

		protected static ICanvasProperties gp, p, pc;

		@BeforeClass
		public static void gobalSetUp() {
			gp = graphics.getCanvasProperties();
		}

		@Test
		public void getAffineTransform() {
			assertEquals(p.getAffineTransform(), pc.getAffineTransform());
			p.setAffineTransform(new AffineTransform(1, 0, 0, 1, 0, 0));
			pc.setAffineTransform(new AffineTransform(1, 0, 0, 1, 10, 10));
			assertFalse(p.getAffineTransform().equals(pc.getAffineTransform()));
			p.setAffineTransform(new AffineTransform(1, 0, 0, 1, 5, 5));
			assertFalse(p.getAffineTransform().equals(pc.getAffineTransform()));
		}

		@Test
		public void getClippingArea() {
			assertEquals(p.getClippingArea(), pc.getClippingArea());
		}

		@Before
		public void localSetUp() {
			p = gp.getCopy();
			pc = p.getCopy();
		}

	}

}
