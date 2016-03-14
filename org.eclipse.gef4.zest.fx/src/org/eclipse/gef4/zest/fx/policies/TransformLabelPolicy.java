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

import org.eclipse.gef4.common.attributes.IAttributeStore;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.operations.ChangeAttributeOperation;
import org.eclipse.gef4.zest.fx.parts.AbstractLabelPart;

/**
 * The {@link TransformLabelPolicy} is a specialization of the
 * {@link FXTransformPolicy} that chains a {@link ChangeAttributeOperation} to
 * affect the underlying model when transforming nodes. It is applicable to
 * {@link IContentPart} with {@link javafx.scene.Node} visual and {@link Node}
 * content.
 *
 * @author anyssen
 *
 */
public class TransformLabelPolicy extends FXTransformPolicy {

	@Override
	public ITransactionalOperation commit() {
		// extract changes
		AffineTransform newTransform = FX2Geometry.toAffineTransform(getTransformOperation().getNewTransform());

		// get visual operation
		ITransactionalOperation visualOperation = super.commit();

		// create model operation
		String attributeKey = null;
		String labelRole = getHost().getContent().getValue();
		if (ZestProperties.ELEMENT_EXTERNAL_LABEL.equals(labelRole)) {
			attributeKey = ZestProperties.ELEMENT_EXTERNAL_LABEL_POSITION;
		} else if (ZestProperties.ELEMENT_LABEL.equals(getHost().getContent().getValue())) {
			// node do not have 'internal' labels
			attributeKey = ZestProperties.EDGE_LABEL_POSITION;
		} else if (ZestProperties.EDGE_SOURCE_LABEL.equals(getHost().getContent().getValue())) {
			attributeKey = ZestProperties.EDGE_SOURCE_LABEL_POSITION;
		} else if (ZestProperties.EDGE_TARGET_LABEL.equals(getHost().getContent().getValue())) {
			attributeKey = ZestProperties.EDGE_TARGET_LABEL_POSITION;
		} else {
			throw new IllegalArgumentException("Unsupported content element.");
		}
		Point finalPosition = new Point(newTransform.getTranslateX(), newTransform.getTranslateY());
		ChangeAttributeOperation modelOperation = new ChangeAttributeOperation(
				(IAttributeStore) getHost().getContent().getKey(), attributeKey, finalPosition);

		// assemble operations
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation("Transform label");
		if (visualOperation != null) {
			fwdOp.add(visualOperation);
		}
		fwdOp.add(modelOperation);
		return fwdOp;
	}

	@Override
	public AbstractLabelPart getHost() {
		return (AbstractLabelPart) super.getHost();
	}

}
