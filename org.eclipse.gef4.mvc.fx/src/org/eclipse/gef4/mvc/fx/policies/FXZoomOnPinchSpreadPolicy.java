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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.policies.AbstractInteractionPolicy;

import javafx.scene.Node;
import javafx.scene.input.ZoomEvent;

/**
 * An {@link IFXOnPinchSpreadPolicy} that performs zooming.
 *
 * @author anyssen
 *
 */
public class FXZoomOnPinchSpreadPolicy extends AbstractInteractionPolicy<Node>
		implements IFXOnPinchSpreadPolicy {

	private FXChangeViewportPolicy getViewportPolicy() {
		return getHost().getRoot().getAdapter(FXChangeViewportPolicy.class);
	}

	@Override
	public void zoom(ZoomEvent e) {
		getViewportPolicy().zoomRelative(e.getZoomFactor(), e.getSceneX(),
				e.getSceneY());
	}

	@Override
	public void zoomFinished(ZoomEvent event) {
		ITransactionalOperation commit = getViewportPolicy().commit();
		if (commit != null && !commit.isNoOp()) {
			try {
				getHost().getRoot().getViewer().getDomain().execute(commit);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void zoomStarted(ZoomEvent e) {
		getViewportPolicy().init();
	}

}
