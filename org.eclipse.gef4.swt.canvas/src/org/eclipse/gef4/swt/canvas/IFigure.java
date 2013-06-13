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
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Event;

public interface IFigure extends INode {

	void addKeyListener(KeyListener listener);

	void addMouseListener(MouseListener listener);

	void addMouseMoveListener(MouseMoveListener listener);

	void addMouseWheelListener(MouseWheelListener listener);

	boolean forceRequestFocus();

	IBounds getBounds();

	Group getContainer();

	GraphicsContextState getPaintStateByReference();

	void handleEvent(Event event);

	void paint(GraphicsContext g);

	void removeKeyListener(KeyListener listener);

	void removeMouseListener(MouseListener listener);

	void removeMouseMoveListener(MouseMoveListener listener);

	void removeMouseWheelListener(MouseWheelListener listener);

	boolean requestFocus();

	/**
	 * change container (do not call)
	 * 
	 * @param group
	 */
	void setContainer(Group group);

	void update();

}
