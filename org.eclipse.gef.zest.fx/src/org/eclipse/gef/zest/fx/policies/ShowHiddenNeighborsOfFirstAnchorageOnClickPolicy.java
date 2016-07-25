/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
package org.eclipse.gef.zest.fx.policies;

import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.zest.fx.models.HidingModel;

import com.google.common.collect.SetMultimap;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link ShowHiddenNeighborsOfFirstAnchorageOnClickPolicy} is an
 * {@link IFXOnClickPolicy} that shows all hidden neighbors of its host upon
 * mouse click by removing them from the {@link HidingModel}.
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighborsOfFirstAnchorageOnClickPolicy extends AbstractInteractionPolicy<Node>
		implements IFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getHost().getAnchoragesUnmodifiable();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		IVisualPart<Node, ? extends Node> anchorage = anchorages.keySet().iterator().next();
		ShowHiddenNeighborsPolicy hiddenNeighborsPolicy = anchorage.getAdapter(ShowHiddenNeighborsPolicy.class);
		init(hiddenNeighborsPolicy);
		hiddenNeighborsPolicy.showHiddenNeighbors();
		commit(hiddenNeighborsPolicy);
	}

}
