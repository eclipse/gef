package org.eclipse.gef4.graphics.tests.swt;

import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.tests.AbstractGraphicsTests;

public class SWTGraphicsTests extends AbstractGraphicsTests {

	@Override
	public IGraphics createGraphics() {
		return Utils.createGraphics();
	}

}
