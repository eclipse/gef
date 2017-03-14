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
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.activate.ActivatableSupport;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.policies.IPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * The {@link AbstractGesture} can be used as a base class for {@link IGesture}
 * implementations.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public abstract class AbstractGesture implements IGesture {

	private ActivatableSupport acs = new ActivatableSupport(this);
	private ReadOnlyObjectWrapper<IDomain> domainProperty = new ReadOnlyObjectWrapper<>();
	private Map<IViewer, List<IPolicy>> activePolicies = new IdentityHashMap<>();

	@Override
	public final void activate() {
		acs.activate(null, this::doActivate);
	}

	@Override
	public final ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	@Override
	public ReadOnlyObjectProperty<IDomain> adaptableProperty() {
		return domainProperty.getReadOnlyProperty();
	}

	/**
	 * Clears the list of active policies of this tool for the given viewer.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to clear the active policies of
	 *            this tool.
	 * @see #getActivePolicies(IViewer)
	 * @see #setActivePolicies(IViewer, Collection)
	 */
	protected void clearActivePolicies(IViewer viewer) {
		if (viewer == null) {
			throw new IllegalArgumentException(
					"The given viewer may not be null.");
		}
		activePolicies.remove(viewer);
	}

	@Override
	public final void deactivate() {
		acs.deactivate(this::doDeactivate, null);
	}

	/**
	 * This method is called when a valid {@link IDomain} is attached to this
	 * tool so that you can register event listeners for various inputs
	 * (keyboard, mouse) or model changes (selection, scroll offset / viewport).
	 */
	protected void doActivate() {
	}

	/**
	 * This method is called when the attached {@link IDomain} is reset to
	 * <code>null</code> so that you can unregister previously registered event
	 * listeners.
	 */
	protected void doDeactivate() {
	}

	@Override
	public List<? extends IPolicy> getActivePolicies(IViewer viewer) {
		if (activePolicies.containsKey(viewer)) {
			return Collections.unmodifiableList(activePolicies.get(viewer));
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public IDomain getAdaptable() {
		return domainProperty.get();
	}

	@Override
	public IDomain getDomain() {
		return getAdaptable();
	}

	/**
	 * Returns the {@link ITargetPolicyResolver} of the {@link IDomain}.
	 *
	 * @return the {@link ITargetPolicyResolver} of the {@link IDomain}.
	 */
	protected ITargetPolicyResolver getTargetPolicyResolver() {
		return getDomain().getAdapter(ITargetPolicyResolver.class);
	}

	@Override
	public final boolean isActive() {
		return acs.isActive();
	}

	/**
	 * Set the active policies of this tool to the given policies.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to store the active policies of
	 *            this tool.
	 * @param activePolicies
	 *            The active policies of this tool.
	 * @see #clearActivePolicies(IViewer)
	 * @see #getActivePolicies(IViewer)
	 */
	protected void setActivePolicies(IViewer viewer,
			Collection<? extends IPolicy> activePolicies) {
		if (viewer == null) {
			throw new IllegalArgumentException(
					"The given viewer may not be null.");
		}
		if (activePolicies == null) {
			throw new IllegalArgumentException(
					"The given activePolicies may not be null.");
		}
		for (IPolicy ap : activePolicies) {
			if (ap.getHost().getViewer() != viewer) {
				throw new IllegalArgumentException(
						"Resolved policy is not hosted within viewer.");
			}
		}
		clearActivePolicies(viewer);
		this.activePolicies.put(viewer, new ArrayList<>(activePolicies));
	}

	@Override
	public void setAdaptable(IDomain adaptable) {
		if (isActive()) {
			throw new IllegalStateException(
					"The reference to the IDomain may not be changed while the tool is active. Please deactivate the tool before setting the IEditDomain and re-activate it afterwards.");
		}
		domainProperty.set(adaptable);
	}

}
