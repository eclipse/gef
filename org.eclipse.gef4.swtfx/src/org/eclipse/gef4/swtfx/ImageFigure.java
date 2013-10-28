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

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.swt.graphics.Image;

public class ImageFigure extends AbstractFigure {

	private Image image;

	public ImageFigure() {
	}

	public ImageFigure(Image image) {
		setImage(image);
	}

	@Override
	public boolean contains(double localX, double localY) {
		return getLayoutBounds().contains(localX, localY);
	}

	@Override
	protected void doPaint(GraphicsContext g) {
		g.drawImage(getImage(), 0, 0);
	}

	@Override
	public Rectangle getBoundsInLocal() {
		return getLayoutBounds();
	}

	public Image getImage() {
		return image;
	}

	@Override
	public Rectangle getLayoutBounds() {
		if (getImage() == null) {
			return new Rectangle();
		}
		return new Rectangle(0, 0, getImage().getBounds().width, getImage()
				.getBounds().height);
	}

	public void setImage(Image image) {
		this.image = image;
	}

}
