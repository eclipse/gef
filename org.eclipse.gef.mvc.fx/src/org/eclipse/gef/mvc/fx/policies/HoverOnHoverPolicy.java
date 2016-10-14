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
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.mvc.fx.models.HoverModel;

import javafx.scene.input.MouseEvent;

/**
 * The {@link HoverOnHoverPolicy} is an {@link IOnHoverPolicy} that hovers
 * its {@link #getHost() host} by altering the {@link HoverModel} when the
 * {@link #getHost() host} is hovered by the mouse.
 *
 * @author anyssen
 *
 */
public class HoverOnHoverPolicy extends AbstractInteractionPolicy
		implements IOnHoverPolicy {

	@Override
	public void hover(MouseEvent e) {
		// do nothing in case there is an explicit event target
		if (!isHover(e)) {
			return;
		}

		getHost().getRoot().getViewer().getAdapter(HoverModel.class)
				.setHover(getHost());
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * hover. Otherwise returns <code>false</code>. Per default, returns
	 * <code>true</code> if the mouse target is not registered in the visual
	 * part map or it is registered for this {@link HoverOnHoverPolicy}'s
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
