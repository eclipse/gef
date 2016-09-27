/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.parts.IBendableContentPart;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.viewer.IViewer;

import javafx.scene.Node;

/**
 *
 *
 * @author wienand
 *
 */
public interface IFXBendableContentPart
		extends IBendableContentPart<Node, Connection> {

	@Override
	default List<org.eclipse.gef.mvc.parts.IBendableContentPart.BendPoint> getVisualBendPoints() {
		List<BendPoint> bendPoints = new ArrayList<>();
		Connection connection = getVisual();
		IViewer<Node> viewer = getRoot().getViewer();
		List<IAnchor> anchors = connection.getAnchorsUnmodifiable();
		for (int i = 0; i < anchors.size(); i++) {
			IAnchor anchor = anchors.get(i);
			if (!connection.getRouter().wasInserted(anchor)) {
				if (connection.isConnected(i)) {
					// provide a position hint for a connected bend point
					Point positionHint = connection.getPoint(i);
					if (i == 0 && connection.getStartPointHint() != null) {
						positionHint = connection.getStartPointHint();
					}
					if (i == anchors.size() - 1
							&& connection.getEndPointHint() != null) {
						positionHint = connection.getEndPointHint();
					}
					// determine anchorage content
					Node anchorageNode = anchor.getAnchorage();
					IVisualPart<Node, ? extends Node> part = FXPartUtils
							.retrieveVisualPart(viewer, anchorageNode);
					Object anchorageContent = null;
					if (part instanceof IContentPart) {
						anchorageContent = ((IContentPart<Node, ? extends Node>) part)
								.getContent();
					}
					bendPoints
							.add(new BendPoint(anchorageContent, positionHint));
				} else {
					bendPoints.add(new BendPoint(connection.getPoint(i)));
				}
			}
		}
		return bendPoints;
	}

}
