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

import com.google.inject.Inject;

public class FXViewer extends AbstractViewer<Node> {

	private ISceneFactory sceneFactory;
	private ISceneContainer sceneContainer;
	private Scene scene = null;

	private void createAndHookScene(ISceneContainer container,
			ISceneFactory sceneFactory, Parent rootVisual) {
		scene = sceneFactory.createScene(rootVisual);
		sceneContainer.setScene(scene);
	}

	@Override
	public FXDomain getDomain() {
		return (FXDomain) super.getDomain();
	}

	public Scene getScene() {
		return scene;
	}

	@Override
	public void setRootPart(IRootPart<Node> rootPart) {
		super.setRootPart(rootPart);
		if (rootPart != null) {
			if (scene == null) {
				if (sceneContainer != null && sceneFactory != null) {
					createAndHookScene(sceneContainer, sceneFactory,
							(Parent) rootPart.getVisual());

				}
			} else {
				scene.setRoot((Parent) rootPart.getVisual());
			}
		}
	}

	public void setSceneContainer(ISceneContainer sceneContainer) {
		this.sceneContainer = sceneContainer;
		if (sceneContainer != null) {
			if (scene == null) {
				IRootPart<Node> rootPart = getRootPart();
				if (rootPart != null && sceneFactory != null) {
					createAndHookScene(sceneContainer, sceneFactory,
							(Parent) rootPart.getVisual());
				}
			} else {
				sceneContainer.setScene(scene);
			}
		}
	}

	@Inject
	public void setSceneFactory(ISceneFactory sceneFactory) {
		this.sceneFactory = sceneFactory;
		IRootPart<Node> rootPart = getRootPart();
		if (scene == null && rootPart != null && sceneFactory != null
				&& sceneContainer != null) {
			createAndHookScene(sceneContainer, sceneFactory,
					(Parent) rootPart.getVisual());
		}
	}
}