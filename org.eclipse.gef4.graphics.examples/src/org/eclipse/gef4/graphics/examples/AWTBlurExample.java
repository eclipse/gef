package org.eclipse.gef4.graphics.examples;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.Image;
import org.eclipse.gef4.graphics.awt.DisplayGraphics;
import org.eclipse.gef4.graphics.filters.ConvolutionFilter;

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

	public AWTBlurExamplePanel() {
		setPreferredSize(new Dimension(640, 480));
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponents(graphics);

		Graphics2D g2d = (Graphics2D) graphics;
		DisplayGraphics g = new DisplayGraphics(g2d);

		renderScene(g, this.getClass().getResource("package-explorer.png"));
	}

	private void renderScene(IGraphics g, URL resource) {
		g.pushState();
		g.blitProperties()
				.filters()
				.add(new ConvolutionFilter(3, 0.1, 0.1, 0.1, 0.1, 0.2, 0.1,
						0.1, 0.1, 0.1));
		g.blit(new Image(resource));
		g.popState();
	}

}