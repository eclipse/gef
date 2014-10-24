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

import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

// TODO: make ITransactional
public class FocusPolicy<VR> extends AbstractPolicy<VR> {

	// TODO: use a ChangeFocusOperation (and provide a hook to decide
	// whether it should be executed on the operation history)

	public void focus() {
		IVisualPart<VR> host = getHost();
		if (host instanceof IContentPart) {
			host.getRoot().getViewer().getAdapter(FocusModel.class)
					.setFocused((IContentPart<VR>) host);
		} else {
			host.getRoot().getViewer().getAdapter(FocusModel.class)
					.setFocused(null);
		}
	}

}
