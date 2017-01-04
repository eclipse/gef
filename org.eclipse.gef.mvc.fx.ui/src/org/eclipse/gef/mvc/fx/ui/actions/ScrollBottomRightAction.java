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

import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;

/**
 *
 * @author mwienand
 *
 */
public class ScrollBottomRightAction extends AbstractViewerAction {

	/**
	 *
	 */
	public ScrollBottomRightAction() {
		super("Scroll to Bottom Right");
		setEnabled(true);
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return null;
	}

}