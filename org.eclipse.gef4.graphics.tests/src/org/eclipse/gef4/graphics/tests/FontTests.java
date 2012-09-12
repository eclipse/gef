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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.graphics.Font;
import org.junit.Test;

public class FontTests {

	@Test
	public void default_constructor() {
		Font f = new Font();
		assertNotNull(f);
		assertEquals(f, f);
		assertEquals(Font.DEFAULT_FAMILY, f.getFamily());
		assertTrue(Font.DEFAULT_SIZE == f.getSize());
		assertEquals(Font.DEFAULT_STYLE, f.getStyle());
	}

	@Test
	public void equals() {
		Font f0 = new Font("Arial", 10, Font.STYLE_NORMAL);
		assertNotNull(f0);
		assertEquals(f0, f0);
		assertFalse(f0.equals(null));
		assertFalse(f0.equals(new Object()));

		Font f1 = new Font("Arial", 10, Font.STYLE_NORMAL);
		assertEquals(f0, f1);
		assertEquals(f1, f0);

		f1.setFamily("A");
		assertFalse(f0.equals(f1));
		assertFalse(f1.equals(f0));
		f1.setFamily("Arial").setSize(1);
		assertFalse(f0.equals(f1));
		assertFalse(f1.equals(f0));
		f1.setSize(10).setStyle(Font.STYLE_BOLD);
		assertFalse(f0.equals(f1));
		assertFalse(f1.equals(f0));

		f1.setTo(f0);
		assertEquals(f0, f1);
		assertEquals(f1, f0);
	}

	@Test
	public void invalid_style() {
		Font f = new Font();
		// smaller than or greater than the minimum or maximum style values,
		// respectively
		for (int style : new int[] { -1234, -123, -12, -1, 8, 89, 8910 }) {
			boolean thrown = false;
			try {
				f.setStyle(style);
			} catch (IllegalArgumentException x) {
				thrown = true;
			}
			assertTrue(thrown);
		}
	}

	@Test
	public void styles() {
		Font f = new Font();
		for (boolean bold : new boolean[] { false, true }) {
			for (boolean italic : new boolean[] { false, true }) {
				for (boolean underlined : new boolean[] { false, true }) {
					f.setStyle((bold ? Font.STYLE_BOLD : 0)
							| (italic ? Font.STYLE_ITALIC : 0)
							| (underlined ? Font.STYLE_UNDERLINED : 0));
					assertTrue((!bold && !italic && !underlined) ? f.isNormal()
							: !f.isNormal());
					assertTrue(bold ? f.isBold() : !f.isBold());
					assertTrue(italic ? f.isItalic() : !f.isItalic());
					assertTrue(underlined ? f.isUnderlined() : !f
							.isUnderlined());
				}
			}
		}
	}

	@Test
	public void test_toString() {
		Font f = new Font("Times New Roman", 12, Font.STYLE_BOLD);
		assertEquals("Font(family = Times New Roman, size = 12.0, style = 1)",
				f.toString());
	}

}
