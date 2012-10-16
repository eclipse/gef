/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.graphics.render.swt.SWTGraphics;
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
					SWTGraphics g = new SWTGraphics(p);
					SimpleGraphicsUtil.renderScene(g);
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
		SWTGraphics g = new SWTGraphics(e.gc);
		SimpleGraphicsUtil.renderScene(g);
		g.cleanUp();
	}

}
