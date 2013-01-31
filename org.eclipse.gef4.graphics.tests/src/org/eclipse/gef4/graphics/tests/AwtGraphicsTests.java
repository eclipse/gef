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

import java.awt.image.BufferedImage;

import org.eclipse.gef4.graphics.awt.AwtGraphics;
import org.junit.Test;

public class AwtGraphicsTests extends AbstractGraphicsTests<AwtGraphics> {

	@Override
	public AwtGraphics createGraphics() {
		return new AwtGraphics(new BufferedImage(640, 480,
				java.awt.image.BufferedImage.TYPE_INT_ARGB).createGraphics());
	}

	@Test
	public void test_toString() {
		assertEquals("IGraphics()", graphics.toString());
	}

}
