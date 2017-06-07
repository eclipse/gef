/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui;

import org.eclipse.osgi.util.NLS;

/**
 * The {@link Messages} class contains all messages within GEF MVC.UI that can
 * be internationalized.
 *
 * @author anyssen
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.gef.mvc.fx.ui.messages"; //$NON-NLS-1$

	/**
	 * Stores the value of the <code>"SetPropertyValueCommand_Label"</code>
	 * message key.
	 */
	public static String SetPropertyValueCommand_Label;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
