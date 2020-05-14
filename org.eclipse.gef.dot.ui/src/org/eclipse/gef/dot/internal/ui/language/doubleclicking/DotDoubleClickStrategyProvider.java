/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
