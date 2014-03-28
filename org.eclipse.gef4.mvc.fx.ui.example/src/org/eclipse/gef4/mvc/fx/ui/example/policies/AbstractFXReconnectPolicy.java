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
package org.eclipse.gef4.mvc.fx.ui.example.policies;

import java.util.List;
import java.util.Map;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXReconnectOperation;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public abstract class AbstractFXReconnectPolicy extends AbstractPolicy<Node> {

	private boolean isStartAnchor;
	private Point2D startPointScene;
	private Point2D startPointLocal;
	private IFXConnection connection;
	private boolean connected;
	private IFXAnchor initialAnchor;
	private IFXAnchor currentAnchor;
	private Map<Object, Object> anchorContext;
	private FXReconnectOperation op;

	protected boolean isLoose(IFXAnchor anchor) {
		return anchor instanceof FXStaticAnchor;
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

	public void press(boolean isStart, Point startPointInScene) {
		getHost().setRefreshVisual(false);
		isStartAnchor = isStart;
		startPointScene = new Point2D(startPointInScene.x, startPointInScene.y);
		startPointLocal = getHost().getVisual().sceneToLocal(startPointScene);
		connection = getConnection();
		if (isStartAnchor) {
			initialAnchor = connection.getStartAnchor();
			anchorContext = FXCurveConnection.START_CONTEXT;
		} else {
			initialAnchor = connection.getEndAnchor();
			anchorContext = FXCurveConnection.END_CONTEXT;
		}
		currentAnchor = initialAnchor;
		connected = !isLoose(initialAnchor);
		op = new FXReconnectOperation("Reconnect", connection, initialAnchor,
				currentAnchor, anchorContext);
	}

	public void dragTo(Point pointInScene,
			List<IContentPart<Node>> partsUnderMouse) {
		Point position = transformToLocal(pointInScene);
		AbstractFXContentPart anchorPart = getAnchorPart(partsUnderMouse);
		if (connected) {
			if (anchorPart != null) {
				// nothing to do/position still fixed by anchor
				return;
			} else {
				currentAnchor = new FXStaticAnchor(getHost().getVisual(),
						position);
				connected = false;
			}
		} else {
			if (anchorPart != null) {
				currentAnchor = anchorPart.getAnchor(getHost());
				connected = true;
			} else {
				currentAnchor = new FXStaticAnchor(getHost().getVisual(),
						position);
			}
		}
		op = new FXReconnectOperation("Reconnect", connection, initialAnchor,
				currentAnchor, anchorContext);

		// execute locally
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public IUndoableOperation commit() {
		getHost().setRefreshVisual(true);
		return op;
	}

	public abstract IFXConnection getConnection();

	protected Point transformToLocal(Point p) {
		Point2D pLocal = getHost().getVisual().sceneToLocal(p.x, p.y);
		Point2D initialPosLocal = getHost().getVisual().sceneToLocal(
				startPointScene);

		Point delta = new Point(pLocal.getX() - initialPosLocal.getX(),
				pLocal.getY() - initialPosLocal.getY());

		return new Point(startPointLocal.getX() + delta.x,
				startPointLocal.getY() + delta.y);
	}

}