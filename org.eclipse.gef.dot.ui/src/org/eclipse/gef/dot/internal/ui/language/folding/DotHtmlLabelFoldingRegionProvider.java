/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
