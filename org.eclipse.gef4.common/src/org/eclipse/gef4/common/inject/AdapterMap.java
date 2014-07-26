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

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.gef4.common.adapt.IAdaptable;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinderBinding;

/**
 * A {@link BindingAnnotation} that can be used to annotate the single parameter
 * (of type <code>Map&lt;AdapterKey&lt;?&gt;, Object&gt;</code>) of an
 * {@link IAdaptable}'s method (annotated with an {@link Inject} annotation) to
 * make it eligible for adapter map injection. The annotation is also used to
 * qualify related {@link AdapterMap} bindings (i.e. specific
 * {@link MapBinderBinding}s) within a {@link Module}.
 * <p>
 * In order to enable the adapter map injection mechanism, one of the
 * {@link Module} s being used to create the {@link Injector} has to bind an
 * {@link AdaptableTypeListener}. This {@link AdaptableTypeListener} will
 * register a specific {@link MembersInjector}, the {@link AdapterMapInjector},
 * whenever it encounters an {@link IAdaptable} type that is eligible for
 * adapter map injection. The {@link AdapterMapInjector} will in turn inject all
 * {@link IAdaptable} instances of that type, evaluating all {@link AdapterMap}
 * bindings that can be obtained from the {@link Injector} with which the
 * {@link AdaptableTypeListener} was injected. In detail, it will inject into an
 * {@link IAdaptable} all adapters, which are bound to an {@link AdapterMap}
 * annotation of a type ( {@link AdapterMap#adaptableType()}), which is either
 * the same or a super-type or super-interface of the {@link IAdaptable} to be
 * injected.
 * 
 * @author anyssen
 *
 * @see IAdaptable
 * @see AdaptableTypeListener
 * @see AdapterMapInjector
 */
@Documented
@Target({ PARAMETER })
@Retention(RUNTIME)
@BindingAnnotation
public @interface AdapterMap {

	/**
	 * The type used to qualify the {@link AdapterMap} annotation. It is used to
	 * infer which bindings are taken into consideration when performing adapter
	 * map injection on an {@link IAdaptable}'s method.
	 * <p>
	 * That is, when injecting a method with an adapter map only those bindings
	 * that are annotated with an {@link AdapterMap} annotation, whose
	 * {@link IAdaptable} type ( {@link #adaptableType()} ) is either the same
	 * or a super-type or super-interface of the to be injected
	 * {@link IAdaptable} instance's runtime type will be considered.
	 * <p>
	 * If a type is specified at an injection point (i.e. when annotating a
	 * method parameter within an {@link IAdaptable} annotation), it is simply
	 * ignored by the {@link AdapterMapInjector}. The runtime type of the to be
	 * injected {@link IAdaptable} instance is instead considered.
	 * <p>
	 * Please note that while the {@link AdapterMapInjector} ignores the type
	 * specified at the injection point (i.e. at the {@link AdapterMap}
	 * -annotated method parameter), Guice will still evaluate it and will
	 * require an {@link AdapterMap} binding for the type (i.e.
	 * {@link IAdaptable} if no type is specified and the default is used),
	 * unless the injection point is explicitly marked to be optional (
	 * <code>@Inject(optional = true)</code>), which is thus highly recommended.
	 * 
	 * @return The {@link Class} used as type of this {@link AdapterMap}.
	 *         {@link IAdaptable} by default.
	 */
	Class<?> adaptableType() default IAdaptable.class;
}