/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.quickfix;

import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;

/**
 * Custom quickfixes.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#quick-fixes
 */
public class DotArrowTypeQuickfixProvider extends DefaultQuickfixProvider {

	// @Fix(DotArrowTypeValidator.INVALID_NAME)
	// public void capitalizeName(final Issue issue, IssueResolutionAcceptor
	// acceptor) {
	// acceptor.accept(issue, "Capitalize name", "Capitalize the name.",
	// "upcase.png", new IModification() {
	// public void apply(IModificationContext context) throws
	// BadLocationException {
	// IXtextDocument xtextDocument = context.getXtextDocument();
	// String firstLetter = xtextDocument.get(issue.getOffset(), 1);
	// xtextDocument.replace(issue.getOffset(), 1, firstLetter.toUpperCase());
	// }
	// });
	// }

}
