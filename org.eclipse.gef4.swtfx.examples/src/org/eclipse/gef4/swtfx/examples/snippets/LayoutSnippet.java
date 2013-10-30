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

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.controls.SwtButton;
import org.eclipse.gef4.swtfx.examples.Application;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.HBox;
import org.eclipse.gef4.swtfx.layout.VBox;
import org.eclipse.swt.widgets.Shell;

public class LayoutSnippet extends Application {

	public static void main(String[] args) {
		new LayoutSnippet();
	}

	private ShapeFigure<?> shape(IShape shape, double r, double g, double b) {
		ShapeFigure<IShape> figure = new ShapeFigure<IShape>(shape);
		figure.setFill(new RgbaColor(r, g, b));
		return figure;
	}

	@Override
	public Scene start(Shell shell) {
		HBox hbox = new HBox();
		VBox col1 = new VBox();
		VBox col2 = new VBox();
		hbox.addChildren(col1, col2);

		ShapeFigure<?> pie = shape(new Pie(0, 0, 100, 100, Angle.fromDeg(15),
				Angle.fromDeg(120)), 0, 1, 1);
		col1.addChildren(new SwtButton("abc"),
				shape(new Polygon(50, 0, 100, 100, 0, 100), 0, 1, 0), pie,
				new SwtButton("123"));
		System.out.println("pie bounds: " + pie.getLayoutBounds());

		col2.addChildren(shape(new Ellipse(0, 0, 70, 80), 1, 0, 0),
				shape(new Rectangle(0, 0, 100, 40), 0, 0, 1), new SwtButton(
						"foobar"),
				shape(new RoundedRectangle(0, 0, 100, 100, 10, 10), 1, 0, 1));

		// set root size and create scene
		hbox.setPrefWidth(400);
		hbox.setPrefHeight(300);
		return new Scene(shell, hbox);
	}

}
