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
package org.eclipse.gef.cloudio.tests.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.cloudio.internal.ui.TagCloud;
import org.eclipse.gef.cloudio.internal.ui.Word;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TagCloudTests {

	static final double DELTA = 1e-9;
	private Display display;
	private boolean createdDisplay = false;
	private Composite composite;

	@Before
	public void setUp() throws Exception {
		display = Display.getCurrent();
		if (display == null) {
			display = new Display();
			createdDisplay = true;
		}
		composite = new Shell(display);
		composite.setLayout(new FillLayout());
	}

	@After
	public void tearDown() throws Exception {
		composite.dispose();
		if (createdDisplay) {
			display.dispose();
		}
	}

	// Lifecycle:

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_NullParent() {
		new TagCloud(null, SWT.NONE);
	}

	@Test
	public void testConstructor_ValidParent() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		Assert.assertNotNull(cloud);
	}

	@Test
	public void testDispose() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.dispose();
		Assert.assertTrue(cloud.isDisposed());
	}

	// Background Color:

	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidBackgroundColor() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setBackground(null);
	}

	@Test
	public void testSetValidBackgroundColor() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		cloud.setBackground(color);
		Assert.assertEquals(color, cloud.getBackground());
	}

	@Test
	public void testDefaultBackgroundColor() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		Assert.assertNotNull(cloud.getBackground());
	}

	// Selection Color:

	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidSelectionColor() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setSelectionColor(null);
	}

	@Test
	public void testSetValidSelectionColor() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		cloud.setSelectionColor(color);
		Assert.assertEquals(color, cloud.getSelectionColor());
	}

	@Test
	public void testDefaultSelectionColor() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		Assert.assertNotNull(cloud.getSelectionColor());
	}

	// Font Size:

	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidMaxFontSize() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setMaxFontSize(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidMinFontSize() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setMinFontSize(0);
	}

	@Test
	public void testSetValidMaxFontSize() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		int size = cloud.getMaxFontSize() + 1;
		cloud.setMaxFontSize(size * 2);
		Assert.assertEquals(size * 2, cloud.getMaxFontSize());
	}

	@Test
	public void testSetValidMinFontSize() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		int size = cloud.getMinFontSize() + 1;
		cloud.setMinFontSize(size * 2);
		Assert.assertEquals(size * 2, cloud.getMinFontSize());
	}

	// Set Words:

	@Test(expected = IllegalArgumentException.class)
	public void testSetIllegalWords1() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setWords(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetIllegalWords2() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		words.add(null);
		cloud.setWords(words, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetIllegalWords3() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		Word w = new Word("Word");
		w.setFontData(composite.getFont().getFontData());
		w.weight = Math.random();
		words.add(w);
		cloud.setWords(words, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetIllegalWords4() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		Word w = new Word("Word");
		w.setColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		w.weight = Math.random();
		words.add(w);
		cloud.setWords(words, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetIllegalWords5() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		Word word = getWord();
		word.angle = -180;
		words.add(word);
		cloud.setWords(words, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetIllegalWords6() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		Word word = getWord();
		word.angle = 180;
		words.add(word);
		cloud.setWords(words, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetIllegalWords7() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		Word word = getWord();
		word.weight = -1;
		words.add(word);
		cloud.setWords(words, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetIllegalWords8() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		Word word = getWord();
		word.weight = 2;
		words.add(word);
		cloud.setWords(words, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetIllegalWords9() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		Word word = new Word(null);
		words.add(word);
		cloud.setWords(words, null);
	}

	private Word getWord() {
		Word w = new Word("Word");
		w.setColor(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		w.setFontData(composite.getFont().getFontData());
		w.weight = 1;
		return w;
	}

	@Test
	public void testSetEmptyWordList() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		int placed = cloud.setWords(words, null);
		Assert.assertEquals(0, placed);
	}

	@Test
	public void testSetWordList() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			words.add(getWord());
		}
		int placed = cloud.setWords(words, null);
		Assert.assertEquals(10, placed);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidOpacity1() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setOpacity(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidOpacity2() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setOpacity(256);
	}

	// Layouter

	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidLayouter() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setLayouter(null);
	}

	// Zoom

	@Test
	public void testZoomIn() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setWords(Arrays.asList(getWord()), null);
		double zoom = cloud.getZoom();
		cloud.zoomIn();
		Assert.assertTrue(cloud.getZoom() > zoom);
	}

	@Test
	public void testZoomReset() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setWords(Arrays.asList(getWord()), null);
		double zoom = cloud.getZoom();
		cloud.zoomReset();
		Assert.assertTrue(cloud.getZoom() > zoom);
		Assert.assertEquals(cloud.getZoom(), 1.0, DELTA);
	}

	@Test
	public void testZoomOut() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setWords(Arrays.asList(getWord()), null);
		cloud.zoomReset();
		double zoom = cloud.getZoom();
		cloud.zoomOut();
		Assert.assertTrue(cloud.getZoom() < zoom);
	}

	@Test
	public void testZoomFit() {
		TagCloud cloud = new TagCloud(composite, SWT.V_SCROLL | SWT.H_SCROLL);
		cloud.setWords(Arrays.asList(getWord()), null);
		cloud.zoomReset();
		double zoom = cloud.getZoom();
		cloud.zoomFit();
		Assert.assertTrue(cloud.getZoom() < zoom);
		// TODO: Test if the cloud really fits the area!
	}

	// Image:

	@Test
	public void testGetImageData() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		Assert.assertNotNull(cloud.getImageData());
	}

	// Test Selection

	@Test
	public void testInitialSelection() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		Set<Word> selection = cloud.getSelection();
		Assert.assertNotNull(selection);
		Assert.assertTrue(selection.isEmpty());
	}

	@Test
	public void testSetSelection() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		words.add(getWord());
		words.add(getWord());
		cloud.setWords(words, null);
		Set<Word> sel = new HashSet<>();
		sel.add(words.get(0));
		cloud.setSelection(sel);
		Set<Word> selection = cloud.getSelection();
		Assert.assertEquals(sel, selection);
		cloud.setSelection(new HashSet<Word>());
		selection = cloud.getSelection();
		Assert.assertTrue(selection.isEmpty());
	}

	@Test
	public void testSetNotExistingSelection1() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		words.add(getWord());
		words.add(getWord());
		cloud.setWords(words, null);
		Set<Word> sel = new HashSet<>();
		sel.add(getWord());
		cloud.setSelection(sel);
		Set<Word> selection = cloud.getSelection();
		Assert.assertTrue(selection.isEmpty());
	}

	@Test
	public void testSetNotExistingSelection2() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		Set<Word> sel = new HashSet<>();
		sel.add(getWord());
		cloud.setSelection(sel);
		Set<Word> selection = cloud.getSelection();
		Assert.assertTrue(selection.isEmpty());
	}

	// Boost

	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidBoost() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setBoost(-1);
	}

	@Test
	public void testSetValidBoost() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		Assert.assertEquals(0, cloud.getBoost());
		cloud.setBoost(3);
		Assert.assertEquals(3, cloud.getBoost());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidBoostFactor() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setBoostFactor(0);
	}

	@Test
	public void testSetValidBoostFactor() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		cloud.setBoostFactor(3.3F);
		Assert.assertEquals(3.3F, cloud.getBoostFactor(), DELTA);
		cloud.setBoostFactor(-2.2F);
		Assert.assertEquals(-2.2F, cloud.getBoostFactor(), DELTA);
	}

	@Test
	public void testLayout() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			words.add(getWord());
		}
		// Initial position must be 0/0
		for (Word word : words) {
			Assert.assertTrue(word.x == 0);
			Assert.assertTrue(word.y == 0);
		}
		cloud.setWords(words, null);
		List<Rectangle> rects = new ArrayList<>();
		// Elements must have been placed
		for (Word word : words) {
			Assert.assertTrue(word.x != 0);
			Assert.assertTrue(word.y != 0);
			word.angle = 45f;
			rects.add(new Rectangle(word.x, word.y, word.width, word.height));
		}
		cloud.layoutCloud(null, false);
		boolean posChanged = false;
		boolean rectChanged = false;
		for (int i = 0; i < words.size(); i++) {
			Word w = words.get(i);
			Rectangle r = rects.get(i);
			if (w.x != r.x || w.y != r.y) {
				posChanged = true;
			}
			if (w.width != r.width || w.height != r.height) {
				rectChanged = true;
			}
		}
		// Positions must have been changed
		Assert.assertTrue(posChanged);
		// Bounds must not have been changed
		Assert.assertFalse(rectChanged);
		cloud.layoutCloud(null, true);
		posChanged = false;
		rectChanged = false;
		for (int i = 0; i < words.size(); i++) {
			Word w = words.get(i);
			Rectangle r = rects.get(i);
			if (w.x != r.x || w.y != r.y) {
				posChanged = true;
			}
			if (w.width != r.width || w.height != r.height) {
				rectChanged = true;
			}
		}
		// Both positions an bounds must have changed
		Assert.assertTrue(posChanged);
		Assert.assertTrue(rectChanged);
	}

	// @Test
	// public void testLayoutTooLarge() {
	// TagCloud cloud = new TagCloud(composite, SWT.NONE);
	// List<Word> words = new ArrayList<Word>();
	// Word w = getWord();
	// words.add(w);
	// cloud.setMaxFontSize(5000);
	// int placed = cloud.setWords(words, null);
	// Assert.assertEquals(0, placed);
	// }

	class UniversalListener
			implements MouseListener, MouseTrackListener, MouseWheelListener, MouseMoveListener, SelectionListener {

		private int mouseUp;
		private int mouseDown;
		private int mouseDC;
		private int mouseMove;
		private int mouseScrolled;
		private Set<Word> selection;

		public void mouseUp(MouseEvent e) {
			mouseUp++;
		}

		public void mouseDown(MouseEvent e) {
			mouseDown++;
		}

		public void mouseDoubleClick(MouseEvent e) {
			mouseDC++;
		}

		public void mouseMove(MouseEvent e) {
			mouseMove++;
			System.out.println("MOVE");
		}

		public void mouseScrolled(MouseEvent e) {
			mouseScrolled++;
		}

		public void mouseEnter(MouseEvent e) {
		}

		public void mouseExit(MouseEvent e) {
		}

		public void mouseHover(MouseEvent e) {
		}

		@SuppressWarnings("unchecked")
		public void widgetSelected(SelectionEvent e) {
			this.selection = (Set<Word>) e.data;
		}

		public void widgetDefaultSelected(SelectionEvent e) {

		}

	}

	@Test
	public void testMouseListener() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		UniversalListener ml = new UniversalListener();
		List<Word> words = new ArrayList<>();
		Word word = getWord();
		words.add(word);
		cloud.setWords(words, null);
		Event e = new Event();
		cloud.addMouseListener(ml);
		cloud.notifyListeners(SWT.MouseUp, e);
		cloud.notifyListeners(SWT.MouseDoubleClick, e);
		cloud.notifyListeners(SWT.MouseDown, e);
		Assert.assertEquals(1, ml.mouseUp);
		Assert.assertEquals(1, ml.mouseDC);
		Assert.assertEquals(1, ml.mouseDown);
		cloud.removeMouseListener(ml);
		cloud.notifyListeners(SWT.MouseUp, e);
		cloud.notifyListeners(SWT.MouseDoubleClick, e);
		cloud.notifyListeners(SWT.MouseDown, e);
		Assert.assertEquals(1, ml.mouseUp);
		Assert.assertEquals(1, ml.mouseDC);
		Assert.assertEquals(1, ml.mouseDown);
	}

	@Test
	public void testMouseMoveListener() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		Word word = getWord();
		words.add(word);
		cloud.setWords(words, null);
		Event e = new Event();
		e.x = word.x;
		e.y = word.y;
		UniversalListener ml = new UniversalListener();
		cloud.addMouseMoveListener(ml);
		cloud.notifyListeners(SWT.MouseMove, e);
		Assert.assertEquals(1, ml.mouseMove);
		cloud.removeMouseMoveListener(ml);
		cloud.notifyListeners(SWT.MouseMove, e);
		Assert.assertEquals(1, ml.mouseMove);
	}

	// @Test
	// public void testMouseTrackListener() {
	// // TODO: Difficult to test... involves zoom, scrollbars...
	// }

	@Test
	public void testMouseWheelListener() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		UniversalListener ml = new UniversalListener();
		cloud.addMouseWheelListener(ml);
		cloud.notifyListeners(SWT.MouseWheel, new Event());
		Assert.assertEquals(1, ml.mouseScrolled);
		cloud.removeMouseWheelListener(ml);
		cloud.notifyListeners(SWT.MouseWheel, new Event());
		Assert.assertEquals(1, ml.mouseScrolled);
	}

	@Test
	public void testSelectionListener() {
		TagCloud cloud = new TagCloud(composite, SWT.NONE);
		List<Word> words = new ArrayList<>();
		Word word = getWord();
		words.add(word);
		cloud.setWords(words, null);
		UniversalListener sl = new UniversalListener();
		cloud.addSelectionListener(sl);
		cloud.setSelection(new HashSet<>(words));
		Assert.assertEquals(1, sl.selection.size());
		cloud.setSelection(new HashSet<Word>());
		Assert.assertEquals(0, sl.selection.size());
		cloud.removeSelectionListener(sl);
		cloud.setSelection(new HashSet<>(words));
		Assert.assertEquals(0, sl.selection.size());

	}

}
