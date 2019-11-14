/*******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)    - initial API and implementation
 *     Tamas Miklossy  (itemis AG)    - add binding for template proposal provider (bug #321775)
 *                                    - add binding for folding region provider (bug #321775)
 *                                    - add binding for EObject hover/hover provider (bug #461506)
 *                                    - add binding for hyperlink helper (bug #461506)
 *                                    - add binding for rename strategy (bug #530423)
 *                                    - add binding for occurrence computer (bug #530699)
 *                                    - add binding for reference finder/reference query executor (bug #531049)
 *                                    - add binding for the Xtext Editor
 *                                    - add binding for token type to partition mapper (bug #532244)
 *                                    - add binding for abstract edit strategy provider (bug #536795)
 *     Zoey Gerrit Prigge (itemis AG) - add binding for doubleClickStrategyProvider (bug #532244)
 *                                    - add binding for DotSourceViewerConfiguration (bug #549412)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language;

import org.eclipse.gef.dot.internal.ui.language.contentassist.DotTemplateProposalProvider;
import org.eclipse.gef.dot.internal.ui.language.doubleclicking.DotDoubleClickStrategyProvider;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditor;
import org.eclipse.gef.dot.internal.ui.language.editor.DotSourceViewerConfiguration;
import org.eclipse.gef.dot.internal.ui.language.editor.DotTerminalsTokenTypeToPartitionMapper;
import org.eclipse.gef.dot.internal.ui.language.editor.autoedit.DotAutoEditStrategyProvider;
import org.eclipse.gef.dot.internal.ui.language.findreferences.DotFindReferencesQueryExecutor;
import org.eclipse.gef.dot.internal.ui.language.findreferences.DotUiReferenceFinder;
import org.eclipse.gef.dot.internal.ui.language.folding.DotFoldingRegionProvider;
import org.eclipse.gef.dot.internal.ui.language.highlighting.DotAntlrTokenToAttributeIdMapper;
import org.eclipse.gef.dot.internal.ui.language.highlighting.DotHighlightingConfiguration;
import org.eclipse.gef.dot.internal.ui.language.highlighting.DotSemanticHighlightingCalculator;
import org.eclipse.gef.dot.internal.ui.language.hover.DotEObjectHover;
import org.eclipse.gef.dot.internal.ui.language.hover.DotHoverProvider;
import org.eclipse.gef.dot.internal.ui.language.hyperlinking.DotHyperlinkHelper;
import org.eclipse.gef.dot.internal.ui.language.markoccurrences.DotOccurrenceComputer;
import org.eclipse.gef.dot.internal.ui.language.renaming.DotRenameStrategy;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.autoedit.AbstractEditStrategyProvider;
import org.eclipse.xtext.ui.editor.contentassist.ITemplateProposalProvider;
import org.eclipse.xtext.ui.editor.doubleClicking.DoubleClickStrategyProvider;
import org.eclipse.xtext.ui.editor.findrefs.IReferenceFinder;
import org.eclipse.xtext.ui.editor.findrefs.ReferenceQueryExecutor;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHover;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkHelper;
import org.eclipse.xtext.ui.editor.model.ITokenTypeToPartitionTypeMapper;
import org.eclipse.xtext.ui.editor.occurrences.IOccurrenceComputer;
import org.eclipse.xtext.ui.editor.syntaxcoloring.AbstractAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.ui.refactoring.IRenameStrategy;

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
		return DotTemplateProposalProvider.class;
	}

	public Class<? extends IFoldingRegionProvider> bindIFoldingRegionProvider() {
		return DotFoldingRegionProvider.class;
	}

	@Override
	public Class<? extends IEObjectHover> bindIEObjectHover() {
		return DotEObjectHover.class;
	}

	public Class<? extends IEObjectHoverProvider> bindIEObjectHoverProvider() {
		return DotHoverProvider.class;
	}

	public Class<? extends IHyperlinkHelper> bindIHyperlinkHelper() {
		return DotHyperlinkHelper.class;
	}

	@Override
	public Class<? extends IRenameStrategy> bindIRenameStrategy() {
		return DotRenameStrategy.class;
	}

	public Class<? extends IOccurrenceComputer> bindIOccurrenceComputer() {
		return DotOccurrenceComputer.class;
	}

	public Class<? extends IReferenceFinder> bindIReferenceFinder() {
		return DotUiReferenceFinder.class;
	}

	public Class<? extends ReferenceQueryExecutor> bindReferenceQueryExecutor() {
		return DotFindReferencesQueryExecutor.class;
	}

	public Class<? extends XtextEditor> bindXtextEditor() {
		return DotEditor.class;
	}

	public Class<? extends ITokenTypeToPartitionTypeMapper> bindITokenTypeToPartitionTypeMapper() {
		return DotTerminalsTokenTypeToPartitionMapper.class;
	}

	public Class<? extends DoubleClickStrategyProvider> bindDoubleClickStrategyProvider() {
		return DotDoubleClickStrategyProvider.class;
	}

	@Override
	public Class<? extends AbstractEditStrategyProvider> bindAbstractEditStrategyProvider() {
		return DotAutoEditStrategyProvider.class;
	}

	public Class<? extends XtextSourceViewerConfiguration> bindXtextSourceViewerConfiguration() {
		return DotSourceViewerConfiguration.class;
	}
}
