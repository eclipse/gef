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
package org.eclipse.gef4.mvc.fx.parts;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;

/**
 * @author anyssen
 */
// TODO: move to FX??
public class VisualBoundsGeometryProvider extends
		VisualOutlineGeometryProvider {

	@Override
	public IGeometry get() {
		IGeometry geometry = super.get();
		if (geometry instanceof ICurve) {
			return geometry;
		} else {
			return geometry.getBounds();
		}
	}

}
