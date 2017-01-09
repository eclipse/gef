/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;

/**
 *
 * @author mwienand
 *
 */
public abstract class AbstractViewerActionGroup extends ActionGroup {

	@Override
	public abstract void fillActionBars(IActionBars bars);

}
