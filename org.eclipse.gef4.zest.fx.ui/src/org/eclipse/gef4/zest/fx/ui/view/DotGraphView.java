/*******************************************************************************
 * Copyright (c) 2014 Fabian Steeg, hbz.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg, hbz - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.ui.view;

import java.net.MalformedURLException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.gef4.dot.DotImport;
import org.eclipse.gef4.internal.dot.export.DotFileUtils;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.ui.ZestFxUiModule;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

public class DotGraphView extends ZestFxUiView {

	private String currentDot = "digraph{}";

	public DotGraphView() {
		super(Guice.createInjector(Modules.override(new ZestFxModule())//
				.with(new ZestFxUiModule())));
		setGraph(new DotImport(currentDot).newGraphInstance());
	}

	@Override
	public void createPartControl(final Composite parent) {
		Action loadAction = new Action("Load *.dot file...") {
			@Override
			public void run() {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(
						parent.getShell(), root, IResource.FILE);
				if (dialog.open() == Window.OK) {
					Object[] selected = dialog.getResult();
					if (selected != null) {
						updateGraph((IFile) selected[0]);
					}
				}
			}
		};
		loadAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		getViewSite().getActionBars().getToolBarManager().add(loadAction);
		super.createPartControl(parent);
	}

	private boolean updateGraph(IFile file) {
		if (file == null || file.getLocationURI() == null || !file.exists()) {
			return false;
		}
		String dotString = currentDot;
		try {
			dotString = DotFileUtils.read(DotFileUtils.resolve(file
					.getLocationURI().toURL()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
		setGraph(new DotImport(dotString).newGraphInstance());
		currentDot = dotString;
		return true;
	}
}
