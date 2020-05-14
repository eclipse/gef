/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.models;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;

/**
 * The {@link FocusModel} stores the {@link IContentPart} which has keyboard
 * focus. Note that you are responsible for synchronizing keyboard focus with
 * the model.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FocusModel
		extends org.eclipse.gef.common.adapt.IAdaptable.Bound.Impl<IViewer>
		implements IDisposable {

	/**
	 * The {@link FocusModel} fires {@link PropertyChangeEvent}s when the
	 * focused part changes. This is the name of the property that is delivered
	 * with the event.
	 *
	 * @see #setFocus(IContentPart)
	 */
	final public static String FOCUS_PROPERTY = "focus";

	private ObjectProperty<IContentPart<? extends Node>> focusedProperty = new SimpleObjectProperty<>(
			this, FOCUS_PROPERTY);

	private MapChangeListener<Node, IVisualPart<? extends Node>> visualPartMapListener = new MapChangeListener<Node, IVisualPart<? extends Node>>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends Node, ? extends IVisualPart<? extends Node>> change) {
			// keep model in sync with part hierarchy
			if (change.wasRemoved()) {
				if (focusedProperty.get() == change.getValueRemoved()) {
					setFocus(null);
				}
			}
		}
	};

	/**
	 * Constructs a new {@link FocusModel}. The {@link #getFocus() focused}
	 * {@link IContentPart} is set to <code>null</code>.
	 */
	public FocusModel() {
	}

	/**
	 * @since 1.1
	 */
	@Override
	public void dispose() {
		// setAdaptable() already clears focus
	}

	/**
	 * Returns an object property providing the currently focused
	 * {@link IContentPart}.
	 *
	 * @return An object property named {@link #FOCUS_PROPERTY}.
	 */
	public ObjectProperty<IContentPart<? extends Node>> focusProperty() {
		return focusedProperty;
	}

	/**
	 * Returns the {@link IContentPart} which has keyboard focus, or
	 * <code>null</code> if no {@link IContentPart} currently has keyboard
	 * focus.
	 *
	 * @return the IContentPart which has keyboard focus, or <code>null</code>
	 */
	public IContentPart<? extends Node> getFocus() {
		return focusedProperty.get();
	}

	@Override
	public void setAdaptable(IViewer adaptable) {
		if (getAdaptable() != null) {
			// unregister visual-part-map listener
			getAdaptable().visualPartMapProperty()
					.removeListener(visualPartMapListener);
		}
		super.setAdaptable(adaptable);
		if (adaptable != null) {
			// register for visual-part-map changes
			adaptable.visualPartMapProperty()
					.addListener(visualPartMapListener);
		}
		// start with a clean FocusModel
		setFocus(null);
	}

	/**
	 * Selects the given IContentPart as the focus part. Note that setting the
	 * focus part does not assign keyboard focus to the part.
	 *
	 * @param focusPart
	 *            The {@link IContentPart} which should become the new focus
	 *            part.
	 */
	public void setFocus(IContentPart<? extends Node> focusPart) {
		focusedProperty.set(focusPart);
	}
}
