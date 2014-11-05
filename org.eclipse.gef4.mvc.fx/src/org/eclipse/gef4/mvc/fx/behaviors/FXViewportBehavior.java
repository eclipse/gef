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
package org.eclipse.gef4.mvc.fx.behaviors;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.behaviors.AbstractViewportBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.parts.IRootPart;

public class FXViewportBehavior extends AbstractViewportBehavior<Node> {

	@Override
	protected void applyViewport(double translateX, double translateY,
			double width, double height) {
		IRootPart<Node> root = getHost().getRoot();
		if (root instanceof FXRootPart) {
			FXRootPart fxRootPart = (FXRootPart) root;

			fxRootPart.getContentLayer().translateXProperty().set(translateX);
			fxRootPart.getContentLayer().translateYProperty().set(translateY);
			fxRootPart.getScrollPane().setPrefViewportWidth(width);
			fxRootPart.getScrollPane().setPrefViewportHeight(height);

		}
	}

}
