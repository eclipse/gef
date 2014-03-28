/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Polyline;

public class FXCurveConnection extends AbstractFXConnection<ICurve> {

	public FXCurveConnection() {
	}

	public FXCurveConnection(IFXAnchor startAnchor, IFXAnchor endAnchor) {
		setStartAnchor(startAnchor);
		setEndAnchor(endAnchor);
	}

	@Override
	public ICurve computeGeometry() {
		return new Polyline(getPoints());
	}

}
