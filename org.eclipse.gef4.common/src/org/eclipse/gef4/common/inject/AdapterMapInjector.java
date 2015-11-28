/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;

import com.google.common.reflect.TypeToken;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.multibindings.MapBinderBinding;
import com.google.inject.multibindings.MultibinderBinding;
import com.google.inject.multibindings.MultibindingsTargetVisitor;
import com.google.inject.spi.BindingTargetVisitor;
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

	/**
	 * Tries to infer the actual type of an adapter from bindings that are
	 * applied, which can only be supported in case linked bindings point to
	 * constructor bindings.
	 * 
	 * @author anyssen
	 *
	 */
	private class AdapterTypeBindingsTargetVisitor
			implements BindingTargetVisitor<Object, TypeToken<?>> {

		@Override
		public TypeToken<?> visit(InstanceBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public TypeToken<?> visit(
				ProviderInstanceBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public TypeToken<?> visit(
				ProviderKeyBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public TypeToken<?> visit(LinkedKeyBinding<? extends Object> binding) {
			Binding<?> linkedKeyBinding = injector
					.getBinding(binding.getLinkedKey());
			return linkedKeyBinding.acceptTargetVisitor(this);
		}

		@Override
		public TypeToken<?> visit(ExposedBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public TypeToken<?> visit(
				UntargettedBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public TypeToken<?> visit(
				ConstructorBinding<? extends Object> binding) {
			return TypeToken.of(binding.getKey().getTypeLiteral().getType());
		}

		@Override
		public TypeToken<?> visit(
				ConvertedConstantBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public TypeToken<?> visit(ProviderBinding<? extends Object> binding) {
			return null;
		}

	}

	/**
	 * Provides the to be injected adapters mapped to {@link AdapterKey}s,
	 * ensuring that the key type of the {@link AdapterKey} either corresponds
	 * to the one provided in the binding, or to the actual type (as far as this
	 * can be inferred). Supports {@link MapBinderBinding}s (in case map binder
	 * does not permit duplicates) and {@link ProviderInstanceBinding}s (in case
	 * map binder permits duplicates).
	 * 
	 * @author anyssen
	 *
	 */
	private class AdaptersTargetVisitor implements
			MultibindingsTargetVisitor<Object, Map<AdapterKey<?>, Object>> {
		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ConstructorBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ConvertedConstantBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ExposedBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final InstanceBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final LinkedKeyBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final MapBinderBinding<? extends Object> mapbinding) {
			// XXX: This is only used in case the MapBinder does not permit
			// duplicates
			final Map<AdapterKey<?>, Object> adapters = new HashMap<AdapterKey<?>, Object>();
			for (final Entry<?, Binding<?>> entry : mapbinding.getEntries()) {
				AdapterKey<?> key = (AdapterKey<?>) entry.getKey();

				Object adapter = entry.getValue().getProvider().get();
				// in case of a constructor binding, we may compute a better
				// type than the runtime type of the adapter.
				TypeToken<?> adapterType = entry.getValue().acceptTargetVisitor(
						new AdapterTypeBindingsTargetVisitor());
				if (adapterType != null) {
					// perform some checks in case a key is also given
					if (key.getKey() != null) {
						if (key.getKey().equals(adapterType)) {
							System.err.println(
									"*** WARNING: The actual type of adapter "
											+ adapter
											+ " could already be inferred as "
											+ adapterType
											+ " from the binding at "
											+ entry.getValue().getSource()
											+ ".");
							System.err.println(
									"             The redundant type key "
											+ key.getKey()
											+ " may thus be omitted in the adapter key of the binding, using AdapterKey.defaultRole() or AdapterKey.role(String) instead.");
						} else if (!adapterType.getRawType()
								.equals(key.getKey().getRawType())) {
							System.err
									.println("*** WARNING: The given key type "
											+ key.getKey()
											+ " does not seem to match the actual type of adapter "
											+ adapter
											+ " which was inferred as "
											+ adapterType
											+ " from the binding at "
											+ entry.getValue().getSource()
											+ ".");
							System.err.println(
									"             The adapter will only be retrievable via types assignable to "
											+ key.getKey() + ".");
						}
					}
				} else {
					adapterType = TypeToken.of(adapter.getClass());
				}
				// in case a key type is given, that takes precedence over the
				// inferred actual type.
				if (key.getKey() != null) {
					adapterType = key.getKey();
				}

				adapters.put(AdapterKey.get(adapterType, key.getRole()),
						adapter);
			}
			return adapters;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final MultibinderBinding<? extends Object> multibinding) {
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ProviderBinding<? extends Object> binding) {
			return null;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ProviderInstanceBinding<? extends Object> binding) {
			Map<AdapterKey<?>, Object> adapters = new HashMap<AdapterKey<?>, Object>();
			// XXX: This is only used in case the MapBinder permits duplicates
			Map<AdapterKey<?>, ?> adaptersByKeys = (Map<AdapterKey<?>, ?>) binding
					.getProviderInstance().get();
			for (AdapterKey<?> adapterKey : adaptersByKeys.keySet()) {
				// provider already provides adapters per AdapterKey
				for (Object adapter : (Set<Object>) adaptersByKeys
						.get(adapterKey)) {
					// determine proper adapter type (for registration of
					// adapter
					TypeToken keyType = adapterKey.getKey();
					// in case no type key is given, fall back to runtime type
					// of adapter.
					TypeToken<?> adapterType = keyType != null ? keyType
							: TypeToken.of(adapter.getClass());
					adapters.put(
							AdapterKey.get(adapterType, adapterKey.getRole()),
							adapter);
				}
			}
			return adapters;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ProviderKeyBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final UntargettedBinding<? extends Object> binding) {
			return null;
		}
	}

	private final List<Object> deferredInstances = new ArrayList<Object>();

	private Injector injector;

	private final Method method;
	private final AdapterMap methodAnnotation;

	/**
	 * Creates a new {@link AdapterMapInjector} to inject the given
	 * {@link Method}, annotated with the given {@link AdapterMap} method
	 * annotation.
	 * 
	 * @param method
	 *            The {@link Method} to be injected.
	 * @param methodAnnotation
	 *            The {@link AdapterMap} annotation specified at the single
	 *            parameter of the to be injected method.
	 */
	public AdapterMapInjector(final Method method,
			final AdapterMap methodAnnotation) {
		this.method = method;
		this.methodAnnotation = methodAnnotation;
	}

	/**
	 * Retrieves all adapter map bindings where the adaptable type of the map
	 * binding is a true super type or true super interface of the one referred
	 * to in the given method annotation, and assignable from the given
	 * adaptable type. The bindings are returned mapped to their keys, sorted
	 * following the inheritance hierarchy of their respective adaptable types.
	 * <p>
	 * As Guice will already inject those map bindings, where the adaptable type
	 * of the map binding is the same as the one given in the method annotation,
	 * these are ignored here. Instead, only those bindings are computed where
	 * the type in the method annotation is not equal to the one of the map
	 * binding, and where the type of the map binding is assignable from the
	 * given concrete adaptable type.
	 * 
	 * @param adaptableType
	 *            The concrete (runtime) adaptable type of the adaptable, whose
	 *            method is to be injected.
	 * @param method
	 *            The to be injected method of the adaptable type.
	 * @param methodAnnotation
	 *            The {@link AdapterMap} method annotation of the to be injected
	 *            adaptable's method.
	 * @return All adapter map bindings (mapped to their binding keys), sorted
	 *         along the type hierarchy of the bindings adaptable types, where
	 *         the adaptable type of the binding is a true super type or true
	 *         interface of the adaptable type given in the method annotation,
	 *         and is furthermore assignable from the given adaptable type.
	 */
	// TODO: Remove method parameter, which is unused
	protected SortedMap<Key<?>, Binding<?>> getPolymorphicAdapterMapBindings(
			final Class<?> adaptableType, final Method method,
			final AdapterMap methodAnnotation) {
		// find available keys
		final Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();
		// XXX: use a sorted map, where keys are sorted according to
		// hierarchy of annotation types (so we have polymorphic injection)
		final SortedMap<Key<?>, Binding<?>> polymorphicBindings = new TreeMap<Key<?>, Binding<?>>(
				new Comparator<Key<?>>() {

					@Override
					public int compare(final Key<?> o1, final Key<?> o2) {
						if (!AdapterMap.class.equals(o1.getAnnotationType())
								|| !AdapterMap.class
										.equals(o2.getAnnotationType())) {
							throw new IllegalArgumentException(
									"Can only compare keys with AdapterMap annotations");
						}
						final AdapterMap a1 = (AdapterMap) o1.getAnnotation();
						final AdapterMap a2 = (AdapterMap) o2.getAnnotation();
						if (a1.adaptableType().equals(a2.adaptableType())) {
							return 0;
						} else if (a1.adaptableType()
								.isAssignableFrom(a2.adaptableType())) {
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
				// If the method specifying an @AdapterMap annotation (on its
				// first parameter) would also specify an @Inject annotation,
				// the default injector would already inject all injection
				// points, where the adaptableType of the method parameter
				// annotation is the same as the one used in the key annotation.
				// In this case we would have to guard ourselves in the form:
				//
				// if(!methodAnnotation.adaptableType().equals(keyAnnotation.adaptableType()
				// &&
				// keyAnnotation.adaptableType().isAssignableFrom(adaptableType))
				// {
				// ...
				// }
				//
				// As the AdaptableTypeListener prevents that an @Inject
				// annotation is used on a method with @AdapterMap annotation,
				// we have to take care of this case ourselves.
				if (keyAnnotation.adaptableType()
						.isAssignableFrom(adaptableType)) {
					// XXX: we use 'keyAnnotation.adaptableType'
					// instead of 'methodAnnotation.adaptableType()' to
					// detect whether injection is to be performed, because
					// the runtime type of the to be injected IAdaptable is
					// relevant (not the one used in the method annotation).

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
		final SortedMap<Key<?>, Binding<?>> polymorphicBindings = getPolymorphicAdapterMapBindings(
				adaptable.getClass(), method, methodAnnotation);
		// System.out.println("--");
		for (final Map.Entry<Key<?>, Binding<?>> entry : polymorphicBindings
				.entrySet()) {
			try {
				final Map<AdapterKey<?>, Object> adaptersPerKey = entry
						.getValue()
						.acceptTargetVisitor(new AdaptersTargetVisitor());
				if ((adaptersPerKey != null) && !adaptersPerKey.isEmpty()) {
					for (AdapterKey<?> key : adaptersPerKey.keySet()) {
						// process all bindings
						Object adapter = adaptersPerKey.get(key);
						TypeToken<?> adapterType = key.getKey();
						String role = key.getRole();

						// System.out.println(
						// "Inject adapter " + adapter + " with type "
						// + adapterType + " for key " + key);
						method.invoke(adaptable,
								new Object[] { adapterType, adapter, role });
					}
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