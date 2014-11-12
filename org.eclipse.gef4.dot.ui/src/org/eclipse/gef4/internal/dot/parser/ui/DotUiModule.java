/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.internal.dot.parser.ui;

import org.eclipse.gef4.internal.dot.parser.ui.syntaxcoloring.DotAntlrTokenToAttributeIdMapper;
import org.eclipse.gef4.internal.dot.parser.ui.syntaxcoloring.DotHighlightingConfiguration;
import org.eclipse.gef4.internal.dot.parser.ui.syntaxcoloring.DotSemanticHighlightingCalculator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.syntaxcoloring.AbstractAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

/**
 * Use this class to register components to be used within the IDE.
 */
public class DotUiModule extends
		org.eclipse.gef4.internal.dot.parser.ui.AbstractDotUiModule {
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

}
