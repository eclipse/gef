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

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.render.IFillProperties;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.junit.Test;

public abstract class AbstractFillPropertiesTests extends
		AbstractGraphicsPropertiesTests<IFillProperties> {

	@Test
	public void getColor() {
		assertEquals(properties.getColor(), propertiesCopy.getColor());
		assertFalse(properties.getColor() == propertiesCopy.getColor());
		properties.setColor(new Color(0, 0, 0, 0));
		propertiesCopy.setColor(new Color(1, 1, 1, 1));
		assertFalse(properties.getColor().equals(propertiesCopy.getColor()));
		properties.setColor(new Color(0, 1, 0, 1));
		assertFalse(properties.getColor().equals(propertiesCopy.getColor()));
		properties.setColor(new Color(1, 1, 1, 1));
		assertEquals(properties.getColor(), propertiesCopy.getColor());
	}

	@Override
	public IFillProperties getProperties(IGraphics g) {
		return g.fillProperties();
	}

	@Test
	public void isAntialiasing() {
		assertEquals(properties.isAntialiasing(),
				propertiesCopy.isAntialiasing());
		properties.setAntialiasing(true);
		propertiesCopy.setAntialiasing(false);
		assertFalse(properties.isAntialiasing() == propertiesCopy
				.isAntialiasing());
		properties.setAntialiasing(true);
		assertFalse(properties.isAntialiasing() == propertiesCopy
				.isAntialiasing());
		properties.setAntialiasing(false);
		assertEquals(properties.isAntialiasing(),
				propertiesCopy.isAntialiasing());
	}
}
