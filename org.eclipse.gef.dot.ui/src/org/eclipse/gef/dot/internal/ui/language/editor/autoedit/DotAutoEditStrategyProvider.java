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
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #536795)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.editor.autoedit;

import org.eclipse.gef.dot.internal.ui.language.editor.DotTerminalsTokenTypeToPartitionMapper;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.ui.editor.autoedit.DefaultAutoEditStrategyProvider;

public class DotAutoEditStrategyProvider
		extends DefaultAutoEditStrategyProvider {

	@Override
	protected void configure(IEditStrategyAcceptor acceptor) {
		super.configure(acceptor);
		configureAngleBrackets(acceptor);

		/*
		 * TODO: verify why the strategy has to added to both content types
		 */
		IAutoEditStrategy strategy = new DotAutoEditStrategy();
		acceptor.accept(strategy, IDocument.DEFAULT_CONTENT_TYPE);
		acceptor.accept(strategy,
				DotTerminalsTokenTypeToPartitionMapper.HTML_STRING_PARTITION);
	}

	protected void configureAngleBrackets(IEditStrategyAcceptor acceptor) {
		acceptor.accept(singleLineTerminals.newInstance("<", ">"), //$NON-NLS-1$ //$NON-NLS-2$
				IDocument.DEFAULT_CONTENT_TYPE);
	}

}
