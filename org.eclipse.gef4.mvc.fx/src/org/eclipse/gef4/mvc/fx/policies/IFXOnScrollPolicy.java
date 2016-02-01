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

import org.eclipse.gef4.mvc.fx.tools.FXScrollTool;
import org.eclipse.gef4.mvc.policies.IPolicy;

import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;

/**
 * An interaction policy that implements the {@link IFXOnScrollPolicy} interface
 * will be notified about scroll events (mouse wheel or touch) by the
 * {@link FXScrollTool}.
 *
 * @author mwienand
 *
 */
public interface IFXOnScrollPolicy extends IPolicy<Node> {

	/**
	 * This callback method is invoked when the user performs mouse scrolling
	 * over the host.
	 *
	 * @param event
	 *            The original {@link ScrollEvent}.
	 */
	void scroll(ScrollEvent event);

	/**
	 * This callback method is invoked when a scroll gesture ends unexpectedly.
	 */
	void scrollAborted();

	/**
	 * This callback method is invoked when the user stopped to perform mouse
	 * scrolling over the host.
	 */
	void scrollFinished();

	/**
	 * This callback method is invoked when the user starts to perform mouse
	 * scrolling over the host.
	 *
	 * @param event
	 *            The original {@link ScrollEvent}.
	 */
	void scrollStarted(ScrollEvent event);

}