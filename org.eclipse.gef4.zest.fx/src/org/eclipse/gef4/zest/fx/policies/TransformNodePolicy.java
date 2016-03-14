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
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.operations.ChangeNodePositionOperation;
import org.eclipse.gef4.zest.fx.operations.ChangeNodeSizeOperation;

/**
 * The {@link TransformNodePolicy} is a specialization of the
 * {@link FXTransformPolicy} that chains a {@link ChangeNodeSizeOperation} to
 * affect the underlying model when transforming nodes. It is applicable to
 * {@link IContentPart} with {@link javafx.scene.Node} visual and {@link Node}
 * content.
 *
 * @author mwienand
 *
 */
public class TransformNodePolicy extends FXTransformPolicy {

	@Override
	public ITransactionalOperation commit() {
		// extract changes
		AffineTransform newTransform = FX2Geometry.toAffineTransform(getTransformOperation().getNewTransform());

		// get visual operation
		ITransactionalOperation visualOperation = super.commit();

		// create model operation
		Point finalPosition = new Point(newTransform.getTranslateX(), newTransform.getTranslateY());
		ChangeNodePositionOperation modelOperation = new ChangeNodePositionOperation(getNode(), finalPosition);

		// assemble operations
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation("Transform Node");
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
