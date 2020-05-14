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

import org.eclipse.gef.cloudio.internal.ui.TagCloudViewer;
import org.eclipse.gef.cloudio.internal.ui.view.TagCloudView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * 
 * @author sschwieb
 *
 */
public abstract class AbstractTagCloudAction implements IWorkbenchWindowActionDelegate {

	private final class ActivationListener implements IPartListener {
		@Override
		public void partOpened(IWorkbenchPart part) {
			if (part instanceof TagCloudView) {
				tcViewPart = (TagCloudView) part;
			}
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			if (part == tcViewPart) {
				tcViewPart = null;
			}
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		@Override
		public void partActivated(IWorkbenchPart part) {
		}
	}

	private Shell shell;
	private TagCloudView tcViewPart;
	private ActivationListener activationListener;
	private IWorkbenchWindow window;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
		window.getPartService().removePartListener(activationListener);
		activationListener = null;
		window = null;
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
		this.shell = window.getShell();
		activationListener = new ActivationListener();
		window.getPartService().addPartListener(activationListener);
		if (window.getActivePage().getActivePart() instanceof TagCloudView) {
			tcViewPart = (TagCloudView) window.getActivePage().getActivePart();
		}
	}

	public Shell getShell() {
		return shell;
	}

	protected TagCloudViewer getViewer() {
		return tcViewPart.getViewer();
	}

}
