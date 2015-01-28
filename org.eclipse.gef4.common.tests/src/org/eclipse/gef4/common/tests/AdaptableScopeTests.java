package org.eclipse.gef4.common.tests;

import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.AdapterStore;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.inject.AdaptableScopes;
import org.eclipse.gef4.common.inject.AdapterMap;
import org.eclipse.gef4.common.inject.AdapterMapInjectionSupport;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;

public class AdaptableScopeTests {

	// an adapter for an adapter store
	public static class AdapterStoreAdapter implements
			IAdaptable.Bound<AdapterStore> {

		@Inject
		protected InjectionTarget fieldTarget;

		private AdapterStore adaptable;

		@Override
		public AdapterStore getAdaptable() {
			return adaptable;
		}

		@Override
		public void setAdaptable(AdapterStore adaptable) {
			this.adaptable = adaptable;
		}

	}

	// a dummy target for injection
	static class InjectionTarget extends Object {
	}

	// a scoped adapter store implementation that may be bound as an adapter to
	// another adapter store.
	public static class ScopingAdapterStore extends AdapterStore implements
			IAdaptable.Bound<AdapterStore> {

		@Inject
		protected InjectionTarget fieldTarget;

		private AdapterStore adaptable;

		public ScopingAdapterStore() {
			AdaptableScopes.enter(this);
		}

		@Override
		public AdapterStore getAdaptable() {
			return adaptable;
		}

		@Override
		public void setAdaptable(AdapterStore adaptable) {
			this.adaptable = adaptable;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Inject(optional = true)
		public void setAdapters(
				@AdapterMap Map<AdapterKey<?>, Object> adaptersWithKeys) {
			for (AdapterKey<?> key : adaptersWithKeys.keySet()) {
				setAdapter((AdapterKey) key, adaptersWithKeys.get(key));
			}
		}
	}

	@Test
	public void testScopingOnAdapters() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterMapInjectionSupport());

				MapBinder<AdapterKey<?>, Object> s1Binder = AdapterMaps
						.getAdapterMapBinder(binder(),
								ScopingAdapterStore.class);
				s1Binder.addBinding(
						AdapterKey.get(AdapterStoreAdapter.class, "a1")).to(
								AdapterStoreAdapter.class);
				s1Binder.addBinding(
						AdapterKey.get(AdapterStoreAdapter.class, "a2")).to(
								AdapterStoreAdapter.class);

				binder().bind(ScopingAdapterStore.class);
				binder().bind(AdapterStoreAdapter.class).in(
						AdaptableScopes.typed(ScopingAdapterStore.class));
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
	public void testScopingOnFields() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterMapInjectionSupport());

				MapBinder<AdapterKey<?>, Object> s1Binder = AdapterMaps
						.getAdapterMapBinder(binder(),
								ScopingAdapterStore.class);
				s1Binder.addBinding(
						AdapterKey.get(AdapterStoreAdapter.class, "a1")).to(
						AdapterStoreAdapter.class);
				s1Binder.addBinding(
						AdapterKey.get(AdapterStoreAdapter.class, "a2")).to(
						AdapterStoreAdapter.class);

				binder().bind(ScopingAdapterStore.class);
				binder().bind(AdapterStoreAdapter.class);
				binder().bind(InjectionTarget.class).in(
						AdaptableScopes.typed(ScopingAdapterStore.class));
			}
		};
		Injector injector = Guice.createInjector(module);
		ScopingAdapterStore s1 = injector
				.getInstance(ScopingAdapterStore.class);
		ScopingAdapterStore s2 = injector
				.getInstance(ScopingAdapterStore.class);

		Assert.assertSame(
				s1.fieldTarget,
				s1.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a1")).fieldTarget);
		Assert.assertSame(
				s1.fieldTarget,
				s1.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a2")).fieldTarget);

		Assert.assertSame(
				s2.fieldTarget,
				s2.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a1")).fieldTarget);
		Assert.assertSame(
				s2.fieldTarget,
				s2.getAdapter(AdapterKey.get(AdapterStoreAdapter.class, "a2")).fieldTarget);

		Assert.assertNotSame(s1.fieldTarget, s2.fieldTarget);
	}
}
