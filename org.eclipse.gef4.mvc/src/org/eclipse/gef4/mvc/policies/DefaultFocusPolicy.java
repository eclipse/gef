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
package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class DefaultFocusPolicy<VR> extends AbstractPolicy<VR> {

	public void focus() {
		IVisualPart<VR> host = getHost();
		if (host instanceof IContentPart) {
			host.getRoot().getViewer().getFocusModel()
					.setFocused(isFocusable() ? (IContentPart<VR>) host : null);
		}
	}

	protected boolean isFocusable() {
		return true;
	}

}
