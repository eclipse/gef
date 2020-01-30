/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Zoey Prigge     (itemis AG) - bind semantic highlighting calculator (bug #552993)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language;

import org.eclipse.gef.dot.internal.ui.language.highlighting.DotArrowTypeSemanticHighlightingCalculator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;

/**
 * Use this class to register components to be used within the IDE.
 */
public class DotArrowTypeUiModule extends
		org.eclipse.gef.dot.internal.ui.language.AbstractDotArrowTypeUiModule {
	public DotArrowTypeUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
		return DotArrowTypeSemanticHighlightingCalculator.class;
	}
}
