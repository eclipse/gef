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

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef4.graphics.awt.AwtGraphics;

public class AwtExample extends JApplet {

	private static final long serialVersionUID = 1L;

	private final IExample example;

	public AwtExample(IExample example) {
		this.example = example;
		init();
	}

	@Override
	public void init() {
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setBackground(new Color(255, 255, 255));
				setPreferredSize(new Dimension(example.getWidth(),
						example.getHeight()));
			}

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2d = (Graphics2D) g;
				AwtGraphics graphics = new AwtGraphics(g2d);
				example.renderScene(graphics);
				graphics.cleanUp();
			}
		};
		getContentPane().add(panel);
		JFrame frame = new JFrame();
		frame.setTitle(example.getTitle() + " (AWT)");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);
	}

}
