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
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.operations.ChangeNodeBoundsOperation;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/**
 * The {@link FXTransformNodePolicy} is a specialization of the
 * {@link FXTransformPolicy} that chains a {@link ChangeNodeBoundsOperation} to
 * affect the underlying model when transforming nodes. Therefore, it is only
 * applicable for {@link NodeContentPart}.
 *
 * @author mwienand
 *
 */
public class FXTransformNodePolicy extends FXTransformPolicy {

	@Override
	public ITransactionalOperation commit() {
		AffineTransform newTransform = FX2Geometry.toAffineTransform(getTransformOperation().getNewTransform());
		ITransactionalOperation visualOperation = super.commit();
		Rectangle currentBounds = ZestProperties.getBounds(getHost().getContent());
		if (currentBounds == null) {
			currentBounds = FX2Geometry.toRectangle(getHost().getVisual().getLayoutBounds());
		}
		Rectangle finalBounds = new Rectangle(newTransform.getTranslateX(), newTransform.getTranslateY(),
				currentBounds.getWidth(), currentBounds.getHeight());
		ChangeNodeBoundsOperation modelOperation = new ChangeNodeBoundsOperation(getHost(), finalBounds);
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation("TransformNode()");
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
