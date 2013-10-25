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

public class TextFigure extends AbstractFigure {

	private String text;
	private Dimension textExtent = new Dimension();
	private boolean textChanged = true;

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
		if (textChanged) {
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		textChanged = true;
	}

}
