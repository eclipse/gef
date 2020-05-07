/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #549412)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.hover;

import org.eclipse.jface.text.IAutoIndentStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEventConsumer;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

/**
 * Fake class to support setting a document for use in delegating hover
 * calculations to html-like label subgrammar.
 *
 * Sole supported methods are getDocument and setDocument.
 */
@SuppressWarnings("deprecation")
public class DotHtmlLabelHoverFakeSourceViewer implements ISourceViewer {
	IDocument document;

	@Override
	public StyledText getTextWidget() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUndoManager(IUndoManager undoManager) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTextDoubleClickStrategy(ITextDoubleClickStrategy strategy,
			String contentType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAutoIndentStrategy(IAutoIndentStrategy strategy,
			String contentType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTextHover(ITextHover textViewerHover, String contentType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void activatePlugins() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetPlugins() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addViewportListener(IViewportListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeViewportListener(IViewportListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTextListener(ITextListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeTextListener(ITextListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTextInputListener(ITextInputListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeTextInputListener(ITextInputListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDocument(IDocument document) {
		this.document = document;
	}

	@Override
	public IDocument getDocument() {
		return document;
	}

	@Override
	public void setEventConsumer(IEventConsumer consumer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEditable(boolean editable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEditable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDocument(IDocument document, int modelRangeOffset,
			int modelRangeLength) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setVisibleRegion(int offset, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetVisibleRegion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IRegion getVisibleRegion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean overlapsWithVisibleRegion(int offset, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void changeTextPresentation(TextPresentation presentation,
			boolean controlRedraw) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void invalidateTextPresentation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTextColor(Color color) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTextColor(Color color, int offset, int length,
			boolean controlRedraw) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITextOperationTarget getTextOperationTarget() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IFindReplaceTarget getFindReplaceTarget() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDefaultPrefixes(String[] defaultPrefixes,
			String contentType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setIndentPrefixes(String[] indentPrefixes, String contentType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSelectedRange(int offset, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Point getSelectedRange() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISelectionProvider getSelectionProvider() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void revealRange(int offset, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTopIndex(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getTopIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getTopIndexStartOffset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBottomIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBottomIndexEndOffset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getTopInset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configure(SourceViewerConfiguration configuration) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAnnotationHover(IAnnotationHover annotationHover) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDocument(IDocument document,
			IAnnotationModel annotationModel) {
		setDocument(document);
	}

	@Override
	public void setDocument(IDocument document,
			IAnnotationModel annotationModel, int modelRangeOffset,
			int modelRangeLength) {
		setDocument(document);
	}

	@Override
	public IAnnotationModel getAnnotationModel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRangeIndicator(Annotation rangeIndicator) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRangeIndication(int offset, int length, boolean moveCursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IRegion getRangeIndication() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeRangeIndication() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void showAnnotations(boolean show) {
		throw new UnsupportedOperationException();
	}

}
