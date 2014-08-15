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
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
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
	private IFXAnchor removedAnchor;
	private int removedAnchorIndex;
	private int anchorIndex;
	private int oldAnchorIndex;

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
		op.getNewAnchors().add(
				segmentIndex,
				new FXStaticAnchor(
						JavaFX2Geometry.toPoint(hostVisual.localToScene(
								mouseInLocal.getX(), mouseInLocal.getY()))));
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

	public AnchorKey getNewAnchorKey(int index) {
		if (index == 0) {
			return op.getConnection().getStartAnchorKey();
		} else if (index == op.getNewAnchors().size() - 1) {
			return op.getConnection().getEndAnchorKey();
		} else {
			return op.getConnection().getWayAnchorKey(index - 1);
		}
	}

	private Point getPosition(IFXAnchor anchor, AnchorKey key) {
		// TODO: Maybe this can be done nicer?
		boolean attached = anchor.isAttached(key);
		if (!attached) {
			anchor.attach(key);
		}
		Point p = anchor.getPosition(key);
		if (!attached) {
			anchor.detach(key);
		}
		return p;
	}

	protected void hideShowOverlaid() {
		// put removed back in
		if (removedAnchor != null) {
			anchorIndex = oldAnchorIndex;
			op.getNewAnchors().add(removedAnchorIndex, removedAnchor);
			removedAnchor = null;
		}

		// do not remove overlaid if there are no way points
		if (op.getNewAnchors().size() <= 2) {
			return;
		}

		removedAnchorIndex = -1;
		oldAnchorIndex = anchorIndex;

		// determine overlaid neighbor
		if (anchorIndex > 0) {
			int prevIndex = anchorIndex - 1;
			if (getPosition(op.getNewAnchors().get(prevIndex),
					getNewAnchorKey(prevIndex)).getDistance(currentPoint) < REMOVE_THRESHOLD) {
				// remove previous
				removedAnchorIndex = prevIndex;
				anchorIndex--;
			}
		}
		if (removedAnchorIndex == -1
				&& anchorIndex < op.getNewAnchors().size() - 1) {
			int nextIndex = anchorIndex + 1;
			if (getPosition(op.getNewAnchors().get(nextIndex),
					getNewAnchorKey(nextIndex)).getDistance(currentPoint) < REMOVE_THRESHOLD) {
				// remove next
				removedAnchorIndex = nextIndex;
			}
		}

		// remove neighbor if overlaid
		if (removedAnchorIndex != -1) {
			removedAnchor = op.getNewAnchors().remove(removedAnchorIndex);
		}
	}

	@Override
	public void init() {
		op = new FXBendOperation((IFXConnection) getHostVisual());
		removedAnchor = null;
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

		currentPoint.x = startPointInLocal.getX() + delta.x;
		currentPoint.y = startPointInLocal.getY() + delta.y;

		// update
		hideShowOverlaid();
		updateCurrentAnchorLink(mouseInScene, partsUnderMouse);
		locallyExecuteOperation();
	}

	public void selectPoint(int segmentIndex, double segmentParameter,
			Point mouseInScene) {
		// store handle part information
		if (segmentParameter == 1) {
			anchorIndex = segmentIndex + 1;
		} else {
			anchorIndex = segmentIndex;
		}

		// initialize position
		startPointInScene = new Point2D(mouseInScene.x, mouseInScene.y);
		startPoint = op.getNewAnchors().get(anchorIndex)
				.getPosition(getNewAnchorKey(anchorIndex));
		currentPoint = startPoint.getCopy();
	}

	@Override
	public String toString() {
		return "FXMoveBendPointPolicy[host=" + getHost() + "]";
	}

	protected void updateCurrentAnchorLink(Point mouseInScene,
			List<IContentPart<Node>> partsUnderMouse) {
		// snaps for start and end (TODO: configurable)
		boolean snaps = anchorIndex == 0
				|| anchorIndex == op.getNewAnchors().size() - 1;

		IFXAnchor anchor = null;
		if (snaps) {
			AbstractFXContentPart anchorPart = getAnchorPart(partsUnderMouse);
			if (anchorPart != null) {
				// use anchor returned by part
				anchor = anchorPart.getAnchor(getHost());
			}
		}

		if (anchor == null) {
			// use static anchor, re-use key
			anchor = new FXStaticAnchor(JavaFX2Geometry.toPoint(getHostVisual()
					.localToScene(currentPoint.x, currentPoint.y)));
		}

		op.getNewAnchors().set(anchorIndex, anchor);
	}

}