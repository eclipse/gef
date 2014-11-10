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
package org.eclipse.gef4.mvc.fx.example.policies;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.ContentPolicy;

import com.google.common.collect.SetMultimap;

public class FXExampleDeleteFirstAnchorageOnClickPolicy extends
		AbstractFXClickPolicy {

	@Override
	public void click(MouseEvent e) {
		SetMultimap<IVisualPart<Node>, String> anchorages = getHost()
				.getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		IVisualPart<Node> anchorage = anchorages.keySet().iterator().next();
		if (anchorage instanceof IContentPart) {
			ContentPolicy<Node> policy = anchorage
					.<ContentPolicy<Node>> getAdapter(ContentPolicy.class);
			if (policy != null) {
				init(policy);
				policy.deleteContent();
				commit(policy);
			}
		}
	}

}
