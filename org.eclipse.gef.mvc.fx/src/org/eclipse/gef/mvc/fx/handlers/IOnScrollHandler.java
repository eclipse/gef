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

import org.eclipse.gef.mvc.fx.gestures.ScrollGesture;

import javafx.scene.input.ScrollEvent;

/**
 * An interaction handler that implements the {@link IOnScrollHandler} interface
 * will be notified about scroll events (mouse wheel or touch) by the
 * {@link ScrollGesture}.
 *
 * @author mwienand
 *
 */
public interface IOnScrollHandler extends IHandler {

	/**
	 * This callback method is invoked when a scroll gesture ends unexpectedly.
	 */
	void abortScroll();

	/**
	 * This callback method is invoked when the user stopped to perform mouse
	 * scrolling over the host.
	 */
	void endScroll();

	/**
	 * This callback method is invoked when the user performs mouse scrolling
	 * over the host.
	 *
	 * @param event
	 *            The original {@link ScrollEvent}.
	 */
	void scroll(ScrollEvent event);

	/**
	 * This callback method is invoked when the user starts to perform mouse
	 * scrolling over the host.
	 *
	 * @param event
	 *            The original {@link ScrollEvent}.
	 */
	void startScroll(ScrollEvent event);

}