/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.scene.Node;

import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

// TODO: adjust API, move to MVC
public class FXZoomOnScrollPolicy extends AbstractPolicy<Node> implements IScrollPolicy<Node> {

	public void zoom(double deltaY) {
		double factor = deltaY > 0 ? 1.25 : 0.8;
		IZoomModel zoomModel = getHost().getRoot().getViewer().getZoomModel();
		zoomModel.setZoomFactor(zoomModel.getZoomFactor() * factor);
	}
	
}
