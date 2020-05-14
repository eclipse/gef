/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.common.adapt.inject;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.reflect.Types;

import com.google.common.reflect.TypeToken;
import com.google.inject.BindingAnnotation;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * A {@link BindingAnnotation} that can be used to qualify adapter (map)
 * bindings, i.e. provide type information related to valid {@link IAdaptable}
 * injection points.
 * <p>
 * Clients should not use it directly, but rather query
 * {@link AdapterMaps#getAdapterMapBinder(com.google.inject.Binder, Class)} to
 * obtain a map binder that already qualifies its bindings with the respective
 * {@link AdapterMap} annotation for a given type. Adapter (map) bindings can
 * then be specified as follows:
 *
 * <pre>
 * // Obtain a map binder bound to MyAdaptable.
 * MapBinder&lt;AdapterKey&lt;?&gt;, Object&gt; adapterMapBinder = AdapterMaps.getAdapterMapBinder(binder(), MyAdaptable.class);
 *
 * // Bind instance of raw type 'A' as adapter with 'default' role to each MyAdaptable instance.
 * // The AdapterKey does not have to specify the adapter type, as it can be inferred from the binding and/or the adapter instance.
 * adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(A.class);
 *
 * // Bind instance of parameterized type 'B&lt;A&gt;' as adapter with 'r' role to each MyAdaptable instance.
 * // The AdapterKey does not have to specify the adapter type, as it can be inferred from the binding.
 * adapterMapBinder.addBinding(AdapterKey.role("r").to(new TypeLiteral&lt;B&lt;A&gt;&gt;(){});
 *
 * // Bind instance 'c' of parameterized type 'C&lt;A&gt;' as adapter with 'r' role to each MyAdaptable instance.
 * // The AdapterKey has to specify the adapter type, as it cannot be inferred from the binding or adapter instance.
 * adapterMapBinder.addBinding(AdapterKey.get(new TypeToken&lt;C&lt;A&gt;&gt;(){}, "r").toInstance(c);
 * </pre>
 *
 * If an {@link IAdaptable} marks itself as eligible for adapter injection (see
 * {@link InjectAdapters}), all adapter (map bindings) that are bound to a
 * {@link AdapterMap#adaptableType() type} (by being qualified with a respective
 * {@link AdapterMap} annotation), which is either the same or a super-type or
 * super-interface of the {@link IAdaptable} will be evaluated, and respective
 * adapters will be injected.
 * <p>
 * If the {@link AdapterMap} specifies a {@link AdapterMap#adaptableContext()
 * context}, adapters will only be injected, if the adaptable is itself resides
 * within a compatible context.
 * <p>
 * In order to enable adapter injection, {@link AdapterInjectionSupport} has to
 * be installed by one of the {@link Module}s used by the {@link Injector}.
 *
 * @author anyssen
 *
 * @see IAdaptable
 * @see AdaptableTypeListener
 * @see AdapterInjector
 */
@Documented
@Target({ PARAMETER })
@Retention(RUNTIME)
@BindingAnnotation
@interface AdapterMap {

	/**
	 * Characterizes a {@link org.eclipse.gef.common.adapt.IAdaptable.Bound
	 * bound} adapter by specifying its type and the role, via which it is
	 * {@link org.eclipse.gef.common.adapt.IAdaptable.Bound bound} to its
	 * {@link IAdaptable adaptable}. It can be used to specify a context element
	 * in the adaptable-adapter chain of the adaptable, into which adapters are
	 * to be injected.
	 * <p>
	 * The information captured by a {@link BoundAdapter} corresponds to that of
	 * an {@link AdapterKey}, which cannot be directly used to characterize the
	 * {@link AdapterMap#adaptableContext() context} within an
	 * {@link AdapterMap} because of the type restrictions that hold for
	 * annotation fields. This is also the cause for representing the type
	 * information not via a {@link TypeToken} directly, but through a
	 * {@link Types#serialize(TypeToken) serialized} string representation, that
	 * has to be {@link Types#deserialize(String) deserialized} into a
	 * {@link TypeToken}.
	 */
	@interface BoundAdapter {

		/**
		 * The default adapter role (if no specific role is to be used).
		 */
		public static final String DEFAULT_ROLE = "default";

		/**
		 * The role under which an adaptable, which itself is
		 * {@link org.eclipse.gef.common.adapt.IAdaptable.Bound}, is registered
		 * at its adaptable.
		 *
		 * @return The role under which the adaptable is bound to its (parent)
		 *         adaptable.
		 */
		String adapterRole() default DEFAULT_ROLE;

		/**
		 * The type of the adaptable that is bound with the specified role.
		 *
		 * @return The type of the adaptable as a serialized {@link TypeToken}
		 *         (see {@link Types#serialize(TypeToken)} and
		 *         {@link Types#deserialize(String)}).
		 */
		String adapterType();
	}

	/**
	 * The context of the adaptable to inject into. If specified the injection
	 * will be restricted to {@link IAdaptable}s with a compatible context only.
	 * <p>
	 * The context of an adaptable is compatible when respective context
	 * elements are visited in the given order when walking the
	 * adaptable-adapter chain, beginning with the adaptable in which to inject.
	 * The actual chain may contain additional elements, that do not correspond
	 * to context element, in between (which are ignored), but it has to contain
	 * the specified context elements in the given order.
	 *
	 * @return The context of the adaptable to inject into.
	 */
	BoundAdapter[] adaptableContext();

	/**
	 * The type used to qualify the {@link AdapterMap} annotation. It is used to
	 * infer which bindings are taken into consideration when performing adapter
	 * injection on an {@link IAdaptable}'s method.
	 * <p>
	 * That is, when injecting adapters into
	 * {@link IAdaptable#setAdapter(TypeToken, Object, String)} only those
	 * bindings that are annotated with an {@link AdapterMap} annotation, whose
	 * {@link IAdaptable} type ( {@link #adaptableType()} ) is either the same
	 * or a super-type or super-interface of the to be injected
	 * {@link IAdaptable} instance's runtime type will be considered.
	 *
	 * @return The {@link Class} used as type of this {@link AdapterMap}.
	 *         {@link IAdaptable} by default.
	 */
	Class<?> adaptableType() default IAdaptable.class;
}