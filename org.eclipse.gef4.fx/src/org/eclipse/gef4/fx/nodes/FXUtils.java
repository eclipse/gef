/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;

import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.AnchorLink;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.geometry.planar.Point;

public class FXUtils {

	/**
	 * Creates a new {@link FXStaticAnchor}, {@link AnchorKey}, and
	 * {@link AnchorLink} from the given values and returns the
	 * {@link AnchorLink}.
	 * 
	 * @param anchored
	 *            {@link Node} which is to be anchored on the
	 *            {@link FXStaticAnchor}.
	 * @param id
	 *            {@link Object} which is used as an additional identifier for
	 *            the {@link AnchorKey} (may as well be the anchored
	 *            {@link Node}).
	 * @param position
	 *            {@link Point} specifying the position for the
	 *            {@link FXStaticAnchor}.
	 * @return A new {@link AnchorLink} holding the new {@link AnchorKey} and
	 *         {@link FXStaticAnchor}.
	 */
	public static AnchorLink createStaticAnchorLink(Node anchored, Object id,
			Point position) {
		AnchorKey key = new AnchorKey(anchored, id);
		FXStaticAnchor anchor = new FXStaticAnchor(key, position);
		return new AnchorLink(anchor, key);
	}

	/**
	 * Performs picking on the scene graph beginning at the specified root node.
	 * 
	 * @param sceneX
	 * @param sceneY
	 * @param root
	 * @return A list of {@link Node}s which contain the the given coordinate.
	 */
	public static List<Node> getNodesAt(Node root, double sceneX, double sceneY) {
		List<Node> picked = new ArrayList<Node>();

		// start with given root node
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(root);

		while (!nodes.isEmpty()) {
			Node current = nodes.remove(0);
			// transform to local coordinates
			Point2D pLocal = current.sceneToLocal(sceneX, sceneY);
			// check if bounds contains (necessary to find children in mouse
			// transparent regions)
			if (!current.isMouseTransparent()
					&& current.getBoundsInLocal().contains(pLocal)) {
				// check precisely
				if (current.contains(pLocal)) {
					picked.add(0, current);
				}
				// test all children, too
				if (current instanceof Parent) {
					nodes.addAll(0,
							((Parent) current).getChildrenUnmodifiable());
				}
			}
		}

		return picked;
	}

}
