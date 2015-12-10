/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.providers;

import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.geometry.planar.IGeometry;

import com.google.inject.Provider;

import javafx.geometry.Bounds;

/**
 * The {@link ShapeOutlineProvider} is a {@link Provider Provider<IGeometry>}
 * that returns an {@link IGeometry} that corresponds to the shape outline of
 * its host visual, i.e. it includes the geometric outline and the stroke of the
 * visual. The {@link IGeometry} is specified within the local coordinate system
 * of the host visual.
 *
 * @author mwienand
 *
 */
public class ShapeOutlineProvider extends GeometricOutlineProvider {

	private static class GeometryResizer extends GeometryNode<IGeometry> {

		public GeometryResizer(IGeometry geom) {
			super(geom);
		}

		@Override
		public void resizeGeometry(double width, double height) {
			super.resizeGeometry(width, height);
		}

	}

	@Override
	public IGeometry get() {
		Bounds layoutBounds = getAdaptable().getVisual().getLayoutBounds();
		GeometryResizer geometryResizer = new GeometryResizer(super.get());
		geometryResizer.resizeGeometry(layoutBounds.getWidth(),
				layoutBounds.getHeight());
		return geometryResizer.getGeometry();
	}

}
