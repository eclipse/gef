package org.eclipse.gef4.graphics.examples;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef4.graphics.awt.AwtGraphics;

public class XorModeAwt extends JApplet {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("First test example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new XorModeAwt();
		applet.init();
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void init() {
		JPanel panel = new XorModeAwtPanel();
		getContentPane().add(panel);
	}
}

class XorModeAwtPanel extends JPanel {

	private static final XorModeUtil XOR_MODE_UTIL = new XorModeUtil();
	private static final long serialVersionUID = 1L;

	public XorModeAwtPanel() {
		setPreferredSize(new Dimension(640, 480));
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponents(graphics);
		Graphics2D g2d = (Graphics2D) graphics;
		AwtGraphics g = new AwtGraphics(g2d);
		XOR_MODE_UTIL.renderScene(g);
		g.cleanUp();
	}

}