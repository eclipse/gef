/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.gef.common.adapt.IAdaptable;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * An annotation to mark an {@link IAdaptable} implementation class'
 * {@link IAdaptable#setAdapter(TypeToken, Object, String)} method as an
 * injection point for adapter injection:
 *
 * <pre>
 * &#64;InjectAdapters
 * public &lt;T&gt; void setAdapter(TypeToken&lt;T&gt; adapterType, T adapter, String role) {
 *   ...
 * }
 * </pre>
 *
 * If an {@link IAdaptable} thereby marks itself as eligible for adapter
 * injection, all adapter (map bindings) that are bound to a
 * {@link AdapterMap#adaptableType() type} (by being qualified with a respective
 * {@link AdapterMap} annotation), which is either the same or a super-type or
 * super-interface of the {@link IAdaptable} will be evaluated, and respective
 * adapters will be injected.
 * <p>
 * In order to enable adapter injection, {@link AdapterInjectionSupport} has to
 * be installed by one of the {@link Module}s used by the {@link Injector}.
 * {@link InjectAdapters} annotations should not be mixed with {@link Inject}
 * annotations.
 *
 * @author anyssen
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface InjectAdapters {

}
