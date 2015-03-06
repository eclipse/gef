/*******************************************************************************
 * Copyright (c) 2014, 2015 Fabian Steeg, hbz.
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
package org.eclipse.gef4.internal.dot.ui;

import java.io.File;
import java.net.MalformedURLException;

import javafx.scene.Scene;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
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
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.dot.DotImport;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.internal.dot.DotDrawer;
import org.eclipse.gef4.internal.dot.DotFileUtils;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef4.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

/**
 * Render DOT content with ZestFx and Graphviz
 *
 * @author Fabian Steeg (fsteeg)
 *
 */
/* provisional API */@SuppressWarnings("restriction")
public class DotGraphView extends ZestFxUiView {

	public static final String STYLES_CSS_FILE = ZestFxUiView.class
			.getResource("styles.css").toExternalForm(); //$NON-NLS-1$
	private static final String EXTENSION = "dot"; //$NON-NLS-1$
	private static final String LOAD_DOT_FILE = DotUiMessages.DotGraphView_0;
	private static final String SYNC_EXPORT_PDF = DotUiMessages.DotGraphView_1;
	private static final String SYNC_IMPORT_DOT = DotUiMessages.DotGraphView_2;
	private static final String GRAPH_NONE = DotUiMessages.DotGraphView_3;
	private static final String GRAPH_RESOURCE = DotUiMessages.DotGraphView_4;
	private static final String FORMAT_PDF = "pdf"; //$NON-NLS-1$
	private boolean listenToDotContent = false;
	private boolean linkImage = false;
	private String currentDot = "digraph{}"; //$NON-NLS-1$
	private File currentFile = null;
	private ExportToggle exportAction;
	private Label resourceLabel = null;

	public DotGraphView() {
		super(Guice.createInjector(Modules.override(new ZestFxUiModule())//
				.with(new ZestFxModule())));
		setGraph(new DotImport(currentDot).newGraphInstance());
	}

	@Override
	public void createPartControl(final Composite parent) {
		exportAction = new ExportToggle();
		add(new UpdateToggle().action(this), ISharedImages.IMG_ELCL_SYNCED);
		add(new LoadFile().action(this), ISharedImages.IMG_OBJ_FILE);
		add(exportAction.action(this), ISharedImages.IMG_ETOOL_PRINT_EDIT);
		parent.setLayout(new GridLayout(1, true));
		resourceLabel = new Label(parent, SWT.WRAP);
		resourceLabel.setText(GRAPH_NONE);
		resourceLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		super.createPartControl(parent);
		getCanvas().setLayoutData(new GridData(GridData.FILL_BOTH));
		Scene scene = getViewer().getScene();
		scene.getStylesheets().add(STYLES_CSS_FILE);
	}

