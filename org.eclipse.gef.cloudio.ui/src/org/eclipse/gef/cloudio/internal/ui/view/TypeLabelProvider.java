/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.internal.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.gef.cloudio.internal.ui.data.Type;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author sschwieb
 *
 */
public class TypeLabelProvider extends BaseLabelProvider implements IEditableCloudLabelProvider {

	private double maxOccurrences;
	private double minOccurrences;

	public enum Scaling {

		LINEAR, LOGARITHMIC;
	}

	private Map<Object, Color> colors = new HashMap<>();
	private Map<Object, FontData[]> fonts = new HashMap<>();
	private Random random = new Random();
	protected List<Color> colorList;
	protected List<Font> fontList;
	protected List<Float> angles;
	private Scaling scaling = Scaling.LOGARITHMIC;

	public TypeLabelProvider() {
		colorList = new ArrayList<>();
		fontList = new ArrayList<>();
		angles = new ArrayList<>();
		angles.add(0F);
	}

	@Override
	public String getLabel(Object element) {
		return ((Type) element).getString();
	}

	@Override
	public double getWeight(Object element) {
		switch (scaling) {
		case LINEAR:
			return ((Type) element).getOccurrences() / maxOccurrences;
		case LOGARITHMIC: {
			double count = Math.log(((Type) element).getOccurrences() - minOccurrences + 1);
			if (count != 0) {
				count /= Math.log(maxOccurrences);
			}
			return count;
		}
		default:
			return 1;
		}

	}

	@Override
	public Color getColor(Object element) {
		Color color = colors.get(element);
		if (color == null) {
			color = colorList.get(random.nextInt(colorList.size()));
			colors.put(element, color);
		}
		return color;
	}

	public FontData[] getFontData(Object element) {
		FontData[] data = fonts.get(element);
		if (data == null) {
			data = fontList.get(random.nextInt(fontList.size())).getFontData();
			fonts.put(element, data);
		}
		return data;
	}

	public void setMaxOccurrences(int occurrences) {
		this.maxOccurrences = occurrences;
	}

	public void setMinOccurrences(int occurrences) {
		this.minOccurrences = occurrences;
	}

	@Override
	public void dispose() {
		for (Color color : colorList) {
			color.dispose();
		}
		for (Font font : fontList) {
			font.dispose();
		}
	}

	public void setAngles(List<Float> angles) {
		this.angles = angles;
	}

	@Override
	public float getAngle(Object element) {
		float angle = angles.get(random.nextInt(angles.size()));
		return angle;
	}

	public void setColors(List<RGB> newColors) {
		if (newColors.isEmpty())
			return;
		for (Color color : colorList) {
			color.dispose();
		}
		colorList.clear();
		colors.clear();
		for (RGB color : newColors) {
			Color c = new Color(Display.getDefault(), color);
			colorList.add(c);
		}
	}

	public void setFonts(List<FontData> newFonts) {
		if (newFonts.isEmpty())
			return;
		for (Font font : fontList) {
			font.dispose();
		}
		fontList.clear();
		fonts.clear();
		for (FontData data : newFonts) {
			Font f = new Font(Display.getDefault(), data);
			fontList.add(f);
		}
	}

	@Override
	public String getToolTip(Object element) {
		return getLabel(element);
	}

	public void setScale(Scaling scaling) {
		this.scaling = scaling;
	}

}
