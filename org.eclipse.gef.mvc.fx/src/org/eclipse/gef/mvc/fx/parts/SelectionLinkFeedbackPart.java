/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.IGeometry;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

/**
 * The {@link SelectionLinkFeedbackPart} is an {@link SelectionFeedbackPart}
 * that uses a dotted line as its visualization. It is used per default to show
 * anchored-anchorage relations where the anchored is not an {@link Connection}.
 *
 * @author anyssen
 *
 */
public class SelectionLinkFeedbackPart extends SelectionFeedbackPart {

	/**
	 * Default constructor.
	 */
	public SelectionLinkFeedbackPart() {
	}

	@Override
	protected GeometryNode<IGeometry> doCreateVisual() {
		GeometryNode<IGeometry> visual = super.doCreateVisual();
		visual.setStroke(Color.GREY);
		visual.getStrokeDashArray().add(5.0);
		visual.setStrokeLineJoin(StrokeLineJoin.BEVEL);
		visual.setStrokeType(StrokeType.CENTERED);
		return visual;
	}

}
