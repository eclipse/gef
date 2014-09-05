/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.mvc.models.ZoomModel;

//TODO: extract interface and use for binding
//TODO: make ITransactional
public class ZoomPolicy<VR> extends AbstractPolicy<VR> {

	// TODO: use a ChangeZoomOperation (and provide a hook to decide
	// whether it should be executed on the operation history)

	public void zoomAbsolute(double absoluteZoom) {
		ZoomModel zoomModel = getHost().getRoot().getViewer()
				.getAdapter(ZoomModel.class);
		zoomModel.setZoomFactor(absoluteZoom);
	}

	public void zoomRelative(double relativeZoom) {
		ZoomModel zoomModel = getHost().getRoot().getViewer()
				.getAdapter(ZoomModel.class);
		zoomModel.setZoomFactor(zoomModel.getZoomFactor() * relativeZoom);
	}
}
