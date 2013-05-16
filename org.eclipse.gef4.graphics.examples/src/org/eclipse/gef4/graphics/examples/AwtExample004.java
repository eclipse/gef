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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class AwtExample004 extends JApplet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("AWT XorMode Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new AwtExample004();
		applet.init();
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void init() {
		JPanel panel = new AwtRadialGradientPanel();
		getContentPane().add(panel);
	}

}

class AwtRadialGradientPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public AwtRadialGradientPanel() {
		setPreferredSize(new Dimension(640, 480));
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate(50, 50);
		g2d.setPaint(new RadialGradientPaint(new Point(50, 50), 50, new Point(
				40, 40), new float[] { 0, .5f, 1 }, new Color[] {
				new Color(255, 255, 255), new Color(255, 0, 0),
				new Color(0, 0, 0) }, CycleMethod.REFLECT));
		g2d.fill(new Rectangle(0, 0, 200, 200));
	}
}
