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
package org.eclipse.gef.mvc.behaviors;

import org.eclipse.gef.common.activate.ActivatableSupport;
import org.eclipse.gef.mvc.parts.IVisualPart;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * The {@link AbstractBehavior} can be used as a base class for
 * {@link IBehavior} implementations. It implements activation and deactivation
 * of its adapters, and provides methods for the addition and removal of
 * feedback and handles, as well as a method that can be used to update the
 * handles for a given target part.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractBehavior<VR> implements IBehavior<VR> {

	private ReadOnlyObjectWrapper<IVisualPart<VR, ? extends VR>> hostProperty = new ReadOnlyObjectWrapper<>();
	private ActivatableSupport acs = new ActivatableSupport(this);

	@Override
	public final void activate() {
		if (!acs.isActive()) {
			acs.activate();
			doActivate();
		}
	}

	@Override
	public ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	@Override
	public ReadOnlyObjectProperty<IVisualPart<VR, ? extends VR>> adaptableProperty() {
		return hostProperty.getReadOnlyProperty();
	}

	@Override
	public final void deactivate() {
		if (acs.isActive()) {
			doDeactivate();
			acs.deactivate();
		}
	}

	/**
	 * Post {@link #activate()} hook that may be overwritten to e.g. register
	 * listeners.
	 */
	protected void doActivate() {
		// nothing to do by default
	}

	/**
	 * Pre {@link #deactivate()} hook that may be overwritten to e.g. unregister
	 * listeners.
	 */
	protected void doDeactivate() {
		// nothing to do by default
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
	public boolean isActive() {
		return acs.isActive();
	}

	@Override
	public void setAdaptable(IVisualPart<VR, ? extends VR> adaptable) {
		this.hostProperty.set(adaptable);
	}

}