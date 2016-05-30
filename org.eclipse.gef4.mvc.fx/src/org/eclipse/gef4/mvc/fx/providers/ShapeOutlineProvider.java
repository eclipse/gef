/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.scene.Node;

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
public class ShapeOutlineProvider
		extends IAdaptable.Bound.Impl<IVisualPart<Node, ? extends Node>>
		implements Provider<IGeometry> {

	@Override
	public IGeometry get() {
		return NodeUtils.getShapeOutline(getAdaptable().getVisual());
	}

}
