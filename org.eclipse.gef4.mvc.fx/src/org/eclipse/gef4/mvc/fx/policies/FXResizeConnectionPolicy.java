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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXBendOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

import javafx.geometry.Bounds;

/**
 * The {@link FXResizeConnectionPolicy} is a specialization of the
 * {@link FXResizePolicy} that performs a resize of an {@link Connection}
 * visual by proportionally relocating its bend points.
 *
 * @author mwienand
 *
 */
public class FXResizeConnectionPolicy extends FXResizePolicy {

	private FXBendOperation op;
	private Point[] initialPositions;
	private Double[] relX = null;
	private Double[] relY = null;

	@Override
	public ITransactionalOperation commit() {
		super.commit();
		ITransactionalOperation commit = op.isNoOp() ? null : op;
		op = null;
		initialPositions = null;
		relX = null;
		relY = null;
		return commit;
	}

	/**
	 * Returns the indices of all movable anchors. Only those anchors are
	 * relocated by this policy.
	 *
	 * @return {@link List} of {@link Integer}s specifying the anchors to
	 *         relocate.
	 */
	protected List<Integer> getIndicesOfMovableAnchors() {
		List<Integer> indices = new ArrayList<Integer>();
		if (!op.getConnection().isStartConnected()) {
			indices.add(0);
		}
		for (int i = 0; i < op.getNewAnchors().size() - 2; i++) {
			if (!op.getConnection().isWayConnected(i)) {
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
		// create operation
		Connection connection = (Connection) getHost().getVisual();
		op = new FXBendOperation(connection);
		// save initial anchor positions
		initialPositions = connection.getPoints();
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
	public void resize(double dw, double dh) {
		for (int i : getIndicesOfMovableAnchors()) {
			Point p = initialPositions[i];
			// scale dw and dh by relX and relY
			Point newPosition = new Point(p.x + relX[i] * dw,
					p.y + relY[i] * dh);
			// relocate bend point
			op.getNewAnchors().set(i,
					new StaticAnchor(op.getConnection(), newPosition));
		}
		// locally execute operation
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
