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

import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContextState;

public abstract class AbstractFigure implements IFigure {

	private GraphicsContextState paintState = new GraphicsContextState();

	protected void cleanUpPaint(GraphicsContext g) {
		g.takeDownGuard();
		g.restore();
	}

	protected abstract void doPaint(GraphicsContext g);

	@Override
	public GraphicsContextState getPaintStateByReference() {
		return paintState;
	}

	@Override
	final public void paint(GraphicsContext g) {
		validatePaint(g);
		doPaint(g);
		cleanUpPaint(g);
	}

	public void validatePaint(GraphicsContext g) {
		g.pushState(paintState);
		g.setUpGuard();
	}

}
