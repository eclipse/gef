/*******************************************************************************
 * Copyright (c) 2014, 2015 Fabian Steeg (hbz), and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg (hbz) - initial API & implementation
 *     Matthias Wienand (itemis AG) - Refactorings and cleanups
 *     Alexander Nyßen (itemis AG) - Refactorings and cleanups
 *     Tamas Miklossy (itemis AG) - Refactoring of preferences (bug #446639)
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import java.io.File;
import java.net.MalformedURLException;

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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.dot.internal.DotExtractor;
import org.eclipse.gef4.dot.internal.DotFileUtils;
import org.eclipse.gef4.dot.internal.DotImport;
import org.eclipse.gef4.dot.internal.DotNativeDrawer;
import org.eclipse.gef4.dot.internal.parser.ui.internal.DotActivator;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef4.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

import javafx.scene.Scene;

/**
 * Render DOT content with ZestFx and Graphviz
 *
 * @author Fabian Steeg (fsteeg)
 * @author Alexander Nyßen (anyssen)
 *
 */
/* provisional API */@SuppressWarnings("restriction")
public class DotGraphView extends ZestFxUiView {

	public static final String STYLES_CSS_FILE = DotGraphView.class
			.getResource("styles.css") //$NON-NLS-1$
			.toExternalForm();
	private static final String EXTENSION = "dot"; //$NON-NLS-1$
	private static final String LOAD_DOT_FILE = DotUiMessages.DotGraphView_0;
	private static final String SYNC_IMPORT_DOT = DotUiMessages.DotGraphView_1;
	private static final String GRAPH_NONE = DotUiMessages.DotGraphView_2;
	private static final String GRAPH_RESOURCE = DotUiMessages.DotGraphView_3;
	private boolean listenToDotContent = false;
	private String currentDot = "digraph{}"; //$NON-NLS-1$
	private File currentFile = null;
	private Link resourceLabel = null;

	public DotGraphView() {
		super(Guice.createInjector(Modules.override(new DotGraphViewModule())
				.with(new ZestFxUiModule())));
		setGraph(new DotImport(currentDot).newGraphInstance());
	}

	@Override
	public void createPartControl(final Composite parent) {
		Action updateToggleAction = new UpdateToggle().action(this);
		Action loadFileAction = new LoadFile().action(this);
		add(updateToggleAction, ISharedImages.IMG_ELCL_SYNCED);
		add(loadFileAction, ISharedImages.IMG_OBJ_FILE);
		parent.setLayout(new GridLayout(1, true));
		initResourceLabel(parent, loadFileAction, updateToggleAction);
		super.createPartControl(parent);
		getCanvas().setLayoutData(new GridData(GridData.FILL_BOTH));
		Scene scene = getViewer().getScene();
		// specify stylesheet
		scene.getStylesheets().add(STYLES_CSS_FILE);
	}

