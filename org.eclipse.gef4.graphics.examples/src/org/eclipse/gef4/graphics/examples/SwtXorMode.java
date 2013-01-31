package org.eclipse.gef4.graphics.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SwtXorMode implements PaintListener {

	public static void main(String[] args) {
		new SwtXorMode("GEF 4 Graphics - SWT Printer");
	}

	public SwtXorMode(String title) {
		Display display = new Display();

		final Shell shell = new Shell(display, SWT.SHELL_TRIM
				| SWT.DOUBLE_BUFFERED);
		shell.addPaintListener(this);
		shell.setText(title);
		shell.pack();
		shell.setBounds(0, 0, 640, 480);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void paintControl(PaintEvent e) {
		e.gc.setAdvanced(false);
		e.gc.setXORMode(true);
		e.gc.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_MAGENTA));
		e.gc.fillRectangle(100, 100, 100, 100);
	}

}
