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
package org.eclipse.gef4.swt.canvas.todo;

import org.eclipse.gef4.geometry.planar.IShape;

public class GeneralBounds extends
		AbstractBounds<GeneralBounds, IShape, IShape> {

	private GeneralBounds() {
	}

	public GeneralBounds(IShape bounds) {
		setShape(bounds);
	}

	@Override
	protected GeneralBounds copy() {
		return new GeneralBounds();
	}

	@Override
	protected boolean isShapeOk(IShape shape) {
		return shape != null;
	}

	@Override
	protected IShape transform(IShape shape) {
		return shape.getTransformed(getTransformByReference());
	}

}
