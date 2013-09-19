/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swt.fx.examples;

import org.eclipse.gef4.geometry.planar.QuadraticCurve;
import org.eclipse.gef4.swtfx.gc.SwtUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestGcLineBug {

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		final Transform tx = new Transform(display);

		// does not work with this set
		tx.setElements((float) 0.527691627751155, (float) 0.527691627751155,
				(float) -0.527691627751155, (float) 0.527691627751155,
				(float) 15.375719146431834, (float) 247.60611440956703);

		// does work with the following set
		// tx.setElements((float) 0.527691627751155, (float) 0.527691627751155,
		// (float) -0.52, (float) 0.527691627751155,
		// (float) 15.375719146431834, (float) 247.60611440956703);

		shell.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setAntialias(SWT.ON);
				e.gc.setLineAttributes(new LineAttributes(3, SWT.CAP_ROUND,
						SWT.JOIN_BEVEL, SWT.LINE_CUSTOM,
						new float[] { 20, 20 }, 0, 11));
				e.gc.setTransform(tx);
				e.gc.drawPath(SwtUtils.createSwtPath(new QuadraticCurve(50, 50,
						200, 50, 200, 200).toPath(), display));
			}
		});

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
