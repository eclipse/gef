/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef4.dot.internal.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.gef4.dot.internal.DotExecutableUtils;
import org.eclipse.gef4.dot.internal.DotFileUtils;
import org.eclipse.gef4.dot.internal.DotImport;
import org.eclipse.gef4.dot.internal.parser.ui.internal.DotActivator;
import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.GraphCopier;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef4.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com.google.inject.Guice;
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
public class DotGraphView extends ZestFxUiView {

	public static class DotExtractor {

		/**
		 * The DOT graph returned if the input contains no DOT graph substring.
		 */
		public static final String NO_DOT = "graph{n1[label=\"no DOT\"]}"; //$NON-NLS-1$
		private String input = NO_DOT;

		/**
		 * @param input
		 *            The string to extract a DOT graph substring from
		 */
		public DotExtractor(final String input) {
			this.input = input;
		}

		/**
		 * @param file
		 *            The file to extract a DOT substring from
		 */
		public DotExtractor(final File file) {
			this(DotFileUtils.read(file));
		}

		/**
		 * @return A DOT string extracted from the input, or the {@code NO_DOT}
		 *         constant, a valid DOT graph
		 */
		public String getDotString() {
			return trimNonDotSuffix(trimNonDotPrefix());
		}

		/**
		 * @return A temporary file containing the DOT string extracted from the
		 *         input, or the {@code NO_DOT} constant, a valid DOT graph
		 */
		public File getDotTempFile() {
			File tempFile = null;
			try {
				tempFile = File.createTempFile("tempDotExtractorFile", ".dot"); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IOException e) {
				System.err
						.println("DotExtractor failed to create temp dot file"); //$NON-NLS-1$
				e.printStackTrace();
			}

			if (tempFile != null) {
				// use try-with-resources to utilize the AutoClosable
				// functionality
				try (BufferedWriter bw = new BufferedWriter(
						new FileWriter(tempFile))) {
					bw.write(getDotString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return tempFile;
		}

		private String trimNonDotPrefix() {
			Matcher m = Pattern.compile("((?:di)?graph\\s*[^{\\s]*\\s*\\{.+)", //$NON-NLS-1$
					Pattern.DOTALL).matcher(input);
			String dotSubstring = m.find() ? m.group(1) : NO_DOT;
			return dotSubstring;
		}

		private String trimNonDotSuffix(String dot) {
			int first = dot.indexOf('{') + 1;
			StringBuilder builder = new StringBuilder(dot.substring(0, first));
			int count = 1; /* we count to include embedded { ... } blocks */
			int index = first;
			while (count > 0 && index < dot.length()) {
				char c = dot.charAt(index);
				builder.append(c);
				count = (c == '{') ? count + 1 : (c == '}') ? count - 1 : count;
				index++;
			}
			return builder.toString().trim();
		}

	}

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

	private Dot2ZestAttributesConverter dot2ZestAttributeCopier = new Dot2ZestAttributesConverter();
	private GraphCopier dot2ZestGraphCopier = new GraphCopier(
			dot2ZestAttributeCopier);

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

	public DotGraphView() {
		super(Guice.createInjector(Modules.override(new ZestFxModule())
				.with(new ZestFxUiModule())));
	}

	@Override
	protected void activate() {
		super.activate();
		setGraph(new DotImport().importDot(currentDot));
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
		super.dispose();
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
		Scene scene = getContentViewer().getScene();
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
					try {
						setGraph(new DotImport().importDot(dot));
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
						return;
					}
					resourceLabel.setText(
							String.format(GRAPH_RESOURCE, file.getName())
									+ (isNativeMode() ? " [native]" //$NON-NLS-1$
											: " [emulated]")); //$NON-NLS-1$
					resourceLabel.setToolTipText(file.getAbsolutePath());
				}
			}
		});

	}

	@Override
	public void setGraph(Graph graph) {
		// do no convert layout algorithm and rankdir in emulated mode, invert
		// y-axis mode (as by default y-axis is interpreted inverse in dot)
		boolean isNativeMode = isNativeMode();
		dot2ZestAttributeCopier.options().emulateLayout = !isNativeMode;
		dot2ZestAttributeCopier.options().invertYAxis = false;
		super.setGraph(dot2ZestGraphCopier.copy(graph));

		// adjust viewport to scroll to top-left
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				InfiniteCanvas canvas = getContentViewer().getCanvas();
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
		// (native mode); otherwise we emulate layout with GEF4 Layout
		// algorithms.
		if (isNativeMode()) {
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
