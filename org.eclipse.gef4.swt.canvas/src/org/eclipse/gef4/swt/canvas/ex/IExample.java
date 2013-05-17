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
package org.eclipse.gef4.swt.canvas.ex;

import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.swt.widgets.Canvas;

public interface IExample {

	/**
	 * Called once during GUI initialization.
	 * 
	 * @param c
	 */
	void addUi(Canvas c);

	/**
	 * Called once during GUI initialization.
	 * 
	 * @return
	 */
	int getHeight();

	/**
	 * Called once during GUI initialization.
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * Called once during GUI initialization.
	 * 
	 * @return
	 */
	int getWidth();

	/**
	 * Called once per paint request. Will not be called before GUI
	 * initialization is finished.
	 * 
	 * @param g
	 */
	void render(GraphicsContext g);

}
