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
package org.eclipse.gef4.zest.fx.policies;

import org.eclipse.gef4.mvc.fx.policies.IFXOnTypePolicy;
import org.eclipse.gef4.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link ShowHiddenNeighborsOnTypePolicy} is an {@link IFXOnTypePolicy}
 * that shows all hidden neighbors of its host upon {@link KeyCode#E} key press.
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighborsOnTypePolicy extends AbstractInteractionPolicy<Node>implements IFXOnTypePolicy {

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	@Override
	public void pressed(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if (KeyCode.E.equals(keyCode)) {
			ShowHiddenNeighborsPolicy hiddenNeighborsPolicy = getHost().getAdapter(ShowHiddenNeighborsPolicy.class);
			init(hiddenNeighborsPolicy);
			hiddenNeighborsPolicy.showHiddenNeighbors();
			commit(hiddenNeighborsPolicy);
		}
	}

	@Override
	public void released(KeyEvent event) {
	}

	@Override
	public void typed(KeyEvent event) {
	}

}
