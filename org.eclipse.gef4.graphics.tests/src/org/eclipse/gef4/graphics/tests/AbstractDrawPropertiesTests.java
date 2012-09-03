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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public abstract class AbstractDrawPropertiesTests extends AbstractGraphicsTests {

	public static class getCopy {

		private static final double DELTA = 0.000001d;
		protected static IDrawProperties gdp, dp, dpc;

		@BeforeClass
		public static void gobalSetUp() {
			gdp = graphics.getDrawProperties();
		}

		@Test
		public void getColor() {
			assertEquals(dp.getColor(), dpc.getColor());
			dp.setColor(new Color(0, 0, 0, 0));
			dpc.setColor(dp.getColor());
			assertEquals(dp.getColor(), dpc.getColor());
			dp.setColor(new Color(255, 0, 0, 255));
			assertTrue(!dp.getColor().equals(dpc.getColor()));
			dpc.setColor(new Color(0, 255, 0, 255));
			assertTrue(!dp.getColor().equals(dpc.getColor()));
			dp.setColor(dpc.getColor());
			assertEquals(dpc.getColor(), dp.getColor());
		}

		@Test
		public void getDashArray() {
			assertNull(dp.getDashArray());
			assertNull(dpc.getDashArray());
			dp.setDashArray(1, 2);
			dpc.setDashArray(1, 2);
			assertArrayEquals(dp.getDashArray(), dpc.getDashArray(), DELTA);
			dp.setDashArray(2, 1);
			dpc.setDashArray(1, 2);
			assertFalse(Arrays.equals(dp.getDashArray(), dpc.getDashArray()));
			dp.setDashArray(1, 1, 2);
			assertFalse(Arrays.equals(dp.getDashArray(), dpc.getDashArray()));
		}

		@Test
		public void getLineCap() {
			assertEquals(dp.getLineCap(), dpc.getLineCap());
			dp.setLineCap(LineCap.FLAT);
			dpc.setLineCap(LineCap.ROUND);
			assertFalse(dp.getLineCap().equals(dpc.getLineCap()));
			dp.setLineCap(LineCap.SQUARE);
			assertFalse(dp.getLineCap().equals(dpc.getLineCap()));
		}

		@Test
		public void getLineJoin() {
			assertEquals(dp.getLineJoin(), dpc.getLineJoin());
			dp.setLineJoin(LineJoin.BEVEL);
			dpc.setLineJoin(LineJoin.ROUND);
			assertFalse(dp.getLineJoin().equals(dpc.getLineJoin()));
			dp.setLineJoin(LineJoin.MITER);
			assertFalse(dp.getLineJoin().equals(dpc.getLineJoin()));
		}

		@Test
		public void getLineWidth() {
			assertEquals(dp.getLineWidth(), dpc.getLineWidth(), DELTA);
			dp.setLineWidth(1);
			dpc.setLineWidth(10);
			assertTrue(dp.getLineWidth() != dpc.getLineWidth());
			dp.setLineWidth(5);
			assertTrue(dp.getLineWidth() != dpc.getLineWidth());
		}

		@Test
		public void getMiterLimit() {
			assertEquals(dp.getMiterLimit(), dpc.getMiterLimit(), DELTA);
			dp.setMiterLimit(1);
			dpc.setMiterLimit(10);
			assertTrue(dp.getMiterLimit() != dpc.getMiterLimit());
			dp.setMiterLimit(5);
			assertTrue(dp.getMiterLimit() != dpc.getMiterLimit());
		}

		@Test
		public void instance_changes() {
			assertTrue(gdp != dp);
			assertTrue(dp != dpc);
		}

		@Test
		public void isAntialiasing() {
			assertEquals(dp.isAntialiasing(), dpc.isAntialiasing());
			dp.setAntialiasing(true);
			dpc.setAntialiasing(false);
			assertTrue(dp.isAntialiasing());
			dp.setAntialiasing(true);
			assertFalse(dpc.isAntialiasing());
		}

		@Before
		public void localSetUp() {
			dp = gdp.getCopy();
			dpc = dp.getCopy();
		}

	}

}
