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
package org.eclipse.gef4.swtfx;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;

public class TextFigure extends AbstractFigure {

	private String text;
	private Dimension textExtent = new Dimension();

	public TextFigure(String text) {
		setText(text);
		FontData fontData = getPaintStateByReference().getFontDataByReference();
		fontData.setHeight(12);
	}

	@Override
	public void autosize() {
		super.autosize();
	}

	@Override
	public boolean contains(double localX, double localY) {
		return getLayoutBounds().contains(localX, localY);
	}

	@Override
	protected void doPaint(GraphicsContext g) {
		// TODO: remove textExtent calculation, this should happen during
		// layout-pass
		if (textExtent.isEmpty()) {
			// this is the correct way of computing
			textExtent = g.textExtent(text);
		}
		g.fillText(text, 0, 0);
		// g.strokeText(text, 0, 0);
	}

	@Override
	public Rectangle getBoundsInLocal() {
		return getLayoutBounds();
	}

	@Override
	public Rectangle getLayoutBounds() {
		return new Rectangle(new Point(), textExtent);
	}

	public void setText(String text) {
		this.text = text;

		Scene scene = getScene();
		if (scene != null) {
			GC gc = new GC(scene);
			// TODO: this is just a hack, use GraphicsContextState
			org.eclipse.swt.graphics.Point extent = gc.textExtent(text);
			textExtent = new Dimension(extent.x, extent.y);
			gc.dispose();
		}
	}

}
