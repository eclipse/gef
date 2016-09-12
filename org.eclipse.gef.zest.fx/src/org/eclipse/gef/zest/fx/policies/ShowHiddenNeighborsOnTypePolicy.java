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
package org.eclipse.gef.zest.fx.policies;

import org.eclipse.gef.mvc.fx.policies.IFXOnStrokePolicy;
import org.eclipse.gef.mvc.fx.policies.IFXOnTypePolicy;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.zest.fx.parts.NodePart;

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
public class ShowHiddenNeighborsOnTypePolicy extends AbstractInteractionPolicy<Node> implements IFXOnStrokePolicy {

	@Override
	public void abortPress() {
	}

	@Override
	public void finalRelease(KeyEvent event) {
	}

	@Override
	public NodePart getHost() {
		return (NodePart) super.getHost();
	}

	@Override
	public void initialPress(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if (KeyCode.E.equals(keyCode)) {
			ShowHiddenNeighborsPolicy hiddenNeighborsPolicy = getHost().getAdapter(ShowHiddenNeighborsPolicy.class);
			init(hiddenNeighborsPolicy);
			hiddenNeighborsPolicy.showHiddenNeighbors();
			commit(hiddenNeighborsPolicy);
		}
	}

	@Override
	public void press(KeyEvent event) {
	}

	@Override
	public void release(KeyEvent event) {
	}

}
