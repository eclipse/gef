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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.gef.dot.internal.ui.language.DotActivatorEx;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.xtext.ui.editor.doubleClicking.DoubleClickStrategyProvider;
import org.eclipse.xtext.ui.editor.model.XtextDocument;

import com.google.inject.Injector;

public class DotHtmlLabelDoubleClickStrategy
		extends DefaultTextDoubleClickStrategy {

	@Override
	protected IRegion findWord(IDocument document, int offset) {
		ITypedRegion region = null;
		try {
			region = document.getPartition(offset);
		} catch (BadLocationException e) {
			DotActivatorEx.logError(e);
			return null;
		}

		// Translate the partition offset for the HtmlLabel by one to point to
		// the position after the angle bracket indicating an HtmlLabelStart in
		// the host grammar.
		int htmlLabelStartOffset = region.getOffset() + 1;
		// Translate the partition length by two points to discount for both
		// HtmlLabel indicating angle brackets (start and end) only present in
		// the dot host grammar.
		int htmlLabelLength = region.getLength() - 2;

		String htmlLabel = null;
		try {
			htmlLabel = document.get(htmlLabelStartOffset, htmlLabelLength);
		} catch (BadLocationException e) {
			DotActivatorEx.logError(e);
			return null;
		}

		Injector injector = DotActivator.getInstance().getInjector(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTHTMLLABEL);

		IDocument htmlDocument = null;
		try {
			htmlDocument = DotEditorUtils.getDocument(injector, htmlLabel);
		} catch (Exception e) {
			DotActivatorEx.logError(e);
			return null;
		}

		// translate the offset of the double-click position relative to
		// the htmlDocument
		int htmlLabelClickOffset = offset - htmlLabelStartOffset;

		ITextDoubleClickStrategy doubleClickStrategy = null;
		try {
			doubleClickStrategy = injector
					.getInstance(DoubleClickStrategyProvider.class)
					.getStrategy(null,
							htmlDocument.getContentType(htmlLabelClickOffset),
							XtextDocument.DEFAULT_PARTITIONING);
		} catch (BadLocationException e) {
			DotActivatorEx.logError(e);
			return null;
		}

		IRegion htmlRegion = findRegionHtml(htmlDocument, htmlLabelClickOffset,
				doubleClickStrategy);

		return new Region(htmlLabelStartOffset + htmlRegion.getOffset(),
				htmlRegion.getLength());
	}

	private IRegion findRegionHtml(IDocument htmlDocument,
			int htmlLabelClickOffset,
			ITextDoubleClickStrategy doubleClickStrategy) {
		IRegion htmlRegion = findExtendedSelectionHtml(doubleClickStrategy,
				htmlDocument, htmlLabelClickOffset);

		if (htmlRegion != null)
			return htmlRegion;

		return findWordHtml(doubleClickStrategy, htmlDocument,
				htmlLabelClickOffset);
	}

	private IRegion findExtendedSelectionHtml(
			ITextDoubleClickStrategy doubleClickStrategy,
			IDocument htmlDocument, int htmlLabelClickOffset) {
		Method findExtendedSelection = findExtendedDoubleClickSelectionMethod(
				doubleClickStrategy.getClass());
		return invokeMethodOn(findExtendedSelection, doubleClickStrategy,
				htmlDocument, htmlLabelClickOffset);
	}

	private IRegion findWordHtml(ITextDoubleClickStrategy doubleClickStrategy,
			IDocument htmlDocument, int htmlLabelClickOffset) {
		Method findWord = findWordMethod(doubleClickStrategy.getClass());
		return invokeMethodOn(findWord, doubleClickStrategy, htmlDocument,
				htmlLabelClickOffset);
	}

	/**
	 * Returns a Method Object encapsulating the protected findWord Method
	 * inherited from DefaultTextDoubleClickStrategy.
	 * 
	 * The method object is set to be accessible.
	 * 
	 * @param strategyClass
	 *            may not be null
	 * @return
	 */
	private Method findWordMethod(Class<?> strategyClass) {
		return methodByName("findWord", strategyClass); //$NON-NLS-1$
	}

	/**
	 * Returns a Method Object encapsulating the protected
	 * findExtendedDoubleClickSelection Method inherited from
	 * DefaultTextDoubleClickStrategy.
	 * 
	 * The method object is set to be accessible.
	 * 
	 * @param strategyClass
	 *            may not be null
	 * @return
	 */
	private Method findExtendedDoubleClickSelectionMethod(
			Class<?> strategyClass) {
		return methodByName("findExtendedDoubleClickSelection", //$NON-NLS-1$
				strategyClass);
	}

	private Method methodByName(String name, Class<?> strategyClass) {
		Method method;
		try {
			method = strategyClass.getDeclaredMethod(name, IDocument.class,
					int.class);
		} catch (NoSuchMethodException | SecurityException e) {
			Class<?> superClass = strategyClass.getSuperclass();
			if (superClass != null)
				return methodByName(name, strategyClass.getSuperclass());
			return null;
		}
		method.setAccessible(true);
		return method;
	}

	private IRegion invokeMethodOn(Method method,
			ITextDoubleClickStrategy doubleClickStrategy,
			IDocument htmlDocument, int htmlLabelClickOffset) {
		IRegion htmlRegion = null;
		try {
			htmlRegion = (IRegion) method.invoke(doubleClickStrategy,
					htmlDocument, htmlLabelClickOffset);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | ClassCastException e) {
			DotActivatorEx.logError(e);
			return null;
		}
		return htmlRegion;
	}

}
