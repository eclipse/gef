/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.text.Text;

import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.geometry.planar.Rectangle;

public class FXLabeledConnection extends FXCurveConnection {

	protected Text text = new Text();

	{
		text.setTextOrigin(VPos.TOP);
		text.setManaged(false);
	}

	public String getLabel() {
		return text.getText();
	}

	@Override
	protected void refreshGeometry() {
		super.refreshGeometry();
		Bounds textBounds = text.getLayoutBounds();
		Rectangle bounds = getCurveNode().getGeometry().getBounds();
		text.setTranslateX(bounds.getX() + bounds.getWidth() / 2
				- textBounds.getWidth() / 2);
		text.setTranslateY(bounds.getY() + bounds.getHeight() / 2
				- textBounds.getHeight());
		if (!getChildren().contains(text)) {
			getChildren().add(text);
		}
	}

	public void setLabel(String label) {
		text.setText(label);
	}

}
