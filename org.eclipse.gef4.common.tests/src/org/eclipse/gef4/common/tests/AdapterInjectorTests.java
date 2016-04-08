/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.AdapterStore;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef4.common.adapt.inject.AdapterInjector;
import org.eclipse.gef4.common.adapt.inject.AdapterMaps;
import org.junit.Test;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class AdapterInjectorTests {

	private static class ParameterizedSubType<T> extends RawType {
	}

	private static class RawType {
	}

	@Test
	public void ensureSuperfluousKeyDetected() throws Exception {
		AdapterStore adaptable = new AdapterStore();

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

		List<String> issues = performInjection(adaptable, module);
		assertEquals(1, issues.size());
		assertTrue(issues.get(0).contains(
				"The redundant type key org.eclipse.gef4.common.tests.AdapterInjectorTests$RawType may be omitted in the adapter key of the binding, using AdapterKey.defaultRole() or AdapterKey.role(String) instead."));
		System.out.println(issues.get(0));
	}

	@Test
	public void ensureUnderspecifiedKeyDetected() throws Exception {
		AdapterStore adaptable = new AdapterStore();

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

		List<String> issues = performInjection(adaptable, module);
		assertEquals(1, issues.size());
		System.out.println(issues.get(0));
		assertTrue(issues.get(0).contains(
				"The given key type org.eclipse.gef4.common.tests.AdapterInjectorTests$RawType does not seem to match the actual (parameterized) type of adapter org.eclipse.gef4.common.tests.AdapterInjectorTests$ParameterizedSubType"));
	}

	@Test
	public void ensureUnmatchedRawKeyDetected() throws Exception {
		AdapterStore adaptable = new AdapterStore();

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

		List<String> issues = performInjection(adaptable, module);
		assertEquals(1, issues.size());
		System.out.println(issues.get(0));
		assertTrue(issues.get(0).contains(
				"The given key (raw) type org.eclipse.gef4.common.tests.AdapterInjectorTests$RawType does not seem to match the actual type of adapter org.eclipse.gef4.common.tests.AdapterInjectorTests$ParameterizedSubType"));
	}

	protected List<String> performInjection(AdapterStore adaptable,
			Module module) throws NoSuchMethodException, IllegalAccessException,
					InvocationTargetException {
		Injector injector = Guice.createInjector(module);
		AdapterInjector adapterInjector = new AdapterInjector(
				AdapterStore.class.getMethod("setAdapter", TypeToken.class,
						Object.class, String.class));
		adapterInjector.setInjector(injector);

		List<String> issues = new ArrayList<>();
		// call adapterInjector.injectAdapters(adaptable, issues);
		Method injectAdaptersMethod = AdapterInjector.class.getDeclaredMethod(
				"injectAdapters", IAdaptable.class, List.class);
		injectAdaptersMethod.setAccessible(true);
		injectAdaptersMethod.invoke(adapterInjector, adaptable, issues);
		return issues;
	}

}
