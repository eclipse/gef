package org.eclipse.gef4.geometry.convert.fx.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Path;
import org.junit.Test;

public class FXConversionTests {

	@Test
	public void test_PathConversion() {
		// Create a Path whose coordinate values are convertible from double to
		// float without loosing precision so that we can compare the Path with
		// an SWT equivalent as an SWT Path consists of float coordinates.
		Path p = new Path().moveTo(50, 50).lineTo(100, 100)
				.quadTo(100, 150, 50, 150).cubicTo(20, 120, 20, 80, 50, 50)
				.close();
		assertEquals(p, JavaFX2Geometry.toPath(Geometry2JavaFX.toPath(p)));
	}

}
