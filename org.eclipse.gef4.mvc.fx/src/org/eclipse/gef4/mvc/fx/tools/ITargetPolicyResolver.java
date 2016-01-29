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
package org.eclipse.gef4.mvc.fx.tools;

import java.util.List;

import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.scene.Node;

/**
 * The {@link ITargetPolicyResolver} provides a mechanism to determine and
 * prioritize all policies that are to be notified about certain input events
 * (see {@link #getTargetPolicies(ITool, IViewer, Node, Class)} for details).
 *
 * @author mwienand
 *
 */
public interface ITargetPolicyResolver {

	/**
	 * Determines and prioritizes all policies of the specified type for the
	 * given {@link IViewer} and target {@link Node} that are to be notified
	 * about an input event that was directed at the {@link Node}.
	 *
	 * @param <T>
	 *            Type parameter specifying the type of policy that is
	 *            collected.
	 * @param contextTool
	 *            The {@link ITool} for which to determine target policies.
	 * @param viewer
	 *            The {@link IViewer} that contains the given {@link Node}.
	 * @param target
	 *            The target {@link Node} that received an input event.
	 * @param policyClass
	 *            The type of the policies to return.
	 * @return All matching policies within the hierarchy from the root part to
	 *         the target part.
	 */
	public <T extends IPolicy<Node>> List<? extends T> getTargetPolicies(
			ITool<Node> contextTool, IViewer<Node> viewer, Node target,
			Class<T> policyClass);

}
