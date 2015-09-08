/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.policies;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;
import org.eclipse.gef4.zest.fx.parts.EdgeLabelPart;

import com.google.common.collect.SetMultimap;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FocusAndSelectFirstAnchorageOnClickPolicy} is an
 * {@link AbstractFXOnClickPolicy} that focuses and select its first anchorage
 * when its {@link #getHost() host} is clicked. By default, this policy is
 * installed for {@link EdgeLabelPart}s so that the {@link EdgeContentPart} is
 * focused and selected when its label is clicked.
 *
 * @author mwienand
 *
 */
public class FocusAndSelectFirstAnchorageOnClickPolicy extends AbstractFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getHost().getAnchorages();
		if (anchorages.isEmpty()) {
			return;
		}

		IVisualPart<Node, ? extends Node> firstAnchorage = anchorages.keys().iterator().next();
		AbstractFXOnClickPolicy anchorageOnClickPolicy = firstAnchorage
				.getAdapter(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY));
		if (anchorageOnClickPolicy instanceof FXFocusAndSelectOnClickPolicy) {
			anchorageOnClickPolicy.click(e);
		}
	}

}
