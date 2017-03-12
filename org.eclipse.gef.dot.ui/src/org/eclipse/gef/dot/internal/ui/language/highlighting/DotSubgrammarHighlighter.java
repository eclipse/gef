/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #321775)
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.highlighting;

import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.StringInputStream;

import com.google.inject.Injector;

public class DotSubgrammarHighlighter {

	private String language;

	public DotSubgrammarHighlighter(String language) {
		this.language = language;
	}

	public void provideHightlightingFor(String text, int startOffset,
			IHighlightedPositionAcceptor hostGrammarAcceptor) {

		Injector injector = DotActivator.getInstance().getInjector(language);
		ISemanticHighlightingCalculator subgrammarCalculator = injector
				.getInstance(ISemanticHighlightingCalculator.class);

		XtextResource xtextResource = null;
		try {
			xtextResource = doGetResource(injector, new StringInputStream(text),
					URI.createURI("dummy:/example.mydsl")); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		}

		subgrammarCalculator.provideHighlightingFor(xtextResource,
				new IHighlightedPositionAcceptor() {

					@Override
					public void addPosition(int offset, int length,
							String... id) {
						hostGrammarAcceptor.addPosition(startOffset + offset,
								length, id);
					}

				});
	}

	private XtextResource doGetResource(Injector injector, InputStream in,
			URI uri) throws Exception {
		XtextResourceSet rs = injector.getInstance(XtextResourceSet.class);
		rs.setClasspathURIContext(getClass());
		XtextResource resource = (XtextResource) injector
				.getInstance(IResourceFactory.class).createResource(uri);
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
}
