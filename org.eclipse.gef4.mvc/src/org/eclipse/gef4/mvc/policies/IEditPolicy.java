/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.mvc.parts.IEditPart;

/**
 * A pluggable contribution implementing a portion of an EditPart's behavior.
 * EditPolicies contribute to the overall <i>editing behavior</i> of an
 * EditPart. Editing behavior is defined as one or more of the following:
 * <ul>
 * <li><b>Command Creation </b>- Returning a <code>Command</code> in response to
 * {@link #getCommand(IRequest)}
 * <li><b>Feedback Management</b> - Showing/erasing source and/or target
 * feedback in response to Requests.
 * <li><b>Delegation/Forwarding</b> - Collecting contributions from other
 * EditParts (and therefore their EditPolicies). In response to a given
 * <code>Request</code>, an EditPolicy may create a derived Request and forward
 * it to other EditParts. For example, during the deletion of a composite
 * EditPart, that composite may consult its children for contributions to the
 * delete command. Then, if the children have any additional work to do, they
 * will return additional comands to be executed.
 * </ul>
 * <P>
 * EditPolicies should determine an EditPart's editing capabilities. It is
 * possible to implement an EditPart such that it handles all editing
 * responsibility. However, it is much more flexible and object-oriented to use
 * EditPolicies. Using policies, you can pick and choose the editing behavior
 * for an EditPart without being bound to its class hierarchy. Code reuse is
 * increased, and code management is easier.
 * <p>
 * IMPORTANT: This interface is <EM>not</EM> intended to be implemented by
 * clients. Clients should inherit from
 * {@link org.eclipse.gef4.mvc.policies.AbstractEditPolicy}. New methods may be
 * added in the future.
 */

public interface IEditPolicy<V> {

	/**
	 * Activates this EditPolicy. The EditPolicy might need to hook listeners.
	 * These listeners should be unhooked in <code>deactivate()</code>. The
	 * EditPolicy might also contribute feedback/visuals immediately, such as
	 * <i>selection handles</i> if the EditPart was selected at the time of
	 * activation.
	 * <P>
	 * Activate is called after the <i>host</i> has been set, and that host has
	 * been activated.
	 * 
	 * @see IEditPart#activate()
	 * @see #deactivate()
	 * @see IEditPart#installEditPolicy(Object, EditPolicy)
	 */
	void activate();

	/**
	 * Deactivates the EditPolicy, the inverse of {@link #activate()}.
	 * Deactivate is called when the <i>host</i> is deactivated, or when the
	 * EditPolicy is uninstalled from an active host. Deactivate unhooks any
	 * listeners, and removes all feedback.
	 * 
	 * @see IEditPart#deactivate()
	 * @see #activate()
	 * @see IEditPart#removeEditPolicy(Object)
	 */
	void deactivate();

	/**
	 * @return the <i>host</i> EditPart on which this policy is installed.
	 */
	IEditPart<V> getHost();

	/**
	 * Sets the host in which this EditPolicy is installed.
	 * 
	 * @param editpart
	 *            the host EditPart
	 */
	void setHost(IEditPart<V> editpart);

}
