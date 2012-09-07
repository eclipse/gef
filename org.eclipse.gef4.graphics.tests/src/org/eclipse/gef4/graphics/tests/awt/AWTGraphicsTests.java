package org.eclipse.gef4.graphics.tests.awt;

import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.tests.AbstractGraphicsTests;

public class AWTGraphicsTests extends AbstractGraphicsTests {

	@Override
	public IGraphics createGraphics() {
		return Utils.createGraphics();
	}

}
