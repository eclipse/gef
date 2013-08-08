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

import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.layout.AnchorPane;
import org.eclipse.gef4.swtfx.layout.AnchorPaneConstraints;
import org.eclipse.swt.widgets.Shell;

public class AnchorPaneExample extends Application {

	public static void main(String[] args) {
		new AnchorPaneExample();
	}

	@Override
	public Scene start(Shell shell) {
		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(shell, root);

		root.setPrefWidth(400);
		root.setPrefHeight(300);

		root.add(new ColoredPiece(60, 60, 0, 0, 1), new AnchorPaneConstraints(
				10d, null, null, 10d));
		root.add(new ColoredPiece(60, 60, 0, 1, 0), new AnchorPaneConstraints(
				null, 10d, 10d, null));
		root.add(new ColoredPiece(60, 60, 1, 0, 0), new AnchorPaneConstraints(
				100d, 100d, 100d, 100d));

		root.add(new ColoredPiece(60, 60, 0, 1, 1), new AnchorPaneConstraints(
				150d, null, 10d, 10d));
		root.add(new ColoredPiece(60, 60, 0, 1, 1), new AnchorPaneConstraints(
				10d, 10d, null, 150d));

		return scene;
	}
}
