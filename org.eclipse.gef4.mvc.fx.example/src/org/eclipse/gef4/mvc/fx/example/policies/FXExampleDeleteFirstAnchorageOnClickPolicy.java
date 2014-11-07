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

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.DeleteContentOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.collect.SetMultimap;

public class FXExampleDeleteFirstAnchorageOnClickPolicy extends
		AbstractFXClickPolicy implements ITransactional {

	private AbstractCompositeOperation commitOperation;

	@Override
	public void click(MouseEvent e) {
		SetMultimap<IVisualPart<Node>, String> anchorages = getHost()
				.getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		IVisualPart<Node> anchorage = anchorages.keySet().iterator().next();
		IViewer<Node> viewer = getHost().getRoot().getViewer();
		if (anchorage instanceof IContentPart) {
			IUndoableOperation o = new DeleteContentOperation<Node>(viewer,
					(IContentPart<Node>) anchorage);
			if (o != null) {
				commitOperation.add(o);
			}
		}
	}

	@Override
	public IUndoableOperation commit() {
		return commitOperation.unwrap();
	}

	@Override
	public void init() {
		commitOperation = new ForwardUndoCompositeOperation("Delete");
	}

}
