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
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.policies.FXTransformConnectionPolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.zest.fx.operations.ChangeEdgeControlPointsOperation;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;

/**
 * The {@link FXTransformEdgePolicy} is a specialization of the
 * {@link FXTransformConnectionPolicy} that chains a
 * {@link ChangeEdgeControlPointsOperation} to affect the underlying model when
 * transforming nodes. Therefore, it is only applicable for
 * {@link EdgeContentPart}.
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
		ChangeEdgeControlPointsOperation modelOperation = new ChangeEdgeControlPointsOperation(getHost(),
				transformedControlPoints);

		// assemble operations
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation("Transform Edge");
		if (visualOperation != null) {
			fwdOp.add(visualOperation);
		}
		fwdOp.add(modelOperation);
		return fwdOp;
	}

	@Override
	public EdgeContentPart getHost() {
		return (EdgeContentPart) super.getHost();
	}

}
