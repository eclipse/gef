/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.folding;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.xtext.ui.editor.folding.DefaultFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionAcceptor;
import org.eclipse.xtext.util.ITextRegion;

public class DotHtmlLabelFoldingRegionProvider
		extends DefaultFoldingRegionProvider {

	@Override
	protected void computeObjectFolding(EObject eObject,
			IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor) {

		/*
		 * Do not calculate folding region if the html content contains only
		 * whitespaces
		 */
		if (eObject instanceof HtmlContent) {
			HtmlContent htmlContent = (HtmlContent) eObject;
			if (htmlContent.getText() != null
					&& !htmlContent.getText().trim().isEmpty()) {
				super.computeObjectFolding(eObject, foldingRegionAcceptor);
			}
		} else {
			super.computeObjectFolding(eObject, foldingRegionAcceptor);
		}
	}
}
