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
package org.eclipse.gef4.zest.fx.policies;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.HoverPolicy;

import com.google.common.collect.SetMultimap;

public class HoverFirstAnchoragePolicy extends HoverPolicy<Node> {

	@Override
	public void hover() {
		SetMultimap<IVisualPart<Node>, String> anchorages = getHost()
				.getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		HoverPolicy<?> anchorageHoverPolicy = anchorages.keySet().iterator()
				.next().getAdapter(HoverPolicy.class);
		anchorageHoverPolicy.hover();
	}

}
