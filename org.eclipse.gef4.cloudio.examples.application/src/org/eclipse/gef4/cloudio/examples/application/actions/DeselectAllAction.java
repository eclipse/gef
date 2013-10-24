/*******************************************************************************
* Copyright (c) 2011 Stephan Schwiebert. All rights reserved. This program and
* the accompanying materials are made available under the terms of the Eclipse
* Public License v1.0 which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* <p/>
* Contributors: Stephan Schwiebert - initial API and implementation
*******************************************************************************/
package org.eclipse.gef4.cloudio.examples.application.actions;

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
