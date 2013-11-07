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


/**
 * The listener interface for receiving basic events from an EditPart. Listeners
 * interested in only one type of Event can extend the
 * {@link IEditPartListener.Stub} implementation rather than implementing the
 * entire interface.
 */

public interface IEditPartListener {

	/**
	 * Listeners interested in just a subset of Events can extend this stub
	 * implementation. Also, extending the Stub will reduce the impact of new
	 * API on this interface.
	 */
	public class Stub implements IEditPartListener {
		/**
		 * @see org.eclipse.gef4.mvc.parts.IEditPartListener#childAdded(IEditPart, int)
		 */
		public void childAdded(IEditPart child, int index) {
		}

		/**
		 * @see org.eclipse.gef4.mvc.parts.IEditPartListener#partActivated(IEditPart)
		 */
		public void partActivated(IEditPart editpart) {
		}

		/**
		 * @see org.eclipse.gef4.mvc.parts.IEditPartListener#partDeactivated(IEditPart)
		 */
		public void partDeactivated(IEditPart editpart) {
		}

		/**
		 * @see org.eclipse.gef4.mvc.parts.IEditPartListener#removingChild(IEditPart, int)
		 */
		public void removingChild(IEditPart child, int index) {
		}

		/**
		 * @see org.eclipse.gef4.mvc.parts.IEditPartListener#selectedStateChanged(IEditPart)
		 */
		public void selectedStateChanged(IEditPart part) {
		}
	};

	// TODO: replace with observable lists
	/**
	 * Called after a child EditPart has been added to its parent.
	 * 
	 * @param child
	 *            the Child
	 * @param index
	 *            the index at which the child was added
	 */
	void childAdded(IEditPart child, int index);

	/**
	 * Called when the editpart has been activated.
	 * 
	 * @param editpart
	 *            the EditPart
	 */
	void partActivated(IEditPart editpart);

	/**
	 * Called when the editpart has been deactivated.
	 * 
	 * @param editpart
	 *            the EditPart
	 */
	void partDeactivated(IEditPart editpart);

	/**
	 * Called before a child EditPart is removed from its parent.
	 * 
	 * @param child
	 *            the Child being removed
	 * @param index
	 *            the child's current location
	 */
	void removingChild(IEditPart child, int index);

}
