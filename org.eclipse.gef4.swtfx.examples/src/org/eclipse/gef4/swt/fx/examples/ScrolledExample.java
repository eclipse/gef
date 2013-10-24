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

import org.eclipse.gef4.swtfx.Orientation;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.controls.SwtScrollBar;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.gef4.swtfx.layout.ScrollPane;
import org.eclipse.swt.widgets.Shell;

public class ScrolledExample extends Application {

	public static void main(String[] args) {
		new ScrolledExample();
	}

	@Override
	public Scene start(Shell shell) {
		Pane root = new Pane();
		root.setPrefWidth(400);
		root.setPrefHeight(300);

		SwtScrollBar hBar = new SwtScrollBar(Orientation.HORIZONTAL);
		SwtScrollBar vBar = new SwtScrollBar(Orientation.VERTICAL);

		ScrollPane scrollPane = new ScrollPane();

		scrollPane.resizeRelocate(100, 100, 100, 100);

		root.addChildNodes(hBar, vBar, scrollPane);

		hBar.resize(200, 0);
		vBar.resizeRelocate(50, 50, 0, 200);

		return new Scene(shell, root);
	}

}
