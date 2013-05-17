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

public interface IFigure extends INode {

	IBounds getBounds();

	Group getContainer();

	GraphicsContextState getPaintStateByReference();

	void handleEvent(Object event);

	void paint(GraphicsContext g);

	/**
	 * change container (do not call)
	 * 
	 * @param group
	 */
	void setContainer(Group group);

	void update();

}
