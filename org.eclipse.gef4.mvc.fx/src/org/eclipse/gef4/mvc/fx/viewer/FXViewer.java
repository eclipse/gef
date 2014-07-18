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
import com.google.inject.name.Named;

public class FXViewer extends AbstractViewer<Node> {

	private ISceneFactory sceneFactory;
	private ISceneContainer sceneContainer;
	private Scene scene = null;

	@Override
	public FXDomain getDomain() {
		return (FXDomain) super.getDomain();
	}

	public Scene getScene() {
		return scene;
	}

	@Inject
	@Override
	public void setRootPart(@Named("AbstractViewer") IRootPart<Node> rootPart) {
		super.setRootPart(rootPart);
		if (rootPart != null) {
			if (scene == null) {
				if (sceneFactory != null) {
					setScene(sceneFactory.createScene((Parent) rootPart
							.getVisual()));
				}
			} else {
				getScene().setRoot((Parent) rootPart.getVisual());
			}
		} else {
			setScene(null);
		}
	}

	private void setScene(Scene scene) {
		if (this.scene != scene) {
			this.scene = scene;
			if (sceneContainer != null) {
				sceneContainer.setScene(scene);
			}
		}
	}

	public void setSceneContainer(ISceneContainer sceneContainer) {
		if (this.sceneContainer != null) {
			this.sceneContainer.unregisterFocusForwarding(this);
		}
		this.sceneContainer = sceneContainer;
		if (sceneContainer != null) {
			sceneContainer.registerFocusForwarding(this);
			if (scene != null) {
				sceneContainer.setScene(scene);
			}
		}
	}

	@Inject
	public void setSceneFactory(ISceneFactory sceneFactory) {
		this.sceneFactory = sceneFactory;
		if (getRootPart() != null && scene == null) {
			setScene(sceneFactory.createScene((Parent) getRootPart()
					.getVisual()));
		}
	}

}