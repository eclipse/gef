/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #498324)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.parser.ui.contentassist;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.parser.ui.internal.DotActivator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.model.DocumentPartitioner;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.Strings;

import com.google.inject.Injector;

/**
 * The implementation of this class is mainly taken from the
 * ContentAssistProcessorTestBuilder java class.
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
	 * Computes the proposals considering the given text and the given
	 * cursorPosition.
	 * 
	 * @param text
	 *            The current text to parse.
	 * @param cursorPosition
	 *            The cursor position within the given text.
	 * @return The list of proposals valid on the given cursor position within
	 *         the given text. Returns an empty list if the proposals cannot be
	 *         determined.
	 */
	public List<String> computeProposals(final String text,
			int cursorPosition) {

		List<String> proposalStrings = new ArrayList<String>();

		ICompletionProposal[] completionProposals = {};
		try {
			completionProposals = computeCompletionProposals(text,
					cursorPosition);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// extract the display string from the completion proposal, forcing
		// the caller to create a new ICompletionProposal with the right
		// context
		for (ICompletionProposal completionProposal : completionProposals) {
			proposalStrings.add(completionProposal.getDisplayString());
		}

		return proposalStrings;
	}

	private ICompletionProposal[] computeCompletionProposals(
			final String currentModelToParse, int cursorPosition)
			throws Exception {

		final IXtextDocument xtextDocument = getDocument(currentModelToParse);
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

	private IXtextDocument getDocument(final String currentModelToParse)
			throws Exception {
		XtextResource xtextResource = doGetResource(
				new StringInputStream(Strings.emptyIfNull(currentModelToParse)),
				URI.createURI("dummy:/example.mydsl")); //$NON-NLS-1$

		return getDocument(xtextResource, currentModelToParse);
	}

	private IXtextDocument getDocument(final XtextResource xtextResource,
			final String model) {
		XtextDocument document = get(XtextDocument.class);
		document.set(model);
		document.setInput(xtextResource);
		DocumentPartitioner partitioner = get(DocumentPartitioner.class);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		return document;
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

	private XtextResource doGetResource(InputStream in, URI uri)
			throws Exception {
		XtextResourceSet rs = get(XtextResourceSet.class);
		rs.setClasspathURIContext(getClass());
		XtextResource resource = (XtextResource) getResourceFactory()
				.createResource(uri);
		rs.getResources().add(resource);
		resource.load(in, null);
		if (resource instanceof LazyLinkingResource) {
			((LazyLinkingResource) resource)
					.resolveLazyCrossReferences(CancelIndicator.NullImpl);
		} else {
			EcoreUtil.resolveAll(resource);
		}
		return resource;
	}

	private IResourceFactory getResourceFactory() {
		return injector.getInstance(IResourceFactory.class);
	}
}
