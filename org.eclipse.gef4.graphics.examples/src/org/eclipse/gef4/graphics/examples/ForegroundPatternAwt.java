package org.eclipse.gef4.graphics.examples;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef4.graphics.awt.AwtGraphics;

public class ForegroundPatternAwt extends JApplet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("GEF4 Graphics Foreground Pattern");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new ForegroundPatternAwt();
		applet.init();
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void init() {
		JPanel panel = new ForegroundPatternAwtPanel();
		getContentPane().add(panel);
	}
}

class ForegroundPatternAwtPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final ForegroundPatternUtil FOREGROUND_PATTERN_UTIL = new ForegroundPatternUtil();

	public ForegroundPatternAwtPanel() {
		setPreferredSize(new Dimension(640, 480));
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponents(graphics);

		Graphics2D g2d = (Graphics2D) graphics;
		AwtGraphics g = new AwtGraphics(g2d);
		FOREGROUND_PATTERN_UTIL.renderScene(g);
		g.cleanUp();
	}

}
