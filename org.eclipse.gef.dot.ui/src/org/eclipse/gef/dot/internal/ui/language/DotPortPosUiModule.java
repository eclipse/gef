/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Use this class to register components to be used within the IDE.
 */
public class DotPortPosUiModule extends
		org.eclipse.gef.dot.internal.ui.language.AbstractDotPortPosUiModule {
	public DotPortPosUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
}
