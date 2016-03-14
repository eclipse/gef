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
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.operations.ChangeAttributeOperation;

/**
 * The {@link ResizeNodePolicy} is a specialization of {@link FXResizePolicy}
 * that chains a {@link ChangeAttributeOperation} for updating the resized
 * {@link Node}. It is applicable to {@link IContentPart} with
 * {@link javafx.scene.Node} visual and {@link Node} content.
 *
 * @author mwienand
 *
 */
public class ResizeNodePolicy extends FXResizePolicy {

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
		ChangeAttributeOperation modelOperation = new ChangeAttributeOperation(getNode(), ZestProperties.NODE_SIZE,
				finalSize);

		// assemble visual and model operations
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation("Resize Node");
		if (visualOperation != null) {
			fwdOp.add(visualOperation);
		}
		fwdOp.add(modelOperation);
		return fwdOp;
	}

	@Override
	public IContentPart<javafx.scene.Node, ? extends javafx.scene.Node> getHost() {
		// XXX: We don't tie this policy to NodePart, but only to
		// IContentPart with a Node content, so it can be re-used in other
		// situations.
		return (IContentPart<javafx.scene.Node, ? extends javafx.scene.Node>) super.getHost();
	}

	/**
	 * Returns the {@link Node} content element of the host part.
	 *
	 * @return The {@link Node} content element.
	 */
	protected Node getNode() {
		return (Node) getHost().getContent();
	}

}
