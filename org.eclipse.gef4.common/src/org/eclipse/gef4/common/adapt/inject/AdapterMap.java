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

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.gef4.common.adapt.IAdaptable;

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
public @interface AdapterMap {

	/**
	 * The default adaptable role (if no specific role is to be used).
	 */
	public static final String DEFAULT_ROLE = "default";

	/**
	 * An (optional) role that can be used to restrict adapter map bindings to
	 * those adaptable instances that provide the respective role.
	 *
	 * @return The adaptable role this {@link AdapterMap} is bound to.
	 */
	String adaptableRole() default DEFAULT_ROLE;

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