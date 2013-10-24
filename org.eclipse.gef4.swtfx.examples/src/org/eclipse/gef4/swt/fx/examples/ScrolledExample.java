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
import org.eclipse.gef4.swtfx.TextFigure;
import org.eclipse.gef4.swtfx.controls.SwtScrollBar;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.gef4.swtfx.layout.ScrollPane;
import org.eclipse.gef4.swtfx.layout.VBox;
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
		scrollPane.setPrefWidth(100);
		scrollPane.setPrefHeight(100);

		VBox vbox = new VBox();

		for (int i = 0; i < 10; i++) {
			vbox.addChildNodes(new TextFigure(
					"alpha beta gamma delta epsilon lambda"));
		}

		scrollPane.setContent(vbox);

		root.addChildNodes(hBar, vBar, scrollPane);

		return new Scene(shell, root);
	}

}
