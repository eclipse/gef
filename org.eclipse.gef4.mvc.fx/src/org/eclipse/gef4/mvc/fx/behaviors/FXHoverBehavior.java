/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.behaviors;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import org.eclipse.gef4.mvc.behaviors.AbstractHoverBehavior;

// TODO: this class is a hack; do not use effect for feedback
public class FXHoverBehavior extends AbstractHoverBehavior<Node> {

	@Override
	protected void hideFeedback() {
		if(!getHost().getRoot().getViewer().getSelectionModel().getSelected().contains(getHost())){
			getHost().getVisual().setEffect(null);
		}
	}
	
	@Override
	protected void showFeedback() {
		DropShadow effect = new DropShadow();
		effect.setColor(new Color(0.5, 0.5, 0.5, 1));
		effect.setOffsetX(5);
		effect.setOffsetY(5);
		effect.setRadius(5);
		getHost().getVisual().setEffect(effect);
	}

}