	private void initResourceLabel(final Composite parent,
			final Action loadAction, final Action toggleAction) {
		resourceLabel = new Link(parent, SWT.WRAP);
		resourceLabel.setText(GRAPH_NONE);
		resourceLabel.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				processEvent(loadAction, toggleAction, GRAPH_NONE, e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				processEvent(loadAction, toggleAction, GRAPH_NONE, e);
			}

			private void processEvent(final Action loadFileAction,
					final Action toggleAction, final String label,
					SelectionEvent e) {
				/*
				 * As we use a single string for the links for localization, we
				 * don't compare specific strings but say the first link
				 * triggers the loadAction, else the toggleAction:
				 */
				if (label.replaceAll("<a>", "").startsWith(e.text)) { //$NON-NLS-1$ //$NON-NLS-2$
					loadFileAction.run();
				} else {
					// toggle as if the button was pressed, then run the action:
					toggleAction.setChecked(!toggleAction.isChecked());
					toggleAction.run();
				}
			}
		});
		resourceLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void add(Action action, String imageName) {
		action.setId(action.getText());
		action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(imageName));
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(action);
	}

	private void setGraphAsync(final String dot, final File file) {
		getViewSite().getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!dot.trim().isEmpty()) {
					DotImport dotImport = new DotImport(dot);
					if (dotImport.getErrors().size() > 0) {
						String message = String.format(
								"Could not import DOT: %s, DOT: %s", //$NON-NLS-1$
								dotImport.getErrors(), dot);
						DotActivator.getInstance().getLog()
								.log(new Status(
										Status.ERROR, DotActivator.getInstance()
												.getBundle().getSymbolicName(),
										message));
						return;
					}
					setGraph(dotImport.newGraphInstance());
					resourceLabel.setText(
							String.format(GRAPH_RESOURCE, file.getName()));
					resourceLabel.setToolTipText(file.getAbsolutePath());
				}
			}
		});

	}

	@Override
	public void setGraph(Graph graph) {
		super.setGraph(new Dot2ZestGraphConverter(graph).convert());
	}

	private boolean toggle(Action action, boolean input) {
		action.setChecked(!action.isChecked());
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		for (IContributionItem item : mgr.getItems()) {
			if (item instanceof ActionContributionItem
					&& ((ActionContributionItem) item).getAction() == action) {
				action.setChecked(!action.isChecked());
				return !input;
			}
		}
		return input;
	}

	private boolean updateGraph(File file) {
		if (file == null || !file.exists()) {
			return false;
		}

		currentFile = file;
		if (currentFile.getName().endsWith("." + EXTENSION)) { //$NON-NLS-1$
			currentDot = DotFileUtils.read(currentFile);
		} else {
			currentDot = new DotExtractor(currentFile).getDotString();
		}

		// if Graphviz 'dot' executable is available, we use it to augment
		// layout information
		if (GraphvizPreferencePage.isGraphvizConfigured()) {
			String[] result = DotNativeDrawer.executeDot(
					new File(GraphvizPreferencePage.getDotExecutablePath()),
					file, null, null);
			currentDot = result[0];
		}
		setGraphAsync(currentDot, currentFile);
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

	private class LoadFile {

		private String lastSelection = null;

		Action action(final DotGraphView view) {
			return new Action(DotGraphView.LOAD_DOT_FILE) {
				@Override
				public void run() {
					FileDialog dialog = new FileDialog(
							view.getViewSite().getShell(), SWT.OPEN);
					dialog.setFileName(lastSelection);
					String dotFileNamePattern = "*." + EXTENSION; //$NON-NLS-1$
					String embeddedDotFileNamePattern = "*.*"; //$NON-NLS-1$
					dialog.setFilterExtensions(new String[] {
							dotFileNamePattern, embeddedDotFileNamePattern });
					dialog.setFilterNames(new String[] {
							String.format("DOT file (%s)", dotFileNamePattern), //$NON-NLS-1$
							String.format("Embedded DOT Graph (%s)", //$NON-NLS-1$
									embeddedDotFileNamePattern) });
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
						IWorkbenchPart activeEditor = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage()
								.getActiveEditor();
						checkActiveEditorAndUpdateGraph(activeEditor);
						workspace.addResourceChangeListener(
								resourceChangeListener,
								IResourceChangeEvent.POST_BUILD
										| IResourceChangeEvent.POST_CHANGE);
						service.addSelectionListener(selectionChangeListener);
					} else {
						workspace.removeResourceChangeListener(
								resourceChangeListener);
						service.removeSelectionListener(
								selectionChangeListener);
					}
				}

			};

			selectionChangeListener = new ISelectionListener() {
				@Override
				public void selectionChanged(IWorkbenchPart part,
						ISelection selection) {
					checkActiveEditorAndUpdateGraph(part);
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
					if (resource.getType() == IResource.FILE
							&& ((IFile) resource).getName()
									.endsWith(EXTENSION)) {
						try {
							final IFile f = (IFile) resource;
							IWorkspaceRunnable workspaceRunnable = view
									.updateGraphRunnable(DotFileUtils.resolve(
											f.getLocationURI().toURL()));
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

		/**
		 * if the active editor is the DOT Editor, update the graph, otherwise
		 * do nothing
		 */
		private void checkActiveEditorAndUpdateGraph(IWorkbenchPart part) {
			if (part instanceof XtextEditor) {
				XtextEditor editor = (XtextEditor) part;
				if ("org.eclipse.gef4.dot.internal.parser.Dot" //$NON-NLS-1$
						.equals(editor.getLanguageName())
						&& editor.getEditorInput() instanceof FileEditorInput) {
					try {
						File resolvedFile = DotFileUtils.resolve(
								((FileEditorInput) editor.getEditorInput())
										.getFile().getLocationURI().toURL());
						if (!resolvedFile.equals(currentFile)) {
							updateGraph(resolvedFile);
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
