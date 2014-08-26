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

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.IGeometry;

import com.google.inject.Provider;

public class FXSelectionLinkFeedbackPart extends FXSelectionFeedbackPart {

	public FXSelectionLinkFeedbackPart(Provider<IGeometry> linkFeedbackGeometry) {
		super(linkFeedbackGeometry);
	}

	@Override
	protected FXGeometryNode<IGeometry> createFeedbackVisual() {
		FXGeometryNode<IGeometry> visual = super.createFeedbackVisual();
		visual.setStroke(Color.GREY);
		visual.getStrokeDashArray().add(5.0);
		visual.setStrokeLineJoin(StrokeLineJoin.BEVEL);
		visual.setStrokeType(StrokeType.CENTERED);
		return visual;
	}

}
