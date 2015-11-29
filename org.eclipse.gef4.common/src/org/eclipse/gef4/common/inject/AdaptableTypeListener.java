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
	private Set<AdapterMapInjector> nonInjectedMemberInjectors = new HashSet<>();

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
							// XXX: The AdapterMapInjector is only capable of
							// injecting into a method with a specific
							// signature. We have to ensure that it is valid
							// here.
							encounter.addError(
									"@AdapterMap annotation may only be applied to operation with signature 'setAdapter(TypeToken, Object, String)'.",
									method);
						}
						if (!IAdaptable.class.equals(
								adapterMapAnnotation.adaptableType())) {
							// XXX: As the adaptable type in the @AdapterMap
							// method annotation is never evaluated (but the
							// actual adaptable type is used instead),
							// specifying a type is not appropriate.
							encounter.addError(
									"Except when being used in bindings, @AdapterMap annotation may never specify an adaptable type. Please remove the specified type so that the default is used.",
									method);
						}

						// XXX: If the method specifying an @AdapterMap
						// annotation (on its first parameter) also specifies an
						// @Inject annotation, the default injector will already
						// inject all injection points, where the adaptableType
						// in the method parameter annotation is the same as the
						// one used in the key annotation. We thus guard this
						// here to prevent any interference.
						Inject injectAnnotation = method
								.getAnnotation(Inject.class);
						if (injectAnnotation != null) {
							encounter.addError(
									"To prevent that Guice member injection interferes with adapter injection, no @Inject annotation may be used on a method that provides an @AdapterMap annotation.");
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
	 * {@link IAdaptable#setAdapter(TypeToken, Object, String)}.
	 * 
	 * @param method
	 *            The {@link Method} to test.
	 * @return <code>true</code> if the method has a compatible signature,
	 *         <code>false</code> otherwise.
	 */
	protected boolean hasCompatibleAdapterMapInjectionSignature(
			final Method method) {
		try {
			Method adaptableMethod = IAdaptable.class
					.getDeclaredMethod("setAdapter", new Class[] {
							TypeToken.class, Object.class, String.class });
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
