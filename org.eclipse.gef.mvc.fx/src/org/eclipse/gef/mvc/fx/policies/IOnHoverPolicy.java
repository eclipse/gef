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

import org.eclipse.gef.mvc.fx.tools.HoverTool;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * An interaction policy that implements the {@link IOnHoverPolicy} interface
 * will be notified about mouse hover events by the {@link HoverTool}.
 *
 * @author mwienand
 *
 */
public interface IOnHoverPolicy extends IPolicy {

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