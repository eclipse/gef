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

import org.eclipse.gef.mvc.fx.policies.IFXOnTypePolicy;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.zest.fx.parts.NodePart;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link HideOnTypePolicy} is an {@link IFXOnTypePolicy} that hides its
 * {@link #getHost() host} {@link NodePart} upon the press of a
 * {@link KeyCode#P} key.
 *
 * @author mwienand
 *
 */
public class HideOnTypePolicy extends AbstractInteractionPolicy<Node> implements IFXOnTypePolicy {

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
		if (KeyCode.P.equals(keyCode)) {
			HidePolicy hidePolicy = getHost().<HidePolicy>getAdapter(HidePolicy.class);
			init(hidePolicy);
			hidePolicy.hide();
			commit(hidePolicy);
		}
	}

	@Override
	public void press(KeyEvent event) {
	}

	@Override
	public void release(KeyEvent event) {
	}

}
