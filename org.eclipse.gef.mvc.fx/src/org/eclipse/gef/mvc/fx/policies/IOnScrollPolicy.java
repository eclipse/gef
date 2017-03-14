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

import org.eclipse.gef.mvc.fx.gestures.ScrollGesture;

import javafx.scene.input.ScrollEvent;

/**
 * An interaction policy that implements the {@link IOnScrollPolicy} interface
 * will be notified about scroll events (mouse wheel or touch) by the
 * {@link ScrollGesture}.
 *
 * @author mwienand
 *
 */
public interface IOnScrollPolicy extends IPolicy {

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