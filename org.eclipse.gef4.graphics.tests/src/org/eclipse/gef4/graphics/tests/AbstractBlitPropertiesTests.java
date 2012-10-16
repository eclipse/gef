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
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.graphics.render.IBlitProperties;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.IBlitProperties.InterpolationHint;
import org.junit.Test;

public abstract class AbstractBlitPropertiesTests extends
		AbstractGraphicsPropertiesTests<IBlitProperties> {

	@Test
	public void getCopy() {
		assertEquals(properties.getInterpolationHint(),
				propertiesCopy.getInterpolationHint());

		propertiesCopy.setInterpolationHint(InterpolationHint.SPEED);
		IBlitProperties speedy = propertiesCopy.getCopy();
		propertiesCopy.setInterpolationHint(InterpolationHint.QUALITY);

		assertTrue(propertiesCopy != speedy);
		assertEquals(InterpolationHint.QUALITY,
				propertiesCopy.getInterpolationHint());
		assertEquals(InterpolationHint.SPEED, speedy.getInterpolationHint());
	}

	@Test
	public void getInterpolationHint_default() {
		assertEquals(IBlitProperties.DEFAULT_INTERPOLATION_HINT,
				propertiesCopy.getInterpolationHint());
	}

	@Override
	public IBlitProperties getProperties(IGraphics g) {
		return g.blitProperties();
	}

	@Test
	public void setInterpolationHint() {
		InterpolationHint hint = propertiesCopy.getInterpolationHint();
		assertEquals(hint, propertiesCopy.getInterpolationHint());

		propertiesCopy.setInterpolationHint(InterpolationHint.SPEED);
		assertEquals(InterpolationHint.SPEED,
				propertiesCopy.getInterpolationHint());

		propertiesCopy.setInterpolationHint(InterpolationHint.QUALITY);
		assertEquals(InterpolationHint.QUALITY,
				propertiesCopy.getInterpolationHint());
	}

}
