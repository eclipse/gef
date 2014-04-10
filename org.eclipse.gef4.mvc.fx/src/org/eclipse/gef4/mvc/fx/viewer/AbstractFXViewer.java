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

import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.viewer.AbstractVisualViewer;

public abstract class AbstractFXViewer extends AbstractVisualViewer<Node>
		implements IFXViewer {

	private Scene scene = null;

	public AbstractFXViewer() {
		super();
	}

	@Override
	public void setRootPart(IRootPart<Node> rootPart) {
		super.setRootPart(rootPart);
		if (rootPart != null) {
			if (getScene() == null) {
				setScene(createScene((Parent) rootPart.getVisual()));
			} else {
				getScene().setRoot((Parent) rootPart.getVisual());
			}
		} else {
			setScene(null);
		}
	}

	protected void setScene(Scene scene) {
		this.scene = scene;
	}

	protected Scene createScene(Parent rootVisual) {
		return new Scene(rootVisual);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef4.mvc.fx.viewer.IFXVisualViewer#getScene()
	 */
	@Override
	public Scene getScene() {
		return scene;
	}

}