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

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef4.graphics.awt.AwtGraphics;

public class SimpleGraphicsAwt extends JApplet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("First test example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new SimpleGraphicsAwt();
		applet.init();
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void init() {
		JPanel panel = new SimpleGraphicsAwtPanel();
		getContentPane().add(panel);
	}
}

class SimpleGraphicsAwtPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public SimpleGraphicsAwtPanel() {
		setPreferredSize(new Dimension(640, 480));
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponents(graphics);

		Graphics2D g2d = (Graphics2D) graphics;
		AwtGraphics g = new AwtGraphics(g2d);
		SimpleGraphicsUtil.renderScene(g);
	}

}
