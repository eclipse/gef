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
package org.eclipse.gef4.mvc.fx.domain;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.eclipse.gef4.mvc.domain.AbstractDomain;
import org.eclipse.gef4.mvc.fx.MvcFxBundle;

import javafx.scene.Node;

/**
 * The {@link FXDomain} is an implementation of {@link AbstractDomain} which
 * binds the visual root type to {@link Node}.
 *
 * @author anyssen
 *
 */
public class FXDomain extends AbstractDomain<Node> {

	/**
	 * The adapter role for the content viewer.
	 */
	public static final String CONTENT_VIEWER_ROLE = "contentViewer";

	// XXX: This is a workaround for JDK-8143907, which happens in standalone
	// applications on Mac OS X El Capitan
	{
		// TODO: Remove when dropping support for JavaSE-1.7
		if (System.getProperty("java.version").startsWith("1.7.0")
				&& System.getProperty("os.name").equals("Mac OS X")
				&& MvcFxBundle.getContext() == null) {
			try {
				Class<?> macFontFinderClass = Class
						.forName("com.sun.t2k.MacFontFinder");
				Field psNameToPathMapField = macFontFinderClass
						.getDeclaredField("psNameToPathMap");
				psNameToPathMapField.setAccessible(true);
				psNameToPathMapField.set(null, new HashMap<String, String>());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
