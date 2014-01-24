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
package org.eclipse.gef4.mvc.models;

import java.util.List;

import org.eclipse.gef4.mvc.IPropertyChangeSupport;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The {@link ISelectionModel} is used to store the current viewer's
 * {@link IContentPart} selection. A selection tool is used to update the
 * {@link ISelectionModel} as the result of input events.
 * 
 * @author anyssen
 * 
 */
public interface ISelectionModel<V> extends IPropertyChangeSupport {

	public static final String SELECTION_PROPERTY = "selection";

	public abstract void appendSelection(IContentPart<V> editpart);

	public abstract void deselect(IContentPart<V> editpart);

	public abstract void deselectAll();

	public abstract List<IContentPart<V>> getSelected();

	public abstract void select(IContentPart<V>... editparts);

}