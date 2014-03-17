/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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

import javafx.collections.MapChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.IFXNodeAnchor;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;

public class FXBinaryConnection extends FXGeometryNode<ICurve> {

	// start and end point anchors
	private IFXNodeAnchor startAnchor;
	private IFXNodeAnchor endAnchor;

	// position listeners for the anchors
	private MapChangeListener<Node, Point> startPosCL;
	private MapChangeListener<Node, Point> endPosCL;

	private List<Point> wayPoints = new ArrayList<Point>();

	public FXBinaryConnection(IFXNodeAnchor startAnchor, IFXNodeAnchor endAnchor) {
		setStartAnchor(startAnchor);
		setEndAnchor(endAnchor);
	}

	public Point[] computeReferencePoints() {
		// compute start/end point in local coordinate space
		Point start = startAnchor.getPosition(this);
		Point end = endAnchor.getPosition(this);

		// find reference points
		Point startReference = end;
		Point endReference = start;

		// first uncontained way point is start reference
		Node startNode = startAnchor.getAnchorageNode();
		if (startNode != null) {
			for (Point p : wayPoints) {
				Point2D local = startNode.sceneToLocal(localToScene(p.x, p.y));
				if (!startNode.contains(local)) {
					startReference = p;
					break;
				}
			}
		}

		// last uncontained way point is end reference
		Node endNode = endAnchor.getAnchorageNode();
		if (endNode != null) {
			for (Point p : wayPoints) {
				Point2D local = endNode.sceneToLocal(localToScene(p.x, p.y));
				if (!endNode.contains(local)) {
					endReference = p;
				}
			}
		}

		return new Point[] { startReference, endReference };
	}

	public List<Point> getWayPoints() {
		return wayPoints;
	}

	public IFXNodeAnchor getEndAnchor() {
		return endAnchor;
	}

	public void setEndAnchor(IFXNodeAnchor endAnchor) {
		this.endAnchor = endAnchor;
	}

	public IFXNodeAnchor getStartAnchor() {
		return startAnchor;
	}

	public void setStartAnchor(IFXNodeAnchor startAnchor) {
		this.startAnchor = startAnchor;
	}

}
