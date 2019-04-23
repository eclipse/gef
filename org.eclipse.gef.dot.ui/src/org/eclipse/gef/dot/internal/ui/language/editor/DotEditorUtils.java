/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
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
package org.eclipse.gef.dot.internal.ui.language.editor;

import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator;
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivatorEx;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.DocumentPartitioner;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.Strings;

import com.google.inject.Injector;

public class DotEditorUtils {

	/**
	 * @param object
	 * 
	 * @return true if the object is the DOT Editor, false otherwise
	 */
	public static boolean isDotEditor(Object object) {
		if (object instanceof XtextEditor) {
			XtextEditor editor = (XtextEditor) object;
			return DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOT
					.equals(editor.getLanguageName());
		}

		return false;
	}

	public static StyledString style(String format, Object... args) {
		String text = String.format(format, args);
		StyledString styled = new StyledString(text);
		int offset = text.indexOf(':');
		styled.setStyle(offset, text.length() - offset,
				StyledString.DECORATIONS_STYLER);
		return styled;
	}

	/**
	 * The implementation of the following helper methods are taken from the
	 * org.eclipse.xtext.junit4.ui.ContentAssistProcessorTestBuilder java class.
	 */
	public static IXtextDocument getDocument(final Injector injector,
			final String currentModelToParse) throws Exception {
		XtextResource xtextResource = getXtextResource(injector,
				currentModelToParse);
		return getDocument(injector, xtextResource, currentModelToParse);
	}

	public static XtextResource getXtextResource(Injector injector,
			final String currentModelToParse) {
		XtextResource xtextResource = null;

		try {
			xtextResource = doGetResource(injector,
					new StringInputStream(
							Strings.emptyIfNull(currentModelToParse)),
					// creating an in-memory EMF Resource
					URI.createURI("")); //$NON-NLS-1$
		} catch (Exception e) {
			DotActivatorEx.logError(e);
		}

		return xtextResource;
	}

	private static IXtextDocument getDocument(final Injector injector,
			final XtextResource xtextResource, final String model) {
		XtextDocument document = injector.getInstance(XtextDocument.class);
		document.set(model);
		document.setInput(xtextResource);
		DocumentPartitioner partitioner = injector
				.getInstance(DocumentPartitioner.class);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		return document;
	}

	private static XtextResource doGetResource(Injector injector,
			InputStream in, URI uri) throws Exception {
		XtextResourceSet rs = injector.getInstance(XtextResourceSet.class);
		rs.setClasspathURIContext(DotEditorUtils.class);
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
