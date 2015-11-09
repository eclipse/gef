/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.scene.Node;

/**
 * The {@link VisualOutlineGeometryProvider} can be registered on an
 * {@link IVisualPart} to provide an {@link IGeometry} based on the outline of
 * the part's visualization.
 *
 * @author anyssen
 *
 */
public class VisualOutlineGeometryProvider
		implements IAdaptable.Bound<IVisualPart<Node, ? extends Node>>,
		Provider<IGeometry> {

	private IVisualPart<Node, ? extends Node> host;

	@Override
	public IGeometry get() {
		// return geometry in local coordinates
		return getGeometry(host.getVisual());
	}

	@Override
	public IVisualPart<Node, ? extends Node> getAdaptable() {
		return host;
	}

	/**
	 * Returns an {@link IGeometry} representing the outline (or tight) bounds
	 * of the passed in visual {@link Node}, within the local coordinate space
	 * of that {@link Node}.
	 *
	 * @param visual
	 *            The {@link Node} for which to retrieve the tight bounds.
	 * @return An {@link IGeometry} representing the tight bounds.
	 */
	protected IGeometry getGeometry(Node visual) {
		if (visual instanceof Connection) {
			Node curveNode = ((Connection) visual).getCurveNode();
			if (curveNode instanceof GeometryNode) {
				return NodeUtils.localToParent(curveNode,
						((GeometryNode<?>) curveNode).getGeometry());
			}
		} else if (visual instanceof GeometryNode) {
			return ((GeometryNode<?>) visual).getGeometry();
		}
		return JavaFX2Geometry.toRectangle(visual.getLayoutBounds());
	}

	@Override
	public void setAdaptable(IVisualPart<Node, ? extends Node> adaptable) {
		this.host = adaptable;
	}

}
