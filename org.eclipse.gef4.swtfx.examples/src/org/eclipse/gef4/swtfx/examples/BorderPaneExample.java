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
package org.eclipse.gef4.swtfx.examples;

import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.layout.BorderPane;
import org.eclipse.gef4.swtfx.layout.BorderPaneConstraints;
import org.eclipse.swt.widgets.Shell;

public class BorderPaneExample extends Application {

	public static void main(String[] args) {
		new BorderPaneExample();
	}

	@Override
	public Scene start(Shell shell) {
		BorderPane root = new BorderPane();

		root.setPrefWidth(640);
		root.setPrefHeight(480);

		root.setTop(new ColoredPiece(100, 50, 0, 0, 1),
				new BorderPaneConstraints());
		root.setLeft(new ColoredPiece(50, 100, 0, 1, 0),
				new BorderPaneConstraints());
		root.setBottom(new ColoredPiece(100, 50, 0, 0, 1),
				new BorderPaneConstraints());
		root.setRight(new ColoredPiece(50, 100, 0, 1, 0),
				new BorderPaneConstraints());
		root.setCenter(new ColoredPiece(100, 100, 1, 0, 0),
				new BorderPaneConstraints());

		return new Scene(shell, root);
	}

}
