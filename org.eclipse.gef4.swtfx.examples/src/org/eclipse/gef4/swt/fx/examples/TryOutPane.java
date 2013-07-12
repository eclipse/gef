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
package org.eclipse.gef4.swt.fx.examples;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.CurvedPolygon;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.swtfx.AbstractParent;
import org.eclipse.gef4.swtfx.ControlNode;
import org.eclipse.gef4.swtfx.IFigure;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.gc.GraphicsContextState;
import org.eclipse.gef4.swtfx.gc.Paint;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.HBox;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.gef4.swtfx.layout.VBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class TryOutPane implements IExample {

	public static void main(String[] args) {
		new Example(new TryOutPane());
	}

	@Override
	public void addUi(IParent root) {
		VBox vbox = new VBox(root.getSwtComposite());

		HBox hbox = new HBox(vbox);
		hbox.addChildNodes(
				shape(new Rectangle(0, 0, 100, 100), 1, 0, 0),
				shape(new Ellipse(0, 0, 100, 100), 0, 1, 0),
				shape(new Pie(0, 0, 100, 100, Angle.fromDeg(15), Angle
						.fromDeg(215)), 0, 0, 1));

		HBox hbox2 = new HBox(vbox);
		hbox2.addChildNodes(
				shape(new Polygon(30, 0, 60, 60, 0, 60), 0.2, 0.7, 0.3),
				shape(new RoundedRectangle(0, 0, 280, 160, 15, 15), 0.3, 1, 0.7),
				shape(new CurvedPolygon(PolyBezier.interpolateCubic(
						new Point(10, 10), new Point(100, 50),
						new Point(30, 70), new Point(10, 10)).toBezier()), 1,
						0, 1));

		HBox hbox3 = new HBox(vbox);
		hbox3.addChildNodes(button(hbox3, "You"), button(hbox3, "can"),
				button(hbox3, "use"), button(hbox3, "SWT"),
				button(hbox3, "controls"));

		((AbstractParent) root).doLayout();
		NestedControls.showAbsoluteBounds(root);
	}

	private ControlNode<Button> button(Pane pane, final String label) {
		Button button = new Button(pane, SWT.PUSH);
		button.setText(label);
		ControlNode<Button> controlNode = new ControlNode<Button>(button);
		controlNode.addEventHandler(ActionEvent.SELECTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						System.out.println(label);
					}
				});
		return controlNode;
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "HBox & VBox";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	private IFigure shape(final IShape shape, final double red,
			final double green, final double blue) {
		return new ShapeFigure(shape) {
			{
				GraphicsContextState gcs = getPaintStateByReference();
				Paint fill = gcs.getFillByReference();
				fill.setColor(new RgbaColor((int) (red * 255),
						(int) (green * 255), (int) (blue * 255)));
			}
		};
	}
}
