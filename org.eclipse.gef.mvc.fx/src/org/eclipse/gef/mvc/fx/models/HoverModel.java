/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
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
	 * This is the name of the property that stores the currently hovered
	 * {@link IVisualPart}.
	 */
	final public static String HOVER_PROPERTY = "hover";

	/**
	 * This property stores the intended hovered
	 */
	// TODO: Pick the best name: deliberate, intentional, intended, wanted, etc.
	final public static String LINGERING_HOVER_PROPERTY = "intentionalHover";

	private ObjectProperty<IVisualPart<? extends Node>> hoverProperty = new SimpleObjectProperty<>(
			this, HOVER_PROPERTY);

	// TODO: Pick the best name: deliberate, intentional, intended, wanted, etc.
	private ObjectProperty<IContentPart<? extends Node>> lingeringHoverProperty = new SimpleObjectProperty<>(
			this, LINGERING_HOVER_PROPERTY);

	/**
	 * Sets the hovered part to <code>null</code>.
	 */
	public void clearHover() {
		setHover(null);
	}

	/**
	 * Sets the intentionally hovered part to <code>null</code>.
	 */
	public void clearIntentionalHover() {
		setLingeringHover(null);
	}

	/**
	 * @since 1.1
	 */
	@Override
	public void dispose() {
		clearHover();
		clearIntentionalHover();
	}

	/**
	 * Returns the currently hovered {@link IVisualPart} or <code>null</code> if
	 * no visual part is hovered.
	 *
	 * @return the currently hovered {@link IVisualPart} or <code>null</code>
	 */
	public IVisualPart<? extends Node> getHover() {
		return hoverProperty.get();
	}

	/**
	 * Returns the currently lingering hovered {@link IContentPart} or
	 * <code>null</code> if no content part is intentionally hovered.
	 *
	 * @return the currently lingering hovered {@link IContentPart} or
	 *         <code>null</code>
	 */
	public IContentPart<? extends Node> getLingeringHover() {
		return lingeringHoverProperty.get();
	}

	/**
	 * Returns an object property representing the current hover part.
	 *
	 * @return A property named {@link #HOVER_PROPERTY}.
	 */
	public ObjectProperty<IVisualPart<? extends Node>> hoverProperty() {
		return hoverProperty;
	}

	/**
	 * Returns an object property representing the lingering hovered part.
	 *
	 * @return A property named {@link #LINGERING_HOVER_PROPERTY}.
	 */
	public ObjectProperty<IContentPart<? extends Node>> lingeringHoverProperty() {
		return lingeringHoverProperty;
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
	 *
	 * @param cp
	 *            hovered {@link IVisualPart} or <code>null</code>
	 */
	public void setHover(IVisualPart<? extends Node> cp) {
		hoverProperty.set(cp);
	}

	/**
	 * Sets the lingering hovered {@link IContentPart} to the given value. The
	 * given part may be <code>null</code> to indicate unhovering.
	 *
	 * @param cp
	 *            The lingering hovered {@link IContentPart} or
	 *            <code>null</code>.
	 */
	public void setLingeringHover(IContentPart<? extends Node> cp) {
		lingeringHoverProperty.set(cp);
	}
}
