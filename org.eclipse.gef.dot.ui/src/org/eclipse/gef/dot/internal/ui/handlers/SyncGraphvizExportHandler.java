/*******************************************************************************
 * Copyright (c) 2015, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - Refactoring of DOT Graph view live update/live export (bug #337644)
 *                                - Add 'Open the exported file automatically' option (bug #521329)                                                                
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.handlers;

import java.io.File;
import java.net.MalformedURLException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.dot.internal.DotExecutableUtils;
import org.eclipse.gef.dot.internal.DotFileUtils;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.gef.dot.internal.ui.preferences.GraphvizPreferencePage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

public class SyncGraphvizExportHandler extends AbstractHandler {

	private static final String EXTENSION = "dot"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent executionEvent)
			throws ExecutionException {

		Event event = (Event) executionEvent.getTrigger();

		ToolItem toolItem = (ToolItem) event.widget;

		if (toolItem.getSelection()) {// the toggle button was switched on

			// check if Graphviz is configured properly
			if (!GraphvizPreferencePage.isGraphvizConfigured()) {
				GraphvizPreferencePage.showGraphvizConfigurationDialog();
			}

			// if Graphviz is still not configured properly, do not export
			if (!GraphvizPreferencePage.isGraphvizConfigured()) {
				toolItem.setSelection(false);
				return null;
			}
			addListeners();

			IWorkbenchPart activeEditor = HandlerUtil
					.getActiveWorkbenchWindow(executionEvent).getActivePage()
					.getActiveEditor();
			checkActiveEditorAndExportGraph(activeEditor);

		} else { // the toggle button was switched off
			removeListeners();
		}

		return null;
	}

	private void addListeners() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.addPartListener(partListener);

		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceChangeListener, IResourceChangeEvent.POST_BUILD
						| IResourceChangeEvent.POST_CHANGE);
	}

	private void removeListeners() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.removePartListener(partListener);

		ResourcesPlugin.getWorkspace()
				.removeResourceChangeListener(resourceChangeListener);
	}

	/**
	 * Listen to part life-cycle events(part activated) and export the graph.
	 */
	private IPartListener2 partListener = new IPartListener2() {

		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
			IWorkbenchPart part = partRef.getPart(false);
			checkActiveEditorAndExportGraph(part);
		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
		}
	};

	/** Listener that passes a visitor if a resource is changed. */
	private IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {

		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			if (event.getType() != IResourceChangeEvent.POST_BUILD
					&& event.getType() != IResourceChangeEvent.POST_CHANGE) {
				return;
			}
			IResourceDelta rootDelta = event.getDelta();
			try {
				rootDelta.accept(resourceDeltaVisitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	};

	/** If a *.dot file is visited, export the graph. */
	private IResourceDeltaVisitor resourceDeltaVisitor = new IResourceDeltaVisitor() {

		@Override
		public boolean visit(final IResourceDelta delta) {
			IResource resource = delta.getResource();
			if (resource.getType() == IResource.FILE
					&& ((IFile) resource).getName().endsWith(EXTENSION)) {
				try {
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					if (!workspace.isTreeLocked()) {
						IFile file = (IFile) resource;
						workspace.run(new DotExportRunnable(file), null);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
	};

	private class DotExportRunnable implements IWorkspaceRunnable {
		private IFile file;

		DotExportRunnable(IFile file) {
			this.file = file;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			exportGraph(file);
		}
	};

	/**
	 * if the active editor is the DOT Editor, export the graph, otherwise do
	 * nothing
	 */
	private void checkActiveEditorAndExportGraph(IWorkbenchPart part) {
		if (DotEditorUtils.isDotEditor(part)) {
			IEditorInput editorInput = ((EditorPart) part).getEditorInput();
			if (editorInput instanceof FileEditorInput) {
				IFile file = ((FileEditorInput) editorInput).getFile();
				exportGraph(file);
			}
		}
	}

	private void exportGraph(IFile inputFile) {
		/**
		 * do not try to export an empty dot file
		 */
		boolean isEmpty = "0  bytes" //$NON-NLS-1$
				.equals(IDEResourceInfoUtils.getSizeString(inputFile));
		if (isEmpty) {
			return;
		}

		File resolvedInputFile = null;
		try {
			resolvedInputFile = DotFileUtils
					.resolve(inputFile.getLocationURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}

		String dotExportFormat = GraphvizPreferencePage.getDotExportFormat();
		if (dotExportFormat.isEmpty()) {
			return;
		}
		String[] outputs = new String[2];

		File outputFile = DotExecutableUtils.renderImage(
				new File(GraphvizPreferencePage.getDotExecutablePath()),
				resolvedInputFile, dotExportFormat, null, outputs);

		// whenever the dot executable call produced any error message, show it
		// to the user within an error message box
		if (!outputs[1].isEmpty()) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					MessageDialog.openError(
							Display.getDefault().getActiveShell(),
							"Errors from dot call:", outputs[1]); //$NON-NLS-1$
				}
			});
		}

		// refresh the parent folder and open the output file if the export
		// was successful
		if (outputFile != null) {
			IFile outputEclipseFile = convertToEclipseFile(outputFile);
			if (outputEclipseFile != null) {
				refreshParent(outputEclipseFile);
				if (GraphvizPreferencePage
						.getDotOpenExportedFileAutomaticallyValue()) {
					openFile(outputEclipseFile);
				}
			}
		}
	}

	private IFile convertToEclipseFile(File file) {
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
				.findFilesForLocation(location);
		return files.length == 1 ? files[0] : null;
	}

	private void refreshParent(IFile file) {
		try {
			file.getParent().refreshLocal(IResource.DEPTH_ONE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void openFile(IFile file) {
		IEditorRegistry registry = PlatformUI.getWorkbench()
				.getEditorRegistry();
		if (registry.isSystemExternalEditorAvailable(file.getName())) {

			/**
			 * in case of opening the exported file from an other thread e.g. in
			 * case of listening to an IResourceChangeEvent
			 */
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					try {
						page.openEditor(new FileEditorInput(file),
								IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}