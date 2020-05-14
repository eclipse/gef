/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.adapt.AdaptableSupport;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.AdapterStore;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport.LoggingMode;
import org.eclipse.gef.common.adapt.inject.AdapterInjector;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.common.adapt.inject.InjectAdapters;
import org.junit.Test;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableMap;

public class AdapterInjectorTests {

	private static class AdapterStoreBoundAdaptable
			implements IAdaptable, IAdaptable.Bound<AdapterStore> {

		private AdaptableSupport<AdapterStoreBoundAdaptable> ads = new AdaptableSupport<>(
				this);

		private ReadOnlyObjectWrapper<AdapterStore> adaptableProperty = new ReadOnlyObjectWrapper<>();

		@Override
		public ReadOnlyObjectProperty<AdapterStore> adaptableProperty() {
			return adaptableProperty.getReadOnlyProperty();
		}

		@Override
		public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty() {
			return ads.adaptersProperty();
		}

		@Override
		public AdapterStore getAdaptable() {
			return adaptableProperty.get();
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
		public <T> AdapterKey<T> getAdapterKey(T adapter) {
			return ads.getAdapterKey(adapter);
		}

		@Override
		public ObservableMap<AdapterKey<?>, Object> getAdapters() {
			return ads.getAdapters();
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
		public void setAdaptable(AdapterStore adaptable) {
			adaptableProperty.set(adaptable);
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

	private final class AdapterStoreExtension extends AdapterStore {
		@InjectAdapters
		@Override
		public <T> void setAdapter(TypeToken<T> adapterType, T adapter,
				String role) {
			try {
				super.setAdapter(adapterType, adapter, role);
			} catch (Exception e) {
				// the injection will fail; we capture this silently here,
				// as it is not relevant for the test
			}
		}
	}

	private static class ParameterizedSubType<T> extends RawType {
	}

	private static class RawType {
	}

	/**
	 * Tests that a warning message is given when no type key is provided in the
	 * binding, and the actual type could also not be inferred from the binding.
	 */
	@Test
	public void ensureMissingKeyDetected() throws Exception {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				// create map bindings for AdapterStore, which is an IAdaptable
				MapBinder<AdapterKey<?>, Object> adapterMapBinder = AdapterMaps
						.getAdapterMapBinder(binder(), AdapterStore.class);
				// use raw type as key and target
				adapterMapBinder.addBinding(AdapterKey.defaultRole())
						.toInstance(new ParameterizedSubType<Integer>());
			}
		};

		AdapterStore adaptable = new AdapterStoreExtension();
		List<String> issues = performInjection(adaptable, module);
		assertEquals(1, issues.size());
		// System.out.println(issues.get(0));
		assertTrue(issues.get(0).contains("WARNING"));
		assertTrue(issues.get(0).contains(
				"The actual type of adapter org.eclipse.gef.common.tests.AdapterInjectorTests$ParameterizedSubType"));
		assertTrue(issues.get(0)
				.contains("could not be inferred from the binding at"));
	}

	/**
	 * Tests that a warning message is given when the actual type of an adapter
	 * could be precisely inferred from the binding, and the binding provides a
	 * type key as well.
	 */
	@Test
	public void ensureSuperfluousKeyDetected() throws Exception {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				// create map bindings for AdapterStore, which is an IAdaptable
				MapBinder<AdapterKey<?>, Object> adapterMapBinder = AdapterMaps
						.getAdapterMapBinder(binder(), AdapterStore.class);
				// use raw type as key and target
				adapterMapBinder.addBinding(AdapterKey.get(RawType.class))
						.to(RawType.class);
			}
		};

		AdapterStore adaptable = new AdapterStore();
		List<String> issues = performInjection(adaptable, module);
		assertEquals(1, issues.size());
		// System.out.println(issues.get(0));
		assertTrue(issues.get(0).contains("INFO"));
		assertTrue(issues.get(0).contains(
				"The redundant type key org.eclipse.gef.common.tests.AdapterInjectorTests$RawType may be omitted in the adapter key of the binding, using AdapterKey.defaultRole() instead."));
	}

