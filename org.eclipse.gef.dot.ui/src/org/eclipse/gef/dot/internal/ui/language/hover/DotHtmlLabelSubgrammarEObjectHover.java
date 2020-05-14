/*******************************************************************************
 * Copyright (c) 2019, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #549412)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider.IInformationControlCreatorProvider;

public class DotHtmlLabelSubgrammarEObjectHover extends DotEObjectHover {
	private IEObjectHoverProvider hoverProvider = DotActivator.getInstance()
			.getInjector(
					DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTHTMLLABEL)
			.getInstance(IEObjectHoverProvider.class);

	/*
	 * Copied from DispatchingEObjectHover to remove serviceProvider as no
	 * actual document exists for HTML code snippets and URI determination fails
	 */
	@Override
	public Object getHoverInfo(EObject first, ITextViewer textViewer,
			IRegion hoverRegion) {
		if (hoverProvider == null)
			return null;
		IInformationControlCreatorProvider creatorProvider = hoverProvider
				.getHoverInfo(first, textViewer, hoverRegion);
		if (creatorProvider == null)
			return null;
		this.lastCreatorProvider = creatorProvider;
		return lastCreatorProvider.getInfo();
	}

	public IInformationControlCreatorProvider getLastCreatorProvider() {
		return lastCreatorProvider;
	}

	public void setContainingAttribute(Attribute containingAttribute) {
		if (hoverProvider instanceof DotHtmlLabelSubgrammarHoverProvider) {
			((DotHtmlLabelSubgrammarHoverProvider) hoverProvider)
					.setContainingAttribute(containingAttribute);
		}
	}
}
