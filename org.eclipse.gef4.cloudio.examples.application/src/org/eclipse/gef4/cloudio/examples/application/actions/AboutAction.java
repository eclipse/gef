/*******************************************************************************
* Copyright (c) 2011 Stephan Schwiebert. All rights reserved. This program and
* the accompanying materials are made available under the terms of the Eclipse
* Public License v1.0 which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* <p/>
* Contributors: Stephan Schwiebert - initial API and implementation
*******************************************************************************/
package org.eclipse.gef4.cloudio.examples.application.actions;

import org.eclipse.gef4.cloudio.examples.application.about.AboutDialog;
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
