/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #321775)
 *     Zoey Prigge    (itemis AG) - deprecation/strikethrough (bug #552993)
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.highlighting;

import java.util.Arrays;

import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.resource.XtextResource;

import com.google.inject.Injector;

public class DotSubgrammarHighlighter {

	private String language;

	public DotSubgrammarHighlighter(String language) {
		this.language = language;
	}

	public void provideHightlightingFor(String text, int startOffset,
			IHighlightedPositionAcceptor hostGrammarAcceptor) {
		provideHightlightingFor(text, startOffset, hostGrammarAcceptor, null);
	}

	public void provideHightlightingFor(String text, int startOffset,
			IHighlightedPositionAcceptor hostGrammarAcceptor, String suffix) {

		Injector injector = DotActivator.getInstance().getInjector(language);
		ISemanticHighlightingCalculator subgrammarCalculator = injector
				.getInstance(ISemanticHighlightingCalculator.class);

		XtextResource xtextResource = DotEditorUtils.getXtextResource(injector,
				text);

		subgrammarCalculator.provideHighlightingFor(xtextResource,
				new IHighlightedPositionAcceptor() {

					@Override
					public void addPosition(int offset, int length,
							String... id) {
						if (suffix != null) {
							id = Arrays.stream(id).map(e -> e + suffix)
									.toArray(String[]::new);
						}

						hostGrammarAcceptor.addPosition(startOffset + offset,
								length, id);
					}

				}, null);
	}
}
