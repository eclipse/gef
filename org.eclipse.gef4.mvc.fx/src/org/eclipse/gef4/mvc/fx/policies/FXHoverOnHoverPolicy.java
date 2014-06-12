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
package org.eclipse.gef4.mvc.fx.policies;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXHoverOnHoverPolicy extends AbstractFXHoverPolicy {

	@Override
	public void hover(MouseEvent e) {
		IVisualPart<Node> host = getHost();
		if (host instanceof IRootPart) {
			getHost().getRoot().getViewer().getHoverModel().setHover(null);
		} else if (host instanceof IContentPart) {
			getHost().getRoot().getViewer().getHoverModel().setHover(host);
		}
	}

}
