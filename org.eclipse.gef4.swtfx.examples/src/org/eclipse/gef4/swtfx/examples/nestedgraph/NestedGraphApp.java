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
package org.eclipse.gef4.swtfx.examples.nestedgraph;

import org.eclipse.gef4.swt.fx.examples.Application;
import org.eclipse.gef4.swtfx.Group;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.swt.widgets.Shell;

public class NestedGraphApp extends Application {

	public static void main(String[] args) {
		new NestedGraphApp();
	}

	@Override
	public Scene start(Shell shell) {
		Group root = new Group();
		root.setPrefWidth(800);
		root.setPrefHeight(600);

		Scene scene = new Scene(shell, root);

		LabPane labPane = new LabPane("Progressbars");
		labPane.addContentNodes(new ProgressNode());
		labPane.relocate(50, 50);

		BallsNode ballsNode = new BallsNode(scene);
		ballsNode.relocate(250, 200);

		root.addChildNodes(labPane, ballsNode);

		return scene;
	}

}
