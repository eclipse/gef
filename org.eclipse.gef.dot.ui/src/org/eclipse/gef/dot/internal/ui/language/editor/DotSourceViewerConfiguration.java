/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #549412)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.editor;

import org.eclipse.gef.dot.internal.ui.language.hover.DotHtmlLabelAdaptingTextHover;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.xtext.ui.editor.ISourceViewerAware;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class DotSourceViewerConfiguration
		extends XtextSourceViewerConfiguration {
	@Inject
	private Provider<DotHtmlLabelAdaptingTextHover> htmlHoverProvider;

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		if (contentType.equals(
				DotTerminalsTokenTypeToPartitionMapper.HTML_STRING_PARTITION)) {
			ITextHover hover = htmlHoverProvider.get();
			if (hover instanceof ISourceViewerAware) {
				((ISourceViewerAware) hover).setSourceViewer(sourceViewer);
			}
			return hover;
		}
		return super.getTextHover(sourceViewer, contentType);
	}

}
