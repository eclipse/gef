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
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.ISourceViewerAware;
import org.eclipse.xtext.ui.editor.hover.IEObjectHover;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocumentUtil;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Injector;

public class DotHtmlLabelAdaptingTextHover extends DotEObjectHover {
	private final Injector injector = DotActivator.getInstance().getInjector(
			DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTHTMLLABEL);

	private final ISourceViewer sourceViewer = new DotHtmlLabelHoverFakeSourceViewer();

	private final IEObjectHover hover;

	public DotHtmlLabelAdaptingTextHover() {
		IEObjectHover hover = injector.getInstance(IEObjectHover.class);
		if (hover instanceof ISourceViewerAware) {
			((ISourceViewerAware) hover).setSourceViewer(sourceViewer);
		}
		this.hover = hover;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		HtmlOffsetPair html = htmlOffsetPair(offset, textViewer);
		if (html == null) {
			return super.getHoverRegion(textViewer, offset);
		}
		synchronized (sourceViewer) {
			try {
				sourceViewer.setDocument(
						DotEditorUtils.getDocument(injector, html.code));
			} catch (Exception e) {
				return super.getHoverRegion(textViewer, offset);
			}
			if (hover instanceof ITextHover) {
				IRegion htmlRegion = ((ITextHover) hover)
						.getHoverRegion(sourceViewer, offset - html.offset);
				if (htmlRegion == null) {
					return null;
				}
				return new Region(html.offset + htmlRegion.getOffset(),
						htmlRegion.getLength());
			}
		}
		return super.getHoverRegion(textViewer, offset);
	}

	private HtmlOffsetPair htmlOffsetPair(int offset, ITextViewer textViewer) {
		// copied from AbstractEObjectHover.getHoverRegion
		IXtextDocument xtextDocument = XtextDocumentUtil.get(textViewer);
		if (xtextDocument == null)
			return null;
		// TODO this is being called on change in the UI-thread. Not a good idea
		// to do such expensive stuff.
		// returning the region on a per token basis would be better.

		return xtextDocument
				.readOnly(new IUnitOfWork<HtmlOffsetPair, XtextResource>() {
					public HtmlOffsetPair exec(XtextResource state)
							throws Exception {
						// resource can be null e.g. read only zip/jar entry
						if (state == null) {
							return null;
						}
						Pair<EObject, IRegion> element = getXtextElementAt(
								state, offset);
						if (element.getFirst() instanceof Attribute) {
							String code = ((Attribute) element.getFirst())
									.getValue().toValue();
							// element offset returns length of entire EObject,
							// i.e. Attribute; we need offset for attributeValue
							int offset = element.getSecond().getOffset()
									+ element.getSecond().getLength() - 1
									- code.length();
							return new HtmlOffsetPair(code, offset);
						} else {
							return null;
						}
					}
				});
	}

	@Override
	public Object getHoverInfo(EObject eObject, ITextViewer textViewer,
			IRegion hoverRegion) {
		if (textViewer == null || hoverRegion == null) {
			return null;
		}

		HtmlOffsetPair html = htmlOffsetPair(hoverRegion.getOffset(),
				textViewer);
		if (html == null) {
			return null;
		}

		synchronized (sourceViewer) {
			try {
				sourceViewer.setDocument(
						DotEditorUtils.getDocument(injector, html.code));
			} catch (Exception e) {
				return null;
			}

			Object hoverInfo = null;

			if (hover instanceof DotHtmlLabelSubgrammarEObjectHover
					&& eObject instanceof Attribute) {
				((DotHtmlLabelSubgrammarEObjectHover) hover)
						.setContainingAttribute((Attribute) eObject);
			}

			if (hover instanceof ITextHoverExtension2) {
				hoverInfo = ((ITextHoverExtension2) hover).getHoverInfo2(
						sourceViewer,
						new Region(hoverRegion.getOffset() - html.offset,
								hoverRegion.getLength()));
			}

			if (hover instanceof DotHtmlLabelSubgrammarEObjectHover) {
				lastCreatorProvider = ((DotHtmlLabelSubgrammarEObjectHover) hover)
						.getLastCreatorProvider();
			}

			return hoverInfo;
		}
	}

	private static class HtmlOffsetPair {
		private String code;
		private int offset;

		private HtmlOffsetPair(String code, int offset) {
			this.code = code;
			this.offset = offset;
		}
	}
}
