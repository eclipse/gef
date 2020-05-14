/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - test scoping on adapters with roles
 *
 *******************************************************************************/
package org.eclipse.gef.common.tests;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.AdapterStore;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class AdaptableScopeTests {

	// an adapter for an adapter store
	public static class Intermediate extends AdapterStore
			implements IAdaptable.Bound<Root> {

		private ReadOnlyObjectWrapper<Root> adaptableProperty = new ReadOnlyObjectWrapper<>();

		@Override
		public ReadOnlyObjectProperty<Root> adaptableProperty() {
			return adaptableProperty.getReadOnlyProperty();
		}

		@Override
		public Root getAdaptable() {
			return adaptableProperty.get();
		}

		@Override
		public void setAdaptable(Root adaptable) {
			this.adaptableProperty.set(adaptable);
		}
	}

	// a dummy target for injection
	static class Leaf extends IAdaptable.Bound.Impl<Intermediate> {
	}

	public static class Root extends AdapterStore {
	}

	@Test
	public void testTransitiveScoping() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				MapBinder<AdapterKey<?>, Object> rootBinder = AdapterMaps
						.getAdapterMapBinder(binder(), Root.class);
				rootBinder.addBinding(AdapterKey.get(Intermediate.class, "a1"))
						.to(Intermediate.class);
				rootBinder.addBinding(AdapterKey.get(Intermediate.class, "a2"))
						.to(Intermediate.class);

				MapBinder<AdapterKey<?>, Object> intermediate1Binder = AdapterMaps
						.getAdapterMapBinder(binder(), Intermediate.class,
								AdapterKey.get(Intermediate.class, "a1"));
				intermediate1Binder.addBinding(AdapterKey.defaultRole())
						.to(Leaf.class);

				MapBinder<AdapterKey<?>, Object> intermediate2Binder = AdapterMaps
						.getAdapterMapBinder(binder(), Intermediate.class,
								AdapterKey.get(Intermediate.class, "a2"));
				intermediate2Binder.addBinding(AdapterKey.defaultRole())
						.to(Leaf.class);

				binder().bind(Leaf.class).in(AdaptableScopes.typed(Root.class));
			}
		};
		Injector injector = Guice.createInjector(module);
		AdapterStore root1 = injector.getInstance(Root.class);

		// ensure intermediate instances are not shared
		Assert.assertNotSame(
				root1.getAdapter(AdapterKey.get(Intermediate.class, "a1")),
				root1.getAdapter(AdapterKey.get(Intermediate.class, "a2")));

		// ensure leaf instance is shared
		Assert.assertSame(
				root1.getAdapter(AdapterKey.get(Intermediate.class, "a1"))
						.getAdapter(Leaf.class),
				root1.getAdapter(AdapterKey.get(Intermediate.class, "a2"))
						.getAdapter(Leaf.class));

		AdapterStore root2 = injector.getInstance(Root.class);

		// ensure intermediate instances are not shared
		Assert.assertNotSame(
				root2.getAdapter(AdapterKey.get(Intermediate.class, "a1")),
				root2.getAdapter(AdapterKey.get(Intermediate.class, "a2")));

		// ensure leaf instance is shared
		Assert.assertSame(
				root2.getAdapter(AdapterKey.get(Intermediate.class, "a1"))
						.getAdapter(Leaf.class),
				root2.getAdapter(AdapterKey.get(Intermediate.class, "a2"))
						.getAdapter(Leaf.class));

		// ensure instances are not shared outside scope
		Assert.assertNotSame(
				root1.getAdapter(AdapterKey.get(Intermediate.class, "a1"))
						.getAdapter(Leaf.class),
				root2.getAdapter(AdapterKey.get(Intermediate.class, "a1"))
						.getAdapter(Leaf.class));

	}

}
