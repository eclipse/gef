/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.awt.AwtGraphics;
import org.eclipse.gef4.graphics.image.AbstractPixelNeighborhoodFilterOperation.EdgeMode;
import org.eclipse.gef4.graphics.image.FilterOperations;
import org.eclipse.gef4.graphics.image.Image;

public class AWTBlurExample extends JApplet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("First test example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new AWTBlurExample();
		applet.init();
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void init() {
		JPanel panel = new AWTBlurExamplePanel();
		getContentPane().add(panel);
	}
}

class AWTBlurExamplePanel extends JPanel {

	private static final int PIXEL_RED = 0xffff0000;
	private static final int STANDARD_DEVIATION = 4;
	private static final long serialVersionUID = 1L;
	private Image img;
	private Image imgNoOp;
	private Image imgConstPixel;
	private Image imgOverlap;
	private Image imgConstNeighbors;

	public AWTBlurExamplePanel() {
		setPreferredSize(new Dimension(640, 480));
	}

	private void initResources(URL resource) throws IOException,
			URISyntaxException {
		if (img == null) {
			img = new Image(ImageIO.read(resource));
		}

		if (imgNoOp == null) {
			imgNoOp = FilterOperations.getGaussianBlur(STANDARD_DEVIATION,
					new EdgeMode.NoOperation()).apply(img);
		}

		if (imgConstPixel == null) {
			imgConstPixel = FilterOperations.getGaussianBlur(
					STANDARD_DEVIATION, new EdgeMode.ConstantPixel(PIXEL_RED))
					.apply(img);
		}

		if (imgOverlap == null) {
			imgOverlap = FilterOperations.getGaussianBlur(STANDARD_DEVIATION,
					new EdgeMode.Overlap()).apply(img);
		}

		if (imgConstNeighbors == null) {
			imgConstNeighbors = FilterOperations.getGaussianBlur(
					STANDARD_DEVIATION,
					new EdgeMode.ConstantPixelNeighbors(PIXEL_RED)).apply(img);
		}
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponents(graphics);

		Graphics2D g2d = (Graphics2D) graphics;
		AwtGraphics g = new AwtGraphics(g2d);

		try {
			renderScene(g, this.getClass().getResource("test.png"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void renderScene(IGraphics g, URL resource) throws IOException,
			URISyntaxException {
		initResources(resource);
		g.pushState();
		g.blit(img).translate(0, img.getHeight()).blit(imgNoOp);
		g.restoreState();
		g.translate(img.getWidth(), 0).blit(imgConstPixel);
		g.pushState();
		g.translate(0, img.getHeight()).blit(imgConstNeighbors);
		g.popState();
		g.translate(img.getWidth(), 0).blit(imgOverlap);
		g.popState();
	}
}