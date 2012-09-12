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
import org.eclipse.gef4.graphics.images.AddComposite;
import org.eclipse.gef4.graphics.images.BoxBlurFilter;
import org.eclipse.gef4.graphics.images.ConvolutionFilter.EdgeMode;
import org.eclipse.gef4.graphics.images.GenericMatrixFilter;

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
		Image image = new Image(resource);
		Image blurred = image.getFiltered(new BoxBlurFilter(5,
				EdgeMode.EDGE_OVERLAP));
		Image powered = image.getComposed(new AddComposite(), image, 0, 0);
		// Image greyScaled = image.getFiltered(new GreyScaleFilter(0.33, 0.33,
		// 0.33));
		Image redChannel = image.getFiltered(new GenericMatrixFilter(1, 0, 0,
				0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0));
		// Image sharpened = image.getFiltered(new SharpenFilter());
		// Image blursharp = blurred.getFiltered(new SharpenFilter());
		double width = image.getWidth();
		double height = image.getHeight();

		g.pushState();
		g.blit(blurred);
		// g.canvasProperties().affineTransform().translate(0, height);
		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform().translate(0, height));
		g.blit(redChannel);
		// g.canvasProperties().affineTransform().translate(0, height);
		// g.blit(sharpened);
		g.popState();
		// g.canvasProperties().affineTransform().translate(width, 0);
		// g.blit(blursharp);
	}
}