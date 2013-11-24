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

import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The default implementation of {@link IEditPolicy}.
 * <P>
 * Since this is the default implementation of an interface, this document deals
 * with proper sub-classing. This class is not the API. For documentation on
 * proper usage of the public API, see the documentation for the interface
 * itself: {@link IEditPolicy}.
 */
public abstract class AbstractEditPolicy<V> implements IEditPolicy<V> {

	private IVisualPart<V> host;

	/**
	 * Does nothing by default.
	 * 
	 * @see org.eclipse.gef4.mvc.policies.IEditPolicy#activate()
	 */
	public void activate() {
	}

	/**
	 * Does nothing by default.
	 * 
	 * @see org.eclipse.gef4.mvc.policies.IEditPolicy#deactivate()
	 */
	public void deactivate() {
	}

	/**
	 * @see org.eclipse.gef4.mvc.policies.IEditPolicy#getHost()
	 */
	public IVisualPart<V> getHost() {
		return host;
	}

	/**
	 * @see org.eclipse.gef4.mvc.policies.IEditPolicy#setHost(IEditPart)
	 */
	public void setHost(IVisualPart<V> host) {
		this.host = host;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String c = getClass().getName();
		c = c.substring(c.lastIndexOf('.') + 1);
		if (getHost() != null) {
			return getHost().toString() + "." + c; //$NON-NLS-1$
		} else {
			return c + " (no host for EditPolicy set yet)"; //$NON-NLS-1$
		}
	}

}