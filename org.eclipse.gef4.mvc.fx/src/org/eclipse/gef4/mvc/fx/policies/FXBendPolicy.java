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
package org.eclipse.gef4.mvc.fx.policies;

import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.AnchorLink;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXBendOperation;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

/**
 * The {@link FXBendPolicy} can be used to manipulate the points constituting an
 * {@link IFXConnection}, i.e. its start point, way points, and end point. When
 * moving a point the policy takes care of:
 * <ul>
 * <li>Removing overlaid neighbor points.</li>
 * <li>Re-adding temporarily removed neighbor points.</li>
 * <li>Reconnecting points to the {@link IVisualPart} under mouse when
 * applicable.</li>
 * </ul>
 *
 * @author mwienand
 */
public class FXBendPolicy extends AbstractPolicy<Node> implements
ITransactional {

	// constants (TODO: make configurable)
	protected static final double REMOVE_THRESHOLD = 10;

	// interaction
	private Point2D startPointInScene;
	private Point startPoint;
	private Point currentPoint;
	private AnchorLink removedLink;
	private int removedLinkIndex;
	private int linkIndex;
	private int oldLinkIndex;

	// operation
	private FXBendOperation op;

	@Override
	public IUndoableOperation commit() {
		return op;
	}

	public void createWayPoint(int segmentIndex, Point mouseInScene) {
		Node hostVisual = getHostVisual();
		Point2D mouseInLocal = hostVisual.sceneToLocal(mouseInScene.x,
				mouseInScene.y);

		// create new way point
		segmentIndex++;
		op.getNewLinks().add(
				segmentIndex,
				((IFXConnection) hostVisual)
				.createWayPointAnchorLink(new Point(
						mouseInLocal.getX(), mouseInLocal.getY())));
		locallyExecuteOperation();

		// select newly created way point
		selectPoint(segmentIndex, 0, mouseInScene);
	}

	protected AbstractFXContentPart getAnchorPart(
			List<IContentPart<Node>> partsUnderMouse) {
		for (IContentPart<Node> cp : partsUnderMouse) {
			AbstractFXContentPart part = (AbstractFXContentPart) cp;
			IFXAnchor anchor = part.getAnchor(getHost());
			if (anchor != null) {
				return part;
			}
		}
		return null;
	}

	protected Node getHostVisual() {
		return getHost().getVisual();
	}

	private Point getPosition(AnchorLink link) {
		// TODO: Maybe this can be done nicer?
		boolean attached = link.getAnchor().isAttached(link.getKey());
		if (!attached) {
			link.getAnchor().attach(link.getKey());
		}
		Point p = link.getPosition();
		if (!attached) {
			link.getAnchor().detach(link.getKey());
		}
		return p;
	}

	protected void hideShowOverlaid() {
		// put removed back in
		if (removedLink != null) {
			linkIndex = oldLinkIndex;
			op.getNewLinks().add(removedLinkIndex, removedLink);
			removedLink = null;
		}

		// do not remove overlaid if there are no way points
		if (op.getNewLinks().size() <= 2) {
			return;
		}

		removedLinkIndex = -1;
		oldLinkIndex = linkIndex;

		// determine overlaid neighbor
		if (linkIndex > 0) {
			int prevIndex = linkIndex - 1;
			if (getPosition(op.getNewLinks().get(prevIndex)).getDistance(
					currentPoint) < REMOVE_THRESHOLD) {
				// remove previous
				removedLinkIndex = prevIndex;
				linkIndex--;
			}
		}
		if (removedLinkIndex == -1 && linkIndex < op.getNewLinks().size() - 1) {
			int nextIndex = linkIndex + 1;
			if (getPosition(op.getNewLinks().get(nextIndex)).getDistance(
					currentPoint) < REMOVE_THRESHOLD) {
				// remove next
				removedLinkIndex = nextIndex;
			}
		}

		// remove neighbor if overlaid
		if (removedLinkIndex != -1) {
			removedLink = op.getNewLinks().remove(removedLinkIndex);
		}
	}

	@Override
	public void init() {
		op = new FXBendOperation((IFXConnection) getHostVisual());
		removedLink = null;
	}

	protected void locallyExecuteOperation() {
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	public void movePoint(Point mouseInScene,
			List<IContentPart<Node>> partsUnderMouse) {
		// update position
		Point2D mouseInLocal = getHostVisual().sceneToLocal(mouseInScene.x,
				mouseInScene.y);
		Point2D startPointInLocal = getHostVisual().sceneToLocal(
				startPointInScene);
		Point delta = new Point(mouseInLocal.getX() - startPointInLocal.getX(),
				mouseInLocal.getY() - startPointInLocal.getY());

		currentPoint.x = startPoint.x + delta.x;
		currentPoint.y = startPoint.y + delta.y;

		// update
		hideShowOverlaid();
		updateCurrentAnchorLink(mouseInScene, partsUnderMouse);
		locallyExecuteOperation();
	}

	public void selectPoint(int segmentIndex, double segmentParameter,
			Point mouseInScene) {
		// store handle part information
		if (segmentParameter == 1) {
			linkIndex = segmentIndex + 1;
		} else {
			linkIndex = segmentIndex;
		}

		// initialize position
		startPointInScene = new Point2D(mouseInScene.x, mouseInScene.y);
		startPoint = op.getNewLinks().get(linkIndex).getPosition();
		currentPoint = startPoint.getCopy();
	}

	@Override
	public String toString() {
		return "FXMoveBendPointPolicy[host=" + getHost() + "]";
	}

	protected void updateCurrentAnchorLink(Point mouseInScene,
			List<IContentPart<Node>> partsUnderMouse) {
		// snaps for start and end (TODO: configurable)
		boolean snaps = linkIndex == 0
				|| linkIndex == op.getNewLinks().size() - 1;

		AnchorLink link = null;
		if (snaps) {
			AbstractFXContentPart anchorPart = getAnchorPart(partsUnderMouse);
			if (anchorPart != null) {
				// use anchor returned by part
				IFXAnchor anchor = anchorPart.getAnchor(getHost());
				link = new AnchorLink(anchor, op.getOldLinks().get(linkIndex)
						.getKey());
			}
		}

		if (link == null) {
			// use static anchor, re-use key
			AnchorKey key = op.getNewLinks().get(linkIndex).getKey();
			FXStaticAnchor anchor = new FXStaticAnchor(currentPoint);
			link = new AnchorLink(anchor, key);
		}

		op.getNewLinks().set(linkIndex, link);
	}

}