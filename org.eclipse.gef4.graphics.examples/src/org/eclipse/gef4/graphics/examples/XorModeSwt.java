package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.graphics.swt.SwtGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class XorModeSwt implements PaintListener {

	public static void main(String[] args) {
		new XorModeSwt("GEF4 Graphics Xor-Mode (SWT)");
	}

	private final XorModeUtil XOR_MODE_UTIL = new XorModeUtil();
	private Shell shell;

	private SwtGraphics g;

	public XorModeSwt(String title) {
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
		XOR_MODE_UTIL.renderScene(g);
		g.cleanUp();
	}

}
