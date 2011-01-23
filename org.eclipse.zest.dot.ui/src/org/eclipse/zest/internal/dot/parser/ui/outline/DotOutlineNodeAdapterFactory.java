/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.zest.internal.dot.parser.ui.outline;

import org.eclipse.xtext.ui.editor.outline.actions.DefaultContentOutlineNodeAdapterFactory;

public class DotOutlineNodeAdapterFactory extends
		DefaultContentOutlineNodeAdapterFactory {

	private static final Class<?>[] types = {
	// provide list of classes to adapt to, e.g.:
	// Entity.class
	// Service.class
	};

	@Override
	public Class<?>[] getAdapterList() {
		return types;
	}

}
