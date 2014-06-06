/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.domain;

public interface IDomainBound<VR> {

	/**
	 * Returns the {@link IDomain} this {@link IDomainBound} is bound to.
	 * 
	 * @return The {@link IDomain} this {@link IDomainBound} is bound to, or
	 *         <code>null</code> if this {@link IDomainBound} is not (yet) bound
	 *         to an {@link IDomain}.
	 */
	public abstract IDomain<VR> getDomain();

	/**
	 * Called to set/change/unset the {@link IDomain} this {@link IDomainBound}
	 * is bound to. To set or change the {@link IDomain}, pass in a valid
	 * {@link IDomain}, to unset it, pass in <code>null</code>.
	 * 
	 * @param domain
	 *            The {@link IDomain} to which this {@link IDomainBound} is
	 *            bound to
	 */
	public abstract void setDomain(IDomain<VR> domain);

}