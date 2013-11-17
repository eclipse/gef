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
package org.eclipse.gef4.swtfx;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Paint;

public class SwtFXScene extends javafx.scene.Scene {

	private SwtFXCanvas canvas;

	public SwtFXScene(Parent root) {
		super(root);
	}

	public SwtFXScene(Parent root, double width, double height) {
		super(root, width, height);
	}

	public SwtFXScene(Parent root, double width, double height,
			boolean depthBuffer) {
		super(root, width, height, depthBuffer);
	}

	public SwtFXScene(Parent root, double width, double height, Paint fill) {
		super(root, width, height, fill);
	}

	public SwtFXScene(Parent root, Paint fill) {
		super(root, fill);
	}

	public SwtFXCanvas getFXCanvas() {
		return canvas;
	}

	public void setFXCanvas(SwtFXCanvas canvas) {
		SwtFXCanvas old = this.canvas;
		this.canvas = canvas;

		List<Node> nodes = new LinkedList<Node>();
		if (getRoot() != null) {
			nodes.add(getRoot());
		}

		while (!nodes.isEmpty()) {
			Node node = nodes.remove(0);
			if (node instanceof AbstractSwtFXControl) {
				((AbstractSwtFXControl<?>) node).canvasChanged(old, canvas);
			}
			if (node instanceof Parent) {
				nodes.addAll(((Parent) node).getChildrenUnmodifiable());
			}
		}
	}

}
