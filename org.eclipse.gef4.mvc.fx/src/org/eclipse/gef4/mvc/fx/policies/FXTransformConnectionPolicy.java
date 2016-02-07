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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXBendOperation;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

/**
 * The {@link FXTransformConnectionPolicy} is an {@link FXTransformPolicy} that
 * is adjusted for the relocation of an {@link Connection}. It uses an
 * {@link FXBendOperation} to update the anchors of the {@link Connection}
 * according to the applied translation.
 *
 * @author mwienand
 *
 */
public class FXTransformConnectionPolicy extends FXTransformPolicy {

	private FXBendOperation op;
	private Point[] initialPositions;

	@Override
	public void applyTransform(AffineTransform newTransform) {
		// transform all anchor points
		for (int i : getIndicesOfMovableAnchors()) {
			Point p = initialPositions[i];
			Point pTx = newTransform.getTransformed(p);
			double nx = pTx.x;
			double ny = pTx.y;
			// TODO: make stepping (0.5) configurable
			Dimension snapToGridOffset = getSnapToGridOffset(getHost().getRoot()
					.getViewer().<GridModel> getAdapter(GridModel.class), nx,
					ny, 0.5, 0.5);
			op.getNewAnchors().set(i,
					new StaticAnchor(getHost().getVisual(),
							new Point(nx - snapToGridOffset.width,
									ny - snapToGridOffset.height)));
		}
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ITransactionalOperation commit() {
		// super#commit() so that it is reset properly, but throw away its
		// operation as we have prepared our own
		super.commit();
		return op.isNoOp() ? null : op;
	}

	/**
	 * Returns the indices of all movable anchors. Only those anchors are
	 * relocated by this policy.
	 *
	 * @return {@link List} of {@link Integer}s specifying the anchors to
	 *         relocate.
	 */
	protected List<Integer> getIndicesOfMovableAnchors() {
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
		// create operation
		op = new FXBendOperation((Connection) getHost().getVisual());
		// compute inverse transformation
		AffineTransform inverse = null;
		try {
			inverse = getInitialNodeTransform().invert();
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		// compute initial anchor positions (inverse transformed)
		initialPositions = op.getConnection().getPoints()
				.toArray(new Point[] {});
		for (int i : getIndicesOfMovableAnchors()) {
			initialPositions[i] = inverse.getTransformed(initialPositions[i]);
		}
	}

}