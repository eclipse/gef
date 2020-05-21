/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #513196)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.quickfix;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolution;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Injector;

class DotHtmlLabelQuickfixDelegator {

	private Injector injector;

	public DotHtmlLabelQuickfixDelegator() {
		injector = DotActivator.getInstance().getInjector(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTHTMLLABEL);
	}

	public void provideQuickfixes(Issue originalIssue, Issue subgrammarIssue,
			IssueResolutionAcceptor acceptor) {
		List<IssueResolution> resolutions = getResolutions(subgrammarIssue);
		for (IssueResolution issueResolution : resolutions) {
			acceptor.accept(originalIssue, issueResolution.getLabel(),
					issueResolution.getDescription(),
					issueResolution.getImage(), new ISemanticModification() {

						@Override
						public void apply(EObject element,
								IModificationContext context) throws Exception {
							Attribute attribute = (Attribute) element;
							String originalText = attribute.getValue()
									.toValue();
							String modifiedText = getModifiedText(originalText,
									issueResolution);
							attribute.setValue(ID.fromValue(modifiedText,
									ID.Type.HTML_STRING));
						}
					});
		}
	}

	private List<IssueResolution> getResolutions(Issue issue) {
		IssueResolutionProvider quickfixProvider = injector
				.getInstance(IssueResolutionProvider.class);
		return quickfixProvider.getResolutions(issue);
	}

	private String getModifiedText(String originalText,
			IssueResolution issueResolution) {
		/*
		 * manually create an IModificationContext with an XtextDocument and
		 * call the apply method of the issueResolution with that
		 * IModificationContext
		 */
		IXtextDocument document = getDocument(originalText);
		IModificationContext modificationContext = new IModificationContext() {

			@Override
			public IXtextDocument getXtextDocument() {
				return document;
			}

			@Override
			public IXtextDocument getXtextDocument(URI uri) {
				return document;
			}
		};

		new IssueResolution(issueResolution.getLabel(),
				issueResolution.getDescription(), issueResolution.getImage(),
				modificationContext, issueResolution.getModification(),
				issueResolution.getRelevance()).apply();
		return document.get();
	}

	/**
	 * The implementation of the following helper methods are taken from the
	 * org.eclipse.xtext.ui.testing.ContentAssistProcessorTestBuilder class.
	 */
	private IXtextDocument getDocument(String model) {
		XtextResource xtextResource = getXtextResource(model);
		XtextDocument document = injector.getInstance(XtextDocument.class);
		document.set(model);
		document.setInput(xtextResource);
		return document;
	}

	private XtextResource getXtextResource(String model) {
		StringInputStream in = new StringInputStream(
				Strings.emptyIfNull(model));

		// creating an in-memory EMF Resource
		URI uri = URI.createURI(""); //$NON-NLS-1$
		Resource resource = injector.getInstance(IResourceFactory.class)
				.createResource(uri);
		new XtextResourceSet().getResources().add(resource);
		try {
			resource.load(in, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (resource instanceof LazyLinkingResource) {
			((LazyLinkingResource) resource)
					.resolveLazyCrossReferences(CancelIndicator.NullImpl);
		} else {
			EcoreUtil.resolveAll(resource);
		}
		return (XtextResource) resource;
	}
}
