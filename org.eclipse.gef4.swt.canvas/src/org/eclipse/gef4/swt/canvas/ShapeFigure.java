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

import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;

public class ShapeFigure extends AbstractFigure {

	private IShape shape;

	public ShapeFigure(IShape shape) {
		this.shape = shape;
	}

	@Override
	public void doPaint(GraphicsContext g) {
		g.fillPath(shape.toPath());
	}

}
