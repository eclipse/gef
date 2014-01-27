/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef4.mvc.IPropertyChangeSupport;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The IFocusModel stores the IContentPart which has keyboard focus. Note that
 * you are responsible for synchronizing keyboard focus with the model.
 * 
 * @param <V> type of visual
 */
public interface IFocusModel<V> extends IPropertyChangeSupport {
	
	/**
	 * The {@link IFocusModel} fires {@link PropertyChangeEvent}s when the
	 * focused part changes. This is the name of the property that is delivered
	 * with the event.
	 */
	final public static String FOCUS_PROPERTY = "Focus";
	
	/**
	 * Returns the IContentPart which has keyboard focus, or <code>null</code>
	 * if no IContentPart currently has keyboard focus.
	 * 
	 * @return the IContentPart which has keyboard focus, or <code>null</code>
	 */
	public IContentPart<V> getFocused();

	/**
	 * Selects the given IContentPart as the focus part. Note that setting the
	 * focus part does not assign keyboard focus to the part.
	 * 
	 * @param focusPart
	 */
	public void setFocused(IContentPart<V> focusPart);

}
