/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package org.eclipse.gef4.zest.internal.dot;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.zest.DotUiMessages;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

/**
 * View showing the Zest import for a DOT input. Listens to *.dot files and
 * other files with DOT content in the workspace and allows for image file
 * export via calling a local 'dot' (location is selected in a dialog and stored
 * in the preferences).
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class ZestGraphView extends ViewPart {

	public static final String ID = "org.eclipse.gef4.zest.dot.ZestView"; //$NON-NLS-1$

	private static final String ADD_EXPORT_QUESTION = DotUiMessages.ZestGraphView_0;
	private static final String ADD_EXPORT_MESSAGE = DotUiMessages.ZestGraphView_1
			+ DotUiMessages.ZestGraphView_2 + DotUiMessages.ZestGraphView_3;

	private static final String LOAD = DotUiMessages.ZestGraphView_4;
	private static final String RESET = DotUiMessages.ZestGraphView_5;
	private static final String LAYOUT = DotUiMessages.ZestGraphView_6;
	private static final String EXPORT = DotUiMessages.ZestGraphView_7;
	private static final String EXPORT_MODE = DotUiMessages.ZestGraphView_8;
	private static final String UPDATE_MODE = DotUiMessages.ZestGraphView_9;
	private static final String LINK_MODE = DotUiMessages.ZestGraphView_10;

	private static final String RESOURCES_ICONS_OPEN_GIF = "resources/icons/open.gif"; //$NON-NLS-1$
	private static final String RESOURCES_ICONS_EXPORT_GIF = "resources/icons/export.gif"; //$NON-NLS-1$
	private static final String RESOURCES_ICONS_RESET = "resources/icons/ask.gif"; //$NON-NLS-1$
	private static final String RESOURCES_ICONS_LAYOUT = "resources/icons/layout.gif"; //$NON-NLS-1$
	private static final String RESOURCES_ICONS_EXPORT_MODE = "resources/icons/export-mode.gif"; //$NON-NLS-1$
	private static final String RESOURCES_ICONS_UPDATE_MODE = "resources/icons/update-mode.gif"; //$NON-NLS-1$
	private static final String RESOURCES_ICONS_LINK_MODE = "resources/icons/link-mode.gif"; //$NON-NLS-1$

	private static final String EXTENSION = "dot"; //$NON-NLS-1$
	private static final String FORMAT_PDF = "pdf"; //$NON-NLS-1$
	private static final String FORMAT_PNG = "png"; //$NON-NLS-1$

	private boolean exportFromZestGraph = true;
	private boolean listenToDotContent = false;
	private boolean linkImage = false;

	private Composite composite;
	private Graph graph;
	private IFile file;

	private String dotString = ""; //$NON-NLS-1$
	private boolean addReference = true;

	/** Listener that passes a visitor if a resource is changed. */
	private IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {
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

	/**
	 * If a *.dot file or a file with DOT content is visited, we update the
	 * graph from it.
	 */
	private IResourceDeltaVisitor resourceVisitor = new IResourceDeltaVisitor() {
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
						public void run(final IProgressMonitor monitor)
								throws CoreException {
							setGraph(f);
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

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(final Composite parent) {
		composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		if (file != null) {
			try {
				updateGraph();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		addUpdateModeButton();
		addLoadButton();
		addLayoutButton();
		addResetButton();
		addExportModeButton();
		addExportButton();
		addLinkModeButton();
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(final String dot, boolean async) {
		dotString = dot;
		Runnable runnable = new Runnable() {
			public void run() {
				updateZestGraph(dot);
			}

			private void updateZestGraph(final String currentDot) {
				if (graph != null) {
					graph.dispose();
				}
				if (!dot.trim().isEmpty() && composite != null) {
					DotImport dotImport = new DotImport(dotString);
					if (dotImport.getErrors().size() > 0) {
						String message = String.format(
								"Could not import DOT: %s, DOT: %s", //$NON-NLS-1$
								dotImport.getErrors(), dotString);
						DotUiActivator
								.getDefault()
								.getLog()
								.log(new Status(Status.ERROR,
										DotUiActivator.PLUGIN_ID, message));
						return;
					}
					graph = dotImport.newGraphInstance(composite, SWT.NONE);
					setupLayout();
					composite.layout();
					graph.applyLayout();
					handleWikiText(currentDot);
					linkCorrespondingImage();
				}
			}
		};
		Display display = getViewSite().getShell().getDisplay();
		if (async) {
			display.asyncExec(runnable);
		} else {
			display.syncExec(runnable);
		}
	}

	protected void linkCorrespondingImage() {
		boolean canExportFromZest = exportFromZestGraph && graph != null;
		boolean canExportFromDot = !exportFromZestGraph && dotString != null;
		if (linkImage && (canExportFromZest || canExportFromDot)) {
			File image = generateImageFromGraph(true, FORMAT_PNG);
			openFile(image);
		}
	}

	private void addUpdateModeButton() {
		Action toggleUpdateModeAction = new Action(UPDATE_MODE, SWT.TOGGLE) {
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
		toggleUpdateModeAction.setImageDescriptor(DotUiActivator
				.getImageDescriptor(RESOURCES_ICONS_UPDATE_MODE));
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(toggleUpdateModeAction);
	}

	private void addExportModeButton() {
		Action toggleRenderingAction = new Action(EXPORT_MODE, SWT.TOGGLE) {
			public void run() {
				exportFromZestGraph = toggle(this, exportFromZestGraph);
			}
		};
		toggleRenderingAction.setId(toggleRenderingAction.getText());
		toggleRenderingAction.setImageDescriptor(DotUiActivator
				.getImageDescriptor(RESOURCES_ICONS_EXPORT_MODE));
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(toggleRenderingAction);
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

	private void addLayoutButton() {
		Action layoutAction = new Action(LAYOUT) {
			public void run() {
				if (graph != null) {
					graph.applyLayout();
				}
			}
		};
		layoutAction.setImageDescriptor(DotUiActivator
				.getImageDescriptor(RESOURCES_ICONS_LAYOUT));
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(layoutAction);
		mgr.add(new Separator());
	}

	private void addExportButton() {
		Action exportAction = new Action(EXPORT) {
			public void run() {
				if ((exportFromZestGraph && graph != null)
						|| (!exportFromZestGraph && dotString != null)) {
					File image = generateImageFromGraph(true, FORMAT_PDF);
					openFile(image);
				}
			}
		};
		exportAction.setImageDescriptor(DotUiActivator
				.getImageDescriptor(RESOURCES_ICONS_EXPORT_GIF));
		getViewSite().getActionBars().getToolBarManager().add(exportAction);
	}

	private void openFile(File file) {
		if (this.file == null) { // no workspace file for current graph
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(
					new Path("")); //$NON-NLS-1$
			fileStore = fileStore.getChild(file.getAbsolutePath());
			if (!fileStore.fetchInfo().isDirectory()
					&& fileStore.fetchInfo().exists()) {
				IWorkbenchPage page = getSite().getPage();
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
					getViewSite().getPage().openEditor(
							new FileEditorInput(copy),
							IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void addLinkModeButton() {
		Action linkModeAction = new Action(LINK_MODE, SWT.TOGGLE) {
			public void run() {
				linkImage = toggle(this, linkImage);
			}
		};
		linkModeAction.setId(linkModeAction.getText());
		linkModeAction.setImageDescriptor(DotUiActivator
				.getImageDescriptor(RESOURCES_ICONS_LINK_MODE));
		getViewSite().getActionBars().getToolBarManager().add(linkModeAction);
	}

	private void addResetButton() {
		Action resetAction = new Action(RESET) {
			public void run() {
				DotDirStore.setDotDirPath();
			}
		};
		resetAction.setImageDescriptor(DotUiActivator
				.getImageDescriptor(RESOURCES_ICONS_RESET));
		getViewSite().getActionBars().getToolBarManager().add(resetAction);
	}

	private void addLoadButton() {
		Action loadAction = new Action(LOAD) {
			public void run() {
				Shell shell = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell();
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(
						shell, root, IResource.FILE);
				if (dialog.open() == ResourceListSelectionDialog.OK) {
					Object[] selected = dialog.getResult();
					if (selected != null) {
						file = (IFile) selected[0];
						setGraph(file);
					}
				}
			}
		};
		loadAction.setImageDescriptor(DotUiActivator
				.getImageDescriptor(RESOURCES_ICONS_OPEN_GIF));
		getViewSite().getActionBars().getToolBarManager().add(loadAction);
	}

	private void setGraph(final IFile file) {
		this.file = file;
		try {
			updateGraph();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void updateGraph() throws MalformedURLException {
		if (file == null || file.getLocationURI() == null || !file.exists()) {
			return;
		}
		final String currentDot = dotExtraction() ? new DotExtractor(file)
				.getDotString() : DotFileUtils.read(DotFileUtils.resolve(file
				.getLocationURI().toURL()));
		if (currentDot.equals(dotString)
				|| currentDot.equals(DotExtractor.NO_DOT)) {
			return;
		}
		setGraph(currentDot, true);
	}

	private boolean dotExtraction() {
		return !file.getName().endsWith(EXTENSION); /*
													 * no need to extract if we
													 * listen to a *.dot file
													 */
	}

	private void handleWikiText(final String dot) {
		if (file == null) {
			return;
		}
		try {
			IEditorDescriptor editor = IDE.getEditorDescriptor(file);
			/*
			 * TODO get ID from registry, not MarkupEditor.ID internal API or
			 * hard-coded string IEditorRegistry registry =
			 * getSite().getWorkbenchWindow
			 * ().getWorkbench().getEditorRegistry();
			 */
			if (editor.getId().equals(
					"org.eclipse.mylyn.wikitext.ui.editor.markupEditor")) { //$NON-NLS-1$
				try {
					File image = generateImageFromGraph(true, FORMAT_PNG);
					File wikiFile = DotFileUtils.resolve(file.getLocationURI()
							.toURL());
					String imageLinkWiki = createImageLinkMarkup(image);
					if (!DotFileUtils.read(wikiFile).contains(imageLinkWiki)
							&& addReference && supported(file)) {
						String message = String.format(ADD_EXPORT_MESSAGE,
								file.getName());
						if (MessageDialog.openQuestion(getSite().getShell(),
								ADD_EXPORT_QUESTION, message)) {
							addReference(dot, wikiFile, imageLinkWiki);
						} else {
							addReference = false;
						}
					}
					refreshParent(file);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

	private boolean supported(final IFile wikiFile) {
		// TODO support other markup languages
		return wikiFile.getFileExtension().endsWith("textile"); //$NON-NLS-1$
	}

	private String createImageLinkMarkup(final File image) {
		// TODO support other markup languages
		return String.format("\n!%s!\n", image.getName()); //$NON-NLS-1$
	}

	private void addReference(final String dot, final File wikiFile,
			final String imageLinkWiki) {
		/*
		 * This approach only works for textile markup, where the code is marked
		 * only at the beginning
		 */
		String content = DotFileUtils.read(wikiFile).replace(dot,
				dot + "\n" + imageLinkWiki); //$NON-NLS-1$
		DotFileUtils.write(content, wikiFile);
	}

	private void setupLayout() {
		if (graph != null) {
			GridData gd = new GridData(GridData.FILL_BOTH);
			graph.setLayout(new GridLayout());
			graph.setLayoutData(gd);
		}
	}

	private File generateImageFromGraph(final boolean refresh,
			final String format) {
		DotExport dotExport = exportFromZestGraph ? new DotExport(graph)
				: new DotExport(dotString);
		File image = dotExport.toImage(DotDirStore.getDotDirPath(), format,
				null);
		if (file == null) {
			return image;
		}
		try {
			URL url = file.getParent().getLocationURI().toURL();
			File copy = DotFileUtils.copySingleFile(DotFileUtils.resolve(url),
					file.getName() + "." + format, image); //$NON-NLS-1$
			return copy;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (refresh) {
			refreshParent(file);
		}
		return image;
	}

	private void refreshParent(final IFile file) {
		try {
			file.getParent().refreshLocal(IResource.DEPTH_ONE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose() {
		super.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				resourceChangeListener);
		if (graph != null) {
			graph.dispose();
		}
		if (composite != null) {
			composite.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		if (graph != null && !graph.isDisposed()) {
			graph.setFocus();
		}
	}
}
