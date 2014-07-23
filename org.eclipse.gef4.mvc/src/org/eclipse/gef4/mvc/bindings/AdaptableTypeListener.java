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
package org.eclipse.gef4.mvc.bindings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * A specific {@link TypeListener} to support adaptable member injection.
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
				for (int i = 0; i < method.getParameterAnnotations().length; i++) {
					AdapterMap methodAnnotation = getAnnotation(
							method.getParameterAnnotations()[i],
							AdapterMap.class);
					// we have a method annotated with AdapterBinding
					if (methodAnnotation != null) {
						if (method.getParameterTypes().length != 1) {
							encounter
									.addError(
											"AdapterBinding annotation is only valid on one-parameter operations.",
											method);
						}
						// TODO: check parameter type is appropriate
						// System.out.println("Registering member injector to "
						// + type);
						AdapterMapInjector membersInjector = new AdapterMapInjector(
								method, methodAnnotation);
						if(injector != null){
							injector.injectMembers(membersInjector);
						}
						else {
							nonInjectedMemberInjectors.add(membersInjector);
						}
						encounter.register((MembersInjector<I>)membersInjector);
					}
				}
			}
		}
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
