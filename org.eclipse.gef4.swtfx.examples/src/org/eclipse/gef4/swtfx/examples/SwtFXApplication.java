package org.eclipse.gef4.swtfx.examples;

import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class SwtFXApplication {

	public SwtFXApplication() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		SwtFXCanvas canvas = new SwtFXCanvas(shell, SWT.NONE);

		SwtFXScene scene = createScene();
		canvas.setScene(scene);

		shell.setSize((int) scene.getWidth(), (int) scene.getHeight());
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	public abstract SwtFXScene createScene();

}
