/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.mvc.parts.IContentPart;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link FXTraverseFocusOnTypePolicy} implements focus traversal via
 * keyboard input.
 *
 * @author mwienand
 *
 */
public class FXTraverseFocusOnTypePolicy extends AbstractFXInteractionPolicy
		implements IFXOnPressPolicy {

	@Override
	public void abortPress() {
	}

	@Override
	public void finalRelease(KeyEvent event) {
	}

	@Override
	public void initialPress(KeyEvent event) {
		// discard keystrokes other than TAB
		if (!isTraverse(event)) {
			return;
		}
		// get traversal policy
		FXFocusTraversalPolicy focusTraversalPolicy = getHost()
				.getAdapter(FXFocusTraversalPolicy.class);
		if (focusTraversalPolicy == null) {
			throw new IllegalStateException(
					"Cannot find <FXFocusTraversalPolicy> for host <"
							+ getHost() + ">.");
		}
		// perform focus traversal
		init(focusTraversalPolicy);
		IContentPart<Node, ? extends Node> focused = event.isShiftDown()
				? focusTraversalPolicy.focusPrevious()
				: focusTraversalPolicy.focusNext();
		// reveal the newly focused part
		if (focused != null) {
			focused.getRoot().getViewer().reveal(focused);
		}
		// execute on stack
		commit(focusTraversalPolicy);
	}

	/**
	 * Returns <code>true</code> if the given {@link KeyEvent} should trigger
	 * focus traversal. Otherwise returns <code>false</code>. Per default
	 * returns <code>true</code> if <code>&lt;Tab&gt;</code> is pressed..
	 *
	 * @param event
	 *            The {@link KeyEvent} in question.
	 * @return <code>true</code> if the given {@link KeyEvent} should trigger
	 *         focus traversal, otherwise <code>false</code>.
	 */
	protected boolean isTraverse(KeyEvent event) {
		return KeyCode.TAB.equals(event.getCode());
	}

	@Override
	public void press(KeyEvent event) {
	}

	@Override
	public void release(KeyEvent event) {
	}

}
