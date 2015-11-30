/*******************************************************************************
 * Copyright (c) 2011, 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.inject.AdapterInjectionSupport;
import org.eclipse.gef4.common.inject.AdapterMap;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.common.inject.InjectAdapters;
import org.eclipse.gef4.common.tests.AdaptableScopeTests.AdapterStoreAdapter;
import org.junit.Test;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.spi.Message;

public class AdaptableTypeListenerTests {

	public static class AdaptableSpecifyingAdaptableTypeInAdapterMapAnnotation
			implements IAdaptable {

		private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		private AdaptableSupport<AdaptableSpecifyingAdaptableTypeInAdapterMapAnnotation> ads = new AdaptableSupport<>(
				this, pcs);

		@Override
		public void addPropertyChangeListener(PropertyChangeListener listener) {
			pcs.addPropertyChangeListener(listener);
		}

		public void clear() {
			for (Object adapter : ads.getAdapters().values()) {
				ads.unsetAdapter(adapter);
			}
		}

		@Override
		public <T> T getAdapter(AdapterKey<T> key) {
			return ads.getAdapter(key);
		}

		@Override
		public <T> T getAdapter(Class<T> key) {
			return ads.getAdapter(key);
		}

		@Override
		public <T> T getAdapter(TypeToken<T> key) {
			return ads.getAdapter(key);
		}

		@Override
		public <T> Map<AdapterKey<? extends T>, T> getAdapters(
				Class<? super T> key) {
			return ads.getAdapters(key);
		}

		@Override
		public <T> Map<AdapterKey<? extends T>, T> getAdapters(
				TypeToken<? super T> key) {
			return ads.getAdapters(key);
		}

		@Override
		public void removePropertyChangeListener(
				PropertyChangeListener listener) {
			pcs.removePropertyChangeListener(listener);
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
		@Inject(optional = true)
		@Override
		public <T> void setAdapter(
				@AdapterMap(adaptableType = AdaptableSpecifyingAdaptableTypeInAdapterMapAnnotation.class) TypeToken<T> adapterType,
				T adapter, String role) {
			ads.setAdapter(adapterType, adapter, role);
		}

		@Override
		public <T> void unsetAdapter(T adapter) {
			ads.unsetAdapter(adapter);
		}
	}

	@Test
	public void ensureMisuseOfAdapterMapAnnotationDetected() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				MapBinder<AdapterKey<?>, Object> mapBinder = AdapterMaps
						.getAdapterMapBinder(binder(),
								AdaptableSpecifyingAdaptableTypeInAdapterMapAnnotation.class);
				// bind adapter under different roles (which is valid)
				mapBinder.addBinding(AdapterKey.role("a1"))
						.to(AdapterStoreAdapter.class);
			}
		};
		try {
			Injector injector = Guice.createInjector(module);
			injector.getInstance(
					AdaptableSpecifyingAdaptableTypeInAdapterMapAnnotation.class);
			fail("Configuration should have failed, because adapter map annotation is not properly used (adaptable type is specified).");
		} catch (ConfigurationException e) {
			System.out.println(
					"Guice reported the following configuration problems: ");
			for (Message message : e.getErrorMessages()) {
				System.out.println("  - " + message.toString());
			}
			// we expect the configuration to fail
			assertTrue(e.getMessage().contains(
					"@AdapterMap annotation may only be used in adapter map bindings, not to mark an injection point. Annotate method with @InjectAdapters instead."));
			assertTrue(e.getMessage().contains(
					"To prevent that Guice member injection interferes with adapter injection, no @Inject annotation may be used on a method that provides an @InjectAdapters annotation."));

		}
	}

}
