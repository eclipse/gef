/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import javafx.scene.input.ScrollEvent;

/**
 * The {@link FXZoomOnScrollPolicy} is an {@link AbstractFXOnScrollPolicy} that
 * scales the viewport when the user scroll with the mouse wheel while pressing
 * either <code>&lt;Control&gt;</code> or <code>&lt;Alt&gt;</code>.
 *
 * @author mwienand
 *
 */
public class FXZoomOnScrollPolicy extends AbstractFXOnScrollPolicy {

	private FXChangeViewportPolicy viewportPolicy;

	/**
	 * Returns the {@link FXChangeViewportPolicy} that is to be used for
	 * changing the viewport. This method is called within
	 * {@link #scrollStarted(ScrollEvent)} where the resulting policy is cached
	 * ({@link #setViewportPolicy(FXChangeViewportPolicy)}) for the scroll
	 * gesture.
	 *
	 * @return The {@link FXChangeViewportPolicy} that is to be used for
	 *         changing the viewport.
	 */
	protected FXChangeViewportPolicy determineViewportPolicy() {
		return getHost().getRoot().getAdapter(FXChangeViewportPolicy.class);
	}

	/**
	 * Returns the {@link FXChangeViewportPolicy} that is used for changing the
	 * viewport within the current scroll gesture. This policy is set within
	 * {@link #scrollStarted(ScrollEvent)} to the value determined by
	 * {@link #determineViewportPolicy()}.
	 *
	 * @return The {@link FXChangeViewportPolicy} that is used for changing the
	 *         viewport within the current scroll gesture.
	 */
	protected FXChangeViewportPolicy getViewportPolicy() {
		return viewportPolicy;
	}

	/**
	 * Returns <code>true</code> if the given {@link ScrollEvent} should trigger
	 * zooming. Otherwise returns <code>false</code>. Per default, either
	 * <code>&lt;Control&gt;</code> or <code>&lt;Alt&gt;</code> has to be
	 * pressed so that <code>true</code> is returned.
	 *
	 * @param event
	 *            The {@link ScrollEvent} in question.
	 * @return <code>true</code> if the given {@link ScrollEvent} should trigger
	 *         zooming, otherwise <code>false</code>.
	 */
	protected boolean isZoom(ScrollEvent event) {
		return event.isControlDown() || event.isAltDown();
	}

	@Override
	public void scroll(ScrollEvent event) {
		if (isZoom(event)) {
			getViewportPolicy().zoomRelative(
					event.getDeltaY() > 0 ? 1.05 : 1 / 1.05, event.getSceneX(),
					event.getSceneY());
		}
	}

	@Override
	public void scrollFinished() {
		commit(getViewportPolicy());
	}

	@Override
	public void scrollStarted(ScrollEvent event) {
		setViewportPolicy(determineViewportPolicy());
		init(getViewportPolicy());
		// delegate to scroll() to perform zooming
		scroll(event);
	}

	/**
	 * Sets the {@link FXChangeViewportPolicy} that is used to manipulate the
	 * viewport for the current scroll gesture to the given value.
	 *
	 * @param viewportPolicy
	 *            The new {@link FXChangeViewportPolicy} that is to be used to
	 *            manipulate the viewport for the current scroll gesture.
	 */
	protected void setViewportPolicy(FXChangeViewportPolicy viewportPolicy) {
		this.viewportPolicy = viewportPolicy;
	}

}
