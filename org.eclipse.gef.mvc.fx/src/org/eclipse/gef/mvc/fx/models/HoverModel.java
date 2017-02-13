/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import javafx.scene.Node;

/**
 * The {@link HoverModel} is used to store the current viewer's mouse hover
 * target, i.e. the {@link IVisualPart} that is currently under the mouse
 * cursor.
 *
 * @author mwienand
 *
 */
public class HoverModel
		extends org.eclipse.gef.common.adapt.IAdaptable.Bound.Impl<IViewer>
		implements IDisposable {

	/**
	 * The {@link HoverModel} fires {@link PropertyChangeEvent}s when the
	 * hovered part changes. This is the name of the property that is delivered
	 * with the event.
	 */
	final public static String HOVER_PROPERTY = "hover";

	private ObjectProperty<IVisualPart<? extends Node>> hoverProperty = new SimpleObjectProperty<>(
			this, HOVER_PROPERTY);

	/**
	 * Sets the hovered part to <code>null</code>.
	 * <p>
	 * Fires a {@link PropertyChangeEvent}.
	 */
	public void clearHover() {
		setHover(null);
	}

	/**
	 * @since 1.1
	 */
	@Override
	public void dispose() {
		hoverProperty.set(null);
	}

	/**
	 * Returns the currently hovered {@link IContentPart} or <code>null</code>
	 * if no visual part is hovered.
	 *
	 * @return the currently hovered {@link IContentPart} or <code>null</code>
	 */
	public IVisualPart<? extends Node> getHover() {
		return hoverProperty.get();
	}

	/**
	 * Returns an object property representing the current hover part.
	 *
	 * @return A property named {@link #HOVER_PROPERTY}.
	 */
	public ObjectProperty<IVisualPart<? extends Node>> hoverProperty() {
		return hoverProperty;
	}

	@Override
	public void setAdaptable(IViewer adaptable) {
		// The viewer can only be changed when there are no parts in this model.
		// Otherwise, the model was/is inconsistent.
		if (getAdaptable() != adaptable) {
			if (hoverProperty.get() != null) {
				throw new IllegalStateException(
						"Inconsistent HoverModel: IVisualPart present although the IViewer is changed.");
			}
		}
		super.setAdaptable(adaptable);
	}

	/**
	 * Sets the hovered {@link IVisualPart} to the given value. The given part
	 * may be <code>null</code> in order to unhover.
	 * <p>
	 * Fires a {@link PropertyChangeEvent}.
	 *
	 * @param cp
	 *            hovered {@link IVisualPart} or <code>null</code>
	 */
	public void setHover(IVisualPart<? extends Node> cp) {
		hoverProperty.set(cp);
	}
}
