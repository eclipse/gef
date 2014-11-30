/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.inject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.multibindings.MapBinderBinding;
import com.google.inject.multibindings.MultibinderBinding;
import com.google.inject.multibindings.MultibindingsTargetVisitor;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.UntargettedBinding;

/**
 * A specific {@link MembersInjector} to support adapter map injection, i.e.
 * injection of adapters into {@link IAdaptable} instances. Will be able to
 * perform adapter map injection if being registered (by an
 * {@link AdaptableTypeListener}) on an {@link IAdaptable} encounter, which:
 * <ul>
 * <li>is annotated with {@link Inject}, and</li>
 * <li>contains a single parameter of type
 * <code>Map&lt;AdapterKey&lt;?&gt;, Object&gt;</code>, which is annotated with
 * an {@link AdapterMap} annotation.</li>
 * </ul>
 * Being registered for a specific {@link IAdaptable} an
 * {@link AdapterMapInjector} will inject all instances of that type or any
 * sub-type, evaluating all {@link AdapterMap} bindings that can be obtained
 * from the {@link Injector} which was forwarded by the
 * {@link AdaptableTypeListener} via {@link #setInjector(Injector)}. This means,
 * that it will inject via the respective method all adapters, for which
 * bindings with a matching {@link AdapterMap} annotation exist. Here, matching
 * means, that the type provided in the {@link AdapterMap} annotation of the
 * {@link IAdaptable}#s method ({@link AdapterMap#adaptableType()}) is either
 * the same or a sub-type of the type used with the {@link AdapterMap}
 * annotation of the related binding.
 *
 * @see AdapterMap
 * @see AdaptableTypeListener
 * @author anyssen
 */
public class AdapterMapInjector implements MembersInjector<IAdaptable> {

	private class AdapterBindingsTargetVisitor implements
			MultibindingsTargetVisitor<Object, Map<AdapterKey<?>, Object>> {
		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ConstructorBinding<? extends Object> binding) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ConvertedConstantBinding<? extends Object> binding) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ExposedBinding<? extends Object> binding) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final InstanceBinding<? extends Object> binding) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final LinkedKeyBinding<? extends Object> binding) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final MapBinderBinding<? extends Object> mapbinding) {

			final Map<AdapterKey<?>, Object> bindings = new HashMap<AdapterKey<?>, Object>();
			for (final Entry<?, Binding<?>> entry : mapbinding.getEntries()) {
				final AdapterKey<?> key = (AdapterKey<?>) entry.getKey();
				final Object value = entry.getValue().getProvider().get();
				bindings.put(key, value);
			}
			return bindings;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final MultibinderBinding<? extends Object> multibinding) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ProviderBinding<? extends Object> binding) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ProviderInstanceBinding<? extends Object> binding) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ProviderKeyBinding<? extends Object> binding) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final UntargettedBinding<? extends Object> binding) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private final List<Object> deferredInstances = new ArrayList<Object>();

	private Injector injector;

	private final Method method;
	private final AdapterMap methodAnnotation;

	public AdapterMapInjector(final Method method,
			final AdapterMap methodAnnotation) {
		this.method = method;
		this.methodAnnotation = methodAnnotation;
	}

	protected SortedMap<Key<?>, Binding<?>> getPolymorphicAdapterBindingKeys(
			final Class<?> type, final Method method,
			final AdapterMap methodAnnotation) {
		// find available keys
		final Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();
		// IMPORTANT: use a sorted map, where keys are sorted according to
		// hierarchy of annotation types (so we have polymorphic injection)
		final SortedMap<Key<?>, Binding<?>> polymorphicBindings = new TreeMap<Key<?>, Binding<?>>(
				new Comparator<Key<?>>() {

					@Override
					public int compare(final Key<?> o1, final Key<?> o2) {
						if (!AdapterMap.class.equals(o1.getAnnotationType())
								|| !AdapterMap.class.equals(o2
										.getAnnotationType())) {
							throw new IllegalArgumentException(
									"Can only compare keys with AdapterMap annotations");
						}
						final AdapterMap a1 = (AdapterMap) o1.getAnnotation();
						final AdapterMap a2 = (AdapterMap) o2.getAnnotation();
						if (a1.adaptableType().equals(a2.adaptableType())) {
							return 0;
						} else if (a1.adaptableType().isAssignableFrom(
								a2.adaptableType())) {
							return -1;
						} else {
							return 1;
						}
					}
				});
		for (final Key<?> key : allBindings.keySet()) {
			if ((key.getAnnotationType() != null)
					&& AdapterMap.class.equals(key.getAnnotationType())) {
				final AdapterMap keyAnnotation = (AdapterMap) key
						.getAnnotation();
				// Guice will already have injected all bindings where the
				// adaptableType used in the method annotation is the same as
				// the one used in the key annotation.
				if (!methodAnnotation.adaptableType().equals(
						keyAnnotation.adaptableType())
						/*
						 * The annotation in the binding refers to a true
						 * super-type of instance runtime type (check, because
						 * if the type is the same, the default injector will
						 * already inject the values) IMPORTANT: we use type
						 * instead of methodAnnotation .adaptableType() here,
						 * because the runtime type of the to be injected
						 * IAdaptable is relevant
						 */
						&& keyAnnotation.adaptableType().isAssignableFrom(type)) {
					// System.out.println("Applying binding for " +
					// keyAnnotation.value() + " to " + type +
					// " as subtype of " + methodAnnotation.value());
					polymorphicBindings.put(key, allBindings.get(key));
				}
			}
		}
		return polymorphicBindings;
	}

	/**
	 * Performs the adapter map injection for the given adaptable instance.
	 * 
	 * @param adaptable
	 *            The adaptable to inject adapters into.
	 */
	protected void injectAdapters(final Object adaptable) {
		final SortedMap<Key<?>, Binding<?>> polymorphicBindings = getPolymorphicAdapterBindingKeys(
				adaptable.getClass(), method, methodAnnotation);
		// System.out.println("--");
		for (final Map.Entry<Key<?>, Binding<?>> entry : polymorphicBindings
				.entrySet()) {
			// System.out.println(((AdapterMap)entry.getKey().getAnnotation()).value());
			try {
				final Map<AdapterKey<?>, Object> target = entry
						.getValue()
						.acceptTargetVisitor(new AdapterBindingsTargetVisitor());
				if ((target != null) && !target.isEmpty()) {
					// System.out.println("Injecting " + method.getName()
					// + " of " + instance + " with " + target
					// + " based on " + entry.getValue());
					method.invoke(adaptable, target);
				}
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			} catch (final InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void injectMembers(final IAdaptable instance) {
		// inject all adapters bound by polymorphic AdapterBinding
		// annotations
		if (injector == null) {
			// IMPORTANT: it may happen that this member injector is
			// exercised before the type listener is injected. In such a
			// case we need to defer the injections until the injector is
			// available (bug #439949).
			deferredInstances.add(instance);
		} else {
			injectAdapters(instance);
		}
	}

	/**
	 * Sets the {@link Injector}, being used for adapter map injection.
	 * 
	 * @param injector
	 *            The {@link Injector} to use.
	 */
	@Inject
	public void setInjector(final Injector injector) {
		this.injector = injector;
		// perform deferred injections (if there have been any)
		for (final Object instance : deferredInstances) {
			injectAdapters(instance);
		}
		deferredInstances.clear();
	}

}