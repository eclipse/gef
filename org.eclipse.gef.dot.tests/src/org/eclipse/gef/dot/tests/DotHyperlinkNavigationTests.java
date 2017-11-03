/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider;
import org.eclipse.gef.dot.internal.language.dot.NodeId;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkHelper;
import org.eclipse.xtext.ui.editor.hyperlinking.XtextHyperlink;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Injector;

@RunWith(XtextRunner.class)
@InjectWith(DotUiInjectorProvider.class)
public class DotHyperlinkNavigationTests {

	@Inject
	private Injector injector;

	@Inject
	private IHyperlinkHelper hyperlinkHelper;

	@Test
	public void testHyperlinkLeftSideOfAnEdge() {
		String text = "graph{1;2 1--2}";
		String source = "1";

		verifyHyperlink(text, source);
	}

	@Test
	public void testHyperlinkRightSideOfAnEdge() {
		String text = "graph{1;2 1--2}";
		String source = "2";

		verifyHyperlink(text, source);
	}

	private void verifyHyperlink(String text, String source) {

		XtextResource resource = createXtextResource(text);
		IHyperlink[] hyperlinks = hyperlinkHelper.createHyperlinksByOffset(
				resource, text.lastIndexOf(source), false);

		assertEquals(hyperlinks.length, 1);

		IHyperlink hyperlink = hyperlinks[0];

		assertTrue(hyperlink instanceof XtextHyperlink);

		XtextHyperlink xtextHyperlink = (XtextHyperlink) hyperlink;

		URI targetUri = xtextHyperlink.getURI();

		EObject targetEObject = resource.getResourceSet().getEObject(targetUri,
				true);

		assertTrue(targetEObject instanceof NodeId);

		NodeId targetNodeId = (NodeId) targetEObject;

		ICompositeNode compositeNode = NodeModelUtils
				.findActualNodeFor(targetNodeId);
		assertEquals(text.indexOf(source), compositeNode.getTotalOffset());
		assertEquals(source.length(), compositeNode.getTotalLength());

		// verify that the target nodeId has the same name as the source nodeId
		assertEquals(source, targetNodeId.getName().toString());

		int hyperlinkOffset = xtextHyperlink.getHyperlinkRegion().getOffset();
		assertEquals(text.lastIndexOf(source), hyperlinkOffset);

		int hyperlinkLength = xtextHyperlink.getHyperlinkRegion().getLength();
		assertEquals(source.length(), hyperlinkLength);
	}

	private XtextResource createXtextResource(
			final String currentModelToParse) {
		XtextResource xtextResource = null;

		try {
			xtextResource = doGetResource(
					new StringInputStream(
							Strings.emptyIfNull(currentModelToParse)),
					URI.createURI("dummy:/example.mydsl")); //$NON-NLS-1$
		} catch (Exception exception) {
			exception.printStackTrace();
			fail("Cannot create Xtext Resource");
		}

		return xtextResource;
	}

	/**
	 * The implementation of the following helper methods are taken from the
	 * org.eclipse.xtext.junit4.ui.ContentAssistProcessorTestBuilder java class.
	 */
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

}
