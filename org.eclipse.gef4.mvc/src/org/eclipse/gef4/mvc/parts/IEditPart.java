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
package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef4.mvc.commands.AbstractCommand;
import org.eclipse.gef4.mvc.policies.IEditPolicy;

/**
 * EditParts are the building blocks of GEF Viewers. As the <I>Controller</I>,
 * an EditPart ties the application's model to a visual representation.
 * EditParts are responsible for making changes to the model. EditParts
 * typically control a single model object or a coupled set of object. Visual
 * representations include {@link org.eclipse.draw2d.IFigure Figures} and
 * {@link org.eclipse.swt.widgets.TreeItem TreeItems}. Model objects are often
 * composed of other objects that the User will interact with. Similarly,
 * EditParts can be composed of or have references to other EditParts.
 * <P>
 * The creator of an EditPart should call only setModel(Object). The remaining
 * API is used mostly by Tools, EditPolicies, and other EditParts. CHANGES are
 * made to the model, not the EditPart.
 * <P>
 * Most interaction with EditParts is achieved using {@link IRequest Requests}.
 * A Request specifies the type of interaction. Requests are used in
 * {@link #getTargetEditPart(IRequest) targeting}, filtering the selection
 * (using {@link #understandsRequest(IRequest)}), graphical
 * {@link #showSourceFeedback(IRequest)} feedback, and most importantly,
 * {@link #getCommand(IRequest) obtaining} commands. Only {@link AbstractCommand
 * Commands} should change the model.
 * <p>
 * IMPORTANT: This interface is <EM>not</EM> intended to be implemented by
 * clients. Clients should inherit from
 * {@link org.eclipse.gef4.mvc.parts.AbstractEditPart}. New methods may be added
 * in the future.
 */
public interface IEditPart<V> extends IAdaptable {

	/**
	 * Activates the EditPart. EditParts that observe a dynamic model or support
	 * editing must be <i>active</i>. Called by the managing EditPart, or the
	 * Viewer in the case of the {@link IRootEditPart}. This method may be
	 * called again once {@link #deactivate()} has been called.
	 * <P>
	 * During activation the receiver should:
	 * <UL>
	 * <LI>begin to observe its model if appropriate, and should continue the
	 * observation until {@link #deactivate()} is called.
	 * <LI>activate all of its EditPolicies. EditPolicies may also observe the
	 * model, although this is rare. But it is common for EditPolicies to
	 * contribute additional visuals, such as selection handles or feedback
	 * during interactions. Therefore it is necessary to tell the EditPolicies
	 * when to start doing this, and when to stop.
	 * <LI>call activate() on the EditParts it manages. This includes its
	 * children, and for GraphicalEditParts, its <i>source connections</i>.
	 * </UL>
	 */
	void activate();

	/**
	 * Adds a listener to the EditPart. Duplicate calls result in duplicate
	 * notification.
	 * 
	 * @param listener
	 *            the Listener
	 */
	void addEditPartListener(IEditPartListener listener);

	/**
	 * Deactivates the EditPart. EditParts that observe a dynamic model or
	 * support editing must be <i>active</i>. <code>deactivate()</code> is
	 * guaranteed to be called when an EditPart will no longer be used. Called
	 * by the managing EditPart, or the Viewer in the case of the
	 * {@link IRootEditPart}. This method may be called multiple times.
	 * <P>
	 * During deactivation the receiver should:
	 * <UL>
	 * <LI>remove all listeners that were added in {@link #activate}
	 * <LI>deactivate all of its EditPolicies. EditPolicies may be contributing
	 * additional visuals, such as selection handles or feedback during
	 * interactions. Therefore it is necessary to tell the EditPolicies when to
	 * start doing this, and when to stop.
	 * <LI>call deactivate() on the EditParts it manages. This includes its
	 * children, and for <code>GraphicalEditParts</code>, its <i>source
	 * connections</i>.
	 * </UL>
	 */
	void deactivate();
	
	List<IContentsEditPart<V>> getChildren();
	
	void synchronizeChildren();

	/**
	 * @param key
	 *            the key identifying the EditPolicy
	 * @return <code>null</code> or the EditPolicy installed with the given key
	 */
	<P extends IEditPolicy<V>> P getEditPolicy(Class<P> key);

	/**
	 * Returns the {@link IRootEditPart}. This method should only be called
	 * internally or by helpers such as edit policies. The root can be used to
	 * get the viewer.
	 * 
	 * @return <code>null</code> or the {@link IRootEditPart}
	 */
	IRootEditPart<V> getRoot();
	
	/**
	 * Installs an EditPolicy for a specified <i>role</i>. A <i>role</i> is is
	 * simply an Object used to identify the EditPolicy. An example of a role is
	 * layout. {@link IEditPolicy#LAYOUT_ROLE} is generally used as the key for
	 * this EditPolicy. <code>null</code> is a valid value for reserving a
	 * location.
	 * 
	 * @param role
	 *            an identifier used to key the EditPolicy
	 * @param editPolicy
	 *            the EditPolicy
	 */
	<P extends IEditPolicy<V>> void installEditPolicy(Class<P> key, P editPolicy);
	
	V getVisual();
	
	void refreshVisual();

	/**
	 * Removes the first occurrence of the specified listener from the list of
	 * listeners. Does nothing if the listener was not present.
	 * 
	 * @param listener
	 *            the listener being removed
	 */
	void removeEditPartListener(IEditPartListener listener);

	/**
	 * Removes the EditPolicy for the given <i>role</i>. The EditPolicy is
	 * deactivated if it is active. The position for that role is maintained
	 * with <code>null</code> in the place of the old EditPolicy.
	 * 
	 * @param role
	 *            the key identifying the EditPolicy to be removed
	 * @see #installEditPolicy(Object, IEditPolicy)
	 */
	<P extends IEditPolicy<V>> void uninstallEditPolicy(Class<P> key);

}
