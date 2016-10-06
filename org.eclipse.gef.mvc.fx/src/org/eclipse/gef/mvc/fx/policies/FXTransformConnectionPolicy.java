/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.StaticAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.IConnectionRouter;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.operations.FXBendConnectionOperation;
import org.eclipse.gef.mvc.fx.parts.IFXBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IFXTransformableVisualPart;
import org.eclipse.gef.mvc.operations.BendContentOperation;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.parts.IBendableContentPart;
import org.eclipse.gef.mvc.parts.IBendableContentPart.BendPoint;

import javafx.collections.ObservableList;

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
	private List<Point> initialConnectionPositions;
	private List<Integer> movableConnectionIndices;
	private List<Integer> movableOperationIndices;

	@Override
	public ITransactionalOperation commit() {
		ITransactionalOperation commit = super.commit();

		// clear state
		initialConnectionPositions = null;
		initialBendPoints = null;
		movableConnectionIndices = null;
		movableOperationIndices = null;

		return commit;
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return new FXBendConnectionOperation(getHost().getVisual());
	}

	@Override
	protected ITransactionalOperation createTransformContentOperation() {
		IFXBendableContentPart bendablePart = (IFXBendableContentPart) getHost();
		return new BendContentOperation<>(bendablePart, initialBendPoints,
				bendablePart.getVisualBendPoints());
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
	public IFXTransformableVisualPart<Connection> getHost() {
		return (IFXTransformableVisualPart<Connection>) super.getHost();
	}

	/**
	 * Returns the indices of all movable anchors within the operation. Only
	 * those anchors are relocated by this policy.
	 *
	 * @return {@link List} of {@link Integer}s specifying the anchors to
	 *         relocate.
	 */
	protected List<Integer> getIndicesOfUnconnectedAnchors() {
		List<Integer> indices = new ArrayList<>();

		Connection connection = getBendConnectionOperation().getConnection();
		IConnectionRouter router = connection.getRouter();
		ObservableList<IAnchor> anchorsUnmodifiable = connection
				.getAnchorsUnmodifiable();
		for (int i = 0; i < anchorsUnmodifiable.size(); i++) {
			IAnchor a = anchorsUnmodifiable.get(i);
			if (!connection.isConnected(i) && !router.wasInserted(a)) {
				indices.add(i);
			}
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

		// determine indices of movable anchors
		movableConnectionIndices = getIndicesOfUnconnectedAnchors();
		movableOperationIndices = new ArrayList<>();
		Connection connection = getBendConnectionOperation().getConnection();
		List<IAnchor> newAnchors = getBendConnectionOperation().getNewAnchors();
		for (int i = 0; i < newAnchors.size(); i++) {
			IAnchor anchor = newAnchors.get(i);
			// exclude connected
			if (anchor == null || anchor.getAnchorage() == null
					|| anchor.getAnchorage() == connection) {
				movableOperationIndices.add(i);
			}
		}

		// compute initial anchor positions (inverse transformed)
		initialConnectionPositions = new ArrayList<>();
		for (Point p : connection.getPointsUnmodifiable()) {
			initialConnectionPositions.add(inverse.getTransformed(p));
		}
		initialBendPoints = ((IFXBendableContentPart) getHost())
				.getVisualBendPoints();
	}

	@Override
	protected boolean isContentTransformable() {
		return getHost() instanceof IBendableContentPart;
	}

	@Override
	protected void updateTransformOperation(AffineTransform newTransform) {
		// transform all anchor points
		for (int i = 0; i < movableConnectionIndices.size(); i++) {
			int connectionIndex = movableConnectionIndices.get(i);
			int operationIndex = movableOperationIndices.get(i);
			Point pTx = newTransform.getTransformed(
					initialConnectionPositions.get(connectionIndex));
			getBendConnectionOperation().getNewAnchors().set(operationIndex,
					new StaticAnchor(getHost().getVisual(),
							new Point(pTx.x, pTx.y)));
		}
	}

}