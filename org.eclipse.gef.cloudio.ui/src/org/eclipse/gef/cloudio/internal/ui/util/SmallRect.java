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
package org.eclipse.gef.cloudio.internal.ui.util;

import org.eclipse.swt.SWT;

/**
 * A custom variation of a rectangle, which stores the required values as short
 * instead of int, thus saving some space.
 * 
 * @author sschwieb
 *
 */
// TODO: replace with org.eclipse.gef.geometry.planar.Rectangle
public class SmallRect {

	final short x, y, width, height;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SmallRect other = (SmallRect) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public SmallRect(int x, int y, int width, int height) {
		this.x = (short) x;
		this.y = (short) y;
		this.width = (short) width;
		this.height = (short) height;
	}

	public boolean intersects(SmallRect rect) {
		if (rect == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		return rect == this || intersects(rect.x, rect.y, rect.width, rect.height);
	}

	public boolean intersects(final int x, final int y, final int width, final int height) {
		return (x < this.x + this.width) && (y < this.y + this.height) && (x + width > this.x) && (y + height > this.y);
	}

	public String toString() {
		return "Rectangle {" + x + ", " + y + ", " + width + ", " + height + "}";
	}

}
