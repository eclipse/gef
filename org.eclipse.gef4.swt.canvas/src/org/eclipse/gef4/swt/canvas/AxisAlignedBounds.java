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
package org.eclipse.gef4.swt.canvas;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Rectangle;

public class AxisAlignedBounds extends
		AbstractBounds<AxisAlignedBounds, Rectangle, Rectangle> {

	private AxisAlignedBounds() {
	}

	public AxisAlignedBounds(Rectangle bounds) {
		setShape(bounds);
	}

	@Override
	protected AxisAlignedBounds copy() {
		return new AxisAlignedBounds();
	}

	@Override
	protected boolean isShapeOk(IShape shape) {
		return shape instanceof Rectangle;
	}

	@Override
	protected Rectangle transform(Rectangle box) {
		AffineTransform at = getTransformByReference();
		return new Rectangle(at.getTransformed(box.getTopLeft()),
				at.getTransformed(box.getBottomRight()));
	}

}
