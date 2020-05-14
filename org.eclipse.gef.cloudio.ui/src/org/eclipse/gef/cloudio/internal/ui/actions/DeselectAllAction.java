/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.internal.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * 
 * @author sschwieb
 *
 */
public class DeselectAllAction extends AbstractTagCloudAction {

	@Override
	public void run(IAction action) {
		StructuredSelection selection = new StructuredSelection();
		getViewer().setSelection(selection);
	}

}
