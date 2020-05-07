/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Use this class to register components to be used within the IDE.
 */
public class DotColorListUiModule extends
		org.eclipse.gef.dot.internal.ui.language.AbstractDotColorListUiModule {
	public DotColorListUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
}
