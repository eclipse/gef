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

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The {@link IFocusModel} stores the {@link IContentPart} which has keyboard
 * focus. Note that you are responsible for synchronizing keyboard focus with
 * the model.
 * 
 * @author mwienand
 * @author anyssen
 * 
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 * 
 */
public interface IFocusModel<VR> extends IPropertyChangeNotifier {

	/**
	 * The {@link IFocusModel} fires {@link PropertyChangeEvent}s when the
	 * focused part changes. This is the name of the property that is delivered
	 * with the event.
	 * 
	 * @see #setFocused(IContentPart)
	 */
	final public static String FOCUS_PROPERTY = "Focus";

	/**
	 * The {@link IFocusModel} fires {@link PropertyChangeEvent}s when the
	 * viewer focused state changes. This is the name of the property that is
	 * delivered with the event.
	 * 
	 * @see #setViewerFocused(boolean)
	 */
	final public static String VIEWER_FOCUS_PROPERTY = "ViewerFocus";

	/**
	 * Returns the {@link IContentPart} which has keyboard focus, or
	 * <code>null</code> if no {@link IContentPart} currently has keyboard
	 * focus.
	 * 
	 * @return the IContentPart which has keyboard focus, or <code>null</code>
	 */
	public IContentPart<VR> getFocused();

	/**
	 * Returns <code>true</code> if the viewer where this model is registered
	 * currently has keyboard focus. Otherwise returns <code>false</code>.
	 * 
	 * @return <code>true</code> if the viewer where this model is registered
	 *         currently has keyboard focus. Otherwise returns
	 *         <code>false</code>.
	 */
	public boolean isViewerFocused();

	/**
	 * Selects the given IContentPart as the focus part. Note that setting the
	 * focus part does not assign keyboard focus to the part.
	 * 
	 * @param focusPart
	 *            The {@link IContentPart} which should become the new focus
	 *            part.
	 */
	public void setFocused(IContentPart<VR> focusPart);

	/**
	 * Updates the {@link #isViewerFocused()} property of this model.
	 * 
	 * @param viewerFocused
	 *            <code>true</code> to indicate that the viewer has keyboard
	 *            focus, or <code>false</code> to indicate that the viewer does
	 *            not have keyboard focus.
	 */
	public void setViewerFocused(boolean viewerFocused);

}
