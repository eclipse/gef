/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.gef4.zest.tests.dot;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.swt.widgets.Composite;

/**
 * Experimental stuff for transformation of DOT files or strings to Zest Graph
 * instances. These methods try creating an instance from the actual graph
 * subclass generated, by compiling the class (see different implementations of
 * GraphFromDot) and instantiating it via reflection.
 * <p/>
 * An alternative way to get an instance is to walk the AST and create a graph
 * via normal API. This would probably be easier, but for the preview
 * functionality in the wizard, it would be less accurate, because it would not
 * be a preview of the thing that is generated.
 * 
 * @author Fabian Steeg (fsteeg)
 */
final class ExperimentalDotImport {

	private ExperimentalDotImport() { /* Enforce non-instantiability */
	}

	/**
	 * EXPERIMENTAL - NOT REALLY WORKING YET
	 * <p/>
	 * Load a graph instance using a URLClassLoader
	 */
	static Graph loadGraph(final String graphName, final URL outputDirUrl,
			final Composite parent, final int style) {
		try {
			URLClassLoader ucl = getUrlClassLoader(outputDirUrl);
			Class<?> clazz = Class.forName("org.eclipse.gef4.zest.dot." + graphName, //$NON-NLS-1$
					true, ucl);
			for (Constructor<?> c : clazz.getConstructors()) {
				if (c.getParameterTypes().length == 2) {
					Object object = c.newInstance(parent, (Integer) style);
					return (Graph) object;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static URLClassLoader getUrlClassLoader(final URL outputDirUrl) {
		URL[] urls;
		List<URL> urlList = new ArrayList<URL>();
		if (outputDirUrl != null) {
			urlList.add(outputDirUrl);
		}
		urls = urlList.toArray(new URL[] {});
		/*
		 * We pass the current classloader as parent to make it find all the
		 * required classes when running as a plug-in:
		 */
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URLClassLoader ucl = new URLClassLoader(urls, loader);
		return ucl;
	}
}
