/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.mvc.fx.handlers.TranslateSelectedOnDragHandler;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;

import javafx.scene.Node;

public class RelocateLinkedOnDragHandler extends TranslateSelectedOnDragHandler {

	@Override
	public List<IContentPart<? extends Node>> getTargetParts() {
		List<IContentPart<? extends Node>> selected = super.getTargetParts();
		List<IContentPart<? extends Node>> linked = new ArrayList<>(selected);
		for (IContentPart<? extends Node> cp : selected) {
			// ensure that linked parts are moved with us during dragging
			ArrayList<IContentPart<? extends Node>> linkedContentParts = new ArrayList<>(
					PartUtils.filterParts(PartUtils.getAnchoreds(cp, "link"), IContentPart.class));
			for (IContentPart<? extends Node> lcp : linkedContentParts) {
				if (!linked.contains(lcp)) {
					linked.add(lcp);
				}
			}
		}
		return linked;
	}
}
