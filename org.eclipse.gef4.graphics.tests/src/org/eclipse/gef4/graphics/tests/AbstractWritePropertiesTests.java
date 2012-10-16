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
import org.eclipse.gef4.graphics.Font;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.IWriteProperties;
import org.junit.Test;

public abstract class AbstractWritePropertiesTests extends
		AbstractGraphicsPropertiesTests<IWriteProperties> {

	@Test
	public void getBackgroundColor() {
		assertEquals(properties.getBackgroundColor(),
				propertiesCopy.getBackgroundColor());
		assertFalse(properties.getBackgroundColor() == propertiesCopy
				.getBackgroundColor());
		properties.setBackgroundColor(new Color(0, 0, 0, 0));
		propertiesCopy.setBackgroundColor(new Color(1, 1, 1, 1));
		assertFalse(properties.getBackgroundColor().equals(
				propertiesCopy.getBackgroundColor()));
		properties.setBackgroundColor(new Color(0, 1, 0, 1));
		assertFalse(properties.getBackgroundColor().equals(
				propertiesCopy.getBackgroundColor()));
		properties.setBackgroundColor(new Color(1, 1, 1, 1));
		assertEquals(properties.getBackgroundColor(),
				propertiesCopy.getBackgroundColor());
	}

	@Test
	public void getFont() {
		assertEquals(properties.getFont(), propertiesCopy.getFont());
		assertFalse(properties.getFont() == propertiesCopy.getFont());
		properties.setFont(new Font("Times", 10, Font.STYLE_NORMAL));
		propertiesCopy.setFont(new Font("Times", 12, Font.STYLE_NORMAL));
		assertFalse(properties.getFont().equals(propertiesCopy.getFont()));
		properties.setFont(new Font("Times", 12, Font.STYLE_UNDERLINED));
		assertFalse(properties.getFont().equals(propertiesCopy.getFont()));
		properties.setFont(new Font("Times", 12, Font.STYLE_NORMAL));
		assertEquals(properties.getFont(), propertiesCopy.getFont());
	}

	@Test
	public void getForegroundColor() {
		assertEquals(properties.getForegroundColor(),
				propertiesCopy.getForegroundColor());
		assertFalse(properties.getForegroundColor() == propertiesCopy
				.getForegroundColor());
		properties.setForegroundColor(new Color(0, 0, 0, 0));
		propertiesCopy.setForegroundColor(new Color(1, 1, 1, 1));
		assertFalse(properties.getForegroundColor().equals(
				propertiesCopy.getForegroundColor()));
		properties.setForegroundColor(new Color(0, 1, 0, 1));
		assertFalse(properties.getForegroundColor().equals(
				propertiesCopy.getForegroundColor()));
		properties.setForegroundColor(new Color(1, 1, 1, 1));
		assertEquals(properties.getForegroundColor(),
				propertiesCopy.getForegroundColor());
	}

	@Override
	public IWriteProperties getProperties(IGraphics g) {
		return g.writeProperties();
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
