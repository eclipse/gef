/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import javafx.scene.Node;

import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;

public class DefaultChopBoxAlgorithm {

	public static DefaultChopBoxAlgorithm getInstance() {
		if (instance == null) {
			instance = new DefaultChopBoxAlgorithm();
		}
		return instance;
	}

	private static DefaultChopBoxAlgorithm instance = null;

	public ICurve computeOutlineInScene(Node node, IGeometry geometryInLocal) {
		// TODO: we cannot handle Path yet
		if (!(geometryInLocal instanceof IShape)
				&& !(geometryInLocal instanceof ICurve)) {
			throw new IllegalArgumentException(
					"The given IGeometry is neither an ICurve nor an IShape.");
		}

		IGeometry geometryInScene = FXUtils.localToScene(node, geometryInLocal);

		if (geometryInScene instanceof IShape) {
			return ((IShape) geometryInScene).getOutline();
		} else if (geometryInScene instanceof ICurve) {
			return (ICurve) geometryInScene;
		} else {
			throw new IllegalStateException(
					"The transformed geometry is neither an ICurve nor an IShape.");
		}
	}

	public Point computePositionInScene(Node anchorage,
			IGeometry anchorageGeometryInLocal, Node anchored,
			Point anchoredReferencePointInLocal) {
		Point anchoredReferencePointInScene = localToScene(anchored,
				anchoredReferencePointInLocal);
		Point anchorageReferencePointInScene = computeReferencePointInScene(
				anchorage, anchorageGeometryInLocal);
		Line referenceLineInScene = new Line(anchorageReferencePointInScene,
				anchoredReferencePointInScene);

		ICurve anchorageOutlineInScene = computeOutlineInScene(anchorage,
				anchorageGeometryInLocal);

		Point nearestIntersectionInScene = anchorageOutlineInScene
				.getNearestIntersection(referenceLineInScene,
						anchoredReferencePointInScene);
		if (nearestIntersectionInScene != null) {
			return nearestIntersectionInScene;
		}

		// in case of emergency, return the anchorage reference point
		return anchorageReferencePointInScene;
	}

	public Point computeReferencePointInLocal(Node node,
			IGeometry geometryInLocal) {
		// TODO: we cannot handle Path yet
		if (!(geometryInLocal instanceof IShape)
				&& !(geometryInLocal instanceof ICurve)) {
			throw new IllegalArgumentException(
					"The given IGeometry is neither an IShape nor an ICurve.");
		}

		// determine the bounds center
		Point boundsCenterInLocal = geometryInLocal.getBounds().getCenter();

		// if the bounds center is contained, it is good enough as a
		// reference point
		if (!geometryInLocal.contains(boundsCenterInLocal)) {
			// otherwise we have to search for another reference point
			if (geometryInLocal instanceof IShape) {
				// in case of an IShape we can pick the vertex nearest to
				// the center point
				Point nearestVertex = getNearestVertex(boundsCenterInLocal,
						(IShape) geometryInLocal);
				if (nearestVertex != null) {
					return nearestVertex;
				} else {
					throw new IllegalArgumentException(
							"The given IShape does not provide any vertices.");
				}
			} else {
				// TODO: Which point shall we use for curves?
				return ((ICurve) geometryInLocal).getP1();
			}
		} else {
			return boundsCenterInLocal;
		}
	}

	public Point computeReferencePointInScene(Node node,
			IGeometry geometryInLocal) {
		return localToScene(node,
				computeReferencePointInLocal(node, geometryInLocal));
	}

	protected Point getNearestVertex(Point boundsCenter, IShape shape) {
		ICurve[] outlineSegments = shape.getOutlineSegments();
		if (outlineSegments.length == 0) {
			return null;
		}
		// find vertex nearest to boundsCenter
		Point nearestVertex = outlineSegments[0].getP1();
		double minDistance = boundsCenter.getDistance(nearestVertex);
		for (int i = 1; i < outlineSegments.length; i++) {
			Point v = outlineSegments[i].getP1();
			double d = boundsCenter.getDistance(v);
			if (d < minDistance) {
				nearestVertex = v;
				minDistance = d;
			}
		}
		return nearestVertex;
	}

	// TODO: Move to FXUtils?
	private Point localToScene(Node anchored,
			Point anchoredReferencePointInLocal) {
		AffineTransform anchoredToSceneTransform = FXUtils
				.getLocalToSceneTx(anchored);
		Point anchoredReferencePointInScene = anchoredToSceneTransform
				.getTransformed(anchoredReferencePointInLocal);
		return anchoredReferencePointInScene;
	}

}