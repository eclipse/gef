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

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.AbstractVisualViewer;

public class FXViewer extends AbstractVisualViewer<Node> {

	private FXCanvas canvas;

	public FXViewer(FXCanvas canvas) {
		this.canvas = canvas;
		setRootPart(createRootPart());
	}

	/**
	 * Creates the {@link FXRootVisualPart} which provides the root element for
	 * the JavaFX {@link Scene}.
	 * 
	 * @return an {@link FXRootVisualPart}
	 */
	protected FXRootPart createRootPart() {
		return new FXRootPart();
	}

	@Override
	public void setRootPart(IRootPart<Node> editpart) {
		super.setRootPart(editpart);
		if (editpart != null) {
			canvas.setScene(createScene((Parent) editpart.getVisual()));
		} else {
			canvas.setScene(null);
		}
	}

	protected Scene createScene(Parent rootVisual) {
		return new Scene(rootVisual);
	}

	public FXCanvas getCanvas() {
		return canvas;
	}

	@Override
	public void reveal(IVisualPart<Node> editpart) {
	}

}
