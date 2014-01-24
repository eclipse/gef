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
package org.eclipse.gef4.mvc.tools;

import org.eclipse.gef4.mvc.domain.IEditDomain;

/**
 * 
 * @author anyssen
 * @author mwienand
 *
 * @param <V>
 */
public abstract class AbstractTool<V> implements ITool<V> {

	private IEditDomain<V> domain;
	private boolean isActive;

	@Override
	public void setDomain(IEditDomain<V> domain) {
		if (isActive) {
			throw new IllegalStateException(
					"The reference to the IEditDomain may not be changed while the tool is active. Please deactivate the tool before setting the IEditDomain and re-activate it afterwards.");
		}
		IEditDomain<V> oldDomain = getDomain();

		if (oldDomain != null && domain == null) {
			unregisterListeners();
		}

		this.domain = domain;

		if (oldDomain == null && domain != null) {
			registerListeners();
		}
	}

	/**
	 * This method is called when a valid {@link IEditDomain} is attached to
	 * this tool so that you can register event listeners for various inputs
	 * (keyboard, mouse) or model changes (selection, scroll offset / viewport).
	 */
	protected void registerListeners() {
	}

	/**
	 * This method is called when the attached {@link IEditDomain} is reset to
	 * <code>null</code> so that you can unregister previously registered event
	 * listeners.
	 */
	protected void unregisterListeners() {
	}

	@Override
	public IEditDomain<V> getDomain() {
		return domain;
	}

	@Override
	public void activate() {
		if (domain == null) {
			throw new IllegalStateException(
					"The IEditDomain has to be set via setDomain(IEditDomain) before activation.");
		}
		this.isActive = true;
	}

	@Override
	public void deactivate() {
		this.isActive = false;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

}
