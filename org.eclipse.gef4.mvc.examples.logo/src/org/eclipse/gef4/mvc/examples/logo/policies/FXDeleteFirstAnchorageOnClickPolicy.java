/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.mvc.examples.logo.policies;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.ContentPolicy;

import com.google.common.collect.SetMultimap;

public class FXDeleteFirstAnchorageOnClickPolicy
		extends AbstractFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		IVisualPart<Node, ? extends Node> host = getHost();
		if (host instanceof IContentPart) {
			ContentPolicy<Node> policy = host
					.<ContentPolicy<Node>> getAdapter(ContentPolicy.class);
			if (policy != null) {
				init(policy);
				policy.deleteContent();
				commit(policy);
			}
		}
	}

	@Override
	public IVisualPart<Node, ? extends Node> getHost() {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = super.getHost()
				.getParent().getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return null;
		}
		return anchorages.keySet().iterator().next();
	}

}
