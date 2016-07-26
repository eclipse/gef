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
 * Note: Parts of this class have been transferred from org.eclipse.gef.ui.parts.AbstractEditPartViewer.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.viewer;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.gef.common.activate.ActivatableSupport;
import org.eclipse.gef.common.adapt.AdaptableSupport;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.adapt.inject.AdaptableScope;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.InjectAdapters;
import org.eclipse.gef.mvc.behaviors.ContentPartPool;
import org.eclipse.gef.mvc.domain.IDomain;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableMap;

/**
 * The {@link AbstractViewer} can be used as a base class for {@link IViewer}
 * implementations.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractViewer<VR> implements IViewer<VR> {

	@Inject
	// scoped to single instance within viewer
	private ContentPartPool<VR> contentPartPool;

	private ActivatableSupport acs = new ActivatableSupport(this);
	private AdaptableSupport<IViewer<VR>> ads = new AdaptableSupport<>(this);

	private Map<Object, IContentPart<VR, ? extends VR>> contentsToContentPartMap = new IdentityHashMap<>();
	private Map<VR, IVisualPart<VR, ? extends VR>> visualsToVisualPartMap = new HashMap<>();

	private ReadOnlyObjectWrapper<IDomain<VR>> domainProperty = new ReadOnlyObjectWrapper<>();

	/**
	 * Creates a new {@link AbstractViewer} instance, setting the
	 * {@link AdaptableScope} for each of its {@link IAdaptable}-compliant types
	 * (super classes implementing {@link IAdaptable} and super-interfaces
	 * extending {@link IAdaptable}) to the newly created instance (see
	 * AdaptableScopes#scopeTo(IAdaptable)).
	 */
	public AbstractViewer() {
		AdaptableScopes.enter(this);
	}

	@Override
	public void activate() {
		if (!acs.isActive()) {
			if (getDomain() == null) {
				throw new IllegalStateException(
						"Domain has to be set before activation.");
			}
			acs.activate();
		}
	}

	@Override
	public ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	@Override
	public ReadOnlyObjectProperty<IDomain<VR>> adaptableProperty() {
		return domainProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty() {
		return ads.adaptersProperty();
	}

	@Override
	public void deactivate() {
		if (acs.isActive()) {
			if (getDomain() == null) {
				throw new IllegalStateException(
						"Domain may not be unset before deactivation is completed.");
			}
			acs.deactivate();
		}
	}

	@Override
	public void dispose() {
		// leave adaptable scope
		AdaptableScopes.leave(this);

		// dispose adapters (including root part and models)
		ads.dispose();
		ads = null;

		// dispose the content part pool (we share a single instance by all
		// content behaviors of a viewer, so need to dispose this here)
		for (IContentPart<VR, ? extends VR> cp : contentPartPool.getPooled()) {
			cp.dispose();
		}
		contentPartPool.clear();
		contentPartPool = null;

		// clear content part map
		if (!contentsToContentPartMap.isEmpty()) {
			throw new IllegalStateException(
					"Content part map was not properly cleared!");
		}
		contentsToContentPartMap = null;

		// clear visual part map
		if (!visualsToVisualPartMap.isEmpty()) {
			throw new IllegalStateException(
					"Visual part map was not properly cleared!");
		}
		visualsToVisualPartMap = null;

		// unset activatable support
		acs = null;
	}

	@Override
	public IDomain<VR> getAdaptable() {
		return domainProperty.get();
	}

	@Override
	public <T> T getAdapter(AdapterKey<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(Class<T> classKey) {
		return ads.getAdapter(classKey);
	}

	@Override
	public <T> T getAdapter(TypeToken<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> AdapterKey<T> getAdapterKey(T adapter) {
		return ads.getAdapterKey(adapter);
	}

	@Override
	public ObservableMap<AdapterKey<?>, Object> getAdapters() {
		return ads.getAdapters();
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			Class<? super T> classKey) {
		return ads.getAdapters(classKey);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> key) {
		return ads.getAdapters(key);
	}

	/**
	 * @see IViewer#getContentPartMap()
	 */
	@Override
	public Map<Object, IContentPart<VR, ? extends VR>> getContentPartMap() {
		return contentsToContentPartMap;
	}

	/**
	 * @see IViewer#getDomain()
	 */
	@Override
	public IDomain<VR> getDomain() {
		return domainProperty.get();
	}

	@SuppressWarnings("serial")
	@Override
	public IRootPart<VR, ? extends VR> getRootPart() {
		return ads.getAdapter(
				new TypeToken<IRootPart<VR, ? extends VR>>(getClass()) {
				});
	}

	/**
	 * @see IViewer#getVisualPartMap()
	 */
	@Override
	public Map<VR, IVisualPart<VR, ? extends VR>> getVisualPartMap() {
		return visualsToVisualPartMap;
	}

	@Override
	public boolean isActive() {
		return acs.isActive();
	}

	@Override
	public void setAdaptable(IDomain<VR> domain) {
		domainProperty.set(domain);
	}

	@Override
	public <T> void setAdapter(T adapter) {
		ads.setAdapter(adapter);
	}

	@Override
	public <T> void setAdapter(T adapter, String role) {
		ads.setAdapter(adapter, role);
	}

	@Override
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter) {
		ads.setAdapter(adapterType, adapter);
	}

	@InjectAdapters
	@Override
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter,
			String role) {
		ads.setAdapter(adapterType, adapter, role);
	}

	@Override
	public <T> void unsetAdapter(T adapter) {
		ads.unsetAdapter(adapter);
	}

}
