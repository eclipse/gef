/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.parts;

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.parts.AbstractVisualPart;

public class EdgeLabelPart extends AbstractVisualPart<Node, Text> {

	private Translate translate;

	@Override
	protected Text createVisual() {
		Text text = new Text();
		text.setTextOrigin(VPos.TOP);
		text.setManaged(false);
		text.setPickOnBounds(true);

		translate = new Translate();
		text.getTransforms().add(translate);
		return text;
	}

	@Override
	protected void doRefreshVisual(Text visual) {
		EdgeContentPart edgeContentPart = getParent();
		Rectangle bounds = edgeContentPart.getVisual().getCurveNode()
				.getGeometry().getBounds();
		Bounds textBounds = getVisual().getLayoutBounds();
		visual.setTranslateX(bounds.getX() + bounds.getWidth() / 2
				- textBounds.getWidth() / 2);
		visual.setTranslateY(bounds.getY() + bounds.getHeight() / 2
				- textBounds.getHeight());
	}

	@Override
	public EdgeContentPart getParent() {
		return (EdgeContentPart) super.getParent();
	}

	public Translate getOffset() {
		return translate;
	}

}