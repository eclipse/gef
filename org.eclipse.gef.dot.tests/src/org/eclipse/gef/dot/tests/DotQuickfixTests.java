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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider;
import org.eclipse.gef.dot.internal.language.dot.DotAst;
import org.eclipse.gef.dot.internal.ui.language.quickfix.DotQuickfixProvider;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.model.DocumentPartitioner;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IssueModificationContext;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolution;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.validation.Issue;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Injector;

@RunWith(XtextRunner.class)
@InjectWith(DotUiInjectorProvider.class)
public class DotQuickfixTests {

	@Inject
	private Injector injector;

	@Inject
	private ParseHelper<DotAst> parseHelper;

	@Inject
	private ValidationTestHelper validationTestHelper;

	@Inject
	private DotQuickfixProvider quickfixProvider;

	@Test
	public void edge_style() {
		// test unquoted edge style
		String text = "graph{1--2[style=foo]}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		String[][] expectedQuickfixes = {
				{ "Replace 'foo' with '\"bold\"'.",
						"Use valid '\"bold\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"bold\"]}" },
				{ "Replace 'foo' with '\"dashed\"'.",
						"Use valid '\"dashed\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"dashed\"]}" },
				{ "Replace 'foo' with '\"dotted\"'.",
						"Use valid '\"dotted\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"dotted\"]}" },
				{ "Replace 'foo' with '\"invis\"'.",
						"Use valid '\"invis\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"invis\"]}" },
				{ "Replace 'foo' with '\"solid\"'.",
						"Use valid '\"solid\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"solid\"]}" },
				{ "Replace 'foo' with '\"tapered\"'.",
						"Use valid '\"tapered\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"tapered\"]}" } };

		assertIssueResolutions(text, expectedQuickfixes);

		// test quoted edge style
		text = "graph{1--2[style=\"foo\"]}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {
				{ "Replace 'foo' with '\"bold\"'.",
						"Use valid '\"bold\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"bold\"]}" },
				{ "Replace 'foo' with '\"dashed\"'.",
						"Use valid '\"dashed\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"dashed\"]}" },
				{ "Replace 'foo' with '\"dotted\"'.",
						"Use valid '\"dotted\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"dotted\"]}" },
				{ "Replace 'foo' with '\"invis\"'.",
						"Use valid '\"invis\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"invis\"]}" },
				{ "Replace 'foo' with '\"solid\"'.",
						"Use valid '\"solid\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"solid\"]}" },
				{ "Replace 'foo' with '\"tapered\"'.",
						"Use valid '\"tapered\"' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"tapered\"]}" } };

		assertIssueResolutions(text, expectedQuickfixes);

	}

	private void assertIssueResolutions(String text, String[][] expected) {
		DotAst dotAst = null;
		try {
			dotAst = parseHelper.parse(text);
		} catch (Exception e) {
			fail(e.getMessage());
			e.printStackTrace();
		}

		assertNotNull(dotAst);
		List<Issue> issues = validationTestHelper.validate(dotAst);

		assertEquals(1, issues.size());

		List<IssueResolution> issueResolutions = quickfixProvider
				.getResolutions(issues.get(0));

		assertEquals(expected.length, issueResolutions.size());

		for (int i = 0; i < issueResolutions.size(); i++) {
			IssueResolution actual = issueResolutions.get(i);

			String expectedLabel = expected[i][0];
			String expectedDescription = expected[i][1];
			String expectedResult = expected[i][2];

			assertEquals(expectedLabel, actual.getLabel());
			assertEquals(expectedDescription, actual.getDescription());
			assertIssueResolutionEffect(text, actual, expectedResult);
		}
	}

	private void assertIssueResolutionEffect(String originalText,
			IssueResolution issueResolution, String expectedResult) {
		IXtextDocument xtextDocument = null;

		try {
			xtextDocument = getDocument(originalText);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertNotNull(xtextDocument);

		TestIssueModificationContext modificationContext = new TestIssueModificationContext();
		modificationContext.setDocument(xtextDocument);

		issueResolution = new IssueResolution(issueResolution.getLabel(),
				issueResolution.getDescription(), issueResolution.getImage(),
				modificationContext, issueResolution.getModification(),
				issueResolution.getRelevance());

		issueResolution.apply();

		String actualResult = issueResolution.getModificationContext()
				.getXtextDocument().get();

		assertEquals(actualResult, expectedResult);
	}

	/**
	 * The implementation of the following helper methods are taken from the
	 * org.eclipse.xtext.junit4.ui.ContentAssistProcessorTestBuilder java class.
	 */

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

	private class TestIssueModificationContext
			extends IssueModificationContext {
		private IXtextDocument doc;

		@Override
		public IXtextDocument getXtextDocument() {
			return doc;
		}

		public void setDocument(IXtextDocument doc) {
			this.doc = doc;
		}
	}
}
