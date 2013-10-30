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
import org.eclipse.gef4.swtfx.TextFigure;
import org.eclipse.gef4.swtfx.controls.SwtButton;
import org.eclipse.gef4.swtfx.controls.SwtLabel;
import org.eclipse.gef4.swtfx.examples.Application;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.AnchorPane;
import org.eclipse.gef4.swtfx.layout.AnchorPaneConstraints;
import org.eclipse.gef4.swtfx.layout.HBox;
import org.eclipse.gef4.swtfx.layout.VBox;
import org.eclipse.swt.widgets.Shell;

public class LayoutSample extends Application {

	public static void main(String[] args) {
		new LayoutSample();
	}

	private ShapeFigure<?> shape(IShape shape, double r, double g, double b) {
		ShapeFigure<IShape> figure = new ShapeFigure<IShape>(shape);
		figure.setFill(new RgbaColor(r, g, b));
		return figure;
	}

	@Override
	public Scene start(Shell shell) {
		VBox root = new VBox();

		HBox hbox = new HBox();
		AnchorPane anchorPane = new AnchorPane();

		root.add(hbox, true);
		root.add(anchorPane, true);
		root.setGrower(anchorPane);

		// fill HBox
		VBox col1 = new VBox();
		VBox col2 = new VBox();
		VBox col3 = new VBox();
		hbox.addChildren(col1, col2, col3);

		col1.addChildren(
				new SwtButton("abc"),
				shape(new Polygon(50, 0, 100, 100, 0, 100), 0, 1, 0),
				shape(new Pie(0, 0, 100, 100, Angle.fromDeg(15), Angle
						.fromDeg(120)), 0, 1, 1));

		col2.addChildren(shape(new Ellipse(0, 0, 70, 80), 1, 0, 0),
				shape(new Rectangle(0, 0, 100, 40), 0, 0, 1), new SwtButton(
						"test"),
				shape(new RoundedRectangle(0, 0, 100, 100, 10, 10), 1, 0, 1));

		col3.addChildren(shape(new Rectangle(0, 0, 100, 100), 1, 1, 0),
				new SwtButton("foobar"), new SwtButton("gaga"));

		// fill AnchorPane
		anchorPane.add(new SwtLabel("SWT Label"), new AnchorPaneConstraints(
				10d, 10d, null, null));
		anchorPane.add(new TextFigure("TextFigure"), new AnchorPaneConstraints(
				null, null, 10d, 10d));

		// set root pref size
		root.setPrefWidth(400);
		root.setPrefHeight(400);

		return new Scene(shell, root);
	}
}
