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
 * annotation of a type ( {@link AdapterMap#value()}), which is either the same
 * or a super-type or super-interface of the {@link IAdaptable} to be injected.
 * 
 * @author anyssen
 *
 * @see IAdaptable
 * @see AdaptableTypeListener
 * @see AdapterMapInjector
 */
@Target({ PARAMETER })
@Retention(RUNTIME)
@BindingAnnotation
public @interface AdapterMap {

	/**
	 * The type used to qualify the {@link AdapterMap} annotation. It will be
	 * used to infer which {@link AdapterMap} bindings are taken into
	 * consideration when performing adapter injection. That means that when
	 * injecting the members of an {@link IAdaptable}, only those bindings
	 * referring to the same or a super-type or super-interface of the
	 * {@link IAdaptable}'s type will be considered.
	 * 
	 * @return The {@link Class} used as type of this {@link AdapterMap}.
	 */
	Class<?> value();
}