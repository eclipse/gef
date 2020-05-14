/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.internal.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.cloudio.internal.ui.layout.ILayouter;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;

/**
 * A model-based adapter for a {@link TagCloud}.
 * 
 * @author sschwieb
 */
public class TagCloudViewer extends ContentViewer {

	private TagCloud cloud;

	private Set<Word> selection = new HashSet<>();

	private Map<Object, Word> objectMap = new HashMap<>();

	private int maxWords = 300;

	private IProgressMonitor monitor;

	/**
	 * Create a new TagCloudViewer for the given {@link TagCloud}, which must
	 * not be <code>null</code>.
	 * 
	 * @param cloud
	 */
	public TagCloudViewer(TagCloud cloud) {
		Assert.isLegal(cloud != null, "TagCloud must not be null!");
		Assert.isLegal(!cloud.isDisposed(), "TagCloud must not be disposed!");
		this.cloud = cloud;
		initListeners();
	}

	/**
	 * Initialize the default tag cloud listeners. Can be overridden to modify
	 * the behaviour of the viewer.
	 */
	protected void initListeners() {
		initSelectionListener();
		initMouseWheelListener();
		initToolTipSupport();
	}

	/**
	 * Initialize tool tip support when the cursor hovers a word.
	 */
	protected void initToolTipSupport() {
		cloud.addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseHover(MouseEvent e) {
			}

			@Override
			public void mouseExit(MouseEvent e) {
				cloud.setToolTipText(null);
			}

			@Override
			public void mouseEnter(MouseEvent e) {
				Word word = (Word) e.data;
				ICloudLabelProvider labelProvider = (ICloudLabelProvider) getLabelProvider();
				cloud.setToolTipText(labelProvider.getToolTip(word.data));
			}
		});
	}

	/**
	 * Initialize the mouse wheel listener to support zooming in and out.
	 */
	protected void initMouseWheelListener() {
		cloud.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e) {
				if (e.count > 0) {
					cloud.zoomIn();
				} else {
					cloud.zoomOut();
				}

			}
		});
	}

	/**
	 * Initialize default selection behaviour: Words can be selected by mouse
	 * click, and selection listeners are notified when the selection changed.
	 */
	protected void initSelectionListener() {
		cloud.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				Word word = (Word) e.data;
				if (word == null)
					return;
				boolean remove = selection.remove(word);
				if (!remove)
					selection.add(word);
				cloud.setSelection(selection);
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		cloud.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Object> data = new ArrayList<>();
				@SuppressWarnings("unchecked")
				Set<Word> selected = (Set<Word>) e.data;
				for (Word word : selected) {
					if (word.data != null) {
						data.add(word.data);
					}
				}
				StructuredSelection selection = new StructuredSelection(data);
				fireSelectionChanged(new SelectionChangedEvent(TagCloudViewer.this, selection));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.Viewer#getControl()
	 */
	@Override
	public Control getControl() {
		return getCloud();
	}

	/**
	 * Returns the currently selected elements, as an
	 * {@link IStructuredSelection}. Returns an empty selection if no elements
	 * are selected.
	 */
	@Override
	public ISelection getSelection() {
		List<Object> elements = new ArrayList<>();
		for (Word word : selection) {
			elements.add(word.data);
		}
		return new StructuredSelection(elements);
	}

	@Override
	public void refresh() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.Viewer#setSelection(org.eclipse.jface.viewers.
	 * ISelection, boolean)
	 */
	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		this.selection.clear();
		IStructuredSelection sel = (IStructuredSelection) selection;
		Iterator<?> iterator = sel.iterator();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			Word word = objectMap.get(next);
			if (word != null) {
				this.selection.add(word);
			}
		}
		cloud.setSelection(this.selection);
	}

	/**
	 * Resets the {@link TagCloud}. If <code>recalc</code> is <code>true</code>,
	 * the displayed elements will be updated with the values provided by the used
	 * {@link ICloudLabelProvider}. Otherwise, the cloud will only be
	 * re-layouted, keeping fonts, colors and angles untouched.
	 * 
	 * @param monitor
	 * @param recalc
	 */
	public void reset(IProgressMonitor monitor, boolean recalc) {
		cloud.layoutCloud(monitor, recalc);
	}

	/**
	 * Returns the {@link TagCloud} managed by this viewer.
	 * 
	 * @return the {@link TagCloud} of this viewer
	 */
	public TagCloud getCloud() {
		return cloud;
	}

	/**
	 * Sets the label provider of this viewer, which must be an
	 * {@link ICloudLabelProvider}.
	 */
	@Override
	public void setLabelProvider(IBaseLabelProvider labelProvider) {
		super.setLabelProvider(labelProvider);
		Assert.isLegal(labelProvider instanceof ICloudLabelProvider);
	}

	/**
	 * Sets the content provider of this viewer, which must be an
	 * {@link IStructuredContentProvider}.
	 */
	@Override
	public void setContentProvider(IContentProvider contentProvider) {
		Assert.isLegal(contentProvider instanceof IStructuredContentProvider);
		super.setContentProvider(contentProvider);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.Viewer#inputChanged(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	protected void inputChanged(Object input, Object oldInput) {
		selection.clear();
		objectMap.clear();
		IStructuredContentProvider contentProvider = (IStructuredContentProvider) getContentProvider();
		Object[] elements = contentProvider.getElements(input);
		List<Word> words = new ArrayList<>();
		ICloudLabelProvider labelProvider = (ICloudLabelProvider) getLabelProvider();
		short i = 0;
		for (Object element : elements) {
			Word word = new Word(labelProvider.getLabel(element));
			word.setColor(labelProvider.getColor(element));
			word.weight = labelProvider.getWeight(element);
			word.setFontData(labelProvider.getFontData(element));
			word.angle = labelProvider.getAngle(element);
			word.data = element;
			Assert.isLegal(word.string != null, "Labelprovider must return a String for each element");
			Assert.isLegal(word.getColor() != null, "Labelprovider must return a Color for each element");
			Assert.isLegal(word.getFontData() != null, "Labelprovider must return a FontData for each element");
			Assert.isLegal(word.weight >= 0,
					"Labelprovider must return a weight between 0 and 1 (inclusive), but value was " + word.weight);
			Assert.isLegal(word.weight <= 1,
					"Labelprovider must return a weight between 0 and 1 (inclusive), but value was " + word.weight);
			Assert.isLegal(word.angle >= -90,
					"Angle of an element must be between -90 and +90 (inclusive), but was " + word.angle);
			Assert.isLegal(word.angle <= 90,
					"Angle of an element must be between -90 and +90 (inclusive), but was " + word.angle);
			words.add(word);
			i++;
			word.id = i;
			objectMap.put(element, word);
			if (i == maxWords)
				break;
		}
		selection.clear();
		if (monitor != null) {
			monitor.subTask("Layouting...");
		}
		cloud.setWords(words, monitor);
	}

	/**
	 * Sets the maximum number of elements which will be displayed by the cloud.
	 * Note that there is no guarantee that this amount of elements will
	 * actually be displayed, as this depends on additional factors.
	 */
	public void setMaxWords(int words) {
		this.maxWords = words;
	}

	/**
	 * Calls {@link TagCloud#zoomFit()} to scale the cloud such that it fits the
	 * current visible area.
	 */
	public void zoomFit() {
		cloud.zoomFit();
	}

	/**
	 * Zooms in
	 */
	public void zoomIn() {
		cloud.zoomIn();
	}

	/**
	 * Zooms out
	 */
	public void zoomOut() {
		cloud.zoomOut();
	}

	/**
	 * Resets the zoom to 100%
	 */
	public void zoomReset() {
		cloud.zoomReset();
	}

	public void setBoost(int boost) {
		cloud.setBoost(boost);
	}

	/**
	 * Returns the maximum number of elements which will be displayed by the
	 * cloud. Note that there is no guarantee that this amount of elements will
	 * actually be displayed, as this depends on additional factors.
	 * 
	 * @return the maximum number of words that can be placed
	 */
	public int getMaxWords() {
		return maxWords;
	}

	/**
	 * Same as {@link TagCloudViewer#setInput(Object)}, but with an
	 * {@link IProgressMonitor} to provide feedback during the layout phase.
	 * 
	 * @param input
	 * @param progressMonitor
	 */
	public void setInput(Object input, IProgressMonitor progressMonitor) {
		this.monitor = progressMonitor;
		super.setInput(input);
		this.monitor = null;
	}

	public void setBoostFactor(float boostFactor) {
		cloud.setBoostFactor(boostFactor);
	}

	public void setLayouter(ILayouter layouter) {
		cloud.setLayouter(layouter);
	}

	public ILayouter getLayouter() {
		return cloud.getLayouter();
	}

}
