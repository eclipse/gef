package org.eclipse.gef4.graphics.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SwtImageLinesTest implements PaintListener {

	public static void main(String[] args) {
		new SwtImageLinesTest();
	}

	public SwtImageLinesTest() {
		Display display = new Display();

		Shell shell = new Shell(display, SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText("SWT Image Lines Test");
		shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		shell.pack();
		shell.open();
		shell.setBounds(0, 0, 640, 480);
		shell.addPaintListener(this);
		shell.redraw();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;

		gc.setAdvanced(true);

		ImageData imageData = new ImageData(100, 100,
				gc.getDevice().getDepth(), new PaletteData(0xff, 0xff00,
						0xff0000));
		Image image = new Image(gc.getDevice(), imageData);

		GC igc = new GC(image);

		Transform t = new Transform(igc.getDevice(), 1, 0, 0, 1, 0, 0);
		igc.setTransform(t);
		igc.setClipping((Path) null);
		igc.setAntialias(SWT.ON);
		igc.setTextAntialias(SWT.ON);
		igc.setLineAttributes(new LineAttributes(20, 1, 3, SWT.LINE_CUSTOM,
				new float[] {}, 0, 11));
		igc.setAlpha(255);
		igc.setForeground(new Color(Display.getCurrent(), 255, 0, 0));

		igc.drawLine(20, 20, 80, 20);

		t.dispose();
		igc.dispose();

		t = new Transform(gc.getDevice(), 1, 0, 0, 1, 20, 20);
		gc.setTransform(t);

		gc.drawImage(image, 0, 0);

		t.dispose();
		image.dispose();
	}
}
