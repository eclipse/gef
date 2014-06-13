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
package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class HoverPolicy<VR> extends AbstractPolicy<VR> {

	public void hover() {
		IVisualPart<VR> host = getHost();
		if (!(host instanceof IContentPart) || !isHoverable()) {
			getHost().getRoot().getViewer().getHoverModel().setHover(null);
		} else if (host instanceof IContentPart) {
			getHost().getRoot().getViewer().getHoverModel().setHover(host);
		}
	}

	protected boolean isHoverable() {
		return true;
	}
}
