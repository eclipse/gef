/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #532244)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.doubleclicking;

import org.eclipse.gef.dot.internal.ui.language.editor.DotTerminalsTokenTypeToPartitionMapper;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.xtext.ui.editor.doubleClicking.DoubleClickStrategyProvider;

public class DotDoubleClickStrategyProvider
		extends DoubleClickStrategyProvider {

	@Override
	public ITextDoubleClickStrategy getStrategy(ISourceViewer sourceViewer,
			String contentType, String documentPartitioning) {
		if (DotTerminalsTokenTypeToPartitionMapper.HTML_STRING_PARTITION
				.equals(contentType)) {
			return new DotHtmlLabelDoubleClickStrategy();
		}
		return super.getStrategy(sourceViewer, contentType,
				documentPartitioning);
	}

}
