/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - test scoping on adapters with roles
 *******************************************************************************/
package org.eclipse.gef4.common.tests;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.AdapterStore;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef4.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef4.common.adapt.inject.AdapterMaps;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class AdaptableScopeTests {

	// an adapter for an adapter store
	public static class AdapterStoreAdapter
			implements IAdaptable.Bound<AdapterStore> {
		@Inject
		protected InjectionTarget fieldTarget;

		private ReadOnlyObjectWrapper<AdapterStore> adaptableProperty = new ReadOnlyObjectWrapper<>();

		@Override
		public ReadOnlyObjectProperty<AdapterStore> adaptableProperty() {
			return adaptableProperty.getReadOnlyProperty();
		}

		@Override
		public AdapterStore getAdaptable() {
			return adaptableProperty.get();
		}

		@Override
		public void setAdaptable(AdapterStore adaptable) {
			this.adaptableProperty.set(adaptable);
		}
	}

	// a dummy target for injection
	static class InjectionTarget extends Object {
	}

	// adapter store
	public static class MyAdapterStore extends AdapterStore
			implements IAdaptable.Bound<AdapterStore> {

		@Inject
		protected InjectionTarget fieldTarget;

		private ReadOnlyObjectWrapper<AdapterStore> adaptableProperty = new ReadOnlyObjectWrapper<>();

		public MyAdapterStore() {
			AdaptableScopes.enter(this);
		}

		@Override
		public ReadOnlyObjectProperty<AdapterStore> adaptableProperty() {
			return adaptableProperty.getReadOnlyProperty();
		}

		@Override
		public AdapterStore getAdaptable() {
			return adaptableProperty.get();
		}

		@Override
		public void setAdaptable(AdapterStore adaptable) {
			this.adaptableProperty.set(adaptable);
		}
	}

	// a scoped adapter store implementation that may be bound as an adapter to
	// another adapter store.
	public static class ScopingAdapterStore extends AdapterStore
			implements IAdaptable.Bound<AdapterStore> {

		@Inject
		protected InjectionTarget fieldTarget;

		private ReadOnlyObjectWrapper<AdapterStore> adaptableProperty = new ReadOnlyObjectWrapper<>();

		public ScopingAdapterStore() {
			AdaptableScopes.enter(this);
		}

		@Override
		public ReadOnlyObjectProperty<AdapterStore> adaptableProperty() {
			return adaptableProperty.getReadOnlyProperty();
		}

		@Override
		public AdapterStore getAdaptable() {
			return adaptableProperty.get();
		}

		@Override
		public void setAdaptable(AdapterStore adaptable) {
			this.adaptableProperty.set(adaptable);
		}
	}

	@Test
	public void testScopingOnAdapters() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				MapBinder<AdapterKey<?>, Object> s1Binder = AdapterMaps
						.getAdapterMapBinder(binder(),
								ScopingAdapterStore.class);
				// bind adapter under different roles (which is valid)
				s1Binder.addBinding(
						AdapterKey.get(AdapterStoreAdapter.class, "a1"))
						.to(AdapterStoreAdapter.class);
				s1Binder.addBinding(
						AdapterKey.get(AdapterStoreAdapter.class, "a2"))
						.to(AdapterStoreAdapter.class);

				binder().bind(ScopingAdapterStore.class);
				binder().bind(AdapterStoreAdapter.class)
						.in(AdaptableScopes.typed(ScopingAdapterStore.class));
				binder().bind(InjectionTarget.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		ScopingAdapterStore s1 = injector
				.getInstance(ScopingAdapterStore.class);
		ScopingAdapterStore s2 = injector
				.getInstance(ScopingAdapterStore.class);

		Assert.assertSame(
				s1.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a1")),
				s1.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a2")));

		Assert.assertSame(
				s2.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a1")),
				s2.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a2")));

		Assert.assertNotSame(
				s1.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a1")),
				s2.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a1")));

		Assert.assertNotSame(
				s1.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a2")),
				s2.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a2")));
	}

	@Test
	public void testScopingOnAdaptersWithRoles() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				MapBinder<AdapterKey<?>, Object> topBinder = AdapterMaps
						.getAdapterMapBinder(binder(), MyAdapterStore.class);
				topBinder
						.addBinding(
								AdapterKey.get(ScopingAdapterStore.class, "r1"))
						.to(ScopingAdapterStore.class);
				topBinder
						.addBinding(
								AdapterKey.get(ScopingAdapterStore.class, "r2"))
						.to(ScopingAdapterStore.class);

				MapBinder<AdapterKey<?>, Object> s1Binder1 = AdapterMaps
						.getAdapterMapBinder(binder(),
								ScopingAdapterStore.class, "r1");
				MapBinder<AdapterKey<?>, Object> s1Binder2 = AdapterMaps
						.getAdapterMapBinder(binder(),
								ScopingAdapterStore.class, "r2");
				// bind adapter under different roles (which is valid)
				s1Binder1
						.addBinding(
								AdapterKey.get(AdapterStoreAdapter.class, "a1"))
						.to(AdapterStoreAdapter.class);
				s1Binder1
						.addBinding(
								AdapterKey.get(AdapterStoreAdapter.class, "a2"))
						.to(AdapterStoreAdapter.class);

				s1Binder2
						.addBinding(
								AdapterKey.get(AdapterStoreAdapter.class, "a1"))
						.to(AdapterStoreAdapter.class);
				s1Binder2
						.addBinding(
								AdapterKey.get(AdapterStoreAdapter.class, "a2"))
						.to(AdapterStoreAdapter.class);

				binder().bind(ScopingAdapterStore.class);
				binder().bind(AdapterStoreAdapter.class)
						.in(AdaptableScopes.typed(ScopingAdapterStore.class));
				binder().bind(InjectionTarget.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyAdapterStore s = injector.getInstance(MyAdapterStore.class);

		ScopingAdapterStore s1 = s
				.getAdapter(AdapterKey.get(ScopingAdapterStore.class, "r1"));
		ScopingAdapterStore s2 = s
				.getAdapter(AdapterKey.get(ScopingAdapterStore.class, "r2"));

		Assert.assertSame(
				s1.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a1")),
				s1.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a2")));

		Assert.assertSame(
				s2.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a1")),
				s2.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a2")));

		Assert.assertNotSame(
				s1.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a1")),
				s2.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a1")));

		Assert.assertNotSame(
				s1.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a2")),
				s2.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a2")));
	}

	@Test
	public void testScopingOnFields() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				MapBinder<AdapterKey<?>, Object> s1Binder = AdapterMaps
						.getAdapterMapBinder(binder(),
								ScopingAdapterStore.class);
				s1Binder.addBinding(
						AdapterKey.get(AdapterStoreAdapter.class, "a1"))
						.to(AdapterStoreAdapter.class);
				s1Binder.addBinding(
						AdapterKey.get(AdapterStoreAdapter.class, "a2"))
						.to(AdapterStoreAdapter.class);

				binder().bind(ScopingAdapterStore.class);
				binder().bind(AdapterStoreAdapter.class);
				binder().bind(InjectionTarget.class)
						.in(AdaptableScopes.typed(ScopingAdapterStore.class));
			}
		};
		Injector injector = Guice.createInjector(module);
		ScopingAdapterStore s1 = injector
				.getInstance(ScopingAdapterStore.class);
		ScopingAdapterStore s2 = injector
				.getInstance(ScopingAdapterStore.class);

		Assert.assertSame(s1.fieldTarget, s1.getAdapter(
				AdapterKey.get(AdapterStoreAdapter.class, "a1")).fieldTarget);
		Assert.assertSame(s1.fieldTarget, s1.getAdapter(
				AdapterKey.get(AdapterStoreAdapter.class, "a2")).fieldTarget);

		Assert.assertSame(s2.fieldTarget, s2.getAdapter(
				AdapterKey.get(AdapterStoreAdapter.class, "a1")).fieldTarget);
		Assert.assertSame(s2.fieldTarget, s2.getAdapter(
				AdapterKey.get(AdapterStoreAdapter.class, "a2")).fieldTarget);

		Assert.assertNotSame(s1.fieldTarget, s2.fieldTarget);
	}
}
