package org.eclipse.gef4.graphics.examples;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.PrintConfiguration;
import org.eclipse.gef4.graphics.awt.AwtGraphics;
import org.eclipse.gef4.graphics.awt.AwtPrinterGraphics;

public class PrintingAwt extends JApplet {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("First test example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new PrintingAwt();
		applet.init();
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void init() {
		JPanel panel = new PrintingAwtPanel();
		getContentPane().add(panel);
	}
}

class PrintingAwtPanel extends JPanel implements Printable {

	private static final long serialVersionUID = 1L;
	private PrintConfiguration printConfig = new PrintConfiguration(
			new Rectangle(0, 0, 500, 500));

	public PrintingAwtPanel() {
		final Printable me = this;
		setPreferredSize(new Dimension(640, 480));
		((Button) add(new Button("Print")))
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PrinterJob job = PrinterJob.getPrinterJob();
						job.setPrintable(me);
						if (job.printDialog()) {
							try {
								job.print();
							} catch (PrinterException x) {
								x.printStackTrace();
							}
						}
					}
				});
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponents(graphics);

		Graphics2D g2d = (Graphics2D) graphics;
		AwtGraphics g = new AwtGraphics(g2d);

		PrintingUtil.renderScene(g);
		g.cleanUp();
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (pageIndex != 0) {
			return NO_SUCH_PAGE;
		}

		Graphics2D g2d = (Graphics2D) graphics;
		AwtPrinterGraphics g = new AwtPrinterGraphics(printConfig, pageIndex,
				pageFormat, g2d);

		PrintingUtil.renderScene(g);
		g.cleanUp();

		return PAGE_EXISTS;
	}

}