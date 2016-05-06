/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXBendConnectionOperation;
import org.eclipse.gef4.mvc.operations.BendContentOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IBendableContentPart;
import org.eclipse.gef4.mvc.parts.IBendableContentPart.BendPoint;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.scene.Node;

/**
 * The {@link FXTransformConnectionPolicy} is an {@link FXTransformPolicy} that
 * is adjusted for the relocation of an {@link Connection}. It uses an
 * {@link FXBendConnectionOperation} to update the anchors of the
 * {@link Connection} according to the applied translation.
 *
 * @author mwienand
 *
 */
public class FXTransformConnectionPolicy extends FXTransformPolicy {

	private List<BendPoint> initialBendPoints;

	private Point[] initialPositions;

	@Override
	public ITransactionalOperation commit() {
		ITransactionalOperation commit = super.commit();

		// clear state
		initialPositions = null;
		initialBendPoints = null;

		return commit;
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return new FXBendConnectionOperation(getHost().getVisual());
	}

	@Override
	protected ITransactionalOperation createTransformContentOperation() {
		return new BendContentOperation<>(
				(IBendableContentPart<Node, ? extends Node>) getHost(),
				initialBendPoints,
				FXBendConnectionPolicy.getCurrentBendPoints(getHost()));
	}

	/**
	 * Returns the {@link FXBendConnectionOperation} to be used by this policy.
	 *
	 * @return The {@link FXBendConnectionOperation} used to transform the
	 *         visual.
	 */
	protected FXBendConnectionOperation getBendConnectionOperation() {
		return (FXBendConnectionOperation) getOperation();
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
	protected List<Integer> getIndicesOfUnconnectedAnchors() {
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
		// super#init() so that the policy is properly initialized
		super.init();
		// compute inverse transformation
		AffineTransform inverse = null;
		try {
			inverse = getInitialTransform().invert();
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		// compute initial anchor positions (inverse transformed)
		initialPositions = getBendConnectionOperation().getConnection()
				.getPointsUnmodifiable().toArray(new Point[] {});
		for (int i : getIndicesOfUnconnectedAnchors()) {
			initialPositions[i] = inverse.getTransformed(initialPositions[i]);
		}

		initialBendPoints = FXBendConnectionPolicy
				.getCurrentBendPoints(getHost());
	}

	@Override
	protected boolean isContentTransformable() {
		return getHost() instanceof IBendableContentPart;
	}

	@Override
	protected void updateTransformOperation(AffineTransform newTransform) {
		// transform all anchor points
		for (int i : getIndicesOfUnconnectedAnchors()) {
			Point pTx = newTransform.getTransformed(initialPositions[i]);
			getBendConnectionOperation().getNewAnchors().set(i,
					new StaticAnchor(getHost().getVisual(),
							new Point(pTx.x, pTx.y)));
		}
	}
}