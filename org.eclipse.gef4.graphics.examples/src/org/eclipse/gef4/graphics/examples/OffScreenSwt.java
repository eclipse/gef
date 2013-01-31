package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.graphics.swt.SwtGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class OffScreenSwt implements PaintListener {

	public static void main(String[] args) {
		new OffScreenSwt("SWT Off-Screen Rendering");
	}

	private final OffScreenUtil OFF_SCREEN_UTIL = new OffScreenUtil();
	private Shell shell;

	private SwtGraphics g;

	public OffScreenSwt(String title) {
		Display display = new Display();

		shell = new Shell(display, SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText(title);
		shell.pack();
		shell.open();
		shell.setBounds(0, 0, 640, 480);
		shell.addPaintListener(this);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void paintControl(PaintEvent e) {
		g = new SwtGraphics(e.gc);
		OFF_SCREEN_UTIL.renderScene(g);
		g.cleanUp();
	}

}
