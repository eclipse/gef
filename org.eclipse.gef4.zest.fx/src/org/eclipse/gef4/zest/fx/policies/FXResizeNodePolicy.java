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

import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.operations.ChangeNodeBoundsOperation;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/**
 * The {@link FXResizeNodePolicy} is a specialization of {@link FXResizePolicy}
 * that chains a {@link ChangeNodeBoundsOperation} for updating the resized
 * {@link Node}. Therefore, it is only applicable for {@link NodeContentPart}.
 *
 * @author mwienand
 *
 */
public class FXResizeNodePolicy extends FXResizePolicy {

	@Override
	public ITransactionalOperation commit() {
		Dimension initialSize = getResizeOperation().getInitialSize();
		double dw = getResizeOperation().getDw();
		double dh = getResizeOperation().getDh();
		ITransactionalOperation visualOperation = super.commit();
		Rectangle currentBounds = ZestProperties.getBounds(getHost().getContent());
		if (currentBounds == null) {
			currentBounds = FX2Geometry.toRectangle(getHost().getVisual().getLayoutBounds());
		}
		Rectangle finalBounds = new Rectangle(currentBounds.getX(), currentBounds.getY(), initialSize.width + dw,
				initialSize.height + dh);
		ChangeNodeBoundsOperation modelOperation = new ChangeNodeBoundsOperation(getHost(), finalBounds);
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation("ResizeNode()");
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
