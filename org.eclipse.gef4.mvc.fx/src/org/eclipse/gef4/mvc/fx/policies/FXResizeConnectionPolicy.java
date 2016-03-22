/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXBendConnectionOperation;
import org.eclipse.gef4.mvc.fx.operations.FXRevealOperation;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.BendContentOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IBendableContentPart;
import org.eclipse.gef4.mvc.parts.IBendableContentPart.BendPoint;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.geometry.Bounds;
import javafx.scene.Node;

/**
 * The {@link FXResizeConnectionPolicy} is a specialization of the
 * {@link FXResizePolicy} that performs a resize of an {@link Connection} visual
 * by proportionally relocating its bend points.
 *
 * @author mwienand
 *
 */
public class FXResizeConnectionPolicy extends FXResizePolicy {

	private List<BendPoint> initialBendPoints;
	private Point[] initialPositions;
	private Double[] relX = null;
	private Double[] relY = null;

	@Override
	public ITransactionalOperation commit() {
		ITransactionalOperation commit = super.commit();

		// clear state
		initialPositions = null;
		relX = null;
		relY = null;
		initialBendPoints = null;

		return commit;
	}

	@Override
	protected ITransactionalOperation createOperation() {
		ForwardUndoCompositeOperation resizeAndRevealOperation = new ForwardUndoCompositeOperation(
				"Bend and Reveal");
		resizeAndRevealOperation
				.add(new FXBendConnectionOperation(getHost().getVisual()));
		resizeAndRevealOperation.add(new FXRevealOperation(getHost()));
		return resizeAndRevealOperation;
	}

	@Override
	protected ITransactionalOperation createResizeContentOperation() {
		return new BendContentOperation<>(
				(IBendableContentPart<Node, ? extends Node>) getHost(),
				initialBendPoints,
				FXBendConnectionPolicy.getCurrentBendPoints(getHost()));
	}

	/**
	 * Returns the {@link FXBendConnectionOperation} to be used by this policy.
	 *
	 * @return The {@link FXBendConnectionOperation} used to resize the visual.
	 */
	protected FXBendConnectionOperation getBendConnectionOperation() {
		return (FXBendConnectionOperation) ((AbstractCompositeOperation) getOperation())
				.getOperations().get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IVisualPart<Node, Connection> getHost() {
		return (IVisualPart<Node, Connection>) super.getHost();
	}

	/**
	 * Returns the indices of all movable anchors. Only those anchors are
	 * relocated by this policy.
	 *
	 * @return {@link List} of {@link Integer}s specifying the anchors to
	 *         relocate.
	 */
	protected List<Integer> getIndicesOfMovableAnchors() {
		FXBendConnectionOperation op = getBendConnectionOperation();
		List<Integer> indices = new ArrayList<>();
		if (!op.getConnection().isStartConnected()) {
			indices.add(0);
		}
		for (int i = 0; i < op.getNewAnchors().size() - 2; i++) {
			if (!op.getConnection().isControlConnected(i)) {
				indices.add(i + 1);
			}
		}
		if (!op.getConnection().isEndConnected()) {
			indices.add(op.getNewAnchors().size() - 1);
		}
		return indices;
	}

	@Override
	public void init() {
		super.init();
		// save initial anchor positions
		Connection connection = getHost().getVisual();
		initialPositions = connection.getPoints().toArray(new Point[] {});
		// compute relative positions
		Bounds layoutBounds = connection.getLayoutBounds();
		relX = new Double[initialPositions.length];
		relY = new Double[initialPositions.length];
		for (int i : getIndicesOfMovableAnchors()) {
			relX[i] = (initialPositions[i].x - layoutBounds.getMinX())
					/ layoutBounds.getWidth();
			relY[i] = (initialPositions[i].y - layoutBounds.getMinY())
					/ layoutBounds.getHeight();
		}
	}

	@Override
	protected boolean isContentResizable() {
		return getHost() instanceof IBendableContentPart;
	}

	@Override
	protected void updateResizeOperation(double dw, double dh) {
		FXBendConnectionOperation bendConnectionOperation = getBendConnectionOperation();
		for (int i : getIndicesOfMovableAnchors()) {
			Point p = initialPositions[i];
			// scale dw and dh by relX and relY
			Point newPosition = new Point(p.x + relX[i] * dw,
					p.y + relY[i] * dh);
			// relocate bend point
			bendConnectionOperation.getNewAnchors().set(i, new StaticAnchor(
					bendConnectionOperation.getConnection(), newPosition));
		}
	}

}
