/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.xtext.junit4.ui.AbstractEditorTest;
import org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil;
import org.eclipse.xtext.ui.XtextProjectHelper;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.StringInputStream;
import org.junit.Test;

@SuppressWarnings("restriction")
public class DotHighlightingTests extends AbstractEditorTest {

	private final String TEST_PROJECT = "dottestproject";

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createTestProjectWithXtextNature();
	}

	@Override
	protected String getEditorId() {
		return DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOT;
	}

	// lexical highlighting test cases
	@Test
	public void numbers() {
		test(DotTestGraphs.EDGE_ARROWSIZE_GLOBAL, "1.5", SWT.NORMAL, 125, 125,
				125);
	}

	@Test
	public void quotedAttributeValue() {
		test(DotTestGraphs.QUOTED_LABELS, "node 1", SWT.NORMAL, 255, 0, 0);
	}

	@Test
	public void unquotedAttributeValue() {
		test(DotTestGraphs.GRAPH_LAYOUT_DOT, "dot", SWT.NORMAL, 153, 76, 0);
	}

	@Test
	public void compassPt() {
		test(DotTestGraphs.PORTS, "ne", SWT.NORMAL, 153, 76, 0);
		test(DotTestGraphs.PORTS, "_", SWT.NORMAL, 153, 76, 0);
	}

	@Test
	public void htmlString() {
		test(DotTestGraphs.HTML_LIKE_LABELS_WITH_COMMENT, "<B>Bold Label</B>",
				SWT.NORMAL, 153, 76, 0);
	}

	@Test
	public void comments() {
		test(DotTestGraphs.EMPTY_WITH_COMMENTS,
				"// This is a C++-style single line comment.", SWT.NORMAL, 63,
				127, 95);
		test(DotTestGraphs.EMPTY_WITH_COMMENTS, "/*", SWT.NORMAL, 63, 127, 95);
		test(DotTestGraphs.EMPTY_WITH_COMMENTS, "* This is a C++-style",
				SWT.NORMAL, 63, 127, 95);
		test(DotTestGraphs.EMPTY_WITH_COMMENTS, "* multi line comment.",
				SWT.NORMAL, 63, 127, 95);
		test(DotTestGraphs.EMPTY_WITH_COMMENTS, "*/", SWT.NORMAL, 63, 127, 95);
		test(DotTestGraphs.EMPTY_WITH_COMMENTS,
				"# This is considered as a line output from C-preprocessor and discarded.",
				SWT.NORMAL, 63, 127, 95);
	}

	@Test
	public void keywords() {
		test(DotTestGraphs.KEYWORDS, "strict", SWT.BOLD, 0, 0, 0);
		test(DotTestGraphs.KEYWORDS, "digraph", SWT.BOLD, 0, 0, 0);
		test(DotTestGraphs.KEYWORDS, "\tgraph", SWT.BOLD, 0, 0, 0);
		test(DotTestGraphs.KEYWORDS, "node", SWT.BOLD, 0, 0, 0);
		test(DotTestGraphs.KEYWORDS, "edge", SWT.BOLD, 0, 0, 0);
		test(DotTestGraphs.KEYWORDS, "subgraph", SWT.BOLD, 0, 0, 0);
	}

	// semantic highlighting test cases
	@Test
	public void graphName() {
		test(DotTestGraphs.EXTRACTED_01, "name", SWT.NORMAL, 0, 0, 0);
	}

	@Test
	public void nodeName() {
		test(DotTestGraphs.ONE_NODE, "1", SWT.NORMAL, 0, 0, 0);
	}

	@Test
	public void port() {
		test(DotTestGraphs.PORTS, "portID", SWT.NORMAL, 0, 153, 76);
		test(DotTestGraphs.PORTS, "portID2", SWT.NORMAL, 0, 153, 76);
	}

	@Test
	public void attributeName() {
		test(DotTestGraphs.GRAPH_LAYOUT_DOT, "layout", SWT.NORMAL, 0, 76, 153);
	}

	@Test
	public void edgeOperatorDirected() {
		test(DotTestGraphs.ONE_DIRECTED_EDGE, "->", SWT.NORMAL, 0, 153, 0);
	}

	@Test
	public void edgeOperatorUnDirected() {
		test(DotTestGraphs.ONE_EDGE, "--", SWT.NORMAL, 0, 153, 0);
	}

	private void test(String content, String subString, int expectedFontStyle,
			int expectedR, int expectedG, int expectedB) {

		XtextEditor editor = null;
		try {
			editor = openEditor(createTestFile(content));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// wait for the Xtext framework to apply the semantic highlighting,
		// since the semantic highlighting stage is executed asynchronously in
		// the background
		// TODO: replace the hard-coded sleep value with a proper wait
		// condition
		try {
			sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		StyledText textWidget = editor.getInternalSourceViewer()
				.getTextWidget();

		int startPosition = content.indexOf(subString);
		for (int i = 0; i < subString.length(); i++) {
			int currentPosition = startPosition + i;
			StyleRange styleRange = textWidget
					.getStyleRangeAtOffset(currentPosition);

			String character = textWidget.getContent()
					.getTextRange(currentPosition, 1);

			// skipping the whitespace characters
			if (character.equals(" ") || character.equals("\t")) {
				continue;
			}

			assertEquals(
					"Expected font style does not correspond to the actual font style on character "
							+ character,
					expectedFontStyle, styleRange.fontStyle);

			assertEquals(
					"Expected foreground color does not correspond to the actual foreground color on character "
							+ character,
					new Color(null, expectedR, expectedG, expectedB),
					getActualColor(styleRange.foreground));
		}
	}

	private Color getActualColor(Color color) {
		// the default color is black
		return color == null ? new Color(null, 0, 0, 0) : color;
	}

	/**
	 * The implementation of the following helper methods is mainly taken from
	 * the org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil java class.
	 */
	private IFile createTestFile(String content) throws Exception {
		return createFile(TEST_PROJECT + "/test.dot", content);
	}

	private void createTestProjectWithXtextNature() throws Exception {
		IProject project = IResourcesSetupUtil.createProject(TEST_PROJECT);
		IResourcesSetupUtil.addNature(project, XtextProjectHelper.NATURE_ID);
	}

	private static IFile createFile(String wsRelativePath, String s)
			throws CoreException, InvocationTargetException,
			InterruptedException {
		return createFile(new Path(wsRelativePath), s);
	}

	private static IFile createFile(IPath wsRelativePath, final String s)
			throws CoreException, InvocationTargetException,
			InterruptedException {
		final IFile file = root().getFile(wsRelativePath);
		new WorkspaceModifyOperation() {

			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				create(file.getParent());
				file.delete(true, monitor());
				file.create(new StringInputStream(s), true, monitor());
			}

		}.run(monitor());
		return file;
	}

	private static IWorkspaceRoot root() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	private static IProgressMonitor monitor() {
		return new NullProgressMonitor();
	}

	private static void create(final IContainer container) throws CoreException,
			InvocationTargetException, InterruptedException {
		new WorkspaceModifyOperation() {

			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				if (!container.exists()) {
					create(container.getParent());
					if (container instanceof IFolder) {
						((IFolder) container).create(true, true, monitor());
					} else {
						IProject iProject = (IProject) container;
						createProject(iProject);
					}
				}
			}
		}.run(monitor());
	}

	private static IProject createProject(IProject project)
			throws CoreException {
		if (!project.exists())
			project.create(monitor());
		project.open(monitor());
		return project;
	}
}
