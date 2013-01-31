/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.Pattern;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;
import org.junit.Test;

public class PatternTests {

	@Test
	public void test_equals() {
		Pattern p0 = new Pattern(new Color(), new Gradient.Linear(new Point(),
				new Point()), new Image(1, 1), Pattern.Mode.COLOR);
		Pattern p1 = new Pattern(new Color(), new Gradient.Linear(new Point(),
				new Point()), new Image(1, 1), Pattern.Mode.COLOR);
		assertEquals(p0, p1);
	}

}
