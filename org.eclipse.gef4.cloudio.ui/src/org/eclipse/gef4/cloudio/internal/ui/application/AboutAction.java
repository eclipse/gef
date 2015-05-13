/******************************************************************************
 * Copyright (c) 2011, 2015 Stephan Schwiebert and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.cloudio.internal.ui.application;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * 
 * @author sschwieb
 *
 */
public class AboutAction extends Action implements IWorkbenchAction {
	
	public AboutAction() {
		super.setId("about");
		setText("About");
	}

	@Override
	public void run() {
		AboutDialog dialog = new AboutDialog(Display.getCurrent().getActiveShell());
		dialog.open();
	}

	@Override
	public void dispose() {
		
	}

}
