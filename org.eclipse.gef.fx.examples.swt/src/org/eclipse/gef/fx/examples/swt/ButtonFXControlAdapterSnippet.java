/*******************************************************************************
 * Copyright (c) 2013, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.examples.swt;

import org.eclipse.gef.fx.swt.controls.FXControlAdapter;
import org.eclipse.gef.fx.swt.controls.FXControlAdapter.IControlFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ButtonFXControlAdapterSnippet extends AbstractFxSwtExample {

	protected static FXControlAdapter<org.eclipse.swt.widgets.Button> createButtonAdapter(
			final String text) {
		return new FXControlAdapter<>(
				new IControlFactory<org.eclipse.swt.widgets.Button>() {

					@Override
					public org.eclipse.swt.widgets.Button createControl(
							Composite canvas) {
						org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button(
								canvas, SWT.PUSH);
						b.setText(text);
						return b;
					}
				});
	}

	public static void main(String[] args) {
		new ButtonFXControlAdapterSnippet();
	}

	private static Shape shape(Shape shape, double r, double g, double b) {
		shape.setFill(new Color(r, g, b, 1));
		shape.setStroke(new Color(0, 0, 0, 1));
		return shape;
	}

	public ButtonFXControlAdapterSnippet() {
		super("FXControlAdapter Example (Buttons)");
	}

	@Override
	public Scene createScene() {
		HBox hbox = new HBox();
		VBox col1 = new VBox();
		VBox col2 = new VBox();
		HBox.setMargin(col1, new Insets(10.0));
		HBox.setMargin(col2, new Insets(10.0));
		hbox.getChildren().addAll(col1, col2);
		HBox.setHgrow(col1, Priority.ALWAYS);
		HBox.setHgrow(col2, Priority.ALWAYS);

		col1.getChildren().addAll(new Button("JavaFX Button 1"),
				shape(new Arc(0, 0, 50, 50, 15, 120) {
					{
						setType(ArcType.ROUND);
					}
				}, 0.52, 0.49, 0.15), createButtonAdapter("SWT Button 1"));

		col2.getChildren().addAll(
				shape(new Rectangle(0, 0, 100, 50), 0.49, 0.36, 0.20),
				createButtonAdapter("SWT Button 2"),
				shape(new Rectangle(0, 0, 100, 100) {
					{
						setArcHeight(20);
						setArcWidth(20);
					}
				}, 0.87, 0.83, 0.49), new Button("JavaFX Button 2"));

		return new Scene(hbox, 400, 300);
	}

}
