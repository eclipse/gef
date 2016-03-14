/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.mvc.fx.policies.FXTransformConnectionPolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.operations.ChangeEdgeControlPointsOperation;

/**
 * The {@link FXTransformEdgePolicy} is a specialization of the
 * {@link FXTransformConnectionPolicy} that chains a
 * {@link ChangeEdgeControlPointsOperation} to affect the underlying model when
 * transforming nodes. It is applicable to {@link IContentPart}'s with a
 * {@link Connection} visual and {@link Edge} content.
 *
 * @author anyssen
 *
 */
public class FXTransformEdgePolicy extends FXTransformConnectionPolicy {

	@Override
	public ITransactionalOperation commit() {
		// get visual operation
		ITransactionalOperation visualOperation = super.commit();

		// create model operation
		List<Point> transformedControlPoints = new ArrayList<>();
		List<IAnchor> finalAnchors = getBendOperation().getNewAnchors();
		for (int i = 1; i < finalAnchors.size() - 1; i++) {
			transformedControlPoints
					.add(finalAnchors.get(i).getPosition(getHost().getVisual().getControlAnchorKey(i - 1)));
		}
		ChangeEdgeControlPointsOperation modelOperation = new ChangeEdgeControlPointsOperation(getEdge(),
				transformedControlPoints);

		// assemble operations
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation("Transform Edge");
		if (visualOperation != null) {
			fwdOp.add(visualOperation);
		}
		fwdOp.add(modelOperation);
		return fwdOp;
	}

	/**
	 * Returns the {@link Edge} content element of the host part.
	 *
	 * @return The {@link Edge} content element.
	 */
	protected Edge getEdge() {
		return (Edge) getHost().getContent();
	}

	@SuppressWarnings("unchecked")
	@Override
	public IContentPart<javafx.scene.Node, ? extends Connection> getHost() {
		// XXX: We don't tie this policy to EdgeContentPart, but only to
		// IContentPart with a Edge content, so it can be re-used in other
		// situations.
		return (IContentPart<javafx.scene.Node, ? extends Connection>) super.getHost();
	}

}
