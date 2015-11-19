/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.geometry.planar.IGeometry;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

/**
 * The {@link FXSelectionLinkFeedbackPart} is an {@link FXSelectionFeedbackPart}
 * that uses a dotted line as its visualization. It is used per default to show
 * anchored-anchorage relations where the anchored is not an {@link Connection}.
 *
 * @author anyssen
 *
 */
public class FXSelectionLinkFeedbackPart extends FXSelectionFeedbackPart {

	/**
	 * Default constructor.
	 */
	public FXSelectionLinkFeedbackPart() {
	}

	@Override
	protected GeometryNode<IGeometry> createVisual() {
		GeometryNode<IGeometry> visual = super.createVisual();
		visual.setStroke(Color.GREY);
		visual.getStrokeDashArray().add(5.0);
		visual.setStrokeLineJoin(StrokeLineJoin.BEVEL);
		visual.setStrokeType(StrokeType.CENTERED);
		return visual;
	}

}
