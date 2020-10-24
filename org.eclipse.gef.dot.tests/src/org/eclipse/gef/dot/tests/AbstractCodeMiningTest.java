/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil.addNature;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.ui.XtextProjectHelper;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.testing.AbstractEditorTest;
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;

/**
 * This class is a copy of the
 * org.eclipse.xtext.ui.testing.AbstractCodeMiningTest class available from
 * Xtext version 2.24 (Eclipse 2020-12).
 *
 * TODO: remove this class as soon as updating to Xtext version 2.24 or above.
 */
public class AbstractCodeMiningTest extends AbstractEditorTest {

	@Inject
	protected FileExtensionProvider fileExtensionProvider;

	@Inject
	protected ICodeMiningProvider codeMiningProvider;

	/**
	 * Test that the expected code minings are present on a given DSL text.
	 *
	 * @param initialText
	 *            The initial DSL text.
	 * @param expected
	 *            The DSL text where the expected code minings are inserted on
	 *            the right position.
	 */
	public void testCodeMining(CharSequence initialText, String expected) {
		// Given
		IFile dslFile = dslFile(initialText);
		// When
		XtextEditor editor = openEditor(dslFile);
		// Then
		codeMiningsArePresent(editor, expected);
	}

	@Override
	protected XtextEditor openEditor(IFile dslFile) {
		try {
			XtextEditor editor = super.openEditor(dslFile);
			assertNotNull(editor);
			return editor;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void codeMiningsArePresent(XtextEditor editor, String expected) {
		String actual = insertCodeMinings(editor);
		assertEquals(expected, actual);
	}

	protected String insertCodeMinings(XtextEditor editor) {
		ISourceViewer viewer = editor.getInternalSourceViewer();

		String text = editor.getDocument().get();
		StringBuilder sb = new StringBuilder(text);

		List<? extends ICodeMining> codeMinings;
		codeMinings = Futures.getUnchecked(codeMiningProvider
				.provideCodeMinings(viewer, new NullProgressMonitor()));

		for (ICodeMining codeMining : codeMinings) {
			Futures.getUnchecked(
					codeMining.resolve(viewer, new NullProgressMonitor()));
			assertTrue("CodeMining is not resolved!", codeMining.isResolved());
		}

		codeMinings.sort((ICodeMining cm1,
				ICodeMining cm2) -> cm2.getPosition().getOffset()
						- cm1.getPosition().getOffset());

		Map<Integer, List<ICodeMining>> byPos = codeMinings.stream()
				.collect(Collectors.groupingBy(e -> e.getPosition().getOffset(),
						LinkedHashMap::new, Collectors.toList()));
		for (Entry<Integer, List<ICodeMining>> e : byPos.entrySet()) {
			int codeMiningOffset = e.getKey();
			List<ICodeMining> miningsAtOffset = e.getValue();
			String codeMiningsText = getCodeMiningsText(miningsAtOffset);
			if (containsLineHeaderCodeMining(miningsAtOffset)) {
				sb.insert(codeMiningOffset,
						((IDocumentExtension4) editor.getDocument())
								.getDefaultLineDelimiter());
			}
			sb.insert(codeMiningOffset, codeMiningsText);
		}
		return sb.toString();
	}

	protected String getCodeMiningsText(
			List<? extends ICodeMining> codeMinings) {
		StringBuilder result = new StringBuilder();

		int count = 0;
		for (ICodeMining codeMining : codeMinings) {
			String codeMiningLabel = codeMining.getLabel();
			if (codeMiningLabel != null) {
				if (count != 0) {
					result.append(" | ");
				}
				result.append(codeMiningLabel);
			}
			count++;
		}
		return result.toString();
	}

	protected boolean containsLineHeaderCodeMining(
			List<ICodeMining> codeMinings) {
		for (ICodeMining codeMining : codeMinings) {
			if (codeMining instanceof LineHeaderCodeMining) {
				return true;
			}
		}
		return false;
	}

	protected IFile dslFile(CharSequence text) {
		IFile file = null;
		try {
			file = IResourcesSetupUtil.createFile(getProjectName(),
					getFileName(), getFileExtension(), text.toString());
		} catch (InvocationTargetException | CoreException
				| InterruptedException e) {
			e.printStackTrace();
		}

		/*
		 * TODO: find a better (with good performance) solution to set the Xtext
		 * nature on the test project.
		 */
		IProject project = file.getProject();
		try {
			if (!project.hasNature(XtextProjectHelper.NATURE_ID)) {
				addNature(project, XtextProjectHelper.NATURE_ID);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return file;
	}

	protected String getProjectName() {
		return "CodeMiningTestProject";
	}

	protected String getFileName() {
		return "codeMining";
	}

	protected String getFileExtension() {
		return fileExtensionProvider.getPrimaryFileExtension();
	}

}