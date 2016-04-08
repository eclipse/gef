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
 * Note: Parts of this class have been transferred from org.eclipse.gef.editpolicies.AbstractEditPolicy.
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Abstract base implementation of {@link IPolicy}.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractPolicy<VR> implements IPolicy<VR> {

	private ReadOnlyObjectWrapper<IVisualPart<VR, ? extends VR>> hostProperty = new ReadOnlyObjectWrapper<>();

	@Override
	public ReadOnlyObjectProperty<IVisualPart<VR, ? extends VR>> adaptableProperty() {
		return hostProperty.getReadOnlyProperty();
	}

	@Override
	public IVisualPart<VR, ? extends VR> getAdaptable() {
		return getHost();
	}

	@Override
	public IVisualPart<VR, ? extends VR> getHost() {
		return hostProperty.get();
	}

	@Override
	public void setAdaptable(IVisualPart<VR, ? extends VR> adaptable) {
		this.hostProperty.set(adaptable);
	}

}