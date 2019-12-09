/*******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #498324)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.gef.dot.internal.ui.language.DotActivatorEx;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;

import com.google.inject.Injector;

/**
 * The implementation of this class is mainly taken from the
 * org.eclipse.xtext.junit4.ui.ContentAssistProcessorTestBuilder java class.
 * 
 * @author miklossy
 *
 */
public class DotProposalProviderDelegator {

	private Injector injector;

	public DotProposalProviderDelegator(String language) {
		injector = DotActivator.getInstance().getInjector(language);
	}

	/**
	 * Computes the configurable completion proposals considering the given text
	 * and the given cursorPosition.
	 * 
	 * @param text
	 *            The current text to parse.
	 * @param cursorPosition
	 *            The cursor position within the given text.
	 * @return The configurable completion proposals valid on the given cursor
	 *         position within the given text. Returns an empty list if the
	 *         proposals cannot be determined.
	 */
	public List<ConfigurableCompletionProposal> computeConfigurableCompletionProposals(
			final String text, int cursorPosition) {

		List<ConfigurableCompletionProposal> configurableCompletionProposal = new ArrayList<>();
		ICompletionProposal[] completionProposals = {};
		try {
			completionProposals = computeCompletionProposals(text,
					cursorPosition);
		} catch (Exception e) {
			DotActivatorEx.logError(e);
		}

		// convert the completionProposals into configurableCompletionProposals
		for (ICompletionProposal completionProposal : completionProposals) {
			if (completionProposal instanceof ConfigurableCompletionProposal) {
				configurableCompletionProposal.add(
						(ConfigurableCompletionProposal) completionProposal);
			}
		}

		return configurableCompletionProposal;
	}

	private ICompletionProposal[] computeCompletionProposals(
			final String currentModelToParse, int cursorPosition)
			throws Exception {

		final IXtextDocument xtextDocument = DotEditorUtils
				.getDocument(injector, currentModelToParse);
		return computeCompletionProposals(xtextDocument, cursorPosition);
	}

	private ICompletionProposal[] computeCompletionProposals(
			final IXtextDocument xtextDocument, int cursorPosition)
			throws BadLocationException {
		Shell shell = new Shell();
		try {
			return computeCompletionProposals(xtextDocument, cursorPosition,
					shell);
		} finally {
			shell.dispose();
		}
	}

	private ICompletionProposal[] computeCompletionProposals(
			final IXtextDocument xtextDocument, int cursorPosition, Shell shell)
			throws BadLocationException {
		XtextSourceViewerConfiguration configuration = get(
				XtextSourceViewerConfiguration.class);
		ISourceViewer sourceViewer = getSourceViewer(shell, xtextDocument,
				configuration);
		return computeCompletionProposals(xtextDocument, cursorPosition,
				configuration, sourceViewer);
	}

	private ICompletionProposal[] computeCompletionProposals(
			final IXtextDocument xtextDocument, int cursorPosition,
			XtextSourceViewerConfiguration configuration,
			ISourceViewer sourceViewer) throws BadLocationException {
		IContentAssistant contentAssistant = configuration
				.getContentAssistant(sourceViewer);
		String contentType = xtextDocument.getContentType(cursorPosition);
		IContentAssistProcessor processor = contentAssistant
				.getContentAssistProcessor(contentType);
		if (processor != null) {
			return processor.computeCompletionProposals(sourceViewer,
					cursorPosition);
		}
		return new ICompletionProposal[0];
	}

	private <T> T get(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

	private ISourceViewer getSourceViewer(Shell shell,
			final IXtextDocument xtextDocument,
			XtextSourceViewerConfiguration configuration) {
		XtextSourceViewer.Factory factory = get(
				XtextSourceViewer.Factory.class);
		ISourceViewer sourceViewer = factory.createSourceViewer(shell, null,
				null, false, 0);
		sourceViewer.configure(configuration);
		sourceViewer.setDocument(xtextDocument);
		return sourceViewer;
	}
}
