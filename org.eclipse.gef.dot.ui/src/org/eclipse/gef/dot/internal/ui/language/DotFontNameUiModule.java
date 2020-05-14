/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #542663)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Use this class to register components to be used within the IDE.
 */
public class DotFontNameUiModule extends
		org.eclipse.gef.dot.internal.ui.language.AbstractDotFontNameUiModule {
	public DotFontNameUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
}
