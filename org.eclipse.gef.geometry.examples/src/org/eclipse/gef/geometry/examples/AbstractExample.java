/*******************************************************************************
 * Copyright (c) 2012, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

abstract public class AbstractExample implements PaintListener {

	public ControllableShapeViewer viewer;
	public Shell shell;

	public AbstractExample(String title, String... infos) {
		Display display = new Display();

		shell = new Shell(display, SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText(title);
		shell.setBounds(0, 0, 640, 480);
		shell.setLayout(new FormLayout());

		if (infos.length > 0) {
			Label infoLabel = new Label(shell, SWT.NONE);
			FormData infoLabelFormData = new FormData();
			infoLabelFormData.right = new FormAttachment(100, -10);
			infoLabelFormData.bottom = new FormAttachment(100, -10);
			infoLabel.setLayoutData(infoLabelFormData);

			String infoText = "You can...";
			for (int i = 0; i < infos.length; i++) {
				infoText += "\n..." + infos[i];
			}
			infoLabel.setText(infoText);
		}

		// open the shell before creating the controllable shapes so that their
		// default coordinates are not changed due to the resize of their canvas
		shell.setBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		shell.open();

		viewer = new ControllableShapeViewer(shell);
		for (ControllableShape cs : getControllableShapes()) {
			viewer.addShape(cs);
		}

		onInit();

		shell.addPaintListener(this);
		shell.redraw(); // triggers a PaintEvent platform independently

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	abstract protected ControllableShape[] getControllableShapes();

	public void onInit() {
	}

	@Override
	public void paintControl(PaintEvent e) {
	}

}
