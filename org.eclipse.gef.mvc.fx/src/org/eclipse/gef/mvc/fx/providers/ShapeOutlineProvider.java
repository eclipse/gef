/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.providers;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.scene.Node;

/**
 * The {@link ShapeOutlineProvider} is a {@link Provider
 * Provider&lt;IGeometry&gt;} that returns an {@link IGeometry} that corresponds
 * to the shape outline of its host visual, i.e. it includes the geometric
 * outline and the stroke of the visual. The {@link IGeometry} is specified
 * within the local coordinate system of the host visual.
 *
 * @author mwienand
 *
 */
public class ShapeOutlineProvider
		extends IAdaptable.Bound.Impl<IVisualPart<? extends Node>>
		implements Provider<IGeometry> {

	@Override
	public IGeometry get() {
		return NodeUtils.getShapeOutline(getAdaptable().getVisual());
	}

}
