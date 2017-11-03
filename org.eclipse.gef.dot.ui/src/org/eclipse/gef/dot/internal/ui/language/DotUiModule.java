/*******************************************************************************
 * Copyright (c) 2016, 2018 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - add binding for template proposal provider (bug #321775)
 *                                 - add binding for folding region provider (bug #321775)
 *                                 - add binding for EObject hover/hover provider (bug #461506)
 *                                 - add binding for hyperlink helper (bug #461506)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language;

import org.eclipse.gef.dot.internal.ui.language.contentassist.DynamicTemplateProposalProvider;
import org.eclipse.gef.dot.internal.ui.language.folding.DotFoldingRegionProvider;
import org.eclipse.gef.dot.internal.ui.language.highlighting.DotAntlrTokenToAttributeIdMapper;
import org.eclipse.gef.dot.internal.ui.language.highlighting.DotHighlightingConfiguration;
import org.eclipse.gef.dot.internal.ui.language.highlighting.DotSemanticHighlightingCalculator;
import org.eclipse.gef.dot.internal.ui.language.hover.DotEObjectHover;
import org.eclipse.gef.dot.internal.ui.language.hover.DotHoverProvider;
import org.eclipse.gef.dot.internal.ui.language.hyperlinking.DotHyperlinkHelper;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.contentassist.ITemplateProposalProvider;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkHelper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.AbstractAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

/**
 * Use this class to register components to be used within the IDE.
 */
public class DotUiModule
		extends org.eclipse.gef.dot.internal.ui.language.AbstractDotUiModule {

	public DotUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
		return DotSemanticHighlightingCalculator.class;
	}

	public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
		return DotHighlightingConfiguration.class;
	}

	public Class<? extends AbstractAntlrTokenToAttributeIdMapper> bindAbstractAntlrTokenToAttributeIdMapper() {
		return DotAntlrTokenToAttributeIdMapper.class;
	}

	@Override
	public Class<? extends ITemplateProposalProvider> bindITemplateProposalProvider() {
		return DynamicTemplateProposalProvider.class;
	}

	public Class<? extends IFoldingRegionProvider> bindIFoldingRegionProvider() {
		return DotFoldingRegionProvider.class;
	}

	@Override
	public Class<? extends org.eclipse.xtext.ui.editor.hover.IEObjectHover> bindIEObjectHover() {
		return DotEObjectHover.class;
	}

	public Class<? extends IEObjectHoverProvider> bindIEObjectHoverProvider() {
		return DotHoverProvider.class;
	}

	public Class<? extends IHyperlinkHelper> bindIHyperlinkHelper() {
		return DotHyperlinkHelper.class;
	}
}
