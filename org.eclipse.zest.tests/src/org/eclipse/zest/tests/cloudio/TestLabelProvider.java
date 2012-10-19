/*******************************************************************************
 * Copyright (c) 2011 Stephan Schwiebert. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Stephan Schwiebert - initial API and implementation
 *******************************************************************************/
package org.eclipse.zest.tests.cloudio;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.cloudio.ICloudLabelProvider;

public class TestLabelProvider extends BaseLabelProvider implements
		ICloudLabelProvider {

	public static final double WEIGHT = 0.987D;
	public static final float ANGLE = 12.34F;
	public static Color COLOR = new Color(Display.getDefault(), new RGB(100,
			100, 100));
	public static FontData[] FONT_DATA = Display.getDefault().getShells()[0]
			.getFont().getFontData();

	public String getLabel(Object element) {
		return element.toString();
	}

	public double getWeight(Object element) {
		return WEIGHT;
	}

	public Color getColor(Object element) {
		return COLOR;
	}

	public FontData[] getFontData(Object element) {
		return FONT_DATA.clone();
	}

	public float getAngle(Object element) {
		return ANGLE;
	}

	public String getToolTip(Object element) {
		return getLabel(element);
	}

}
