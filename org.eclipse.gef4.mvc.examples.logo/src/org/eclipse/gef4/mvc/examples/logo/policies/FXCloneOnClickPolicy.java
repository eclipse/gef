/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.policies;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.policies.CreationPolicy;

import com.google.common.collect.HashMultimap;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class FXCloneOnClickPolicy extends AbstractFXOnClickPolicy {

	private AffineTransform originalTransform;

	@Override
	public void click(MouseEvent e) {
		if (!isCloneModifierDown(e)) {
			return;
		}

		// clone content
		Object cloneContent = getHost()
				.getAdapter(AbstractCloneContentPolicy.class).cloneContent();

		// create the clone
		IRootPart<Node, ? extends Node> root = getHost().getRoot();
		CreationPolicy<Node> creationPolicy = root
				.<CreationPolicy<Node>> getAdapter(CreationPolicy.class);
		init(creationPolicy);
		IContentPart<Node, ? extends Node> clonedContentPart = creationPolicy
				.create(cloneContent,
						(IContentPart<Node, ? extends Node>) getHost()
								.getParent(),
						HashMultimap
								.<IContentPart<Node, ? extends Node>, String> create());
		commit(creationPolicy);

		// copy the transformation
		FXTransformPolicy transformPolicy = clonedContentPart
				.getAdapter(FXTransformPolicy.class);
		init(transformPolicy);
		transformPolicy.setTransform(originalTransform);
		commit(transformPolicy);
	}

	protected boolean isCloneModifierDown(MouseEvent e) {
		return e.isAltDown() || e.isShiftDown();
	}

}
