/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.examples.logo.handlers;

import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.DeletionPolicy;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class DeleteFirstAnchorageOnClickHandler extends AbstractHandler implements IOnClickHandler {

	@Override
	public void click(MouseEvent e) {
		IVisualPart<? extends Node> targetPart = getTargetPart();
		if (targetPart instanceof IContentPart) {
			// delete the part
			IRootPart<? extends Node> root = targetPart.getRoot();
			DeletionPolicy policy = root.getAdapter(DeletionPolicy.class);
			if (policy != null) {
				init(policy);
				policy.delete((IContentPart<? extends Node>) targetPart);
				commit(policy);
			}
		}
	}

	/**
	 * Returns the target {@link IVisualPart} for this policy. Per default the
	 * first anchorage is returned.
	 *
	 * @return The target {@link IVisualPart} for this policy.
	 */
	protected IVisualPart<? extends Node> getTargetPart() {
		return getHost().getAnchoragesUnmodifiable().keySet().iterator().next();
	}
}
