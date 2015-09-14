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

import javafx.scene.input.ZoomEvent;

/**
 * An {@link AbstractFXOnPinchSpreadPolicy} that performs zooming.
 *
 * @author anyssen
 *
 */
public class FXZoomOnPinchSpreadPolicy extends AbstractFXOnPinchSpreadPolicy {

	private FXChangeViewportPolicy getViewportPolicy() {
		return getHost().getRoot().getAdapter(FXChangeViewportPolicy.class);
	}

	@Override
	public void zoom(ZoomEvent e) {
		getViewportPolicy().zoomRelative(e.getZoomFactor(), e.getSceneX(),
				e.getSceneY());
	}

	@Override
	public void zoomFinished(ZoomEvent e) {
		ITransactionalOperation commit = getViewportPolicy().commit();
		if (commit != null) {
			getHost().getRoot().getViewer().getDomain().execute(commit);
		}
	}

	@Override
	public void zoomStarted(ZoomEvent e) {
		getViewportPolicy().init();
	}

}
