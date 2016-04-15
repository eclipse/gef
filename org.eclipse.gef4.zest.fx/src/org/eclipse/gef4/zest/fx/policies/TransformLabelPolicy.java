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
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.fx.operations.FXTransformOperation;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
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

	private Point initialOffset;

	private IContentPart<javafx.scene.Node, ? extends javafx.scene.Node> getFirstAnchorage() {
		return (IContentPart<javafx.scene.Node, ? extends javafx.scene.Node>) getHost().getAnchoragesUnmodifiable()
				.keySet().iterator().next();
	}

	@Override
	public AbstractLabelPart getHost() {
		return (AbstractLabelPart) super.getHost();
	}

	private Point getLabelOffsetInParent() {
		Point labelPositionInScene = getHost().getStoredLabelPosition();
		if (labelPositionInScene == null) {
			return null;
		}
		Point referencePositionInScene = getLabelReferencePointInScene(getHost().getContent().getValue());
		Point labelOffset = NodeUtils.sceneToLocal(getHost().getVisual().getParent(),
				labelPositionInScene.getTranslated(referencePositionInScene.getNegated()));
		return labelOffset;
	}

	/**
	 * Retrieve the reference position for the host label in scene coordinates.
	 *
	 * @param labelRole
	 *            The role of the label, i.e. one of
	 *            {@link ZestProperties#EXTERNAL_LABEL__NE},
	 *            {@link ZestProperties#LABEL__NE},
	 *            {@link ZestProperties#SOURCE_LABEL__E}, or
	 *            {@link ZestProperties#TARGET_LABEL__E}.
	 * @return The reference position in scene coordinates.
	 */
	// TODO: make reference position configurable via Zest properties
	private Point getLabelReferencePointInScene(String labelRole) {
		IAttributeStore contentElement = getHost().getContent().getKey();
		if (ZestProperties.EXTERNAL_LABEL__NE.equals(labelRole)) {
			if (contentElement instanceof Node) {
				// node center
				return NodeUtils
						.localToScene(getFirstAnchorage().getVisual(),
								FX2Geometry.toRectangle(getFirstAnchorage().getVisual().getLayoutBounds()))
						.getBounds().getCenter();
			} else if (getHost().getContent().getKey() instanceof Edge) {
				// edge mid segment mid point (or middle index); ensure external
				// label does not collide with label (thus offset with height)
				// TODO: we should detect whether a label is set and use the
				// height of the label instead
				Connection connection = (Connection) getFirstAnchorage().getVisual();
				return NodeUtils.localToScene(connection,
						connection.getCenter().getTranslated(0, getHost().getVisual().getLayoutBounds().getHeight()));
			} else {
				throw new IllegalArgumentException("Unsupported element.");
			}
		} else if (ZestProperties.LABEL__NE.equals(labelRole)) {
			// node do not have 'internal' labels
			if (contentElement instanceof Edge) {
				Connection connection = (Connection) getFirstAnchorage().getVisual();
				return NodeUtils.localToScene(connection, connection.getCenter());
			} else {
				throw new IllegalArgumentException("Unsupported element.");
			}
		} else if (ZestProperties.SOURCE_LABEL__E.equals(labelRole)) {
			Connection connection = (Connection) getFirstAnchorage().getVisual();
			return NodeUtils.localToScene(connection, connection.getStartPoint());
		} else if (ZestProperties.TARGET_LABEL__E.equals(labelRole)) {
			Connection connection = (Connection) getFirstAnchorage().getVisual();
			return NodeUtils.localToScene(connection, connection.getEndPoint());
		} else {
			throw new IllegalArgumentException("Unsupported content element.");
		}
	}

	@Override
	public void init() {
		super.init();
		// store initial relative label position (in scene coordinates)
		initialOffset = getLabelOffsetInParent();
	}

	/**
	 * Enforce that label is preserved at its respective relative location.
	 *
	 * @return Whether the position was adjusted or not.
	 */
	protected boolean preserveLabelOffset() {
		if (initialOffset == null) {
			return false;
		}
		Point currentLabelOffset = getLabelOffsetInParent();
		Point delta = currentLabelOffset.getTranslated(initialOffset.getNegated());
		FXTransformOperation op = ((FXTransformOperation) getOperation());
		op.getNewTransform().setTx(op.getInitialTransform().getTx() - delta.x);
		op.getNewTransform().setTy(op.getInitialTransform().getTy() - delta.y);
		locallyExecuteOperation();
		return true;
	}
}
