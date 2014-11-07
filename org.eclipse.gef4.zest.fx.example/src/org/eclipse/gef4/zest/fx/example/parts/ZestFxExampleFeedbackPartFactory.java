/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.example.parts;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class ZestFxExampleFeedbackPartFactory extends
		FXDefaultFeedbackPartFactory {

	@Override
	protected IFeedbackPart<Node> createLinkFeedbackPart(
			IVisualPart<Node> anchored, IVisualPart<Node> anchorage,
			String anchorageRole) {
		return null;
	}

}
