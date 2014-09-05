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

import org.eclipse.gef4.mvc.models.HoverModel;

//TODO: make ITransactional
public class HoverPolicy<VR> extends AbstractPolicy<VR> {

	// TODO: use a ChangeHoverOperation (and provide a hook to decide
	// whether it should be executed on the operation history)

	public void hover() {
		getHost().getRoot().getViewer().getAdapter(HoverModel.class)
		.setHover(getHost());
	}

}
