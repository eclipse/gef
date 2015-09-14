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

import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

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

	private FXChangeViewportPolicy getViewportPolicy() {
		return getHost().getRoot().getAdapter(FXChangeViewportPolicy.class);
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
			zoomRelative(event.getDeltaY() > 0 ? 1.05 : 1 / 1.05,
					event.getSceneX(), event.getSceneY());
		}
	}

	/**
	 * Scales the viewport by the given <i>relativeZoom</i> factor around the
	 * given pivot point in scene coordinates.
	 *
	 * @param relativeZoom
	 *            The scale factor.
	 * @param sceneX
	 *            The pivot x-coordinate.
	 * @param sceneY
	 *            The pivot y-coordinate.
	 */
	public void zoomRelative(double relativeZoom, double sceneX,
			double sceneY) {
		FXChangeViewportPolicy viewportPolicy = getViewportPolicy();
		viewportPolicy.init();
		viewportPolicy.zoomRelative(relativeZoom, sceneX, sceneY);
		ITransactionalOperation commit = viewportPolicy.commit();
		if (commit != null) {
			getHost().getRoot().getViewer().getDomain().execute(commit);
		}
	}

}
