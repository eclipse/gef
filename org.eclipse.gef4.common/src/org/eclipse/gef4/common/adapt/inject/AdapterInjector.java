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
package org.eclipse.gef4.common.adapt.inject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.adapt.inject.AdapterInjectionSupport.LoggingMode;

import com.google.common.reflect.TypeToken;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * A specific {@link MembersInjector} that supports injection of adapters into
 * an {@link IAdaptable} implementation class'
 * {@link IAdaptable#setAdapter(TypeToken, Object, String)} method, that is
 * marked as being eligible for adapter injection (see {@link InjectAdapters}).
 * <p>
 * Being registered for a specific {@link IAdaptable} an {@link AdapterInjector}
 * will inject all instances of that type or any sub-type, evaluating all
 * {@link AdapterMap} bindings that can be obtained from the {@link Injector},
 * which was forwarded to it via {@link #setInjector(Injector)}. It will inject
 * all adapters, for which adapter (map) bindings with a matching
 * {@link AdapterMap} annotation exist. Here, matching means, that the type
 * provided in the {@link AdapterMap} annotation of the {@link IAdaptable}#s
 * method ( {@link AdapterMap#adaptableType()}) is either the same or a sub-type
 * of the type used with the {@link AdapterMap} annotation of the related
 * binding.
 * <p>
 * The {@link AdapterInjector} supports that type information about the actual
 * adapter type may be omitted from the adapter map binding (i.e. the used
 * {@link AdapterKey} only provides a role but no type key). It will try to
 * infer the actual adapter type from respective bindings, or fall back to the
 * type inferred from the adapter instance (which will not be adequate for
 * generic types because of type erasure) in such a case.
 *
 * @see AdapterMap
 * @see AdaptableTypeListener
 * @author anyssen
 */
public class AdapterInjector implements MembersInjector<IAdaptable> {

	/**
	 * Provides the to be injected adapters mapped to {@link AdapterKey}s,
	 * ensuring that the key type of the {@link AdapterKey} either corresponds
	 * to the one provided in the binding, or to the actual type (as far as this
	 * can be inferred).
	 *
	 * @author anyssen
	 *
	 */
	private class AdapterMapInferrer implements
			MultibindingsTargetVisitor<Object, Map<AdapterKey<?>, Object>> {

		private List<String> issues;

		/**
		 * Constructs a new AdapterMapInferrer.
		 *
		 * @param issues
		 *            A {@link String} list, to which the
		 *            {@link AdapterMapInferrer} may add its issues.
		 */
		public AdapterMapInferrer(List<String> issues) {
			this.issues = issues;
		}

		protected TypeToken<?> determineAdapterType(Binding<?> binding,
				AdapterKey<?> bindingKey, Object adapter) {
			// try to infer the actual type of the adapter from the binding
			TypeToken<?> bindingInferredType = binding
					.acceptTargetVisitor(new AdapterTypeInferrer());

			// perform some sanity checks
			validateBindings(adapter, binding, bindingKey, bindingInferredType);

			// The key type always takes precedence. Otherwise, if we could
			// infer a type from the binding, we use that before falling back to
			// inferring the type from the adapter instance itself.
			TypeToken<?> bindingKeyType = bindingKey.getKey();
			return bindingKeyType != null ? bindingKeyType
					: (bindingInferredType != null ? bindingInferredType
							: TypeToken.of(adapter.getClass()));
		}

		protected void validateBindings(Object adapter, Binding<?> binding,
				AdapterKey<?> bindingAdapterKey,
				TypeToken<?> bindingInferredType) {
			TypeToken<?> bindingKeyType = bindingAdapterKey.getKey();
			if (bindingInferredType != null) {
				if (bindingKeyType != null) {
					if (bindingKeyType.equals(bindingInferredType)) {
						// a key type is given and equals the inferred type;
						// issue a warning because of the superfluous
						// information
						issues.add("*** INFO: The actual type of adapter "
								+ adapter + " could already be inferred as "
								+ bindingInferredType + " from the binding at "
								+ binding.getSource() + ".\n"
								+ "          The redundant type key "
								+ bindingKeyType
								+ " may be omitted in the adapter key of the binding, using "
								+ (AdapterMap.DEFAULT_ROLE
										.equals(bindingAdapterKey.getRole())
												? "AdapterKey.defaultRole()"
												: " AdapterKey.role("
														+ bindingAdapterKey
																.getRole()
														+ ")")
								+ " instead.");
					} else {
						if (bindingInferredType
								.getType() instanceof ParameterizedType) {
							// we know (from a binding) that the actual type
							// is a parameterized type and the key type
							// is not equal, so this is a problem
							issues.add("*** WARNING: The given key type "
									+ bindingKeyType
									+ " does not seem to match the actual type of adapter "
									+ adapter + " which was inferred as "
									+ bindingInferredType
									+ " from the binding at "
									+ binding.getSource() + ".\n"
									+ "             The adapter will only be retrievable via key types assignable to "
									+ bindingKeyType
									+ ". You should probably adjust your binding.");
						} else {
							// the actual type (inferred from the
							// binding) is a raw type; the key raw type
							// should at least match this raw type
							if (!bindingInferredType.getRawType()
									.equals(bindingKeyType.getRawType())) {
								issues.add(
										"*** ERROR: The given key (raw) type "
												+ bindingKeyType.getRawType()
														.getName()
												+ " does not match the actual (raw) type of adapter "
												+ adapter
												+ " which was inferred as "
												+ bindingInferredType
												+ " from the binding at "
												+ binding.getSource() + ".\n"
												+ "           The adapter will only be retrievable via key types assignable to "
												+ bindingKeyType
												+ ". You need to adjust your binding.");
							}
						}
					}
				}
			} else {
				// no type could be inferred from the binding
				if (bindingKeyType == null) {
					issues.add(
							"*** WARNING: The actual type of adapter " + adapter
									+ " could not be inferred from the binding at "
									+ binding.getSource()
									+ ". The adapter will only be retrievable via key types assignable to "
									+ TypeToken.of(adapter.getClass())
									+ ", which is the actual type inferred from the instance.\n"
									+ "             You should probably adjust your binding to provide a type key using "
									+ (AdapterMap.DEFAULT_ROLE
											.equals(bindingAdapterKey.getRole())
													? "AdapterKey.get(<type>)"
													: "AdapterKey.get(<type>, "
															+ bindingAdapterKey
																	.getRole()
															+ ")")
									+ ".");

				} else {
					// check that at least key raw type and the type inferred
					// from the adapter instance match
					if (!bindingKeyType.getRawType()
							.isAssignableFrom(adapter.getClass())
							|| (!adapter.getClass().isAnonymousClass()
									&& !adapter.getClass().isAssignableFrom(
											bindingKeyType.getRawType()))) {
						issues.add("*** ERROR: The given key (raw) type "
								+ bindingKeyType.getRawType().getName()
								+ " does not match the actual (raw) type of adapter "
								+ adapter + ", which was inferred as "
								+ adapter.getClass().getName() + ".\n"
								+ "           You need to adjust your binding.");
					} else {
						// warn that the type could not be inferred and thus
						// both types have to match
						issues.add("*** WARNING: The actual type of adapter "
								+ adapter
								+ " could not be inferred from the binding at "
								+ binding.getSource()
								+ ". Therefore, the given type key "
								+ bindingKeyType + " can not be confirmed.\n"
								+ "             Make sure the provided type key "
								+ bindingKeyType
								+ " matches to the actual type of the adapter.");
					}
				}
			}
		}

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
			@SuppressWarnings("unchecked")
			Map.Entry<AdapterKey<?>, ?> entry = (Map.Entry<AdapterKey<?>, ?>) binding
					.getProvider().get();

			// TODO: check in which cases an instance binding is found and
			// whether its safe to assume that the entry is a provider in each
			// case.
			AdapterKey<?> key = entry.getKey();
			Object adapter = ((Provider<?>) entry.getValue()).get();

			// determine adapter type
			TypeToken<?> adapterType = determineAdapterType(binding, key,
					adapter);

			return Collections.<AdapterKey<?>, Object> singletonMap(
					AdapterKey.get(adapterType, key.getRole()), adapter);
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final LinkedKeyBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final MapBinderBinding<? extends Object> mapbinding) {
			final Map<AdapterKey<?>, Object> adapters = new HashMap<>();
			for (final Entry<?, Binding<?>> entry : mapbinding.getEntries()) {
				AdapterKey<?> key = (AdapterKey<?>) entry.getKey();
				Object adapter = entry.getValue().getProvider().get();

				// determine adapter type
				TypeToken<?> adapterType = determineAdapterType(
						entry.getValue(), key, adapter);

				adapters.put(AdapterKey.get(adapterType, key.getRole()),
						adapter);
			}
			return adapters;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final MultibinderBinding<? extends Object> multibinding) {
			Map<AdapterKey<?>, Object> adapters = new HashMap<>();
			for (Binding<?> binding : multibinding.getElements()) {
				// process the individual bindings
				adapters.putAll(binding.acceptTargetVisitor(this));
			}
			return adapters;
		}

		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ProviderBinding<? extends Object> binding) {
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<AdapterKey<?>, Object> visit(
				final ProviderInstanceBinding<? extends Object> binding) {
			Map<AdapterKey<?>, Object> adapters = new HashMap<>();
			Map<AdapterKey<?>, ?> adaptersOrProvidersByKeys = (Map<AdapterKey<?>, ?>) binding
					.getProviderInstance().get();
			for (AdapterKey<?> adapterKey : adaptersOrProvidersByKeys
					.keySet()) {
				// provider already provides adapters per AdapterKey
				Object adaptersOrProvider = adaptersOrProvidersByKeys
						.get(adapterKey);
				if (adaptersOrProvider instanceof Provider) {
					Object adapter = ((Provider<?>) adaptersOrProvider).get();
					TypeToken<?> adapterType = determineAdapterType(binding,
							adapterKey, adapter);
					adapters.put(
							AdapterKey.get(adapterType, adapterKey.getRole()),
							adapter);
				} else if (adaptersOrProvider instanceof Set) {
					for (Object adapter : (Set<?>) adaptersOrProvider) {
						// determine adapter type
						TypeToken<?> adapterType = determineAdapterType(binding,
								adapterKey, adapter);
						adapters.put(AdapterKey.get(adapterType,
								adapterKey.getRole()), adapter);
					}
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

	/**
	 * Tries to infer the actual type of an adapter from bindings that are
	 * applied (in case linked bindings point to constructor bindings).
	 *
	 * @author anyssen
	 *
	 */
	private class AdapterTypeInferrer
			implements BindingTargetVisitor<Object, TypeToken<?>> {

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
		public TypeToken<?> visit(ExposedBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public TypeToken<?> visit(InstanceBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public TypeToken<?> visit(LinkedKeyBinding<? extends Object> binding) {
			Binding<?> linkedKeyBinding = injector
					.getBinding(binding.getLinkedKey());
			return linkedKeyBinding.acceptTargetVisitor(this);
		}

		@Override
		public TypeToken<?> visit(ProviderBinding<? extends Object> binding) {
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
		public TypeToken<?> visit(
				UntargettedBinding<? extends Object> binding) {
			return null;
		}

	}

	private static final Comparator<Key<?>> ADAPTER_MAP_BINDING_KEY_COMPARATOR = new Comparator<Key<?>>() {

		@Override
		public int compare(final Key<?> o1, final Key<?> o2) {
			if (!AdapterMap.class.equals(o1.getAnnotationType())
					|| !AdapterMap.class.equals(o2.getAnnotationType())) {
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
	};

	private final List<IAdaptable> deferredInstances = new ArrayList<>();

	private Injector injector;

	private final Method method;

	private LoggingMode loggingMode;

	/**
	 * Creates a new {@link AdapterInjector} to inject the given {@link Method},
	 * annotated with the given {@link AdapterMap} method annotation.
	 *
	 * @param method
	 *            The {@link Method} to be injected.
	 */
	public AdapterInjector(final Method method) {
		this.method = method;
	}

	/**
	 * Creates a new {@link AdapterInjector} to inject the given {@link Method},
	 * annotated with the given {@link AdapterMap} method annotation.
	 * <p>
	 * If in {@link LoggingMode#DEVELOPMENT} mode, binding-related information,
	 * warning, and error messages will be printed. If in
	 * {@link LoggingMode#PRODUCTION} mode, only error messages will be printed,
	 * and information and warning messages will be suppressed.
	 *
	 * @param method
	 *            The {@link Method} to be injected.
	 * @param loggingMode
	 *            The {@link LoggingMode} to use.
	 * @since 1.1
	 */
	public AdapterInjector(final Method method, LoggingMode loggingMode) {
		this.method = method;
		this.loggingMode = loggingMode;
	}

	/**
	 * Performs the adapter map injection for the given adaptable instance.
	 *
	 * @param adaptable
	 *            The adaptable to inject adapters into.
	 */
	protected void injectAdapters(final IAdaptable adaptable) {
		List<String> issues = new ArrayList<>();
		injectAdapters(adaptable, issues);
		for (String issue : issues) {
			if (LoggingMode.DEVELOPMENT.equals(loggingMode)
					|| issue.startsWith("*** ERROR")) {
				System.err.println(issue);
			}
		}
	}

	/**
	 * Performs the adapter map injection for the given adaptable instance.
	 *
	 * @param adaptable
	 *            The adaptable to inject adapters into.
	 * @param issues
	 *            A {@link String} list, to which issues may be added that arise
	 *            during injection.
	 */
	protected void injectAdapters(final IAdaptable adaptable,
			final List<String> issues) {
		final Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();
		// XXX: Use a sorted map, where keys are sorted according to
		// hierarchy of annotation types

		final SortedMap<Key<?>, Binding<?>> directlyApplicableBindings = new TreeMap<>(
				ADAPTER_MAP_BINDING_KEY_COMPARATOR);
		final IdentityHashMap<Key<?>, Binding<?>> deferredBindings = new IdentityHashMap<>();
		for (final Key<?> key : allBindings.keySet()) {
			// only consider bindings that are qualified by an AdapterMap
			// binding annotation.
			if ((key.getAnnotationType() != null)
					&& AdapterMap.class.equals(key.getAnnotationType())) {
				final AdapterMap keyAnnotation = (AdapterMap) key
						.getAnnotation();
				if (keyAnnotation.adaptableType()
						.isAssignableFrom(adaptable.getClass())) {
					// TODO: check role
					if (!AdapterMap.DEFAULT_ROLE
							.equals(keyAnnotation.adaptableRole())) {
						// the adapter map binding is targeting a specific role
						// if the adaptable is itself Adaptable.Bound and uses a
						// role for its registration, consider that role here
						if (adaptable instanceof IAdaptable.Bound) {
							// if the adaptable is already registered as an
							// adaptable, we might evaluate the bindings
							// directly. Otherwise we have to defer the
							// evaluation until the adaptable is registered as
							// adapter.
							if (((IAdaptable.Bound<?>) adaptable)
									.getAdaptable() != null) {
								if (keyAnnotation.adaptableRole()
										.equals(((IAdaptable.Bound<?>) adaptable)
												.getAdaptable()
												.getAdapterKey(adaptable)
												.getRole())) {
									// add all bindings in case the roles match
									directlyApplicableBindings.put(key,
											allBindings.get(key));
								}
							} else {
								// defer the binding until the adaptable is
								// registered as an adapter itself.
								Binding<?> binding = allBindings.get(key);
								// System.out.println(
								// "Deferring binding " + binding);
								deferredBindings.put(key, binding);
							}
						}
					} else {
						// XXX: All adapter (map) bindings that are bound to the
						// adaptable type, or to a super type or super interface
						// will be considered.

						// System.out.println("Applying binding for " +
						// keyAnnotation.value() + " to " + type +
						// " as subtype of " + methodAnnotation.value());
						directlyApplicableBindings.put(key,
								allBindings.get(key));
					}
				}
			}
		}
		injectAdapters(adaptable, issues, directlyApplicableBindings);
		if (!deferredBindings.isEmpty()) {
			// defer injection until the adaptable is
			// registered as an adapter itself
			((IAdaptable.Bound<?>) adaptable).adaptableProperty()
					.addListener(new ChangeListener<IAdaptable>() {

						@Override
						public void changed(
								ObservableValue<? extends IAdaptable> observable,
								IAdaptable oldValue, IAdaptable newValue) {
							// if the adaptable is itself registered as adapter,
							// check if the role (under which it is registered
							// at its own adaptable) matches the role in the map
							// binding.
							if (newValue != null) {
								String adaptableRole = newValue
										.getAdapterKey(adaptable).getRole();
								// System.out.println(
								// "Evaluating deferred bindings for adaptable "
								// + adaptable + " with role "
								// + adaptableRole);
								final SortedMap<Key<?>, Binding<?>> deferredApplicableBindings = new TreeMap<>(
										ADAPTER_MAP_BINDING_KEY_COMPARATOR);
								for (final Key<?> key : deferredBindings
										.keySet()) {
									if (((AdapterMap) key.getAnnotation())
											.adaptableRole()
											.equals(adaptableRole)) {
										deferredApplicableBindings.put(key,
												allBindings.get(key));
									}
								}
								injectAdapters(adaptable, issues,
										deferredApplicableBindings);
								// if we defer the injection, we have to print
								// issues ourselves
								for (String issue : issues) {
									if (LoggingMode.DEVELOPMENT
											.equals(loggingMode)
											|| issue.startsWith("*** ERROR")) {
										System.err.println(issue);
									}
								}
								observable.removeListener(this);
							}
						}
					});
		}
	}

	/**
	 * Performs the adapter map injection with the given adapter map bindings
	 * for the given adaptable instance.
	 *
	 * @param adaptable
	 *            The adaptable to inject adapters into.
	 * @param issues
	 *            A {@link String} list, to which issues may be added that arise
	 *            during injection.
	 * @param adapterMapBindings
	 *            The bindings for the injection.
	 */
	protected void injectAdapters(final IAdaptable adaptable,
			List<String> issues,
			final SortedMap<Key<?>, Binding<?>> adapterMapBindings) {
		// XXX: Ensure scope is switched to the given adaptable instance so that
		// bound instances are re-used. This is necessary even though the scope
		// is entered in the constructor for domain, viewer, and parts, because
		// Guice creates all instances before putting them together.
		// FIXME: Moreover, enter, leave (clean-up), and swtich-to should be
		// properly integrated into Guice instead of having to call these
		// functions from the individual IAdaptables. Also, only IAdaptables
		// that are used as adapters can be scoped using the current mechanism.
		AdaptableScopes.switchTo(adaptable);

		// System.out.println("--");
		for (final Map.Entry<Key<?>, Binding<?>> entry : adapterMapBindings
				.entrySet()) {
			try {
				// retrieve the to be injected adapters, mapped to adapter keys.
				final Map<AdapterKey<?>, Object> adapterMap = entry.getValue()
						.acceptTargetVisitor(new AdapterMapInferrer(issues));

				if ((adapterMap != null) && !adapterMap.isEmpty()) {
					for (AdapterKey<?> key : adapterMap.keySet()) {
						// inject the adapter
						Object adapter = adapterMap.get(key);
						TypeToken<?> adapterType = key.getKey();
						String role = key.getRole();

						// System.out.println("Inject adapter " + adapter
						// + " with type " + adapterType + " for key "
						// + key + " to adaptable " + adaptable);
						method.setAccessible(true);
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
			// XXX: It may happen that this member injector is
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
		for (final IAdaptable instance : deferredInstances) {
			injectAdapters(instance);
		}
		deferredInstances.clear();
	}

}