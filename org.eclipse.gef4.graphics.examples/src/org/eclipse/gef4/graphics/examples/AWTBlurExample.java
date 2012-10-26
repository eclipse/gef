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
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef4.graphics.Image;
import org.eclipse.gef4.graphics.images.AbstractPixelNeighborhoodFilterOperation.EdgeMode;
import org.eclipse.gef4.graphics.images.FilterOperations;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.awt.AWTGraphics;

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
			img = new Image(new File(resource.toURI()));
		}

		if (imgNoOp == null) {
			imgNoOp = FilterOperations.getGaussianBlur(2.25,
					new EdgeMode.NoOperation()).apply(img);
		}

		if (imgConstPixel == null) {
			imgConstPixel = FilterOperations.getGaussianBlur(2.25,
					new EdgeMode.ConstantPixel(0x00000000)).apply(img);
		}

		if (imgOverlap == null) {
			imgOverlap = FilterOperations.getGaussianBlur(2.25,
					new EdgeMode.Overlap()).apply(img);
		}

		if (imgConstNeighbors == null) {
			imgConstNeighbors = FilterOperations.getGaussianBlur(2.25,
					new EdgeMode.ConstantPixelNeighbors(0xffffffff)).apply(img);
		}
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponents(graphics);

		Graphics2D g2d = (Graphics2D) graphics;
		AWTGraphics g = new AWTGraphics(g2d);

		try {
			renderScene(g, this.getClass().getResource("test.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void renderScene(IGraphics g, URL resource) throws IOException,
			URISyntaxException {
		initResources(resource);

		g.pushState();

		g.blit(img);

		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform()
						.translate(0, img.getHeight()));

		g.blit(imgNoOp);

		g.popState();
		g.pushState();

		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform()
						.translate(img.getWidth(), 0));

		g.blit(imgConstPixel);

		g.pushState();

		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform()
						.translate(0, img.getHeight()));

		g.blit(imgConstNeighbors);

		g.popState();

		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform()
						.translate(img.getWidth(), 0));

		g.blit(imgOverlap);

		g.popState();
	}
}