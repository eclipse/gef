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
package org.eclipse.gef4.swt.canvas.examples;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.canvas.Group;
import org.eclipse.gef4.swt.canvas.ShapeFigure;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.gef4.swt.canvas.gc.RgbaColor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;

public class KeyboardExample implements IExample {

	public static void main(String[] args) {
		new Example(new KeyboardExample());
	}

	private ShapeFigure keyboardRect;

	@Override
	public void addUi(Group rootGroup) {
		keyboardRect = new ShapeFigure(new Rectangle(0, 0, 100, 100)) {
			private String text = " ";

			{
				getPaintStateByReference().getFillByReference().setColor(
						new RgbaColor(128, 128, 255, 128));
				getPaintStateByReference().getFontDataByReference().setHeight(
						32);
				addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						text = Character.toString((char) e.keyCode);
						update();
					}
				});
			}

			@Override
			public void doPaint(GraphicsContext g) {
				// paint background
				super.doPaint(g);

				// place text in middle
				Point extent = g.getGcByReference().textExtent(text);
				double x = 0, y = 0, maxWidth = 100;
				if (extent.x < 100) {
					x = (100 - extent.x) / 2;
					maxWidth = extent.x;
				}
				if (extent.y < 100) {
					y = (100 - extent.y) / 2;
				}

				g.strokeText(text, x, y, maxWidth);
			}
		};

		rootGroup.addFigures(keyboardRect);
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Keyboard";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
	public void render(GraphicsContext g) {
		// everything is set up! (shell is visible now)
		keyboardRect.requestFocus();
	}

}
