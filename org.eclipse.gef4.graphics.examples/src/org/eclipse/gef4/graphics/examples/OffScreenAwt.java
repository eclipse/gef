package org.eclipse.gef4.graphics.examples;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef4.graphics.awt.AwtGraphics;

public class OffScreenAwt extends JApplet {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("Off-Screen Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new OffScreenAwt();
		applet.init();
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void init() {
		JPanel panel = new OffScreenAwtPanel();
		getContentPane().add(panel);
	}
}

class OffScreenAwtPanel extends JPanel {

	private static final OffScreenUtil OFF_SCREEN_UTIL = new OffScreenUtil();
	private static final long serialVersionUID = 1L;

	public OffScreenAwtPanel() {
		setPreferredSize(new Dimension(500, 400));
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponents(graphics);
		Graphics2D g2d = (Graphics2D) graphics;
		AwtGraphics g = new AwtGraphics(g2d);
		OFF_SCREEN_UTIL.renderScene(g);
		g.cleanUp();
	}

}