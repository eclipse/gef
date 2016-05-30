/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for bug #483710
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.providers;

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;

/**
 * The {@link GeometricOutlineProvider} is a {@link Provider Provider
 * <IGeometry>} that returns an {@link IGeometry} that corresponds to the
 * geometric outline of its host visual, i.e. it does not include the stroke of
 * the visual or other visual properties (e.g. clip or effect). The
 * {@link IGeometry} is specified within the local coordinate system of the host
 * visual.
 * <p>
 * A {@link GeometricOutlineProvider} can be used to determine an
 * {@link IGeometry} for a {@link GeometryNode}, a {@link Connection}, as well
 * as the following JavaFX {@link Node} implementations:
 * <ul>
 * <li>{@link Arc}
 * <li>{@link Circle}
 * <li>{@link CubicCurve}
 * <li>{@link Ellipse}
 * <li>{@link Line}
 * <li>{@link Path}
 * <li>{@link Polygon}
 * <li>{@link Polyline}
 * <li>{@link QuadCurve}
 * <li>{@link Rectangle}
 * </ul>
 *
 * @author anyssen
 *
 */
public class GeometricOutlineProvider
		extends IAdaptable.Bound.Impl<IVisualPart<Node, ? extends Node>>
		implements Provider<IGeometry> {

	@Override
	public IGeometry get() {
		return NodeUtils.getGeometricOutline(getAdaptable().getVisual());
	}

}
