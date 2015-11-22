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

import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnTypePolicy;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link ShowHiddenNeighboursOnTypePolicy} is an
 * {@link AbstractFXOnClickPolicy} that shows all hidden neighbors of its host
 * upon {@link KeyCode#E} key press.
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighboursOnTypePolicy extends AbstractFXOnTypePolicy {

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	@Override
	public void pressed(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if (KeyCode.E.equals(keyCode)) {
			ShowHiddenNeighboursPolicy hiddenNeighboursPolicy = getHost().getAdapter(ShowHiddenNeighboursPolicy.class);
			hiddenNeighboursPolicy.init();
			hiddenNeighboursPolicy.showHiddenNeighbours();
			hiddenNeighboursPolicy.commit();
		}
	}

	@Override
	public void released(KeyEvent event) {
	}

	@Override
	public void typed(KeyEvent event) {
	}

}
