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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.cloudio.internal.ui.layout.DefaultLayouter;
import org.eclipse.gef.cloudio.internal.ui.layout.ILayouter;
import org.eclipse.gef.cloudio.internal.ui.util.CloudMatrix;
import org.eclipse.gef.cloudio.internal.ui.util.RectTree;
import org.eclipse.gef.cloudio.internal.ui.util.SmallRect;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * 
 * @author sschwieb
 * 
 */
public class TagCloud extends Canvas {

	/**
	 * Minimum 'resolution' of the {@link RectTree} used for collision handling.
	 */
	private final int accuracy;

	/**
	 * Maximum size of the {@link RectTree} used for collision handling.
	 */
	private final int maxSize;

	/**
	 * Draw area.
	 */
	private final Rectangle cloudArea;

	/**
	 * Maximum Font Size.
	 */
	private int maxFontSize = 100;

	private final GC gc;

	/**
	 * Highlight color.
	 */
	private Color highlightColor;

	/**
	 * Used to detect mousehover, -enter and -exit.
	 */
	private Word currentWord;

	/**
	 * Opacity of the rendered strings.
	 */
	private int opacity = 255;
	/**
	 * Required for scroll bars
	 */
	private final Point origin = new Point(0, 0);

	/**
	 * Main image, on which all strings are rendered.
	 */
	private Image textLayerImage;

	/**
	 * Second level image: All elements plus selected elements in highlight
	 * color.
	 */

	private Image selectionLayerImage;

	/**
	 * Last level image: All + selected elements, zoomed. This is the image
	 * which will be displayed.
	 */
	private Image zoomLayerImage;

	/**
	 * The list of words to render.
	 */
	private List<Word> wordsToUse;

	private boolean initialized = false;

	/**
	 * Current zoom factor.
	 */
	private double currentZoom = 1;

	/**
	 * Minimum font size.
	 */
	private int minFontSize = 12;

	/**
	 * Set of selected words
	 */
	private Set<Word> selection = new HashSet<>();

	private CloudMatrix cloudMatrix;

	/**
	 * Executor service to process the creation of {@link RectTree} objects in
	 * parallel.
	 */
	private ExecutorService executors;

	private ILayouter layouter;

	/**
	 * The <code>boost</code> words with highest weight will be further
	 * increased in size. Eye-Candy only.
	 */
	private int boost;

	/**
	 * Offset of the region which surrounds the placed words, required to
	 * translate between mouse position and underlying words.
	 */
	private Point regionOffset;

	private int antialias = SWT.ON;

	private float boostFactor;

	private Listener hBarListener;

	private Listener resizeListener;

	private Listener paintListener;

	private Listener mouseTrackListener;

	private Listener mouseMoveListener;

	private Listener mouseUpListener;

	private Listener mouseDCListener;

	private Listener mouseDownListener;

	private Listener mouseWheelListener;

	private Listener vBarListener;

	private Set<EventListener> mouseWheelListeners = new HashSet<>();

	private Set<EventListener> mouseTrackListeners = new HashSet<>();

	private Set<EventListener> mouseMoveListeners = new HashSet<>();

	private Set<EventListener> mouseListeners = new HashSet<>();

	private Set<SelectionListener> selectionListeners = new HashSet<>();

	private ImageData mask;

