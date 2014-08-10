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
package org.eclipse.gef4.swtfx.examples.snippets;

import javafx.embed.swt.FXCanvas;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.gef4.swtfx.controls.ISwtFXControlFactory;
import org.eclipse.gef4.swtfx.controls.SwtFXControlAdapter;
import org.eclipse.gef4.swtfx.examples.SwtFXApplication;
import org.eclipse.swt.SWT;

public class SwtFXButtonSnippet extends SwtFXApplication {

	protected static SwtFXControlAdapter<org.eclipse.swt.widgets.Button> createButtonAdapter(
			final String text) {
		return new SwtFXControlAdapter<org.eclipse.swt.widgets.Button>(
				new ISwtFXControlFactory<org.eclipse.swt.widgets.Button>() {

					@Override
					public org.eclipse.swt.widgets.Button createControl(
							FXCanvas canvas) {
						org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button(
								canvas, SWT.PUSH);
						b.setText(text);
						return b;
					}
				});
	}

	public static void main(String[] args) {
		new SwtFXButtonSnippet();
	}

	private static Shape shape(Shape shape, double r, double g, double b) {
		shape.setFill(new Color(r, g, b, 1));
		shape.setStroke(new Color(0, 0, 0, 1));
		return shape;
	}

	@Override
	public SwtFXScene createScene() {
		HBox hbox = new HBox();
		VBox col1 = new VBox();
		VBox col2 = new VBox();
		hbox.getChildren().addAll(col1, col2);
		HBox.setHgrow(col1, Priority.ALWAYS);
		HBox.setHgrow(col2, Priority.ALWAYS);

		col1.getChildren().addAll(new Button("JavaFX 1"),
				shape(new Arc(0, 0, 50, 50, 15, 120) {
					{
						setType(ArcType.ROUND);
					}
				}, 0.52, 0.49, 0.15), createButtonAdapter("SwtFX 1"));

		col2.getChildren().addAll(
				shape(new Rectangle(0, 0, 100, 50), 0.49, 0.36, 0.20),
				createButtonAdapter("SwtFX 2"),
				shape(new Rectangle(0, 0, 100, 100) {
					{
						setArcHeight(20);
						setArcWidth(20);
					}
				}, 0.87, 0.83, 0.49), new Button("JavaFX 2"));

		return new SwtFXScene(hbox, 400, 400);
	}

}
