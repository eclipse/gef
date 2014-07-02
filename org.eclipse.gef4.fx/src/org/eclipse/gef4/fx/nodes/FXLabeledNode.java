/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.fx.nodes;

import java.util.Arrays;

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class FXLabeledNode extends Group {

	public static final String DEFAULT_LABEL = "-";

	protected Rectangle box = new Rectangle();
	protected Text text = new Text();
	protected double padding = 5;

	{
		setAutoSizeChildren(false);
		getChildren().addAll(box, text);
		box.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT,
				Arrays.asList(new Stop(0, new Color(1, 1, 1, 1)))));
		box.setStroke(new Color(0, 0, 0, 1));
		text.setTextOrigin(VPos.TOP);
		setLabel(DEFAULT_LABEL);
	}

	public double getBoxHeight() {
		return box.getHeight();
	}

	public double getBoxWidth() {
		return box.getWidth();
	}

	public String getLabel() {
		return text.getText();
	}

	protected void refreshLayout() {
		Bounds bounds = text.getLayoutBounds();
		text.setTranslateX(padding);
		text.setTranslateY(padding);
		box.setWidth(bounds.getWidth() + 2 * padding);
		box.setHeight(bounds.getHeight() + 2 * padding);
	}

	public void setBoxHeight(double height) {
		box.setHeight(height);
	}

	public void setBoxWidth(double width) {
		box.setWidth(width);
	}

	public void setLabel(String label) {
		text.setText(label);
		refreshLayout();
	}

}