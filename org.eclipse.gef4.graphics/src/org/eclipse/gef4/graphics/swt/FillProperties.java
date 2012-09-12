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
package org.eclipse.gef4.graphics.swt;

import org.eclipse.gef4.geometry.convert.Geometry2SWT;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.AbstractFillProperties;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IFillProperties;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class FillProperties extends AbstractFillProperties {

	protected GCLazyProperty<Color, org.eclipse.swt.graphics.Color> fillColorProperty = new GCLazyProperty<Color, org.eclipse.swt.graphics.Color>() {
		@Override
		protected org.eclipse.swt.graphics.Color generate(Color data) {
			return Utils.createSWTColor(data);
		}

		@Override
		protected void write(GC gc, org.eclipse.swt.graphics.Color val) {
			gc.setBackground(val);
		}
	};

	private org.eclipse.swt.graphics.Color initialColor;
	private int initialAlpha;

	public FillProperties() {
		fillColorProperty.set(new Color());
	}

	public void applyOn(IGraphics g, Path path) {
		GC gc = ((DisplayGraphics) g).getGC();
		gc.setAntialias(antialiasing ? SWT.ON : SWT.OFF);
		gc.setAlpha(fillColorProperty.data.getAlpha());
		fillColorProperty.apply(gc);

		org.eclipse.swt.graphics.Path swtPath = new org.eclipse.swt.graphics.Path(
				Display.getCurrent(), Geometry2SWT.toSWTPathData(path));
		gc.setFillRule(path.getWindingRule() == Path.WIND_EVEN_ODD ? SWT.FILL_EVEN_ODD
				: SWT.FILL_WINDING);
		gc.fillPath(swtPath);
		swtPath.dispose();
	}

	public void cleanUp(IGraphics g) {
		GC gc = ((DisplayGraphics) g).getGC();
		gc.setBackground(initialColor);
		gc.setAlpha(initialAlpha);
		fillColorProperty.clean();
	}

	public Color getColor() {
		return fillColorProperty.data.getCopy();
	}

	public IFillProperties getCopy() {
		FillProperties copy = new FillProperties();
		copy.setColor(getColor());
		copy.antialiasing = antialiasing;
		return copy;
	}

	public void init(IGraphics g) {
		GC gc = ((DisplayGraphics) g).getGC();
		initialColor = gc.getBackground();
		initialAlpha = gc.getAlpha();
	}

	public IFillProperties setColor(Color fillColor) {
		fillColorProperty.set(fillColor);
		return this;
	}

}
