/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import org.eclipse.gef4.geometry.Point;

public class Pie extends AbstractGeometry implements IGeometry {

	public boolean contains(Point p) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public boolean contains(Rectangle r) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public Rectangle getBounds() {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public boolean intersects(Rectangle r) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public Path toPath() {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public IGeometry getCopy() {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

}
