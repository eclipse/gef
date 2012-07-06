package org.eclipse.gef4.geometry.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Arc;
import org.junit.Test;

public class ArcTests {

	@Test
	public void test_equals() {
		Arc a1 = new Arc(0, 0, 100, 100, Angle.fromDeg(0), Angle.fromDeg(100));
		Arc a2 = new Arc(0, 0, 100, 100, Angle.fromDeg(360d - TestUtils
				.getPrecisionFraction() / 10d), Angle.fromDeg(100));
		assertEquals(a1, a2);
	}
}
