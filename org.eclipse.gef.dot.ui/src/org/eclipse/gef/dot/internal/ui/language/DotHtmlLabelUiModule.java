/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand   (itemis AG) - initial API and implementation
 *     Tamas Miklossy     (itemis AG) - improve html-label highlighting/folding/refactoring
 *     Zoey Gerrit Prigge (itemis AG) - bind double click strategy provider (bug #532244)
 *                                    - bindings for subgrammar hovering (hover/provider) (bug #549412)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language;

import org.eclipse.gef.dot.internal.ui.language.doubleclicking.DotHtmlLabelDoubleClickStrategyProvider;
import org.eclipse.gef.dot.internal.ui.language.editor.DotHtmlLabelTerminalsTokenTypeToPartitionMapper;
import org.eclipse.gef.dot.internal.ui.language.folding.DotHtmlLabelFoldingRegionProvider;
import org.eclipse.gef.dot.internal.ui.language.highlighting.DotHtmlLabelSemanticHighlightingCalculator;
import org.eclipse.gef.dot.internal.ui.language.hover.DotHtmlLabelSubgrammarEObjectHover;
import org.eclipse.gef.dot.internal.ui.language.hover.DotHtmlLabelSubgrammarHoverProvider;
import org.eclipse.gef.dot.internal.ui.language.renaming.DotHtmlLabelRenameStrategy;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.ui.editor.doubleClicking.DoubleClickStrategyProvider;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHover;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.model.ITokenTypeToPartitionTypeMapper;
import org.eclipse.xtext.ui.refactoring.IRenameStrategy;

/**
 * Use this class to register components to be used within the IDE.
 */
public class DotHtmlLabelUiModule extends
		org.eclipse.gef.dot.internal.ui.language.AbstractDotHtmlLabelUiModule {
	public DotHtmlLabelUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
		return DotHtmlLabelSemanticHighlightingCalculator.class;
	}

	public Class<? extends IFoldingRegionProvider> bindIFoldingRegionProvider() {
		return DotHtmlLabelFoldingRegionProvider.class;
	}

	public Class<? extends ITokenTypeToPartitionTypeMapper> bindITokenTypeToPartitionTypeMapper() {
		return DotHtmlLabelTerminalsTokenTypeToPartitionMapper.class;
	}

	public Class<? extends DoubleClickStrategyProvider> bindDoubleClickStrategyProvider() {
		return DotHtmlLabelDoubleClickStrategyProvider.class;
	}

	@Override
	public Class<? extends IRenameStrategy> bindIRenameStrategy() {
		return DotHtmlLabelRenameStrategy.class;
	}

	@Override
	public Class<? extends IEObjectHover> bindIEObjectHover() {
		return DotHtmlLabelSubgrammarEObjectHover.class;
	}

	public Class<? extends IEObjectHoverProvider> bindIEObjectHoverProvider() {
		return DotHtmlLabelSubgrammarHoverProvider.class;
	}

}
