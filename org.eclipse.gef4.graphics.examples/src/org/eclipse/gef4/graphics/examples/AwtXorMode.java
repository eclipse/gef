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
package org.eclipse.gef4.graphics.examples;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class AwtXorMode extends JApplet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("AWT XorMode Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new AwtXorMode();
		applet.init();
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void init() {
		JPanel panel = new AwtXorModePanel();
		getContentPane().add(panel);
	}

}

class AwtXorModePanel extends JPanel {

	private static class XorComposite implements Composite {

		public static XorComposite INSTANCE = new XorComposite();

		private XorContext context = new XorContext();

		@Override
		public CompositeContext createContext(ColorModel srcColorModel,
				ColorModel dstColorModel, RenderingHints hints) {
			return context;
		}

	}

	private static class XorContext implements CompositeContext {

		public XorContext() {
		}

		@Override
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			int w = Math.min(src.getWidth(), dstIn.getWidth());
			int h = Math.min(src.getHeight(), dstIn.getHeight());

			int[] srcRgba = new int[4];
			int[] dstRgba = new int[4];

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					src.getPixel(x, y, srcRgba);
					dstIn.getPixel(x, y, dstRgba);
					for (int i = 0; i < 3; i++) {
						dstRgba[i] ^= srcRgba[i];
					}
					dstOut.setPixel(x, y, dstRgba);
				}
			}
		}

		@Override
		public void dispose() {
		}

	}

	private static final long serialVersionUID = 1L;

	public AwtXorModePanel() {
		setPreferredSize(new Dimension(640, 480));
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.scale(3, 3);
		render(g2d, false);
		g2d.translate(0, 80);
		render(g2d, true);
	}

	private void render(Graphics2D g, boolean xor) {
		g.setColor(new Color(0, 0, 255));
		g.fillRect(5, 5, 90, 45);
		if (xor) {
			g.setComposite(XorComposite.INSTANCE);
		}
		g.setColor(new Color(255, 255, 255));
		g.fillRect(20, 20, 50, 50);
		g.setColor(new Color(255, 0, 0));
		g.fillOval(80, 20, 50, 50);
	}

}
