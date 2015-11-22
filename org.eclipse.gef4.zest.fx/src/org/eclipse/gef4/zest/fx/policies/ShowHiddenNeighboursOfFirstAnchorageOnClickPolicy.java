/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.HidingModel;

import com.google.common.collect.SetMultimap;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link ShowHiddenNeighboursOfFirstAnchorageOnClickPolicy} is an
 * {@link AbstractFXOnClickPolicy} that shows all hidden neighbors of its host
 * upon mouse click by removing them from the {@link HidingModel}.
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighboursOfFirstAnchorageOnClickPolicy extends AbstractFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getHost().getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		IVisualPart<Node, ? extends Node> anchorage = anchorages.keySet().iterator().next();
		ShowHiddenNeighboursPolicy hiddenNeighboursPolicy = anchorage.getAdapter(ShowHiddenNeighboursPolicy.class);
		hiddenNeighboursPolicy.init();
		hiddenNeighboursPolicy.showHiddenNeighbours();
		hiddenNeighboursPolicy.commit();
	}

}
