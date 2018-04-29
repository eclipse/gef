/*******************************************************************************
 * Copyright (c) 2014, 2018 itemis AG and others.
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
 *                                - Render embedded dot graphs in native mode (bug #493694)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.dot.internal.DotExecutableUtils;
import org.eclipse.gef.dot.internal.DotExtractor;
import org.eclipse.gef.dot.internal.DotFileUtils;
import org.eclipse.gef.dot.internal.DotImport;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.ui.actions.FitToViewportAction;
import org.eclipse.gef.mvc.fx.ui.actions.FitToViewportActionGroup;
import org.eclipse.gef.mvc.fx.ui.actions.ScrollActionGroup;
import org.eclipse.gef.mvc.fx.ui.actions.ZoomActionGroup;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.util.Modules;

import javafx.application.Platform;
import javafx.scene.Scene;

/**
 * Render DOT content with ZestFx and Graphviz
 *
 * @author Fabian Steeg (fsteeg)
 * @author Alexander Nyßen (anyssen)
 *
 */
/* provisional API */@SuppressWarnings("restriction")
public class DotGraphView extends ZestFxUiView implements IShowInTarget {

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

	@Inject
	private Dot2ZestGraphCopier dot2ZestGraphCopier;

	private IPropertyChangeListener preferenceChangeListener = new IPropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty()
					.equals(GraphvizPreferencePage.DOT_PATH_PREF_KEY)) {
				// we may enter or leave native mode, so update the graph
				updateGraph(currentFile);
			}
		}
	};

	private ZoomActionGroup zoomActionGroup;
	private FitToViewportActionGroup fitToViewportActionGroup;
	private ScrollActionGroup scrollActionGroup;

	public DotGraphView() {
		super(Guice.createInjector(Modules.override(new DotGraphViewModule())
				.with(new ZestFxUiModule())));
	}

	@Override
	protected void activate() {
		super.activate();
		List<Graph> importDot = new DotImport().importDot(currentDot);
		setGraph(importDot.isEmpty() ? null : importDot.get(0));
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		GraphvizPreferencePage.dotUiPrefStore()
				.addPropertyChangeListener(preferenceChangeListener);
	}

	protected boolean isNativeMode() {
		return GraphvizPreferencePage.isGraphvizConfigured();
	}

	@Override
	public void dispose() {
		GraphvizPreferencePage.dotUiPrefStore()
				.removePropertyChangeListener(preferenceChangeListener);
		currentDot = null;
		currentFile = null;

		if (fitToViewportActionGroup != null) {
			getContentViewer().unsetAdapter(fitToViewportActionGroup);
			fitToViewportActionGroup.dispose();
			fitToViewportActionGroup = null;
		}
		if (zoomActionGroup != null) {
			getContentViewer().unsetAdapter(zoomActionGroup);
			zoomActionGroup.dispose();
			zoomActionGroup = null;
		}
		if (scrollActionGroup != null) {
			getContentViewer().unsetAdapter(scrollActionGroup);
			scrollActionGroup.dispose();
			scrollActionGroup = null;
		}

		getContentViewer().contentsProperty().clear();

		super.dispose();
	}

	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);

		// actions
		Action updateToggleAction = new UpdateToggle().action(this);
		Action loadFileAction = new LoadFile().action(this);
		add(updateToggleAction, ISharedImages.IMG_ELCL_SYNCED);
		add(loadFileAction, ISharedImages.IMG_OBJ_FILE);

		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager mgr = actionBars.getToolBarManager();
		mgr.add(new Separator());

		zoomActionGroup = new ZoomActionGroup();
		getContentViewer().setAdapter(zoomActionGroup);
		fitToViewportActionGroup = new FitToViewportActionGroup();
		getContentViewer().setAdapter(fitToViewportActionGroup);
		scrollActionGroup = new ScrollActionGroup();
		getContentViewer().setAdapter(scrollActionGroup);

		zoomActionGroup.fillActionBars(actionBars);
		mgr.add(new Separator());
		fitToViewportActionGroup.fillActionBars(actionBars);
		mgr.add(new Separator());
		scrollActionGroup.fillActionBars(actionBars);

		// controls
		parent.setLayout(new GridLayout(1, true));
		initResourceLabel(parent, loadFileAction, updateToggleAction);
		getCanvas().setLayoutData(new GridData(GridData.FILL_BOTH));

		// scene
		Scene scene = getContentViewer().getCanvas().getScene();
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
		if (imageName != null) {
			action.setImageDescriptor(PlatformUI.getWorkbench()
					.getSharedImages().getImageDescriptor(imageName));
		}
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(action);
	}

	private void setGraphAsync(final String dot, final File file) {
		getViewSite().getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (!dot.trim().isEmpty()) {
					try {
						List<Graph> importDot = new DotImport().importDot(dot);
						setGraph(importDot.isEmpty() ? null : importDot.get(0));
					} catch (Exception e) {
						e.printStackTrace();
						String message = String.format(
								"Could not import DOT: %s, DOT: %s", //$NON-NLS-1$
								e.getMessage(), dot);
						DotActivator.getInstance().getLog()
								.log(new Status(
										Status.ERROR, DotActivator.getInstance()
												.getBundle().getSymbolicName(),
										message));
						// whenever the dot file could not be imported,
						// show the exception information to the user within an
						// error dialog (not only in the error log)
						MultiStatus status = createMultiStatus(
								e.getLocalizedMessage(), e);
						ErrorDialog.openError(getViewSite().getShell(), "Error", //$NON-NLS-1$
								"Could not import DOT", status); //$NON-NLS-1$

						return;
					}
					resourceLabel.setText(
							String.format(GRAPH_RESOURCE, file.getName())
									+ (isNativeMode() ? " [native]" //$NON-NLS-1$
											: " [emulated]")); //$NON-NLS-1$
					resourceLabel.setToolTipText(file.getAbsolutePath());
				}
			}

			private MultiStatus createMultiStatus(String localizedMessage,
					Throwable t) {
				List<Status> childStatuses = new ArrayList<>();

				String pluginId = DotActivator.getInstance().getBundle()
						.getSymbolicName();

				Status status = new Status(IStatus.ERROR, pluginId, dot);
				childStatuses.add(status);

				MultiStatus ms = new MultiStatus(pluginId, IStatus.ERROR,
						childStatuses.toArray(new Status[] {}), t.toString(),
						t);
				return ms;
			}
		});

	}

	@Override
	public void setGraph(Graph graph) {
		// do no convert layout algorithm and rankdir in emulated mode, invert
		// y-axis mode (as by default y-axis is interpreted inverse in dot)
		boolean isNativeMode = isNativeMode();
		dot2ZestGraphCopier.getAttributeCopier()
				.options().emulateLayout = !isNativeMode;
		dot2ZestGraphCopier.getAttributeCopier().options().invertYAxis = false;
		super.setGraph(dot2ZestGraphCopier.copy(graph));

		// adjust viewport to scroll to top-left
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				InfiniteCanvas canvas = ((InfiniteCanvasViewer) getContentViewer())
						.getCanvas();
				canvas.setHorizontalScrollOffset(
						canvas.getHorizontalScrollOffset()
								- canvas.getContentBounds().getMinX());
				canvas.setVerticalScrollOffset(canvas.getVerticalScrollOffset()
						- canvas.getContentBounds().getMinY());
			}
		});
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
		boolean isEmbeddedDotFile = !currentFile.getName()
				.endsWith("." + EXTENSION); //$NON-NLS-1$

		DotExtractor dotExtractor = null;
		if (isEmbeddedDotFile) {
			dotExtractor = new DotExtractor(currentFile);
			currentDot = dotExtractor.getDotString();
		} else {
			currentDot = DotFileUtils.read(currentFile);
		}

		// if Graphviz 'dot' executable is available, we use it for layout
		// (native mode); otherwise we emulate layout with GEF Layout
		// algorithms.
		if (isNativeMode()) {
			// System.out.println("[DOT Input] [" + currentDot + "]");
			String[] result;
			if (isEmbeddedDotFile) {
				File tempDotFile = dotExtractor.getDotTempFile();
				if (tempDotFile == null) {
					return false;
				}
				result = DotExecutableUtils.executeDot(
						new File(GraphvizPreferencePage.getDotExecutablePath()),
						true, tempDotFile, null, null);
				tempDotFile.delete();
			} else {
				result = DotExecutableUtils.executeDot(
						new File(GraphvizPreferencePage.getDotExecutablePath()),
						true, file, null, null);
			}
			currentDot = result[0];
			// System.out.println("[DOT Output] [" + currentDot + "]");
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
			if (DotEditorUtils.isDotEditor(part)) {
				IEditorInput editorInput = ((EditorPart) part).getEditorInput();
				if (editorInput instanceof FileEditorInput) {
					IFile file = ((FileEditorInput) editorInput).getFile();
					try {
						File resolvedFile = DotFileUtils
								.resolve(file.getLocationURI().toURL());
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

	@Override
	public boolean show(ShowInContext context) {
		File dotFile = null;
		Object input = context.getInput();

		if (input instanceof File) {
			dotFile = (File) input;
		} else if (input instanceof FileEditorInput) {
			FileEditorInput fileEditorInput = (FileEditorInput) input;
			IFile file = fileEditorInput.getFile();
			String workspaceRoot = ResourcesPlugin.getWorkspace().getRoot()
					.getLocation().toString();
			dotFile = new File(workspaceRoot + "/" + file.getFullPath()); //$NON-NLS-1$
		}

		if (dotFile != null) {
			updateGraph(dotFile);
			// wait for the view to set the graph content executed
			// asynchronously
			waitForEventProcessing();
			// call fit to viewport
			fitToViewPort();
			return true;
		}

		return false;
	}

	private void fitToViewPort() {
		FitToViewportAction fitToViewportAction = (FitToViewportAction) fitToViewportActionGroup
				.getContributions().get(0);
		waitForEventProcessing();
		fitToViewportAction.runWithEvent(null);
	}

	private void waitForEventProcessing() {
		while (Display.getDefault().readAndDispatch()) {
		}
	}
}
