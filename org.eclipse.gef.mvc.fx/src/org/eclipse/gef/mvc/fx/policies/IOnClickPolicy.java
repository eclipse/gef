/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.mvc.fx.tools.ClickDragTool;

import javafx.scene.input.MouseEvent;

/**
 * An interaction policy that implements the {@link IOnClickPolicy} interface
 * will be notified about mouse click events by the {@link ClickDragTool}.
 *
 * @author mwienand
 *
 */
public interface IOnClickPolicy extends IPolicy {

	/**
	 * This callback method is invoked when the user performs a mouse click on
	 * the host.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 */
	void click(MouseEvent e);

}