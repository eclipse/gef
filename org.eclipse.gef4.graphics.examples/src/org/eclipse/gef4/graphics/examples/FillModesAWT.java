package org.eclipse.gef4.graphics.examples;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef4.graphics.render.awt.AWTGraphics;

public class FillModesAWT extends JApplet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle(FillModesUtil.TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new FillModesAWT();
		applet.init();
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void init() {
		JPanel panel = new FillModesAWTPanel();
		getContentPane().add(panel);
	}

}

class FillModesAWTPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public FillModesAWTPanel() {
		setPreferredSize(new Dimension(FillModesUtil.WIDTH,
				FillModesUtil.HEIGHT));
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponents(graphics);

		Graphics2D g2d = (Graphics2D) graphics;
		AWTGraphics g = new AWTGraphics(g2d);

		FillModesUtil.renderScene(g);
	}

}