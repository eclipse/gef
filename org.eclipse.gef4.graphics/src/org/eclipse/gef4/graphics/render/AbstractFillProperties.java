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
package org.eclipse.gef4.graphics.render;

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

/**
 * The AbstractFillProperties class partially implements the
 * {@link IFillProperties} interface.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractFillProperties extends AbstractGraphicsProperties
		implements IFillProperties {

	/**
	 * The anti aliasing setting associated with this
	 * {@link AbstractFillProperties}.
	 */
	private boolean antialiasing = IFillProperties.DEFAULT_ANTIALIASING;

	private IFillMode mode = IFillProperties.DEFAULT_MODE;

	public void generalFill(IGraphics graphics, Path path) {
		Rectangle bounds = path.getBounds();
		Point p = bounds.getLocation();

		graphics.pushState();

		for (int x = (int) bounds.getX(); x <= bounds.getX()
				+ bounds.getWidth(); x++) {
			p.x = x;
			for (int y = (int) bounds.getY(); y <= bounds.getY()
					+ bounds.getHeight(); y++) {
				p.y = y;
				if (path.contains(p)) {
					graphics.drawProperties().setColor(getMode().getColorAt(p));
					graphics.draw(p);
				}
			}
		}

		graphics.popState();
	}

	@Override
	public IFillMode getMode() {
		// TODO: return a copy here
		return mode;
	}

	@Override
	public boolean isAntialiasing() {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IFillProperties is denied, because it is currently deactivated.");
		}
		return antialiasing;
	}

	@Override
	public IFillProperties setAntialiasing(boolean antialiasing) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IFillProperties is denied, because it is currently deactivated.");
		}
		this.antialiasing = antialiasing;
		return this;
	}

	@Override
	public IFillProperties setMode(IFillMode mode) {
		if (mode == null) {
			throw new IllegalArgumentException(
					"The mode parameter may not be null.");
		}
		this.mode = mode;
		return this;
	}

}
