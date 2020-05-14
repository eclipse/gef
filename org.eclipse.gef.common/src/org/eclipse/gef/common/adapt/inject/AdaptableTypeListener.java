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
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #496777
 *
 *******************************************************************************/
package org.eclipse.gef.common.adapt.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport.LoggingMode;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * A specific {@link TypeListener} to support adapter injection. It will
 * register an {@link AdapterInjector}, which will perform the adapter
 * injection, for each {@link IAdaptable} that is eligible (see
 * {@link InjectAdapters}.
 * <p>
 * In order to function properly, an {@link AdaptableTypeListener} has to be
 * bound in a Guice {@link Module} as follows:
 *
 * <pre>
 * AdaptableTypeListener adaptableTypeListener = new AdaptableTypeListener();
 * requestInjection(adaptableTypeListener);
 * bindListener(Matchers.any(), adaptableTypeListener);
 * </pre>
 *
 * The call to <code>requestInjection()</code> is important to ensure that
 * {@link AdaptableTypeListener#setInjector(Injector)} will get injected.
 * Without it, the {@link AdaptableTypeListener} will not function properly.
 * <p>
 * Clients should not register an {@link AdaptableTypeListener} themselves but
 * rather install {@link AdapterInjectionSupport} in one of the {@link Module}s
 * that are used by the {@link Injector}.
 *
 * @see AdapterInjectionSupport
 *
 * @author anyssen
 *
 */
public class AdaptableTypeListener implements TypeListener {

	// the injector used to obtain adapter map bindings
	private Injector injector;

	private LoggingMode loggingMode;

	// used to keep track of members that are to be injected before we have
	// obtained the injector (bug #439949)
	private Set<AdapterInjector> nonInjectedMemberInjectors = new HashSet<>();

	/**
	 * Constructs a new {@link AdaptableTypeListener} and specifies the
	 * {@link LoggingMode} to use. If in {@link LoggingMode#DEVELOPMENT} mode,
	 * binding-related information, warning, and error messages will be printed.
	 * If in {@link LoggingMode#PRODUCTION} mode, only error messages will be
	 * printed, and information and warning messages will be suppressed.
	 *
	 * @param loggingMode
	 *            The {@link LoggingMode} to use.
	 */
	public AdaptableTypeListener(LoggingMode loggingMode) {
		this.loggingMode = loggingMode;
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
	protected boolean eligibleForAdapterInjection(final Method method) {
		// method has to be annotated with @InjectAdapters
		if (method.getAnnotation(InjectAdapters.class) != null) {
			// signature has to comply with IAdaptable#setAdapter(TypeToken,
			// Object, String).
			return method.getName().equals("setAdapter")
					&& method.getParameterTypes().length == 3
					&& method.getParameterTypes()[0].equals(TypeToken.class)
					&& method.getParameterTypes()[1].equals(Object.class)
					&& method.getParameterTypes()[2].equals(String.class);
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

	@SuppressWarnings("unchecked")
	@Override
	public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
		if (IAdaptable.class.isAssignableFrom(type.getRawType())) {
			// TODO: method check should be moved into members injector,
			// here, only the type + an additional operation should be checked.
			for (final Method method : type.getRawType().getMethods()) {
				// check that AdapterMap annotation is not used to mark
				// injection points (but only in bindings).
				for (int i = 0; i < method
						.getParameterAnnotations().length; i++) {
					AdapterMap adapterMapAnnotation = getAnnotation(
							method.getParameterAnnotations()[i],
							AdapterMap.class);
					if (adapterMapAnnotation != null) {
						encounter.addError(
								"@AdapterMap annotation may only be used in adapter map bindings, not to mark an injection point. Annotate method with @InjectAdapters instead.",
								method);
					}
				}

				// we have a method annotated with AdapterBinding
				if (eligibleForAdapterInjection(method)) {
					// check that no Guice @Inject annotation is present on the
					// method (so no interference occurs).
					Inject injectAnnotation = method
							.getAnnotation(Inject.class);
					if (injectAnnotation != null) {
						encounter.addError(
								"To prevent that Guice member injection interferes with adapter injection, no @Inject annotation may be used on a method that provides an @InjectAdapters annotation.");
					}

					// register member injector on the IAdaptable (and provide
					// the method to it, so it does not have to look it up
					// again).
					AdapterInjector membersInjector = new AdapterInjector(
							method, loggingMode);
					if (injector != null) {
						injector.injectMembers(membersInjector);
					} else {
						nonInjectedMemberInjectors.add(membersInjector);
					}
					// System.out.println("Registering member injector to "
					// + type);
					encounter.register((MembersInjector<I>) membersInjector);
				}
			}
		}
	}

	/**
	 * In order to work, the {@link AdaptableTypeListener} needs to obtain a
	 * reference to an {@link Injector}, which is forwarded to the
	 * {@link AdapterInjector}, which it registers for any {@link IAdaptable}
	 * encounters, to obtain the {@link AdapterMap} bindings to be injected.
	 *
	 * @param injector
	 *            The injector that is forwarded (used to inject) the
	 *            {@link AdapterInjector}.
	 */
	@Inject
	public void setInjector(Injector injector) {
		this.injector = injector;
		for (AdapterInjector memberInjector : nonInjectedMemberInjectors) {
			injector.injectMembers(memberInjector);
		}
		nonInjectedMemberInjectors.clear();
	}

}