	private void add(Action action, String imageName) {
		action.setId(action.getText());
		action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(imageName));
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(action);
	}

	private void setGraphAsync(final String dot, final File file) {
		final DotGraphView view = this;
		getViewSite().getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!dot.trim().isEmpty()) {
					DotImport dotImport = new DotImport(dot);
					if (dotImport.getErrors().size() > 0) {
						String message = String.format(
								"Could not import DOT: %s, DOT: %s", //$NON-NLS-1$
								dotImport.getErrors(), dot);
						DotUiActivator
								.getDefault()
								.getLog()
								.log(new Status(Status.ERROR,
										DotUiActivator.PLUGIN_ID, message));
						return;
					}
					setGraph(dotImport.newGraphInstance());
					exportAction.linkCorrespondingImage(view);
					resourceLabel.setText(String.format(GRAPH_RESOURCE,
							file.getName()));
					resourceLabel.setToolTipText(file.getAbsolutePath());
				}
			}
		});

	}

	@Override
	public void setGraph(Graph graph) {
		super.setGraph(new DotToZestGraphConverter(graph).convert());
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

	private boolean updateGraph(File file) {
		if (file == null || !file.exists()) {
			return false;
		}
		String dotString = currentDot;
		dotString = DotFileUtils.read(file);
		currentDot = dotString;
		setGraphAsync(dotString, file);
		return true;
	}

	private IWorkspaceRunnable updateGraphRunnable(final File f) {
		if (!listenToDotContent
				&& !f.getAbsolutePath().toString().endsWith(EXTENSION)) {
			return null;
		}
		IWorkspaceRunnable workspaceRunnable = new IWorkspaceRunnable() {
			@Override
			public void run(final IProgressMonitor monitor)
					throws CoreException {
				if (updateGraph(f)) {
					currentFile = f;
				}
			}
		};
		return workspaceRunnable;
	}

	private class ExportToggle {

		private File generateImageFromGraph(final boolean refresh,
				final String format, DotGraphView view) {
			File dotFile = DotFileUtils.write(view.currentDot);
			File image = DotDrawer.renderImage(
					new File(DotDirStore.getDotDirPath()), dotFile, format,
					null);
			if (view.currentFile == null) {
				return image;
			}
			File copy = DotFileUtils.copySingleFile(
					view.currentFile.getParentFile(),
					view.currentFile.getName() + "." + format, image); //$NON-NLS-1$
			if (refresh) {
				refreshParent(view.currentFile);
			}
			return copy;
		}

		private void openFile(File file, DotGraphView view) {
			if (view.currentFile == null) { // no workspace file for cur. graph
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(
						new Path("")); //$NON-NLS-1$
				fileStore = fileStore.getChild(file.getAbsolutePath());
				if (!fileStore.fetchInfo().isDirectory()
						&& fileStore.fetchInfo().exists()) {
					IWorkbenchPage page = view.getSite().getPage();
					try {
						IDE.openEditorOnFileStore(page, fileStore);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			} else {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IPath location = Path.fromOSString(file.getAbsolutePath());
				IFile copy = workspace.getRoot().getFileForLocation(location);
				IEditorRegistry registry = PlatformUI.getWorkbench()
						.getEditorRegistry();
				if (registry.isSystemExternalEditorAvailable(copy.getName())) {
					try {
						view.getViewSite()
								.getPage()
								.openEditor(
										new FileEditorInput(copy),
										IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void refreshParent(final File file) {
			try {
				ResourcesPlugin
						.getWorkspace()
						.getRoot()
						.getFileForLocation(
								Path.fromOSString(file.getAbsolutePath()))
						.getParent().refreshLocal(IResource.DEPTH_ONE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		Action action(final DotGraphView view) {
			return new Action(DotGraphView.SYNC_EXPORT_PDF, SWT.TOGGLE) {
				@Override
				public void run() {
					linkImage = toggle(this, linkImage);
					if (view.currentFile != null) {
						linkCorrespondingImage(view);
					}
				}
			};
		}

		void linkCorrespondingImage(DotGraphView view) {
			if (view.linkImage) {
				File image = generateImageFromGraph(true,
						DotGraphView.FORMAT_PDF, view);
				openFile(image, view);
			}
		}

	}

	private class LoadFile {

		private String lastSelection = null;

		Action action(final DotGraphView view) {
			return new Action(DotGraphView.LOAD_DOT_FILE) {
				@Override
				public void run() {
					FileDialog dialog = new FileDialog(view.getViewSite()
							.getShell(), SWT.OPEN);
					dialog.setFileName(lastSelection);
					String fileNamePattern = "*." + EXTENSION; //$NON-NLS-1$
					dialog.setFilterExtensions(new String[] { fileNamePattern });
					dialog.setFilterNames(new String[] { String.format(
							"DOT Graph (%s)", fileNamePattern) }); //$NON-NLS-1$
					String selection = dialog.open();
					if (selection != null) {
						lastSelection = selection;
						view.updateGraph(new File(selection));
					}
				}
			};
		}

	}

	private class UpdateToggle {

		/** Listener that passes a visitor if a resource is changed. */
		private IResourceChangeListener resourceChangeListener;

		/** If a *.dot file is visited, update the graph. */
		private IResourceDeltaVisitor resourceVisitor;

		/** Listen to selection changes and update graph in view. */
		protected ISelectionListener selectionChangeListener = null;

		Action action(final DotGraphView view) {

			Action toggleUpdateModeAction = new Action(
					DotGraphView.SYNC_IMPORT_DOT, SWT.TOGGLE) {

				@Override
				public void run() {
					listenToDotContent = toggle(this, listenToDotContent);
					toggleResourceListener();
				}

				private void toggleResourceListener() {
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					ISelectionService service = getSite().getWorkbenchWindow()
							.getSelectionService();
					if (view.listenToDotContent) {
						workspace.addResourceChangeListener(
								resourceChangeListener,
								IResourceChangeEvent.POST_BUILD
										| IResourceChangeEvent.POST_CHANGE);
						service.addSelectionListener(selectionChangeListener);
					} else {
						workspace
								.removeResourceChangeListener(resourceChangeListener);
						service.removeSelectionListener(selectionChangeListener);
					}
				}

			};

			selectionChangeListener = new ISelectionListener() {
				@Override
				public void selectionChanged(IWorkbenchPart part,
						ISelection selection) {
					if (part instanceof XtextEditor) {
						XtextEditor editor = (XtextEditor) part;
						if ("org.eclipse.gef4.internal.dot.parser.Dot" //$NON-NLS-1$
								.equals(editor.getLanguageName())
								&& editor.getEditorInput() instanceof FileEditorInput) {
							try {
								view.updateGraph(DotFileUtils
										.resolve(((FileEditorInput) editor
												.getEditorInput()).getFile()
												.getLocationURI().toURL()));
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
						}
					}

				}
			};

			resourceChangeListener = new IResourceChangeListener() {
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

			resourceVisitor = new IResourceDeltaVisitor() {
				@Override
				public boolean visit(final IResourceDelta delta) {
					IResource resource = delta.getResource();
					if (resource.getType() == IResource.FILE) {
						try {
							final IFile f = (IFile) resource;
							IWorkspaceRunnable workspaceRunnable = view
									.updateGraphRunnable(DotFileUtils.resolve(f
											.getLocationURI().toURL()));
							IWorkspace workspace = ResourcesPlugin
									.getWorkspace();
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
			return toggleUpdateModeAction;
		}
	}
}
