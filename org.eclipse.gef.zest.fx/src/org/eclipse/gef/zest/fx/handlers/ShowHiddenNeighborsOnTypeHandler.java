/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.handlers;

import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnStrokeHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnTypeHandler;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.gef.zest.fx.policies.ShowHiddenNeighborsPolicy;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link ShowHiddenNeighborsOnTypeHandler} is an {@link IOnTypeHandler}
 * that shows all hidden neighbors of its host upon {@link KeyCode#E} key press.
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighborsOnTypeHandler extends AbstractHandler implements IOnStrokeHandler {

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
