/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.common.attributes.IAttributeStore;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.policies.FXBendConnectionPolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.operations.ChangeAttributeOperation;
import org.eclipse.gef4.zest.fx.parts.EdgePart;

/**
 * An {@link FXBendConnectionPolicy} that ensures the graph model is properly
 * updated.
 *
 * @author mwienand
 *
 */
public class BendEdgePolicy extends FXBendConnectionPolicy {

	@Override
	public ITransactionalOperation commit() {
		// extract changes
		List<IAnchor> connectionAnchors = getBendOperation().getConnection().getAnchors();
		List<Point> controlPoints = new ArrayList<>();
		for (int i = 1; i < connectionAnchors.size() - 1; i++) {
			IAnchor anchor = connectionAnchors.get(i);
			if (!getBendOperation().getConnection().getRouter().isImplicitAnchor(anchor)) {
				controlPoints.add(getBendOperation().getConnection().getPoint(i));
			}
		}

		// get visual operation
		ITransactionalOperation visualOperation = super.commit();

		if (visualOperation == null) {
			return null;
		}

		// create model operation
		ChangeAttributeOperation modelOperation = new ChangeAttributeOperation(getEdge(),
				ZestProperties.EDGE_CONTROL_POINTS, controlPoints);

		// assemble visual and model operations
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation("Bend Edge");
		if (visualOperation != null) {
			fwdOp.add(visualOperation);
		}
		fwdOp.add(modelOperation);
		return fwdOp;
	}

	private IAttributeStore getEdge() {
		return getHost().getContent();
	}

	@Override
	public EdgePart getHost() {
		return (EdgePart) super.getHost();
	}

}
