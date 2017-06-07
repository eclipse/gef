/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.mvc.fx.gestures.HoverGesture;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * An interaction handler that implements the {@link IOnHoverHandler} interface
 * will be notified about mouse hover events by the {@link HoverGesture}.
 *
 * @author mwienand
 *
 */
public interface IOnHoverHandler extends IHandler {

	/**
	 * This callback method is invoked when the mouse hovers the host.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 */
	void hover(MouseEvent e);

	/**
	 * This callback method is invoked when the mouse is stationary over the
	 * host.
	 *
	 * @param hoverIntent
	 *            The {@link Node} that is hovered.
	 */
	void hoverIntent(Node hoverIntent);

}