	/**
	 * Tests that a warning message is given when no type could be inferred from
	 * the binding, and the type key can thus not be validated.
	 */
	@Test
	public void ensureUncertainKeyDetected() throws Exception {
		Module module = new AbstractModule() {
			@SuppressWarnings("serial")
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				// create map bindings for AdapterStore, which is an IAdaptable
				MapBinder<AdapterKey<?>, Object> adapterMapBinder = AdapterMaps
						.getAdapterMapBinder(binder(), AdapterStore.class);
				// use raw type as key and target
				adapterMapBinder.addBinding(AdapterKey
						.get(new TypeToken<ParameterizedSubType<Integer>>() {
						})).toInstance(new ParameterizedSubType<Integer>());
			}
		};

		AdapterStore adaptable = new AdapterStore();
		List<String> issues = performInjection(adaptable, module);
		assertEquals(1, issues.size());
		// System.out.println(issues.get(0));
		assertTrue(issues.get(0).contains("WARNING"));
		assertTrue(issues.get(0).contains(
				"The actual type of adapter org.eclipse.gef.common.tests.AdapterInjectorTests$ParameterizedSubType"));
		assertTrue(issues.get(0)
				.contains("could not be inferred from the binding at"));
		assertTrue(issues.get(0).contains("Make sure the provided type key "));
		assertTrue(issues.get(0)
				.contains("matches to the actual type of the adapter."));
	}

	/**
	 * Tests that a warning message is given when a type different to the key
	 * type could be inferred from the binding.
	 */
	@Test
	public void ensureUnderspecifiedKeyDetected() throws Exception {

		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				// create map bindings for AdapterStore, which is an IAdaptable
				MapBinder<AdapterKey<?>, Object> adapterMapBinder = AdapterMaps
						.getAdapterMapBinder(binder(), AdapterStore.class);
				// use raw type as key and target
				adapterMapBinder.addBinding(AdapterKey.get(RawType.class))
						.to(new TypeLiteral<ParameterizedSubType<Object>>() {
						});
			}
		};

		AdapterStore adaptable = new AdapterStoreExtension();
		List<String> issues = performInjection(adaptable, module);
		assertEquals(1, issues.size());
		// System.out.println(issues.get(0));
		assertTrue(issues.get(0).contains(
				"The given key type org.eclipse.gef.common.tests.AdapterInjectorTests$RawType does not seem to match the actual type of adapter org.eclipse.gef.common.tests.AdapterInjectorTests$ParameterizedSubType"));
	}

	/**
	 * Tests that an error message is given when the binding provides a type
	 * key, whose raw type does not the actual (raw) type of the adapter, as it
	 * was inferred from the binding (if possible) or the adapter instance.
	 */
	@Test
	public void ensureUnmatchedRawKeyDetected() throws Exception {
		// First case: a module that allows to infer the actual type from the
		// binding
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				// create map bindings for AdapterStore, which is an IAdaptable
				MapBinder<AdapterKey<?>, Object> adapterMapBinder = AdapterMaps
						.getAdapterMapBinder(binder(), AdapterStore.class);
				// use raw type as key and target
				adapterMapBinder.addBinding(AdapterKey.get(RawType.class))
						.to(ParameterizedSubType.class);
			}
		};

		AdapterStore adaptable = new AdapterStoreExtension();
		List<String> issues = performInjection(adaptable, module);
		assertEquals(1, issues.size());
		// System.out.println(issues.get(0));
		assertTrue(issues.get(0).contains("ERROR"));
		assertTrue(issues.get(0).contains(
				"The given key (raw) type org.eclipse.gef.common.tests.AdapterInjectorTests$RawType does not match the actual (raw) type of adapter org.eclipse.gef.common.tests.AdapterInjectorTests$ParameterizedSubType"));

		// Second case: a module that does not allow to infer the actual type
		// from the
		// binding
		module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				// create map bindings for AdapterStore, which is an IAdaptable
				MapBinder<AdapterKey<?>, Object> adapterMapBinder = AdapterMaps
						.getAdapterMapBinder(binder(), AdapterStore.class);
				// use raw type as key and target
				adapterMapBinder.addBinding(AdapterKey.get(RawType.class))
						.toInstance(new ParameterizedSubType<Integer>());
			}
		};

		adaptable = new AdapterStoreExtension();
		issues = performInjection(adaptable, module);
		assertEquals(1, issues.size());
		// System.out.println(issues.get(0));
		assertTrue(issues.get(0).contains("ERROR"));
		assertTrue(issues.get(0).contains(
				"The given key (raw) type org.eclipse.gef.common.tests.AdapterInjectorTests$RawType does not match the actual (raw) type of adapter org.eclipse.gef.common.tests.AdapterInjectorTests$ParameterizedSubType"));
	}

	@SuppressWarnings("serial")
	@Test
	public void injectAdapters() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				MapBinder<AdapterKey<?>, Object> adapterMapBinder = AdapterMaps
						.getAdapterMapBinder(binder(), AdapterStore.class);

				// constructor binding
				adapterMapBinder.addBinding(AdapterKey
						.get(new TypeToken<ParameterizedSubType<Integer>>() {
						}, "a1"))
						.to(new TypeLiteral<ParameterizedSubType<Integer>>() {
						});

				// instance binding
				adapterMapBinder.addBinding(
						AdapterKey.get(new TypeToken<Provider<Integer>>() {
						}, "a2")).toInstance(new Provider<Integer>() {

							@Override
							public Integer get() {
								return 5;
							}
						});

				// provider binding
				adapterMapBinder.addBinding(
						AdapterKey.get(new TypeToken<Provider<Integer>>() {
						}, "a3")).toProvider(new Provider<Provider<Integer>>() {

							@Override
							public Provider<Integer> get() {
								return new Provider<Integer>() {

									@Override
									public Integer get() {
										return 5;
									}
								};
							}
						});
			}
		};
		Injector injector = Guice.createInjector(module);
		AdapterStore adapterStore = new AdapterStore();
		injector.injectMembers(adapterStore);
		assertNotNull(adapterStore.getAdapter(
				AdapterKey.get(new TypeToken<ParameterizedSubType<Integer>>() {
				}, "a1")));
		// retrieve a parameterized type bound as instance
		assertNotNull(adapterStore
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Integer>>() {
				}, "a2")));
		assertNotNull(adapterStore
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Integer>>() {
				}, "a3")));
	}

	/**
	 * Tests that adapters, which are bound to an adaptable of a certain role
	 * are injected to an adaptable, that is itself bound as an adapter with the
	 * respective role.
	 */
	@SuppressWarnings("serial")
	@Test
	public void injectAdaptersToBoundAdaptableOfRole()
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		final String firstRole = "firstRole";
		final String secondRole = "secondRole";
		final String role1 = "a1";
		final String role2 = "a2";
		final String role3 = "a3";

		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				install(new AdapterInjectionSupport());

				MapBinder<AdapterKey<?>, Object> adapterMapBinder = AdapterMaps
						.getAdapterMapBinder(binder(), AdapterStore.class);

				// register adapter for the first role
				adapterMapBinder.addBinding(AdapterKey.role(firstRole))
						.to(AdapterStoreBoundAdaptable.class);

				// create map bindings for AdapterStore, which is an IAdaptable
				MapBinder<AdapterKey<?>, Object> firstRoleBinder = AdapterMaps
						.getAdapterMapBinder(binder(),
								AdapterStoreBoundAdaptable.class,
								AdapterKey.get(AdapterStoreBoundAdaptable.class,
										firstRole));

				// register adapter
				firstRoleBinder.addBinding(AdapterKey.role(role1))
						.to(RawType.class);
				firstRoleBinder.addBinding(AdapterKey
						.get(new TypeToken<ParameterizedSubType<Integer>>() {
						}, role2))
						.to(new TypeLiteral<ParameterizedSubType<Integer>>() {
						});

				firstRoleBinder.addBinding(
						AdapterKey.get(new TypeToken<Provider<Integer>>() {
						}, role3)).toInstance(new Provider<Integer>() {

							@Override
							public Integer get() {
								return 5;
							}
						});

				// register adapter for the second role
				adapterMapBinder.addBinding(AdapterKey.role(secondRole))
						.to(AdapterStoreBoundAdaptable.class);

				// create map bindings for AdapterStore, which is an IAdaptable
				MapBinder<AdapterKey<?>, Object> secondRoleBinder = AdapterMaps
						.getAdapterMapBinder(binder(),
								AdapterStoreBoundAdaptable.class,
								AdapterKey.get(AdapterStoreBoundAdaptable.class,
										secondRole));
				// register adapter
				secondRoleBinder.addBinding(AdapterKey.role(role1))
						.to(RawType.class);
				secondRoleBinder.addBinding(AdapterKey
						.get(new TypeToken<ParameterizedSubType<Integer>>() {
						}, role2))
						.to(new TypeLiteral<ParameterizedSubType<Integer>>() {
						});

				secondRoleBinder.addBinding(
						AdapterKey.get(new TypeToken<Provider<Integer>>() {
						}, role3)).toInstance(new Provider<Integer>() {

							@Override
							public Integer get() {
								return 5;
							}
						});
			}

		};
		Injector injector = Guice.createInjector(module);
		AdapterStore adapterStore = new AdapterStore();
		injector.injectMembers(adapterStore);

		// test first role
		AdapterStoreBoundAdaptable adaptableBound = adapterStore.getAdapter(
				AdapterKey.get(AdapterStoreBoundAdaptable.class, firstRole));
		assertNotNull(adaptableBound);
		assertNotNull(adaptableBound
				.getAdapter(AdapterKey.get(RawType.class, role1)));
		// retrieve by raw type (which works even if we could not infer a type
		// from the binding)
		assertNotNull(adaptableBound
				.getAdapter(AdapterKey.get(ParameterizedSubType.class, role2)));
		// retrieve by parameterized type token (which only works if we could
		// infer a type from the binding)
		assertNotNull(adaptableBound.getAdapter(
				AdapterKey.get(new TypeToken<ParameterizedSubType<Integer>>() {
				}, role2)));
		// retrieve a parameterized type bound as instance
		assertNotNull(adaptableBound
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Integer>>() {
				}, role3)));

		// test second role
		adaptableBound = adapterStore.getAdapter(
				AdapterKey.get(AdapterStoreBoundAdaptable.class, secondRole));
		assertNotNull(adaptableBound);
		assertNotNull(adaptableBound
				.getAdapter(AdapterKey.get(RawType.class, role1)));
		// retrieve by raw type (which works even if we could not infer a type
		// from the binding)
		assertNotNull(adaptableBound
				.getAdapter(AdapterKey.get(ParameterizedSubType.class, role2)));
		// retrieve by parameterized type token (which only works if we could
		// infer a type from the binding)
		assertNotNull(adaptableBound.getAdapter(
				AdapterKey.get(new TypeToken<ParameterizedSubType<Integer>>() {
				}, role2)));
		// retrieve a parameterized type bound as instance
		assertNotNull(adaptableBound
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Integer>>() {
				}, role3)));
	}

	protected List<String> performInjection(AdapterStore adaptable,
			Module module) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Injector injector = Guice.createInjector(module);
		AdapterInjector adapterInjector = new AdapterInjector(
				AdapterStore.class.getMethod("setAdapter", TypeToken.class,
						Object.class, String.class),
				LoggingMode.DEVELOPMENT);
		adapterInjector.setInjector(injector);

		List<String> issues = new ArrayList<>();
		// call adapterInjector.injectAdapters(adaptable, issues);
		Method injectAdaptersMethod = AdapterInjector.class.getDeclaredMethod(
				"performAdapterInjection", IAdaptable.class, List.class);
		injectAdaptersMethod.setAccessible(true);
		injectAdaptersMethod.invoke(adapterInjector, adaptable, issues);
		return issues;
	}

}
