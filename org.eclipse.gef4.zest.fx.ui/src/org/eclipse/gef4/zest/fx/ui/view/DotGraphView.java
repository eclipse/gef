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
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef4.dot.DotImport;
import org.eclipse.gef4.internal.dot.export.DotFileUtils;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.ui.ZestFxUiModule;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

/**
 * Render DOT content with ZestFx
 *
 * @author Fabian Steeg (fsteeg)
 *
 */
public class DotGraphView extends ZestFxUiView {

	private boolean listenToDotContent = false;
	private static final String EXTENSION = "dot";
	private String currentDot = "digraph{}";

	/** Listener that passes a visitor if a resource is changed. */
	private IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {
		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			if (event.getType() != IResourceChangeEvent.POST_BUILD
					&& event.getType() != IResourceChangeEvent.POST_CHANGE) {
				return;
			}
			IResourceDelta rootDelta = event.getDelta();
			try {
				rootDelta.accept(resourceVisitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	};

	/** If a *.dot file is visited, update the graph. */
	private IResourceDeltaVisitor resourceVisitor = new IResourceDeltaVisitor() {
		@Override
		public boolean visit(final IResourceDelta delta) {
			IResource resource = delta.getResource();
			if (resource.getType() == IResource.FILE) {
				try {
					final IFile f = (IFile) resource;
					if (!listenToDotContent
							&& !f.getLocation().toString().endsWith(EXTENSION)) {
						return true;
					}
					IWorkspaceRunnable workspaceRunnable = new IWorkspaceRunnable() {
						@Override
						public void run(final IProgressMonitor monitor)
								throws CoreException {
							updateGraph(f);
						}
					};
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					if (!workspace.isTreeLocked()) {
						workspace.run(workspaceRunnable, null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		}

	};

	public DotGraphView() {
		super(Guice.createInjector(Modules.override(new ZestFxModule())//
				.with(new ZestFxUiModule())));
		setGraph(new DotImport(currentDot).newGraphInstance());
	}

	private void addLoadFileButton(final Composite parent) {
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
	}

	private void addUpdateModeButton() {
		Action toggleUpdateModeAction = new Action(
				"Sync with DOT content in the workspace", SWT.TOGGLE) {

			@Override
			public void run() {
				listenToDotContent = toggle(this, listenToDotContent);
				toggleResourceListener();
			}

			private void toggleResourceListener() {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				if (listenToDotContent) {
					workspace.addResourceChangeListener(resourceChangeListener,
							IResourceChangeEvent.POST_BUILD
							| IResourceChangeEvent.POST_CHANGE);
				} else {
					workspace
					.removeResourceChangeListener(resourceChangeListener);
				}
			}
		};
		toggleUpdateModeAction.setId(toggleUpdateModeAction.getText());
		toggleUpdateModeAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(toggleUpdateModeAction);

	}

	@Override
	public void createPartControl(final Composite parent) {
		addUpdateModeButton();
		addLoadFileButton(parent);
		super.createPartControl(parent);
	}

	private void setGraphAsync(final String dot) {
		getViewSite().getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!dot.trim().isEmpty()) {
					DotImport dotImport = new DotImport(dot);
					if (dotImport.getErrors().size() > 0) {
						System.err.println(String.format(
								"Could not import DOT: %s, DOT: %s",
								dotImport.getErrors(), dot));
						return;
					}
					setGraph(dotImport.newGraphInstance());
				}
			}
		});

	}

	private boolean toggle(Action action, boolean input) {
		action.setChecked(!action.isChecked());
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		for (IContributionItem item : mgr.getItems()) {
			if (item.getId() != null && item.getId().equals(action.getText())) {
				ActionContributionItem i = (ActionContributionItem) item;
				i.getAction().setChecked(!i.getAction().isChecked());
				return !input;
			}
		}
		return input;
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
		currentDot = dotString;
		setGraphAsync(dotString);
		return true;
	}

}
