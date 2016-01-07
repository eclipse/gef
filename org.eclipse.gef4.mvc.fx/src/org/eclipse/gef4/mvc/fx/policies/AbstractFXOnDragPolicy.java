/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #481810
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractInteractionPolicy;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * An {@link AbstractFXOnDragPolicy} is called upon mouse drag events by the
 * {@link FXClickDragTool}. You can use it as an adapter on any
 * {@link IVisualPart} for which mouse drag interaction is desired, and you can
 * also register multiple instances of {@link AbstractFXOnDragPolicy} on the
 * same {@link IVisualPart} (with different adapter roles).
 *
 * @author anyssen
 *
 */
public abstract class AbstractFXOnDragPolicy
		extends AbstractInteractionPolicy<Node>implements IFXOnDragPolicy {

	private CursorSupport cursorSupport = new CursorSupport(this);

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		return cursorSupport;
	}

	@Override
	public void hideIndicationCursor() {
		getCursorSupport().restoreCursor();
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

}
