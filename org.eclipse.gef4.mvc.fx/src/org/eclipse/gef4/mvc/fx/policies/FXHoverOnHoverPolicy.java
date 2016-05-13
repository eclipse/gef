/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.mvc.models.HoverModel;

import com.google.common.reflect.TypeToken;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXHoverOnHoverPolicy} is an {@link IFXOnHoverPolicy} that hovers
 * its {@link #getHost() host} by altering the {@link HoverModel} when the
 * {@link #getHost() host} is hovered by the mouse.
 *
 * @author anyssen
 *
 */
public class FXHoverOnHoverPolicy extends AbstractFXInteractionPolicy
		implements IFXOnHoverPolicy {

	@SuppressWarnings("serial")
	@Override
	public void hover(MouseEvent e) {
		// do nothing in case there is an explicit event target
		if (!isHover(e)) {
			return;
		}

		getHost().getRoot().getViewer()
				.getAdapter(new TypeToken<HoverModel<Node>>() {
				}).setHover(getHost());
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * hover. Otherwise returns <code>false</code>. Per default, returns
	 * <code>true</code> if the mouse target is not registered in the visual
	 * part map or it is registered for this {@link FXHoverOnHoverPolicy}'s
	 * host.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> to indicate that the given {@link MouseEvent}
	 *         should trigger hover, otherwise <code>false</code>.
	 */
	protected boolean isHover(MouseEvent event) {
		return !isRegistered(event.getTarget())
				|| isRegisteredForHost(event.getTarget());
	}

}
