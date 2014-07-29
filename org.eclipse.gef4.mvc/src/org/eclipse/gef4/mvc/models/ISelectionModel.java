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

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The {@link ISelectionModel} is used to store the current viewer's
 * {@link IContentPart} selection. A selection tool is used to update the
 * {@link ISelectionModel} as the result of input events.
 * 
 * @author anyssen
 * 
 * @param <VR> The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 * 
 */
public interface ISelectionModel<VR> extends IPropertyChangeNotifier {

	public static final String SELECTION_PROPERTY = "selection";

	public abstract void appendSelection(IContentPart<VR> editpart);

	public abstract void deselect(IContentPart<VR> editpart);

	public abstract void deselectAll();

	/**
	 * Returns an unmodifiable {@link List} of the selected {@link IContentPart}s.
	 * 
	 * @return An unmodifiable {@link List} of the selected {@link IContentPart}s.
	 */
	public abstract List<IContentPart<VR>> getSelected();

	public abstract void select(List<IContentPart<VR>> editparts);

}