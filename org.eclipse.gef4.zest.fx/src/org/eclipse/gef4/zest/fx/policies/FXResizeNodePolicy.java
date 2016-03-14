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

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.zest.fx.operations.ChangeNodeSizeOperation;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/**
 * The {@link FXResizeNodePolicy} is a specialization of {@link FXResizePolicy}
 * that chains a {@link ChangeNodeSizeOperation} for updating the resized
 * {@link Node}. Therefore, it is only applicable for {@link NodeContentPart}.
 *
 * @author mwienand
 *
 */
public class FXResizeNodePolicy extends FXResizePolicy {

	@Override
	public ITransactionalOperation commit() {
		// extract changes
		Dimension initialSize = getResizeOperation().getInitialSize();
		double dw = getResizeOperation().getDw();
		double dh = getResizeOperation().getDh();

		// get visual operation
		ITransactionalOperation visualOperation = super.commit();

		// create model operation
		Dimension finalSize = new Dimension(initialSize.width + dw, initialSize.height + dh);
		ChangeNodeSizeOperation modelOperation = new ChangeNodeSizeOperation(getHost(), finalSize);

		// assemble visual and model operations
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation("Resize Node");
		if (visualOperation != null) {
			fwdOp.add(visualOperation);
		}
		fwdOp.add(modelOperation);
		return fwdOp;
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

}
