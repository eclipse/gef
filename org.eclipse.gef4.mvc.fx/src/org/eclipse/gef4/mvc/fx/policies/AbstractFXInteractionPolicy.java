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
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.mvc.fx.tools.AbstractFXTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractInteractionPolicy;

import javafx.event.EventTarget;
import javafx.scene.Node;

/**
 * The {@link AbstractFXInteractionPolicy} extends the
 * {@link AbstractInteractionPolicy} and binds its visual root parameter to
 * {@link Node}. It provides two convenience methods that can be used to guard
 * interaction policies from processing events that are intended to be processed
 * by other policies. This is necessary because the {@link AbstractFXTool}
 * iterates the entire visual part hierarchy of the visual that receives the
 * input event and sends the event to all suitable policies on the way.
 * <ul>
 * <li>{@link #isRegistered(EventTarget)}
 * <li>{@link #isRegisteredForHost(EventTarget)}
 * </ul>
 * For example, if a policy should only process events if its host is the
 * explicit event target, the following guard can be implemented within the
 * policy's callback methods (example for an {@link IFXOnHoverPolicy}):
 *
 * <pre>
 * public void hover(MouseEvent e) {
 * 	// do nothing in case there is an explicit event target
 * 	if (isRegistered(e.getTarget()) &amp;&amp; !isRegisteredForHost(e.getTarget())) {
 * 		return;
 * 	}
 * 	// ...
 * }
 * </pre>
 *
 * @author mwienand
 *
 */
public class AbstractFXInteractionPolicy
		extends AbstractInteractionPolicy<Node> {

	/**
	 * Returns <code>true</code> if the given {@link EventTarget} is registered
	 * in the visual-part-map. Otherwise returns <code>false</code>.
	 *
	 * @param eventTarget
	 *            The {@link EventTarget} that is tested.
	 * @return <code>true</code> if the given {@link EventTarget} is registered
	 *         in the visual-part-map, otherwise <code>false</code>.
	 */
	protected boolean isRegistered(EventTarget eventTarget) {
		IVisualPart<Node, ? extends Node> host = getHost();
		IVisualPart<Node, ? extends Node> targetPart = host.getRoot()
				.getViewer().getVisualPartMap().get(eventTarget);
		return targetPart != null;
	}

	/**
	 * Returns <code>true</code> if the given {@link EventTarget} is registered
	 * in the visual-part-map for the {@link #getHost() host} of this
	 * {@link AbstractInteractionPolicy}. Otherwise returns <code>false</code>.
	 *
	 * @param eventTarget
	 *            The {@link EventTarget} that is tested.
	 * @return <code>true</code> if the given {@link EventTarget} is registered
	 *         in the visual-part-map for the host of this policy, otherwise
	 *         <code>false</code>.
	 */
	protected boolean isRegisteredForHost(EventTarget eventTarget) {
		IVisualPart<Node, ? extends Node> host = getHost();
		IVisualPart<Node, ? extends Node> targetPart = host.getRoot()
				.getViewer().getVisualPartMap().get(eventTarget);
		return targetPart == host;
	}

}
