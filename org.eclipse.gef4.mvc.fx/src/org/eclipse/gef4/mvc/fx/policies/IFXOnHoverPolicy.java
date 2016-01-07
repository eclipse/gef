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
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.policies.IPolicy;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * An interaction policy that implements the {@link IFXOnHoverPolicy} interface
 * will be notified about mouse hover events by the {@link FXHoverTool}.
 *
 * @author mwienand
 *
 */
public interface IFXOnHoverPolicy extends IPolicy<Node> {

	/**
	 * This callback method is invoked when the mouse hovers the host.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 */
	void hover(MouseEvent e);

}