/*******************************************************************************
 * Copyright (c) 2014, 2020 itemis AG and others.
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
 *     Zoey Prigge (itemis AG)    - Avoid NPE when setting ungrammatical bgcolor (bug #540508)
 *                                - add support for gv file extension (bug #481267)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotExecutableUtils;
import org.eclipse.gef.dot.internal.DotExtractor;
import org.eclipse.gef.dot.internal.DotFileUtils;
import org.eclipse.gef.dot.internal.DotImport;
import org.eclipse.gef.dot.internal.language.colorlist.ColorList;
import org.eclipse.gef.dot.internal.ui.conversion.Dot2ZestGraphCopier;
import org.eclipse.gef.dot.internal.ui.conversion.DotColorUtil;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.gef.dot.internal.ui.language.DotActivatorEx;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.gef.dot.internal.ui.preferences.GraphvizPreferencePage;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.zest.fx.parts.GraphPart;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.util.Modules;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.paint.Paint;

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
	private static final String[] EXTENSIONS = { "dot", "gv" }; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String GRAPH_NONE = DotUiMessages.DotGraphView_0;
	private boolean listenToDotContent = false;
	private boolean listenToSelectionChanges = false;
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

	private LinkWithDotEditorAction linkWithDotEditorAction;
	private LinkWithSelectionAction linkWithSelectionAction;
	private LoadFileAction loadFileAction;

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
		DotActivatorEx.dotUiPreferenceStore()
				.addPropertyChangeListener(preferenceChangeListener);
	}

	protected boolean isNativeMode() {
		return GraphvizPreferencePage.isGraphvizConfigured();
	}

	@Override
	public void dispose() {
		DotActivatorEx.dotUiPreferenceStore()
				.removePropertyChangeListener(preferenceChangeListener);
		currentDot = null;
		currentFile = null;

		getContentViewer().contentsProperty().clear();

		super.dispose();
	}

	@Override
	protected void disposeActions() {
		linkWithDotEditorAction.dispose();
		linkWithSelectionAction.dispose();
		loadFileAction.dispose();
		super.disposeActions();
	}

	@Override
	protected void createActions() {
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager mgr = actionBars.getToolBarManager();
		linkWithDotEditorAction = new LinkWithDotEditorAction();
		linkWithSelectionAction = new LinkWithSelectionAction();
		loadFileAction = new LoadFileAction();
		mgr.add(linkWithDotEditorAction);
		mgr.add(linkWithSelectionAction);
		mgr.add(loadFileAction);
		mgr.add(new Separator());
		super.createActions();
	}

	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);

		// controls
		parent.setLayout(new GridLayout(1, true));
		initResourceLabel(parent);
		getCanvas().setLayoutData(new GridData(GridData.FILL_BOTH));

		// scene
		Scene scene = getContentViewer().getCanvas().getScene();
		scene.getStylesheets().add(STYLES_CSS_FILE);
	}

	private void initResourceLabel(final Composite parent) {
		resourceLabel = new Link(parent, SWT.WRAP);
		resourceLabel.setText(GRAPH_NONE);
		resourceLabel.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				processEvent(GRAPH_NONE, e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				processEvent(GRAPH_NONE, e);
			}

			private void processEvent(final String label, SelectionEvent e) {
				/*
				 * As we use a single string for the links for localization, we
				 * don't compare specific strings but say the first link
				 * triggers the loadAction, else the toggleAction:
				 */
				if (label.replaceAll("<a>", "").startsWith(e.text)) { //$NON-NLS-1$ //$NON-NLS-2$
					loadFileAction.run();
				} else {
					// toggle as if the button was pressed, then run the action:
					linkWithDotEditorAction
							.setChecked(!linkWithDotEditorAction.isChecked());
					linkWithDotEditorAction.run();
				}
			}
		});
		resourceLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private ImageDescriptor determineImageDescriptor(String imageName) {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(imageName);
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
						MultiStatus status = createMultiStatus(
								e.getLocalizedMessage(), e);
						int style = StatusManager.LOG | StatusManager.SHOW;
						StatusManager.getManager().handle(status, style);
						return;
					}
					if (!resourceLabel.isDisposed()) {
						resourceLabel.setText(String.format(
								DotUiMessages.DotGraphView_4, file.getName())
								+ (isNativeMode() ? " [native]" //$NON-NLS-1$
										: " [emulated]")); //$NON-NLS-1$
						resourceLabel.setToolTipText(file.getAbsolutePath());
					}
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
		// Do not try to set the graph if the DOT Graph View has been closed
		if (getDomain() == null) {
			return;
		}

		// do no convert layout algorithm and rankdir in emulated mode, invert
		// y-axis mode (as by default y-axis is interpreted inverse in dot)
		boolean isNativeMode = isNativeMode();
		dot2ZestGraphCopier.getAttributeCopier()
				.options().emulateLayout = !isNativeMode;
		dot2ZestGraphCopier.getAttributeCopier().options().invertYAxis = false;
		super.setGraph(dot2ZestGraphCopier.copy(graph));

		// apply graph background color
		// TODO: add Zest property for background color
		// TODO: convert bgcolorList (Dot) to background color (Zest) within
		// Dot2ZestAttributesConverter
		DotColorUtil colorUtil = new DotColorUtil();
		ColorList bgcolorList = DotAttributes.getBgcolorParsed(graph);
		Paint backgroundColor = null;
		if (bgcolorList != null && bgcolorList.getColorValues() != null
				&& bgcolorList.getColorValues().size() > 0) {
			// FIXME: apply all colors. Currently, only the first one is
			// applied.
			backgroundColor = colorUtil.computeGraphBackgroundColor(
					DotAttributes.getColorscheme(graph),
					bgcolorList.getColorValues().get(0).getColor());
		}
		if (backgroundColor != null) {
			addGraphBackground(backgroundColor);
		} else {
			removeGraphBackground();
		}
	}

	private void removeGraphBackground() {
		GraphPart graphPart = (GraphPart) getContentViewer().getRootPart()
				.getContentPartChildren().get(0);
		Group group = graphPart.getVisual();
		group.setEffect(null);
	}

	private void addGraphBackground(Paint paint) {
		double margin = 5;
		GraphPart graphPart = (GraphPart) getContentViewer().getRootPart()
				.getContentPartChildren().get(0);
		Group group = graphPart.getVisual();
		Bounds bounds = group.getLayoutBounds();
		group.setEffect(new Blend(BlendMode.SRC_OVER,
				new ColorInput(bounds.getMinX() - margin,
						bounds.getMinY() - margin,
						bounds.getWidth() + 2 * margin,
						bounds.getHeight() + 2 * margin, paint),
				null));
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

	private boolean updateGraph(IFile file) {
		if (file != null) {
			URI locationURI = file.getLocationURI();
			if (locationURI != null) {
				URL url = null;
				try {
					url = locationURI.toURL();
				} catch (MalformedURLException e) {
					DotActivatorEx.logError(e);
					return false;
				}
				if (url != null) {
					File dotFile = DotFileUtils.resolve(url);
					return updateGraph(dotFile);
				}
			}
		}

		return false;
	}

	private boolean updateGraph(File file) {
		if (file == null || !file.exists()) {
			return false;
		}

		currentFile = file;
		boolean isEmbeddedDotFile = !hasDotFileExtension(currentFile.getName());

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
				&& !hasDotFileExtension(f.getAbsolutePath().toString())) {
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

	private class LinkWithDotEditorAction extends Action {

		/** Listener that passes a visitor if a resource is changed. */
		private IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {
			@Override
			public void resourceChanged(final IResourceChangeEvent event) {
				if (event.getType() != IResourceChangeEvent.POST_BUILD && event
						.getType() != IResourceChangeEvent.POST_CHANGE) {
					return;
				}
				IResourceDelta rootDelta = event.getDelta();
				try {
					rootDelta.accept(resourceVisitor);
				} catch (CoreException e) {
					DotActivatorEx.logError(e);
				}
			}
		};

		/** If a *.dot file is visited, update the graph. */
		private IResourceDeltaVisitor resourceVisitor = new IResourceDeltaVisitor() {
			@Override
			public boolean visit(final IResourceDelta delta) {
				IResource resource = delta.getResource();
				if (resource.getType() == IResource.FILE
						&& hasDotFileExtension(((IFile) resource).getName())) {
					try {
						final IFile f = (IFile) resource;
						IWorkspaceRunnable workspaceRunnable = updateGraphRunnable(
								DotFileUtils
										.resolve(f.getLocationURI().toURL()));
						IWorkspace workspace = ResourcesPlugin.getWorkspace();
						if (!workspace.isTreeLocked()) {
							workspace.run(workspaceRunnable, null);
						}
					} catch (Exception e) {
						DotActivatorEx.logError(e);
					}
				}
				return true;
			}
		};

		/** Listen to selection changes and update graph in view. */
		private ISelectionListener selectionChangeListener = new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				checkActiveEditorAndUpdateGraph(part);
			}
		};

		public LinkWithDotEditorAction() {
			super(DotUiMessages.DotGraphView_1, SWT.TOGGLE);
			setId(getText());
			setImageDescriptor(
					determineImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		}

		@Override
		public void run() {
			listenToDotContent = toggle(this, listenToDotContent);
			toggleResourceListener();
		}

		public void dispose() {
			ResourcesPlugin.getWorkspace()
					.removeResourceChangeListener(resourceChangeListener);
			getSite().getWorkbenchWindow().getSelectionService()
					.removeSelectionListener(selectionChangeListener);
		}

		private void toggleResourceListener() {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			ISelectionService service = getSite().getWorkbenchWindow()
					.getSelectionService();
			if (listenToDotContent) {
				IWorkbenchPart activeEditor = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				checkActiveEditorAndUpdateGraph(activeEditor);
				workspace.addResourceChangeListener(resourceChangeListener,
						IResourceChangeEvent.POST_BUILD
								| IResourceChangeEvent.POST_CHANGE);
				service.addSelectionListener(selectionChangeListener);
			} else {
				workspace.removeResourceChangeListener(resourceChangeListener);
				service.removeSelectionListener(selectionChangeListener);
			}
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
						DotActivatorEx.logError(e);
					}
				}
			}
		}
	}

	private class LinkWithSelectionAction extends Action {
		private ISelectionListener listener = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				if (!listenToSelectionChanges) {
					return;
				}
				if (!(selection instanceof IStructuredSelection))
					return;
				IStructuredSelection structured = (IStructuredSelection) selection;
				if (structured.size() != 1)
					return;
				Object selected = structured.getFirstElement();
				IFile file = (IFile) org.eclipse.core.runtime.Platform
						.getAdapterManager().getAdapter(selected, IFile.class);
				updateGraph(file);
			}

		};

		public LinkWithSelectionAction() {
			super(DotUiMessages.DotGraphView_2, SWT.TOGGLE);
			setId(getText());
			setImageDescriptor(
					determineImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		}

		@Override
		public void run() {
			listenToSelectionChanges = toggle(this, listenToSelectionChanges);
			toggleSelectionChangeListener();
		}

		private void toggleSelectionChangeListener() {
			if (listenToSelectionChanges) {
				addSelectionChangeListener();
			} else {
				removeSelectionChangeListener();
			}
		}

		private void addSelectionChangeListener() {
			getSelectionService().addSelectionListener(listener);
		}

		private void removeSelectionChangeListener() {
			getSelectionService().removeSelectionListener(listener);
		}

		private ISelectionService getSelectionService() {
			return getSite().getWorkbenchWindow().getSelectionService();
		}

		public void dispose() {
			removeSelectionChangeListener();
		}
	}

	private class LoadFileAction extends Action {

		private String lastSelection = null;

		public LoadFileAction() {
			super(DotUiMessages.DotGraphView_3,
					determineImageDescriptor(ISharedImages.IMG_OBJ_FILE));
			setId(getText());
		}

		@Override
		public void run() {
			FileDialog dialog = new FileDialog(getViewSite().getShell(),
					SWT.OPEN);
			dialog.setFileName(lastSelection);
			String[] filterSuffixPattern = new String[EXTENSIONS.length + 1];
			String[] filterReadableName = new String[EXTENSIONS.length + 1];

			filterSuffixPattern[0] = "*.*"; //$NON-NLS-1$
			filterReadableName[0] = String.format("Embedded DOT Graph (%s)", //$NON-NLS-1$
					filterSuffixPattern[0]);

			for (int i = 1; i <= EXTENSIONS.length; i++) {
				String suffix = EXTENSIONS[i - 1];
				filterSuffixPattern[i] = "*." + suffix; //$NON-NLS-1$
				filterReadableName[i] = String.format(Locale.ENGLISH,
						"%S file (%s)", suffix, //$NON-NLS-1$
						filterSuffixPattern[i]);
			}

			dialog.setFilterExtensions(filterSuffixPattern);
			dialog.setFilterNames(filterReadableName);
			String selection = dialog.open();
			if (selection != null) {
				lastSelection = selection;
				updateGraph(new File(selection));
			}
		}

		public void dispose() {
			// no action is necessary on dispose
		}
	}

	@Override
	public boolean show(ShowInContext context) {
		/**
		 * The show in context for an editor is typically its input element. For
		 * a view, the context is typically its selection. Both a selection and
		 * an input element are provided in a ShowInContext to give the target
		 * flexibility in determining how to show the source.
		 */
		Object input = context.getInput();
		ISelection selection = context.getSelection();

		// the show-in action is coming from an editor (e.g. DOT Editor)
		if (input instanceof File) {
			File dotFile = (File) input;
			return updateGraph(dotFile);
		} else if (input instanceof FileEditorInput) {
			FileEditorInput fileEditorInput = (FileEditorInput) input;
			IFile dotFile = fileEditorInput.getFile();
			return updateGraph(dotFile);
		}

		// the show-in action is coming from a view (e.g. Package Explorer,
		// Project Explorer, ...)
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			Object firstElement = treeSelection.getFirstElement();

			IFile dotFile = (IFile) org.eclipse.core.runtime.Platform
					.getAdapterManager().getAdapter(firstElement, IFile.class);
			updateGraph(dotFile);
		}

		return false;
	}

	private boolean hasDotFileExtension(String fileName) {
		// matches names that end in .ext, where ext can be any of EXTENSIONS
		// ignoring case
		return fileName.toLowerCase(Locale.ENGLISH).matches(String.format(
				".*\\.(%s)\\Z", //$NON-NLS-1$
				String.join("|", EXTENSIONS).toLowerCase(Locale.ENGLISH))); //$NON-NLS-1$
	}
}
