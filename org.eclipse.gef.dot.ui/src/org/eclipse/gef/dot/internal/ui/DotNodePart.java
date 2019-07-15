/************************************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Tamas Miklossy (itemis AG)   - initial API and implementation
 *     Zoey Prigge (itemis AG)      - DotProperties extraction
 *
 ***********************************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.zest.fx.parts.NodePart;

import javafx.geometry.Bounds;
import javafx.scene.Group;

public class DotNodePart extends NodePart {

	protected GeometryNode<IGeometry> innerShape;

	protected javafx.scene.Group doCreateVisual() {
		Group visual = super.doCreateVisual();
		innerShape = new GeometryNode<>();
		visual.getChildren().add(1, innerShape);
		return visual;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doRefreshVisual(Group visual) {
		super.doRefreshVisual(visual);
		Node node = getContent();
		if (node.attributesProperty()
				.containsKey(DotProperties.INNER_SHAPE__N)) {
			innerShape = (GeometryNode<IGeometry>) node.attributesProperty()
					.get(DotProperties.INNER_SHAPE__N);
			double innerDistance = 0;
			if (node.attributesProperty()
					.containsKey(DotProperties.INNER_SHAPE__N)) {
				innerDistance = (double) node.attributesProperty()
						.get(DotProperties.INNER_SHAPE_DISTANCE__N);
			}
			Bounds b = this.getShape().getBoundsInLocal();
			this.innerShape.resizeRelocate(b.getMinX() + innerDistance,
					b.getMinY() + innerDistance,
					b.getWidth() - 2 * innerDistance,
					b.getHeight() - 2 * innerDistance);
			if (!getVisual().getChildren().contains(innerShape)) {
				visual.getChildren().add(1, innerShape);
			}
		}
	}
}
