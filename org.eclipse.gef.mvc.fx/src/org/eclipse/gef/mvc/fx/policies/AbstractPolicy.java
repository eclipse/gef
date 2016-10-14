/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;

/**
 * Abstract base implementation of {@link IPolicy}.
 *
 * @author anyssen
 *
 */
public abstract class AbstractPolicy implements IPolicy {

	private ReadOnlyObjectWrapper<IVisualPart<? extends Node>> hostProperty = new ReadOnlyObjectWrapper<>();

	@Override
	public ReadOnlyObjectProperty<IVisualPart<? extends Node>> adaptableProperty() {
		return hostProperty.getReadOnlyProperty();
	}

	@Override
	public IVisualPart<? extends Node> getAdaptable() {
		return getHost();
	}

	@Override
	public IVisualPart<? extends Node> getHost() {
		return hostProperty.get();
	}

	@Override
	public void setAdaptable(IVisualPart<? extends Node> adaptable) {
		this.hostProperty.set(adaptable);
	}

}