package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.swt.DisplayGraphics;
import org.eclipse.gef4.graphics.swt.PrinterGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTPrinterExample implements PaintListener {

	public static void main(String[] args) {
		new SWTPrinterExample("GEF 4 Graphics - SWT Printer");
	}

	public SWTPrinterExample(String title) {
		Display display = new Display();

		final Shell shell = new Shell(display, SWT.SHELL_TRIM
				| SWT.DOUBLE_BUFFERED);
		shell.setText(title);

		shell.setLayout(new GridLayout(1, true));

		final Canvas c = new Canvas(shell, SWT.BORDER);
		c.addPaintListener(this);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		c.setLayoutData(gridData);

		final Button b = new Button(shell, SWT.PUSH | SWT.BORDER);
		b.setText("Print");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		b.setLayoutData(gridData);

		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PrintDialog printDialog = new PrintDialog(shell, SWT.NONE);
				printDialog.setText("Print");
				PrinterData printerData = printDialog.open();
				if (!(printerData == null)) {
					Printer p = new Printer(printerData);
					p.startJob("PrintJob");
					p.startPage();
					PrinterGraphics g = new PrinterGraphics(p);
					renderScene(g);
					p.endPage();
					g.cleanUp();
					p.endJob();
					p.dispose();
				}
			}
		});

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
		DisplayGraphics g = new DisplayGraphics(e.gc);
		renderScene(g);
		g.cleanUp();
	}

	public void renderScene(IGraphics g) {
		g.fillProperties().setColor(new Color(255, 0, 0, 255));
		g.fill(new Ellipse(50, 50, 350, 200));
	}

}
