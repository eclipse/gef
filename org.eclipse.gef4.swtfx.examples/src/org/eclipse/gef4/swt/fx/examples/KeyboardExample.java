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

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.AbstractFigure;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.KeyEvent;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;

public class KeyboardExample implements IExample {

	public static void main(String[] args) {
		new Example(new KeyboardExample());
	}

	private AbstractFigure keyboardRect;

	@Override
	public void addUi(IParent root) {
		keyboardRect = new ShapeFigure(new Rectangle(0, 0, 100, 100)) {
			private String text = " ";

			{
				setFill(new RgbaColor(128, 128, 255, 128));

				// TODO: changing fonts (their sizes etc.) should be easier
				Font font = getFont();
				FontData fontData = font.getFontData()[0];
				fontData.setHeight(32);
				setFont(new Font(font.getDevice(), fontData));

				addEventHandler(KeyEvent.KEY_PRESSED,
						new IEventHandler<KeyEvent>() {
							@Override
							public void handle(KeyEvent event) {
								text = Character.toString(event.getChar());
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

				g.setFill(new RgbaColor());
				g.fillText(text, x, y, maxWidth);
			}
		};
		root.addChildNodes(keyboardRect);

		Button btnFocus = new Button(root.getScene(), SWT.PUSH);
		btnFocus.setText("requestFocus");
		btnFocus.setBounds(20, 200, 150, 50);
		btnFocus.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				keyboardRect.requestFocus();
			}
		});
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

}
