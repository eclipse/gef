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
package org.eclipse.gef4.cloudio.internal.ui.actions;

import org.eclipse.gef4.cloudio.internal.ui.view.TagCloudView;
import org.eclipse.gef4.cloudio.ui.TagCloudViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * 
 * @author sschwieb
 *
 */
public abstract class AbstractTagCloudAction implements IWorkbenchWindowActionDelegate {

	private Shell shell;
	private TagCloudView tcViewPart;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.shell = window.getShell();
		tcViewPart = (TagCloudView) window.getActivePage().getActivePart();
	}

	public Shell getShell() {
		return shell;
	}

	protected TagCloudViewer getViewer() {
		return tcViewPart.getViewer();
	}

}
