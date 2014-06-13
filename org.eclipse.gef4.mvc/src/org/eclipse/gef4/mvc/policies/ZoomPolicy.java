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

import org.eclipse.gef4.mvc.models.IZoomModel;

public class ZoomPolicy<VR> extends AbstractPolicy<VR> {

	public void zoomAbsolute(double absoluteZoom){
		IZoomModel zoomModel = getHost().getRoot().getViewer().getZoomModel();
		zoomModel.setZoomFactor(absoluteZoom);
	}
	
	public void zoomRelative(double relativeZoom){
		IZoomModel zoomModel = getHost().getRoot().getViewer().getZoomModel();
		zoomModel.setZoomFactor(zoomModel.getZoomFactor() * relativeZoom);
	}
}
