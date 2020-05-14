/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.mvc.fx.gestures.ClickDragGesture;

import javafx.scene.input.MouseEvent;

/**
 * An interaction handler that implements the {@link IOnClickHandler} interface
 * will be notified about mouse click events by the {@link ClickDragGesture}.
 *
 * @author mwienand
 *
 */
public interface IOnClickHandler extends IHandler {

	/**
	 * This callback method is invoked when the user performs a mouse click on
	 * the host.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 */
	void click(MouseEvent e);

}