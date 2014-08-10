/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.viewer;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;

public class FXViewer extends AbstractViewer<Node> {

	private ISceneContainer sceneContainer;
	private Scene scene = null;

	private void createAndHookScene(ISceneContainer container, Parent rootVisual) {
		scene = new Scene(rootVisual);
		sceneContainer.setScene(scene);
	}

	@Override
	public FXDomain getDomain() {
		return (FXDomain) super.getDomain();
	}

	public Scene getScene() {
		return scene;
	}

	public void setSceneContainer(ISceneContainer sceneContainer) {
		this.sceneContainer = sceneContainer;
		if (sceneContainer != null) {
			if (scene == null) {
				IRootPart<Node> rootPart = getRootPart();
				if (rootPart != null) {
					createAndHookScene(sceneContainer,
							(Parent) rootPart.getVisual());
				}
			} else {
				sceneContainer.setScene(scene);
			}
		}
	}
}