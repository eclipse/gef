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

import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;

public class FXUtils {

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

	public static IGeometry localToParent(Node n, IGeometry g) {
		AffineTransform localToParentTx = JavaFX2Geometry.toAffineTransform(n
				.getLocalToParentTransform());
		return g.getTransformed(localToParentTx);
	}

	public static IGeometry localToScene(Node n, IGeometry g) {
		AffineTransform localToSceneTx = JavaFX2Geometry.toAffineTransform(n
				.getLocalToSceneTransform());
		return g.getTransformed(localToSceneTx);
	}

	public static IGeometry parentToLocal(Node n, IGeometry g) {
		// retrieve transform from scene to target parent, by inverting target
		// parent to scene
		AffineTransform localToParentTx = JavaFX2Geometry.toAffineTransform(n
				.getLocalToParentTransform());
		AffineTransform parentToLocalTx = null;
		try {
			parentToLocalTx = localToParentTx.getCopy().invert();
		} catch (NoninvertibleTransformException e) {
			// TODO: How do we recover from this?!
			throw new IllegalStateException(e);
		}
		return g.getTransformed(parentToLocalTx);
	}

	public static IGeometry sceneToLocal(Node n, IGeometry g) {
		// retrieve transform from scene to target parent, by inverting target
		// parent to scene
		AffineTransform localToSceneTx = JavaFX2Geometry.toAffineTransform(n
				.getLocalToSceneTransform());
		AffineTransform sceneToLocalTx = null;
		try {
			sceneToLocalTx = localToSceneTx.getCopy().invert();
		} catch (NoninvertibleTransformException e) {
			// TODO: How do we recover from this?!
			throw new IllegalStateException(e);
		}
		return g.getTransformed(sceneToLocalTx);
	}
}
