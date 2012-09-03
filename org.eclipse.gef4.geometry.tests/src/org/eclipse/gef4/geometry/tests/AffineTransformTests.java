package org.eclipse.gef4.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.junit.Test;

public class AffineTransformTests {

	@Test
	public void test_equals() {
		AffineTransform t0 = new AffineTransform();
		AffineTransform t1 = new AffineTransform();
		assertEquals(t0, t1);
		assertEquals(t0, t0.getCopy());
		t1.setToTranslation(10, 10);
		assertFalse(t0.equals(t1));
		t0.setToTranslation(5, 5);
		assertFalse(t0.equals(t1));
		t1.setToTranslation(5, 5);
		assertEquals(t0, t1);
	}

}
