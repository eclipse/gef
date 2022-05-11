/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #496777
 *
 *******************************************************************************/
package org.eclipse.gef.common.adapt.inject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport.LoggingMode;
import org.eclipse.gef.common.adapt.inject.AdapterMap.BoundAdapter;
import org.eclipse.gef.common.reflect.Types;

import com.google.common.reflect.TypeToken;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.multibindings.MapBinderBinding;
import com.google.inject.multibindings.MultibinderBinding;
import com.google.inject.multibindings.MultibindingsTargetVisitor;
import com.google.inject.multibindings.OptionalBinderBinding;
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

import javafx.beans.property.ReadOnlyObjectProperty;
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
 * 
 * Contributors:
 *    Alexander Nyssen (itemis AG) - initial API and implementation
 *    Markus Mühlbrandt (itemis AG) - compatibility with later Google Guice version (https://github.com/eclipse/gef/issues/102)
 *     
 */
public class AdapterInjector implements MembersInjector<IAdaptable> {

	private BindingTargetVisitor<Object, TypeToken<?>> ADAPTER_TYPE_INFERRER = new BindingTargetVisitor<Object, TypeToken<?>>() {

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
	};

	// XXX: The MapBinderBindings of relevance are wrapped into
	// ProviderInstanceBindings, so they an instance check is not sufficient
	// to retrieve them, but a MultibindingsTargetVisitor is to be used.
	private MultibindingsTargetVisitor<Object, MapBinderBinding<?>> ADAPTER_MAP_BINDING_FILTER = new MultibindingsTargetVisitor<Object, MapBinderBinding<?>>() {

		@Override
		public MapBinderBinding<?> visit(
				ConstructorBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public MapBinderBinding<?> visit(
				ConvertedConstantBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public MapBinderBinding<?> visit(
				ExposedBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public MapBinderBinding<?> visit(
				InstanceBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public MapBinderBinding<?> visit(
				LinkedKeyBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public MapBinderBinding<?> visit(
				MapBinderBinding<? extends Object> mapbinding) {
			return mapbinding;
		}

		@Override
		public MapBinderBinding<?> visit(
				MultibinderBinding<? extends Object> multibinding) {
			return null;
		}

		@Override
		public MapBinderBinding<?> visit(
				OptionalBinderBinding<? extends Object> optionalbinding) {
			return null;
		}

		@Override
		public MapBinderBinding<?> visit(
				ProviderBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public MapBinderBinding<?> visit(
				ProviderInstanceBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public MapBinderBinding<?> visit(
				ProviderKeyBinding<? extends Object> binding) {
			return null;
		}

		@Override
		public MapBinderBinding<?> visit(
				UntargettedBinding<? extends Object> binding) {
			return null;
		}
	};

	private final List<IAdaptable> deferredInstances = new ArrayList<>();

	private Injector injector;

	private final Method method;

	private LoggingMode loggingMode;

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
	 */
	public AdapterInjector(final Method method, LoggingMode loggingMode) {
		this.method = method;
		this.loggingMode = loggingMode;
	}

	private void deferAdapterInjection(IAdaptable adaptable,
			Runnable runnable) {
		if (adaptable instanceof IAdaptable.Bound) {
			@SuppressWarnings("unchecked")
			ReadOnlyObjectProperty<? extends IAdaptable> adaptableProperty = ((IAdaptable.Bound<? extends IAdaptable>) adaptable)
					.adaptableProperty();
			if (adaptableProperty.get() == null) {
				// defer until we have an adaptable and can test the rest of the
				// chain
				adaptableProperty.addListener(new ChangeListener<IAdaptable>() {
					@Override
					public void changed(
							ObservableValue<? extends IAdaptable> observable,
							IAdaptable oldValue, IAdaptable newValue) {
						if (newValue != null) {
							observable.removeListener(this);
							deferAdapterInjection(newValue, runnable);
						}
					}
				});
			} else {
				// test rest of the chain
				deferAdapterInjection(adaptableProperty.get(), runnable);
			}
		} else {
			// the chain is complete, thus perform the injection
			runnable.run();
		}
	}

	/**
	 * Infers the type of the given adapter, evaluating either the related
	 * bindings or the runtime type of the adapter.
	 *
	 * @param adapterKey
	 *            The key of the map binding, which is an {@link AdapterKey}.
	 * @param binding
	 *            The binding related to the {@link AdapterKey}.
	 * @param adapter
	 *            The adapter instance.
	 * @param issues
	 *            A list of issues that might be filled with error and warning
	 *            messages.
	 *
	 * @return A {@link TypeToken} representing the type of the given adapter
	 *         instance.
	 */
	private TypeToken<?> inferAdapterType(AdapterKey<?> adapterKey,
			Binding<?> binding, Object adapter, List<String> issues) {
		// try to infer the actual type of the adapter from the binding
		TypeToken<?> bindingInferredType = binding
				.acceptTargetVisitor(ADAPTER_TYPE_INFERRER);

		// perform some sanity checks
		validateAdapterBinding(adapterKey, binding, adapter,
				bindingInferredType, issues);

		// The key type always takes precedence. Otherwise, if we could
		// infer a type from the binding, we use that before falling back to
		// inferring the type from the adapter instance itself.
		TypeToken<?> bindingKeyType = adapterKey.getKey();
		return bindingKeyType != null ? bindingKeyType
				: (bindingInferredType != null ? bindingInferredType
						: TypeToken.of(adapter.getClass()));
	}

	/**
	 * Performs the adapter map injection for the given adaptable instance.
	 *
	 * @param adaptable
	 *            The adaptable to inject adapters into.
	 */
	protected void injectAdapters(final IAdaptable adaptable) {
		// defer until the adaptable.bound chain is complete
		deferAdapterInjection(adaptable, () -> {
			List<String> issues = new ArrayList<>();
			performAdapterInjection(adaptable, issues);
			for (String issue : issues) {
				if (LoggingMode.DEVELOPMENT.equals(loggingMode)
						|| issue.startsWith("*** ERROR")) {
					System.err.println(issue);
				}
			}
		});
	}

	@Override
	public void injectMembers(final IAdaptable instance) {
		if (injector == null) {
			// XXX: This member injector may be exercised before the injector
			// (from which the map bindings are inferred) is injected. In such a
			// case we need to defer the adapter injection until the injector is
			// available (bug #439949).
			deferredInstances.add(instance);
		} else {
			injectAdapters(instance);
		}
	}

	private boolean isContextApplicable(IAdaptable injectionTarget,
			BoundAdapter[] injectionContext) {
		// walk up the adaptable chain and see whether context elements can be
		// found
		int contextIndex = 0;
		String contextRole = injectionContext[contextIndex].adapterRole();
		TypeToken<?> contextType = Types
				.deserialize(injectionContext[contextIndex].adapterType());

		IAdaptable chainElement = injectionTarget;
		while (chainElement instanceof IAdaptable.Bound) {
			IAdaptable nextChainElement = ((IAdaptable.Bound<?>) chainElement)
					.getAdaptable();
			if (nextChainElement == null) {
				// this should not happen, as we defer injection
				// until the chain is complete
				throw new IllegalStateException(
						"Adapter injection seems to have been performed while the adaptable chain is not complete yet. The adaptable is not yet set.");
			}
			if (nextChainElement.getAdapterKey(chainElement) == null) {
				throw new IllegalStateException(
						"Adapter injection seems to have been performed while the adaptable chain is not complete yet. The adapter is not yet set.");
			}
			if (contextRole
					.equals(nextChainElement.getAdapterKey(chainElement)
							.getRole())
					&& Types.isAssignable(contextType,
							TypeToken.of(chainElement.getClass()))) {
				contextIndex++;
				if (contextIndex == injectionContext.length) {
					return true;
				}
				contextRole = injectionContext[contextIndex].adapterRole();
				contextType = Types.deserialize(
						injectionContext[contextIndex].adapterType());
			}
			chainElement = nextChainElement;
		}
		return false;
	}

	/**
	 * Performs the adapter map injection for the given adaptable instance.
	 *
	 * @param adaptable
	 *            The adaptable to inject adapters into.
	 * @param issues
	 *            The list of issues.
	 */
	private void performAdapterInjection(final IAdaptable adaptable,
			List<String> issues) {
		// XXX: We have to enter the scope before retrieving adapters
		// System.out.println("Entering scope of " + adaptable);
		AdaptableScopes.enter(adaptable);

		// check which bindings are applicable
		for (final Entry<Key<?>, Binding<?>> entry : injector.getAllBindings()
				.entrySet()) {
			// keep track of the applicable adapter map binding (so it can be
			// used for injection later)
			MapBinderBinding<?> adapterMapBinding = null;

			// only consider bindings that are qualified by an AdapterMap
			// binding annotation.
			Key<?> key = entry.getKey();
			Binding<?> binding = entry.getValue();

			if ((key.getAnnotationType() != null)
					&& AdapterMap.class.equals(key.getAnnotationType())) {
				final AdapterMap keyAnnotation = (AdapterMap) key
						.getAnnotation();
				if (keyAnnotation.adaptableType()
						.isAssignableFrom(adaptable.getClass())) {
					if (keyAnnotation.adaptableContext().length != 0) {
						// the adapter map binding is targeting a specific
						// context
						// if the adaptable is itself Adaptable.Bound and uses a
						// role for its registration, consider that role here
						if (isContextApplicable(adaptable,
								keyAnnotation.adaptableContext())) {
							// XXX: The MapBinderBindings of relevance are
							// wrapped into
							// ProviderInstanceBindings, so they an instance
							// check is not
							// sufficient
							// to retrieve them, but a
							// MultibindingsTargetVisitor is to be used.
							adapterMapBinding = binding.acceptTargetVisitor(
									ADAPTER_MAP_BINDING_FILTER);
						}
					} else {
						// XXX: All adapter (map) bindings that are bound to the
						// adaptable type, or to a super type or super interface
						// will be considered.

						// System.out.println("Applying binding for " +
						// keyAnnotation.value() + " to " + type +
						// " as subtype of " + methodAnnotation.value());

						// XXX: The MapBinderBindings of relevance are wrapped
						// into
						// ProviderInstanceBindings, so they an instance check
						// is not
						// sufficient
						// to retrieve them, but a MultibindingsTargetVisitor is
						// to be used.
						adapterMapBinding = binding.acceptTargetVisitor(
								ADAPTER_MAP_BINDING_FILTER);
					}
				}
			}

			if (adapterMapBinding != null) {
				for (final Entry<?, Binding<?>> adapterBinding : adapterMapBinding
						.getEntries()) {
					AdapterKey<?> adapterKey = (AdapterKey<?>) adapterBinding
							.getKey();
					Object adapter = adapterBinding.getValue().getProvider()
							.get();

					// determine adapter type
					TypeToken<?> adapterType = inferAdapterType(adapterKey,
							adapterBinding.getValue(), adapter, issues);

					// inject the adapter
					try {
						// System.out.println("Inject adapter " + adapter
						// + " with type " + adapterType + " for key "
						// + key + " to adaptable " + adaptable);
						method.setAccessible(true);
						method.invoke(adaptable, new Object[] { adapterType,
								adapter, adapterKey.getRole() });
					} catch (final IllegalAccessException e) {
						throw new IllegalStateException(e);
					} catch (final InvocationTargetException e) {
						issues.add("*** ERROR: Cannot inject binding "
								+ adapterBinding.getValue().getSource() + ": "
								+ e.getCause().getMessage());
					}
				}
			}
		}

		// System.out.println("Leaving scope of " + adaptable);
		AdaptableScopes.leave(adaptable);

		// System.out.println("Finished adapter injection for " + adaptable
		// + " with bindings " + adapterMapBindings);
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
		// perform injections for those instances that had to be exercised
		// before the injector was available (if there have been any)
		for (final IAdaptable instance : deferredInstances) {
			injectAdapters(instance);
		}
		deferredInstances.clear();
	}

	/**
	 * Validates that the given binding is not over or under specified.
	 *
	 * @param adapterKey
	 *            The key of the map binding, which is an {@link AdapterKey}.
	 * @param binding
	 *            The binding related to the {@link AdapterKey}.
	 * @param adapter
	 *            The adapter instance.
	 * @param bindingInferredType
	 *            The type that was inferred for the adapter instance.
	 * @param issues
	 *            A list of issues that might be filled with error and warning
	 *            messages.
	 */
	private void validateAdapterBinding(AdapterKey<?> adapterKey,
			Binding<?> binding, Object adapter,
			TypeToken<?> bindingInferredType, List<String> issues) {
		TypeToken<?> bindingKeyType = adapterKey.getKey();
		if (bindingInferredType != null) {
			if (bindingKeyType != null) {
				if (bindingKeyType.equals(bindingInferredType)) {
					// a key type is given and equals the inferred type;
					// issue a warning because of the superfluous
					// information
					issues.add(
							"*** INFO: The actual type of adapter " + adapter
									+ " could already be inferred as "
									+ bindingInferredType
									+ " from the binding at "
									+ binding.getSource() + ".\n"
									+ "          The redundant type key "
									+ bindingKeyType
									+ " may be omitted in the adapter key of the binding, using "
									+ (AdapterKey.DEFAULT_ROLE
											.equals(adapterKey.getRole())
													? "AdapterKey.defaultRole()"
													: " AdapterKey.role("
															+ adapterKey
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
								+ bindingInferredType + " from the binding at "
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
							issues.add("*** ERROR: The given key (raw) type "
									+ bindingKeyType.getRawType().getName()
									+ " does not match the actual (raw) type of adapter "
									+ adapter + " which was inferred as "
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
				issues.add("*** WARNING: The actual type of adapter " + adapter
						+ " could not be inferred from the binding at "
						+ binding.getSource()
						+ ". The adapter will only be retrievable via key types assignable to "
						+ TypeToken.of(adapter.getClass())
						+ ", which is the actual type inferred from the instance.\n"
						+ "             You should probably adjust your binding to provide a type key using "
						+ (AdapterKey.DEFAULT_ROLE
								.equals(adapterKey.getRole())
										? "AdapterKey.get(<type>)"
										: "AdapterKey.get(<type>, "
												+ adapterKey.getRole() + ")")
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

}