	/**
	 * Creates a new Tag cloud on the given parent. When using this constructor,
	 * please read the following carefully: <br>
	 * Parameter <code>accuracy</code> defines the size of the raster used when
	 * placing strings, and must be a value greater than <code>0</code>. An
	 * accuracy of <code>1</code> will theoretically give best results, as the
	 * drawable area is analyzed most detailed, but this will also be very slow.
	 * <br>
	 * Parameter <code>maxSize</code> defines the maximum size of the drawable
	 * area and <strong>must</strong> be a power of <code>accuracy</code>,
	 * such that <code>accuracy^n=maxSize</code> holds. <br>
	 * To add scroll bars to the cloud, use {@link SWT#HORIZONTAL} and
	 * {@link SWT#VERTICAL}.
	 * 
	 * @param accuracy
	 * @param maxSize
	 * @param parent
	 * @param style
	 */
	public TagCloud(Composite parent, int style, int accuracy, int maxSize) {
		super(parent, style);
		Assert.isLegal(accuracy > 0, "Parameter accuracy must be greater than 0, but was " + accuracy);
		Assert.isLegal(maxSize > 0, "Parameter maxSize must be greater than 0, but was " + maxSize);
		int tmp = maxSize;
		while (tmp > accuracy) {
			tmp /= 2;
		}
		Assert.isLegal(tmp == accuracy, "Parameter maxSize must be a power of accuracy");
		this.accuracy = accuracy;
		this.maxSize = maxSize;
		cloudArea = new Rectangle(0, 0, maxSize, maxSize);
		highlightColor = new Color(getDisplay(), Display.getDefault().getSystemColor(SWT.COLOR_RED).getRGB());
		gc = new GC(this);
		layouter = new DefaultLayouter(accuracy, accuracy);
		setBackground(new Color(getDisplay(), Display.getDefault().getSystemColor(SWT.COLOR_WHITE).getRGB()));
		initListeners();
		textLayerImage = new Image(getDisplay(), 100, 100);
		zoomFit();
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				internalDispose();
			}
		});
	}

	/**
	 * Creates a new Tag cloud on the given parent. To add scroll bars to the
	 * cloud, use {@link SWT#HORIZONTAL} and {@link SWT#VERTICAL}. This is a
	 * shortcut to {@link #TagCloud(Composite, int, int, int)}, which sets the
	 * accuracy to <code>5</code> and the maximum size of the drawable area to
	 * <code>5120</code>.
	 * 
	 * @param parent
	 * @param style
	 */
	public TagCloud(Composite parent, int style) {
		this(parent, style, 5, 5120);
	}

	/**
	 * Disposes all system resources created in this class. Resources which were
	 * provided through a {@link ICloudLabelProvider} etc are not disposed.
	 */
	private void internalDispose() {
		removeListeners();
		textLayerImage.dispose();
		if (selectionLayerImage != null) {
			selectionLayerImage.dispose();
		}
		if (zoomLayerImage != null) {
			zoomLayerImage.dispose();
		}
		if (!this.isDisposed()) {
			gc.dispose();
		}
		super.dispose();
	}

	private void removeListeners() {
		if (isDisposed())
			return;
		removeListener(SWT.Paint, paintListener);
		if (hBarListener != null) {
			removeListener(SWT.H_SCROLL, hBarListener);
		}
		if (vBarListener != null) {
			removeListener(SWT.V_SCROLL, vBarListener);
		}
		removeListener(SWT.MouseDoubleClick, mouseDCListener);
		removeListener(SWT.MouseDown, mouseDownListener);
		removeListener(SWT.MouseMove, mouseTrackListener);
		removeListener(SWT.MouseUp, mouseUpListener);
		removeListener(SWT.Resize, resizeListener);
		removeListener(SWT.MouseMove, mouseMoveListener);
		removeListener(SWT.MouseWheel, mouseWheelListener);
	}

	/**
	 * Resets the zoom to 100 % (original size)
	 */
	public void zoomReset() {
		checkWidget();
		if (selectionLayerImage == null)
			return;
		zoomLayerImage = new Image(getDisplay(), selectionLayerImage.getBounds().width,
				selectionLayerImage.getBounds().height);
		GC gc = new GC(zoomLayerImage);
		gc.drawImage(selectionLayerImage, 0, 0);
		gc.dispose();
		currentZoom = 1;
		updateScrollbars();
		redraw();
	}

	public double getZoom() {
		checkWidget();
		return currentZoom;
	}

	/**
	 * Resets the zoom such that the generated cloud will fit exactly into the
	 * available space (unless the zoom factor is too small or too large).
	 */
	public void zoomFit() {
		checkWidget();
		if (selectionLayerImage == null)
			return;
		Rectangle imageBound = selectionLayerImage.getBounds();
		Rectangle destRect = getClientArea();
		double sx = (double) destRect.width / (double) imageBound.width;
		double sy = (double) destRect.height / (double) imageBound.height;
		currentZoom = Math.min(sx, sy);
		zoom(currentZoom);
	}

	private void zoom(double s) {
		checkWidget();
		if (selectionLayerImage == null)
			return;
		if (s < 0.1)
			s = 0.1;
		if (s > 3)
			s = 3;
		int width = (int) (selectionLayerImage.getBounds().width * s);
		int height = (int) (selectionLayerImage.getBounds().height * s);
		if (width == 0 || height == 0)
			return;
		zoomLayerImage = new Image(getDisplay(), width, height);
		Transform tf = new Transform(getDisplay());
		tf.scale((float) s, (float) s);
		GC gc = new GC(zoomLayerImage);
		gc.setTransform(tf);
		gc.drawImage(selectionLayerImage, 0, 0);
		gc.dispose();
		tf.dispose();
		currentZoom = s;
		updateScrollbars();
		redraw();
	}

	/**
	 * Zooms in, by the factor of 10 percent.
	 */
	public void zoomIn() {
		checkWidget();
		zoom(currentZoom * 1.1);
		redraw();
	}

	/**
	 * Zooms out, by the factor of 10 percent.
	 */
	public void zoomOut() {
		checkWidget();
		zoom(currentZoom * 0.9);
		redraw();
	}

	/**
	 * Returns the maximum cloud area.
	 * 
	 * @return the maximum cloud area
	 */
	protected Rectangle getCloudArea() {
		return cloudArea;
	}

	/**
	 * Returns the font size of the given word. By default, this is calculated
	 * as <code>8 + (word.weight * maxFontSize)</code>.
	 * 
	 * @param word
	 * @return
	 */
	private float getFontSize(Word word) {
		float size = (float) (word.weight * maxFontSize);
		size += minFontSize;
		return size;
	}

	/**
	 * Draws a word with the given color.
	 * 
	 * @param gc
	 * @param word
	 * @param color
	 */
	private void drawWord(final GC gc, final Word word, final Color color) {
		gc.setForeground(color);
		Font font = new Font(gc.getDevice(), word.getFontData());
		gc.setFont(font);
		gc.setAntialias(antialias);
		gc.setAlpha(opacity);
		Point stringExtent = word.stringExtent;
		gc.setForeground(color);
		int xOffset = word.x - regionOffset.x;
		int yOffset = word.y - regionOffset.y;
		double radian = Math.toRadians(word.angle);
		final double sin = Math.abs(Math.sin(radian));
		final double cos = Math.abs(Math.cos(radian));

		int y = (int) ((cos * stringExtent.y) + (sin * stringExtent.x));
		Transform t = new Transform(gc.getDevice());
		if (word.angle < 0) {
			t.translate(xOffset, yOffset + y - (int) (cos * stringExtent.y));
		} else {
			t.translate(xOffset + (int) (sin * stringExtent.y), yOffset);
		}
		t.rotate(word.angle);
		gc.setTransform(t);
		gc.drawString(word.string, 0, 0, true);
		gc.setTransform(null);
		t.dispose();
		font.dispose();
	}

	/**
	 * Calculates the bounds of each word, by determining the {@link Rectangle}
	 * a path would require to render an element.
	 * 
	 * @param monitor
	 */
	protected void calcExtents(IProgressMonitor monitor) {
		checkWidget();
		if (monitor != null) {
			monitor.subTask("Calculating word boundaries...");
		}
		if (wordsToUse == null)
			return;
		double step = 80D / wordsToUse.size();
		double current = 0;
		int next = 10;
		executors = Executors.newFixedThreadPool(getNumberOfThreads());
		final Color color = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
		for (final Word word : wordsToUse) {
			FontData[] fontData = word.getFontData();
			int fontSize = (int) getFontSize(word);
			for (FontData data : fontData) {
				data.setHeight((int) fontSize);
			}
			final Font font = new Font(gc.getDevice(), fontData);
			gc.setFont(font);
			final Point stringExtent = gc.stringExtent(word.string);
			FontMetrics fm = gc.getFontMetrics();
			stringExtent.y = fm.getHeight();
			executors.execute(new Runnable() {
				@Override
				public void run() {
					double radian = Math.toRadians(word.angle);
					final double sin = Math.abs(Math.sin(radian));
					final double cos = Math.abs(Math.cos(radian));
					final int x = (int) ((cos * stringExtent.x) + (sin * stringExtent.y));
					final int y = (int) ((cos * stringExtent.y) + (sin * stringExtent.x));
					ImageData id = createImageData(word, font, stringExtent, sin, cos, x, y, color);
					calcWordExtents(word, id);
					font.dispose();
				}
			});
			if (monitor != null) {
				current += step;
				if (current > next) {
					monitor.worked(5);
					next += 5;
				}
			}
		}
		executors.shutdown();
		try {
			executors.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Collections.sort(wordsToUse, new Comparator<Word>() {

			@Override
			public int compare(Word o1, Word o2) {
				return (o2.width * o2.height) - (o1.width * o1.height);
			}
		});
		short i = 1;
		for (Word word : wordsToUse) {
			word.id = i++;
		}
	}

	private ImageData createImageData(final Word word, Font font, Point stringExtent, final double sin,
			final double cos, int x, int y, Color color) {
		Image img = new Image(null, x, y);
		word.width = x;
		word.height = y;
		word.stringExtent = stringExtent;
		GC g = new GC(img);
		g.setAntialias(antialias);
		g.setForeground(color);
		Transform t = new Transform(img.getDevice());
		if (word.angle < 0) {
			t.translate(0, img.getBounds().height - (int) (cos * stringExtent.y));
		} else {
			t.translate((int) (sin * stringExtent.y), 0);
		}
		t.rotate(word.angle);
		g.setTransform(t);
		g.setFont(font);
		// Why is drawString so slow? between 30 and 90 percent of the whole
		// draw time...
		g.drawString(word.string, 0, 0, false);
		int max = Math.max(x, y);
		int tmp = maxSize;
		while (max < tmp) {
			tmp = tmp / 2;
		}
		tmp = tmp * 2;
		SmallRect root = new SmallRect(0, 0, tmp, tmp);
		word.tree = new RectTree(root, accuracy);
		final ImageData id = img.getImageData();
		g.dispose();
		img.dispose();
		return id;
	}

	/**
	 * Calculates the extents of a word, based on its rendered image.
	 */
	private void calcWordExtents(final Word word, final ImageData id) {
		final int[] pixels = new int[id.width];
		final PaletteData palette = id.palette;
		Set<SmallRect> inserted = new HashSet<>();
		for (int y = 0; y < id.height; y++) {
			id.getPixels(0, y, id.width, pixels, 0);
			for (int i = 0; i < pixels.length; i++) {
				int pixel = pixels[i];
				// Extracting color values as in PaletteData.getRGB(int pixel):
				int r = pixel & palette.redMask;
				r = (palette.redShift < 0) ? r >>> -palette.redShift : r << palette.redShift;
				int g = pixel & palette.greenMask;
				g = (palette.greenShift < 0) ? g >>> -palette.greenShift : g << palette.greenShift;
				int b = pixel & palette.blueMask;
				b = (palette.blueShift < 0) ? b >>> -palette.blueShift : b << palette.blueShift;
				if (r < 250 || g < 250 || b < 250) {
					SmallRect rect = new SmallRect((i / accuracy) * accuracy, (y / accuracy) * accuracy, accuracy,
							accuracy);
					if (!inserted.contains(rect)) {
						word.tree.insert(rect, word.id);
						inserted.add(rect);
					}
					i += accuracy - 1;
				}
			}
		}
		word.tree.releaseRects();
	}

	/**
	 * Generates the layout of the given words.
	 * 
	 * @param wordsToUse
	 * @param monitor
	 *            may be <code>null</code>.
	 * @return the number of words which could be placed
	 */
	protected int layoutWords(Collection<Word> wordsToUse, IProgressMonitor monitor) {
		checkWidget();
		if (monitor != null) {
			monitor.subTask("Placing words...");
		}
		Rectangle r = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
		final Rectangle cloudArea = getCloudArea();
		int w = cloudArea.width;
		int h = cloudArea.height;
		double current = 0;
		int next = 10;
		final Image tmpImage = new Image(getDisplay(), w, h);
		GC gc = new GC(tmpImage);
		gc.setBackground(getBackground());
		gc.setTextAntialias(SWT.ON);
		gc.setBackground(getBackground());
		gc.fillRectangle(tmpImage.getBounds());
		executors = Executors.newFixedThreadPool(1);
		int success = 0;
		if (wordsToUse != null) {
			double step = 100D / wordsToUse.size();
			final GC g = gc;
			for (Word word : wordsToUse) {
				Point point = layouter.getInitialOffset(word, cloudArea);
				boolean result = layouter.layout(point, word, cloudArea, cloudMatrix);
				if (!result) {
					System.err.println("Failed to place " + word.string);
					continue;
				}
				success++;
				if (word.x < r.x) {
					r.x = word.x;
				}
				if (word.y < r.y) {
					r.y = word.y;
				}
				if (word.x + word.width > r.width) {
					r.width = word.x + word.width;
				}
				if (word.y + word.height > r.height) {
					r.height = word.y + word.height;
				}
				final Word wrd = word;
				executors.execute(new Runnable() {

					@Override
					public void run() {
						drawWord(g, wrd, wrd.getColor());
					}
				});
				current += step;
				if (current > next) {
					next += 5;
					if (monitor != null) {
						monitor.worked(5);
					}
				}

			}
			executors.shutdown();
			try {
				executors.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// drawRects(gc);
		gc.dispose();
		if (success == 0)
			return success;
		if (textLayerImage != null) {
			textLayerImage.dispose();
		}
		textLayerImage = new Image(getDisplay(), r.width - r.x, r.height - r.y);
		gc = new GC(textLayerImage);
		gc.drawImage(tmpImage, r.x, r.y, r.width - r.x, r.height - r.y, 0, 0, textLayerImage.getBounds().width,
				textLayerImage.getBounds().height);
		this.regionOffset = new Point(r.x, r.y);
		tmpImage.dispose();
		gc.dispose();
		selectionLayerImage = new Image(getDisplay(), textLayerImage.getBounds());
		gc = new GC(selectionLayerImage);
		gc.drawImage(textLayerImage, 0, 0);
		gc.dispose();
		zoomFit();
		if (monitor != null) {
			monitor.worked(10);
		}
		return success;
	}

	/**
	 * Sets the given list as input of the tag cloud, replacing any previous
	 * content. By default, available word positions will be determined
	 * in-order, starting with the element at position 0.
	 * 
	 * @param values
	 * @param monitor
	 */
	public int setWords(List<Word> values, IProgressMonitor monitor) {
		checkWidget();
		Assert.isLegal(values != null, "List must not be null!");
		for (Word word : values) {
			Assert.isLegal(word != null, "Word must not be null!");
			Assert.isLegal(word.string != null, "Word must define a string!");
			Assert.isLegal(word.getColor() != null, "A word must define a color");
			Assert.isLegal(word.getFontData() != null, "A word must define a fontdata array");
			Assert.isLegal(word.weight >= 0,
					"Word weight must be between 0 and 1 (inclusive), but value was " + word.weight);
			Assert.isLegal(word.weight <= 1,
					"Word weight must be between 0 and 1 (inclusive), but value was " + word.weight);
			Assert.isLegal(word.angle >= -90, "Angle must be between -90 and +90 (inclusive), but was " + word.angle);
			Assert.isLegal(word.angle <= 90, "Angle must be between -90 and +90 (inclusive), but was " + word.angle);
		}
		this.wordsToUse = new ArrayList<>(values);
		if (boost > 0) {
			double factor = boostFactor;
			int i = boost;
			for (Word word : values) {
				if (factor <= 1) {
					break;
				}
				word.weight *= factor;
				factor -= 0.2;
				i--;
				if (i == 0)
					break;
			}
		}
		return layoutCloud(monitor, true);
	}

	/**
	 * Reset the initial matrix
	 */
	private void resetLayout() {
		if (cloudMatrix == null) {
			cloudMatrix = new CloudMatrix(maxSize, accuracy);
		} else {
			cloudMatrix.reset();
		}
		if (mask != null) {
			resetMask();
		}
	}

	/**
	 * Set a background mask to define the drawable area of the cloud. The image
	 * must be a square containing black and white pixels only. It is scaled to
	 * the full size of the drawable region. Black pixels are interpreted as
	 * used, such that strings will be drawn on white areas only. If parameter
	 * <code>bgData</code> is <code>null</code>, the old mask will be removed.
	 * 
	 * @param bgData
	 *            a square containing black and white pixels only
	 */
	public void setBackgroundMask(ImageData bgData) {
		if (mask != null) {
			mask = null;
		}
		if (bgData != null) {
			Image img = new Image(null, cloudArea.width, cloudArea.height);
			GC gc = new GC(img);
			Image tmp = new Image(null, bgData);
			gc.drawImage(tmp, 0, 0, tmp.getBounds().width, tmp.getBounds().height, 0, 0, cloudArea.width,
					cloudArea.height);
			ImageData id = img.getImageData();
			tmp.dispose();
			img.dispose();
			gc.dispose();
			mask = id;
		}
	}

	private void resetMask() {
		Word word = new Word("mask");
		word.tree = new RectTree(new SmallRect(0, 0, cloudArea.width, cloudArea.height), accuracy);
		calcWordExtents(word, mask);
		word.tree.place(cloudMatrix, RectTree.BACKGROUND);
	}

	private int getNumberOfThreads() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * Initialize internal listeners (scrollbar, mouse, paint...).
	 */
	private void initListeners() {
		if (initialized)
			return;
		initialized = true;
		final ScrollBar hBar = this.getHorizontalBar();
		if (hBar != null) {
			hBarListener = new Listener() {
				@Override
				public void handleEvent(Event e) {
					int hSelection = hBar.getSelection();
					int destX = -hSelection - origin.x;
					Rectangle rect = zoomLayerImage.getBounds();
					TagCloud.this.scroll(destX, 0, 0, 0, rect.width, rect.height, false);
					origin.x = -hSelection;
				}
			};
			hBar.addListener(SWT.Selection, hBarListener);
		}
		final ScrollBar vBar = this.getVerticalBar();
		if (vBar != null) {
			vBarListener = new Listener() {
				@Override
				public void handleEvent(Event e) {
					int vSelection = vBar.getSelection();
					int destY = -vSelection - origin.y;
					Rectangle rect = zoomLayerImage.getBounds();
					TagCloud.this.scroll(0, destY, 0, 0, rect.width, rect.height, false);
					origin.y = -vSelection;
				}
			};
			vBar.addListener(SWT.Selection, vBarListener);
		}
		resizeListener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				updateScrollbars();
				TagCloud.this.redraw();
			}
		};
		this.addListener(SWT.Resize, resizeListener);
		paintListener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				GC gc = e.gc;
				if (zoomLayerImage == null)
					return;
				Rectangle rect = zoomLayerImage.getBounds();
				Rectangle client = TagCloud.this.getClientArea();
				int marginWidth = client.width - rect.width;
				gc.setBackground(getBackground());
				if (marginWidth > 0) {
					gc.fillRectangle(rect.width, 0, marginWidth, client.height);
				}
				int marginHeight = client.height - rect.height;
				if (marginHeight > 0) {
					gc.fillRectangle(0, rect.height, client.width, marginHeight);
				}
				gc.drawImage(zoomLayerImage, origin.x, origin.y);
			}
		};
		this.addListener(SWT.Paint, paintListener);
		mouseTrackListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Word word = getWordAt(new Point(event.x, event.y));
				MouseEvent me = createMouseEvent(event, word);
				if (currentWord != null) {
					if (word == currentWord) {
						fireMouseEvent(me, SWT.MouseHover, mouseTrackListeners);
					} else {
						currentWord = null;
						fireMouseEvent(me, SWT.MouseExit, mouseTrackListeners);
					}
				}
				if (currentWord == null && word != null) {
					currentWord = word;
					fireMouseEvent(me, SWT.MouseEnter, mouseTrackListeners);
				}
			}
		};
		this.addListener(SWT.MouseMove, mouseTrackListener);
		mouseMoveListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Word word = getWordAt(new Point(event.x, event.y));
				MouseEvent me = createMouseEvent(event, word);
				fireMouseEvent(me, SWT.MouseMove, mouseMoveListeners);
			}
		};
		this.addListener(SWT.MouseMove, mouseMoveListener);
		mouseUpListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Word word = getWordAt(new Point(event.x, event.y));
				MouseEvent me = createMouseEvent(event, word);
				fireMouseEvent(me, SWT.MouseUp, mouseListeners);
			}
		};
		this.addListener(SWT.MouseUp, mouseUpListener);
		mouseDCListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Word word = getWordAt(new Point(event.x, event.y));
				MouseEvent me = createMouseEvent(event, word);
				fireMouseEvent(me, SWT.MouseDoubleClick, mouseListeners);
			}
		};
		this.addListener(SWT.MouseDoubleClick, mouseDCListener);
		mouseDownListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Word word = getWordAt(new Point(event.x, event.y));
				MouseEvent me = createMouseEvent(event, word);
				fireMouseEvent(me, SWT.MouseDown, mouseListeners);
			}
		};
		this.addListener(SWT.MouseDown, mouseDownListener);
		mouseWheelListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Word word = getWordAt(new Point(event.x, event.y));
				MouseEvent me = createMouseEvent(event, word);
				fireMouseEvent(me, SWT.MouseWheel, mouseWheelListeners);
			}
		};
		this.addListener(SWT.MouseWheel, mouseWheelListener);
	}

	/**
	 * Translates the given point in screen coordinates to the corresponding
	 * point in the (zoomed and scrolled) image and returns the {@link Word} at
	 * this position, or <code>null</code>, if no word exists at this position.
	 * 
	 * @param point
	 * @return
	 */
	private Word getWordAt(Point point) {
		if (cloudMatrix == null || regionOffset == null)
			return null;
		Point translatedMousePos = translateMousePos(point.x, point.y);
		translatedMousePos.x += regionOffset.x;
		translatedMousePos.y += regionOffset.y;
		int x = translatedMousePos.x / accuracy;
		int y = translatedMousePos.y / accuracy;
		if (x >= maxSize || y >= maxSize) {
			return null;
		}
		short wordId = cloudMatrix.get(x, y);
		if (wordId > 0) {
			Word clicked = wordsToUse.get(wordId - 1);
			return clicked;
		}
		return null;
	}

	/**
	 * Translates the current mouse position, such that it corresponds to scroll
	 * bars and zoom.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private Point translateMousePos(final int x, final int y) {
		final Point point = new Point(x - origin.x, y - origin.y);
		point.x /= currentZoom;
		point.y /= currentZoom;
		return point;
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		checkWidget();
		Assert.isLegal(listener != null);
		mouseListeners.add(listener);
	}

	@Override
	public void addMouseMoveListener(MouseMoveListener listener) {
		checkWidget();
		Assert.isLegal(listener != null);
		mouseMoveListeners.add(listener);
	}

	@Override
	public void addMouseTrackListener(MouseTrackListener listener) {
		checkWidget();
		Assert.isLegal(listener != null);
		mouseTrackListeners.add(listener);
	}

	@Override
	public void addMouseWheelListener(MouseWheelListener listener) {
		checkWidget();
		Assert.isLegal(listener != null);
		mouseWheelListeners.add(listener);
	}

	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		Assert.isLegal(listener != null);
		selectionListeners.add(listener);
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		checkWidget();
		mouseListeners.remove(listener);
	}

	@Override
	public void removeMouseMoveListener(MouseMoveListener listener) {
		checkWidget();
		mouseMoveListeners.remove(listener);
	}

	@Override
	public void removeMouseTrackListener(MouseTrackListener listener) {
		checkWidget();
		mouseTrackListeners.remove(listener);
	}

	@Override
	public void removeMouseWheelListener(MouseWheelListener listener) {
		checkWidget();
		mouseWheelListeners.remove(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		selectionListeners.remove(listener);
	}

	private MouseEvent createMouseEvent(Event event, Word word) {
		MouseEvent me = new MouseEvent(event);
		me.x = event.x - origin.x;
		me.y = event.y - origin.y;
		me.data = word;
		me.widget = TagCloud.this;
		me.display = Display.getCurrent();
		return me;
	}

	private void fireMouseEvent(MouseEvent me, int type, Set<EventListener> listeners) {
		for (EventListener listener : listeners) {
			if (listener instanceof MouseListener) {
				MouseListener ml = (MouseListener) listener;
				switch (type) {
				case SWT.MouseUp:
					ml.mouseUp(me);
					break;
				case SWT.MouseDoubleClick:
					ml.mouseDoubleClick(me);
					break;
				case SWT.MouseDown:
					ml.mouseDown(me);
					break;
				}
			}
			if (listener instanceof MouseTrackListener) {
				MouseTrackListener ml = (MouseTrackListener) listener;
				switch (type) {
				case SWT.MouseEnter:
					ml.mouseEnter(me);
					break;
				case SWT.MouseExit:
					ml.mouseExit(me);
					break;
				case SWT.MouseHover:
					ml.mouseHover(me);
					break;
				}
			}
			if (listener instanceof MouseMoveListener) {
				MouseMoveListener ml = (MouseMoveListener) listener;
				switch (type) {
				case SWT.MouseMove:
					ml.mouseMove(me);
					break;
				}
			}
			if (listener instanceof MouseWheelListener) {
				MouseWheelListener ml = (MouseWheelListener) listener;
				switch (type) {
				case SWT.MouseWheel:
					ml.mouseScrolled(me);
					break;
				}
			}
		}
	}

	/**
	 * Marks the set of elements as selected.
	 * 
	 * @param words
	 *            must not be <code>null</code>.
	 */
	public void setSelection(Set<Word> words) {
		checkWidget();
		Assert.isNotNull(words, "Selection must not be null!");
		if (wordsToUse == null)
			return;
		Set<Word> selection = new HashSet<>(words);
		selection.retainAll(wordsToUse);
		int w = textLayerImage.getBounds().width;
		int h = textLayerImage.getBounds().height;
		if (selectionLayerImage != null) {
			selectionLayerImage.dispose();
		}
		selectionLayerImage = new Image(getDisplay(), w, h);
		GC gc = new GC(selectionLayerImage);
		gc.drawImage(textLayerImage, 0, 0);
		for (Word word : selection) {
			drawWord(gc, word, highlightColor);
		}
		if (!selection.equals(this.selection)) {
			this.selection = selection;
			fireSelectionChanged();
		}
		gc.dispose();
		zoom(currentZoom);
		redraw();
	}

	private void fireSelectionChanged() {
		Event e = new Event();
		e.widget = this;
		final SelectionEvent event = new SelectionEvent(e);
		event.data = getSelection();
		event.widget = this;
		event.display = Display.getCurrent();
		for (SelectionListener listener : selectionListeners) {
			listener.widgetSelected(event);
		}
	}

	public void redrawTextLayerImage() {
		if (wordsToUse == null)
			return;
		GC gc = new GC(textLayerImage);
		gc.setBackground(getBackground());
		gc.fillRectangle(0, 0, textLayerImage.getBounds().width, textLayerImage.getBounds().height);
		for (Word word : wordsToUse) {
			drawWord(gc, word, word.getColor());
		}
		gc.dispose();
		setSelection(getSelection());
	}

	/**
	 * Returns the set of selected elements. Never returns <code>null</code>.
	 * 
	 * @return the set of selected words
	 */
	public Set<Word> getSelection() {
		checkWidget();
		Set<Word> copy = new HashSet<>(selection);
		return copy;
	}

	/**
	 * Sets the highlight color of the cloud. Default color is red.
	 * 
	 * @param color
	 */
	public void setSelectionColor(Color color) {
		checkWidget();
		Assert.isLegal(color != null, "Color must not be null!");
		this.highlightColor = color;
	}

	@Override
	public void setBackground(Color color) {
		checkWidget();
		Assert.isLegal(color != null, "Color must not be null!");
		super.setBackground(color);
	}

	/**
	 * Does a full relayout of all displayed elements.
	 * 
	 * @param monitor
	 * @return the number of words that could be placed
	 */
	public int layoutCloud(IProgressMonitor monitor, boolean recalc) {
		checkWidget();
		resetLayout();
		if (selectionLayerImage != null) {
			selectionLayerImage.dispose();
			selectionLayerImage = null;
		}
		regionOffset = new Point(0, 0);
		if (textLayerImage != null)
			textLayerImage.dispose();
		int placedWords = 0;
		try {
			if (recalc) {
				calcExtents(monitor);
			}
			placedWords = layoutWords(wordsToUse, monitor);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Exception while layouting data",
					"An exception occurred while layouting: " + e.getMessage());
			e.printStackTrace();
		}
		// zoomFit();
		redraw();
		updateScrollbars();
		return placedWords;
	}

	private void updateScrollbars() {
		if (zoomLayerImage == null) {
			return;
		}
		Rectangle rect = zoomLayerImage.getBounds();
		Rectangle client = getClientArea();
		ScrollBar hBar = getHorizontalBar();
		ScrollBar vBar = getVerticalBar();
		if (hBar != null) {
			hBar.setMaximum(rect.width);
			hBar.setThumb(Math.min(rect.width, client.width));
			int hPage = rect.width - client.width;
			int hSelection = hBar.getSelection();
			if (hSelection >= hPage) {
				if (hPage <= 0)
					hSelection = 0;
				origin.x = -hSelection;
			}
		}
		if (vBar != null) {
			vBar.setMaximum(rect.height);
			vBar.setThumb(Math.min(rect.height, client.height));
			int vPage = rect.height - client.height;
			int vSelection = vBar.getSelection();
			if (vSelection >= vPage) {
				if (vPage <= 0)
					vSelection = 0;
				origin.y = -vSelection;
			}
		}
	}

	/**
	 * Sets the maximum font size (which must be a value greater 0). Note that
	 * strings which are too large to fit into the cloud region will be skipped.
	 * By default, this value is 500.
	 * 
	 * @param maxSize
	 */
	public void setMaxFontSize(int maxSize) {
		checkWidget();
		Assert.isLegal(maxSize > 0, "Font Size must be greater than zero, but was " + maxSize + "!");
		maxFontSize = maxSize;
	}

	/**
	 * Sets the opacity of the words, which must be a value between 0 and 255
	 * (inclusive). Currently not very useful...
	 * 
	 * @param opacity
	 */
	public void setOpacity(int opacity) {
		checkWidget();
		Assert.isLegal(opacity > 0, "Opacity must be greater than zero: " + opacity);
		Assert.isLegal(opacity < 256, "Opacity must be less than 256: " + opacity);
		this.opacity = opacity;
	}

	/**
	 * Sets the minimum font size. Should be a reasonable value &gt; 0 (twice of
	 * {@link TagCloud#accuracy} is recommended). By default, this value is 12.
	 * 
	 * @param size
	 */
	public void setMinFontSize(int size) {
		checkWidget();
		Assert.isLegal(size > 0, "Font Size must be greater zero: " + size);
		this.minFontSize = size;
	}

	/**
	 * Returns the {@link ImageData} of the text layer image (all rendered
	 * elements, unscaled, without highlighted selection). Can be used to print
	 * or export the cloud.
	 * 
	 * @return the image data of the text layer image
	 */
	public ImageData getImageData() {
		checkWidget();
		if (textLayerImage == null)
			return null;
		return textLayerImage.getImageData();
	}

	/**
	 * Enable boosting for the first <code>boost</code> elements. By default, no
	 * elements are boosted.
	 * 
	 * @param boost
	 */
	public void setBoost(int boost) {
		checkWidget();
		Assert.isLegal(boost >= 0, "Boost cannot be negative");
		this.boost = boost;
	}

	/**
	 * Enable or disable antialiasing. Enabled by default.
	 * 
	 * @param enabled
	 */
	public void setAntiAlias(boolean enabled) {
		checkWidget();
		if (enabled) {
			antialias = SWT.ON;
		} else {
			antialias = SWT.OFF;
		}
	}

	// /**
	// * Work in progress - still broken positioning
	// * @param w
	// * @throws IOException
	// */
	// public void toSVG(Writer w) throws IOException {
	// int counter = 1;
	// w.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
	// +
	// "<!-- Created with Eclipse Tag Cloud -->\n" +
	// "<svg\n" +
	// "xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
	// "xmlns:cc=\"http://creativecommons.org/ns#\"\n" +
	// "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
	// "xmlns:svg=\"http://www.w3.org/2000/svg\"\n" +
	// "xmlns=\"http://www.w3.org/2000/svg\"\n" +
	// "version=\"1.1\"\n" +
	// "width=\"" + textLayerImage.getBounds().width + "\"\n" +
	// "height=\"" + textLayerImage.getBounds().height + "\"\n" +
	// "id=\"svg2\">\n" +
	// "<defs\n" +
	// "id=\"defs4\" />\n" +
	// "<metadata\n" +
	// "id=\"metadata7\">\n" +
	// "<rdf:RDF>\n" +
	// "<cc:Work\n" +
	// "rdf:about=\"\">\n" +
	// "<dc:format>image/svg+xml</dc:format>\n" +
	// "<dc:type\n" +
	// "rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" />\n" +
	// "<dc:title></dc:title>\n" +
	// "</cc:Work>\n" +
	// "</rdf:RDF>\n" +
	// "</metadata>\n" +
	// "<g\n" +
	// "id=\"layer1\">\n");
	// GC tmp = new GC(Display.getDefault());
	// String bg = Integer.toHexString(getBackground().getRed()) +
	// Integer.toHexString(getBackground().getGreen()) +
	// Integer.toHexString(getBackground().getBlue());
	// w.append("<rect x=\"" + 0 + "\" y=\"" + 0 + "\" width=\"" +
	// textLayerImage.getBounds().width + "\" height=\"" +
	// textLayerImage.getBounds().height + "\" style=\"fill:" + bg +
	// ";stroke:#006600;\"/>");
	// for (Word word : wordsToUse) {
	// String id = "text" + counter++;
	// FontData fd = word.fontData[0];
	// String style = "font-size:" + fd.getHeight()+"px;" +
	// "font-family:" + fd.getName() + ";";
	// String text = word.string;
	// int x = 0;
	// int y = 0;
	// double radian = Math.toRadians(word.angle);
	// final double sin = Math.abs(Math.sin(radian));
	// final double cos = Math.abs(Math.cos(radian));
	// float fontSize = getFontSize(word);
	// Font font = new Font(tmp.getDevice(), word.fontData);
	// Path p = new Path(tmp.getDevice());
	// p.addString(word.string, 0, 0, font);
	// float[] bounds = new float[4];
	// p.getBounds(bounds);
	// p.dispose();
	// gc.setFont(font);
	// //Point stringExtent = gc.stringExtent(word.string);
	// font.dispose();
	// if(word.angle < 0) {
	// y = word.height - (int) ( cos * fontSize);
	// } else {
	// x = (int) (sin * fontSize);
	// }
	// x += word.x - regionOffset.x;
	// y += word.y - regionOffset.y;
	//
	// // w.append("<rect x=\"" + 0 + "\" y=\"" + 0 + "\" width=\"" +
	// stringExtent.x + "\" height=\"" + stringExtent.y +
	// "\" style=\"fill:none;stroke:#006600;\"" +
	// // " transform=\"translate(" + x + "," + y + ") rotate(" + word.angle +
	// ")\"/>");
	//
	// int xOff = (int) (-bounds[0] + bounds[2]/2);
	// int yOff = (int)(bounds[3] - bounds[1]);
	// String color = Integer.toHexString(word.color.getRed()) +
	// Integer.toHexString(word.color.getGreen()) +
	// Integer.toHexString(word.color.getBlue());
	// String fullString = "\n<text "
	// + "x=\"" + xOff + "\"\n"
	// + "y=\"" + yOff + "\"\n"
	// + "text-anchor=\"middle\"\n"
	// + "transform = \"translate(" + x + "," + y + ") rotate(" +
	// word.angle+")\"\n"
	// + "id=\"" + id + "\"\n"
	// + "xml:space=\"preserve\"\n"
	// + "style=\"font-size:40px;fill:#" + color +
	// ";fill-opacity:1;stroke:none;font-family:Sans\">\n"
	// + "<tspan "
	// + "style=\""+style+"\">"
	// + text + "</tspan>\n"
	// +"</text>\n";
	//
	// w.append(fullString);
	// }
	// tmp.dispose();
	// w.append("</g>\n</svg>\n");
	// }

	public void setBoostFactor(float boostFactor) {
		Assert.isLegal(boostFactor != 0);
		this.boostFactor = boostFactor;
	}

	public Color getSelectionColor() {
		return highlightColor;
	}

	public void setLayouter(ILayouter layouter) {
		checkWidget();
		Assert.isLegal(layouter != null, "Layouter must not be null!");
		this.layouter = layouter;
	}

	public int getMaxFontSize() {
		checkWidget();
		return maxFontSize;
	}

	public int getMinFontSize() {
		checkWidget();
		return minFontSize;
	}

	public int getBoost() {
		checkWidget();
		return boost;
	}

	public float getBoostFactor() {
		checkWidget();
		return boostFactor;
	}

	public List<Word> getWords() {
		return wordsToUse;
	}

	public ILayouter getLayouter() {
		return layouter;
	}

}
