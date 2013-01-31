package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.swt.SwtGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ForegroundPatternSwt implements PaintListener {

	public static void main(String[] args) {
		new ForegroundPatternSwt("SWT Off-Screen Rendering");
	}

	private final ForegroundPatternUtil FOREGROUND_PATTERN_UTIL = new ForegroundPatternUtil();
	private Shell shell;

	public ForegroundPatternSwt(String title) {
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
		IGraphics g = new SwtGraphics(e.gc);
		FOREGROUND_PATTERN_UTIL.renderScene(g);
		g.cleanUp();
	}

}