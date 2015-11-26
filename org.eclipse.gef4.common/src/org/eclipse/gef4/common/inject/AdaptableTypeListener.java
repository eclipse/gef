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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * A specific {@link TypeListener} to support adapter map injection. It will
 * register an {@link AdapterMapInjector} for each {@link IAdaptable} it
 * encounters, which provides a method that:
 * <ul>
 * <li>is annotated with {@link Inject}</li>
 * <li>contains a single parameter of type
 * <code>Map&lt;AdapterKey&lt;?&gt;, Object&gt;</code>, which is annotated with
 * an {@link AdapterMap} annotation.</li>
 * </ul>
 * The registered {@link AdapterMapInjector} is in turn responsible of
 * performing the actual adapter map injection.
 * <P>
 * In order to function properly, an {@link AdaptableTypeListener} has to be
 * bound in a Guice {@link Module} as follows:
 * 
 * <pre>
 * AdaptableTypeListener adaptableTypeListener = new AdaptableTypeListener();
 * requestInjection(adaptableTypeListener);
 * bindListener(Matchers.any(), adaptableTypeListener);
 * </pre>
 * 
 * Here, the call to <code>requestInjection()</code> is important to ensure that
 * {@link AdaptableTypeListener#setInjector(Injector)} will get injected.
 * Without it, the {@link AdaptableTypeListener} will not function properly.
 *
 * @see AdapterMap
 * 
 * @author anyssen
 * 
 */
public class AdaptableTypeListener implements TypeListener {

	// the injector used to obtain adapter map bindings
	private Injector injector;

	// used to keep track of members that are to be injected before we have
	// obtained the injector (bug #439949)
	private Set<AdapterMapInjector> nonInjectedMemberInjectors = new HashSet<AdapterMapInjector>();

	/**
	 * In order to work, the {@link AdaptableTypeListener} needs to obtain a
	 * reference to an {@link Injector}, which is forwarded to the
	 * {@link AdapterMapInjector}, which it registers for any {@link IAdaptable}
	 * encounters, to obtain the {@link AdapterMap} bindings to be injected.
	 * 
	 * @param injector
	 *            The injector that is forwarded (used to inject) the
	 *            {@link AdapterMapInjector}.
	 */
	@Inject
	public void setInjector(Injector injector) {
		this.injector = injector;
		for (AdapterMapInjector memberInjector : nonInjectedMemberInjectors) {
			injector.injectMembers(memberInjector);
		}
		nonInjectedMemberInjectors.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
		if (IAdaptable.class.isAssignableFrom(type.getRawType())) {
			for (final Method method : type.getRawType().getMethods()) {
				for (int i = 0; i < method
						.getParameterAnnotations().length; i++) {
					AdapterMap adapterMapAnnotation = getAnnotation(
							method.getParameterAnnotations()[i],
							AdapterMap.class);
					// we have a method annotated with AdapterBinding
					if (adapterMapAnnotation != null) {
						if (hasCompatibleAdapterMapInjectionSignature(method)) {
							encounter.addError(
									"AdapterBinding annotation may only be applied to operation with operand types (AdapterKey, TypeToken, Object).",
									method);
						}
						// TODO: check parameter types are appropriate
						// System.out.println("Registering member injector to "
						// + type);
						AdapterMapInjector membersInjector = new AdapterMapInjector(
								method, adapterMapAnnotation);
						if (injector != null) {
							injector.injectMembers(membersInjector);
						} else {
							nonInjectedMemberInjectors.add(membersInjector);
						}
						encounter
								.register((MembersInjector<I>) membersInjector);
					}
				}
			}
		}
	}

	/**
	 * Checks that the given method complies to the signature of
	 * {@link IAdaptable#setAdapter(AdapterKey, TypeToken, Object)}.
	 * 
	 * @param method
	 *            The {@link Method} to test.
	 * @return <code>true<code> if the method has a compatible signature, <code>false</code>
	 *         otherwise.
	 */
	protected boolean hasCompatibleAdapterMapInjectionSignature(final Method method) {
		try {
			Method adaptableMethod = IAdaptable.class
					.getDeclaredMethod("setAdapter", new Class[] {
							AdapterKey.class, TypeToken.class, Object.class });
			return method.toString().equals(adaptableMethod);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private <T extends Annotation> T getAnnotation(Annotation[] annotations,
			Class<T> annotationType) {
		for (Annotation a : annotations) {
			if (annotationType.isAssignableFrom(a.annotationType())) {
				return (T) a;
			}
		}
		return null;
	}

}
