/*******************************************************************************
 * Copyright (c) 2012, 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.graphics.swt;

import org.eclipse.gef4.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.graphics.AbstractGraphics;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.IImageGraphics;
import org.eclipse.gef4.graphics.InterpolationHint;
import org.eclipse.gef4.graphics.LineCap;
import org.eclipse.gef4.graphics.LineJoin;
import org.eclipse.gef4.graphics.Pattern;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.font.Font;
import org.eclipse.gef4.graphics.image.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.Transform;

public class SwtGraphics extends AbstractGraphics {

	private class GcState {
		private boolean advanced;
		private int alpha;
		private int antialias;
		private org.eclipse.swt.graphics.Color background;
		private org.eclipse.swt.graphics.Pattern backgroundPattern;
		private Region region; // TODO: dispose later
		private boolean isDisposed;
		private int fillRule;
		private org.eclipse.swt.graphics.Font font;
		private org.eclipse.swt.graphics.Color foreground;
		private org.eclipse.swt.graphics.Pattern foregroundPattern;
		private int interpolation;
		private LineAttributes lineAttributes;
		private int textAntialias;
		private Transform transform; // TODO: dispose later
		private boolean xorMode;

		public GcState(GC gc) {
			advanced = gc.getAdvanced();
			alpha = gc.getAlpha();
			antialias = gc.getAntialias();
			background = gc.getBackground();
			backgroundPattern = gc.getBackgroundPattern();
			region = new Region(gc.getDevice());
			gc.getClipping(region);
			fillRule = gc.getFillRule();
			font = gc.getFont();
			foreground = gc.getForeground();
			foregroundPattern = gc.getForegroundPattern();
			interpolation = gc.getInterpolation();
			lineAttributes = gc.getLineAttributes();
			textAntialias = gc.getTextAntialias();
			transform = new Transform(gc.getDevice());
			gc.getTransform(transform);
			xorMode = gc.getXORMode();
		}

		@SuppressWarnings("deprecation")
		public void apply(GC gc) {
			if (isDisposed) {
				return;
			}
			gc.setAdvanced(advanced);
			gc.setAlpha(alpha);
			gc.setAntialias(antialias);
			gc.setBackground(background);
			gc.setBackgroundPattern(backgroundPattern);
			gc.setClipping(region);
			// TODO: How to dispose the Region? region.dispose(); is not allowed
			// while the Region is set in the GC.
			isDisposed = true;
			gc.setFillRule(fillRule);
			gc.setFont(font);
			gc.setForeground(foreground);
			gc.setForegroundPattern(foregroundPattern);
			gc.setInterpolation(interpolation);
			gc.setLineAttributes(lineAttributes);
			gc.setTextAntialias(textAntialias);
			gc.setTransform(transform);
			// TODO: How to dispose the Transform? transform.dispose(); is not
			// allowed while the Transform is set in the GC.
			gc.setXORMode(xorMode);
		}
	}

	private GC gc;
	private GcState gcState;

	private org.eclipse.swt.graphics.Image xmDstImg;
	private org.eclipse.swt.graphics.Image xmSrcImg;
	private GC xmGc;
	private Rectangle xmBounds;

	public SwtGraphics(GC gc) {
		this.gc = gc;
		gcState = new GcState(this.gc);
		this.gc.setAdvanced(true);
		initialize();
	}

	@Override
	public void cleanUp() {
		gcState.apply(gc);
	}

	private Image createGradientImage(Rectangle bounds, Gradient<?> gradient) {
		Image image = new Image((int) bounds.getWidth() + 1,
				(int) bounds.getHeight() + 1, new Color(0, 0, 0, 0));

		Point p = new Point();
		for (int x = 0; x < image.getWidth(); x++) {
			p.x = x;
			for (int y = 0; y < image.getHeight(); y++) {
				p.y = y;
				Color colorAt = gradient.getColorAt(p);
				image.setPixel(x, y, colorAt);
			}
		}

		return image;
	}

	@Override
	public IImageGraphics createImageGraphics(
			org.eclipse.gef4.graphics.image.Image image) {
		return new SwtImageGraphics(image);
	}

	@Override
	public IGraphics draw(ICurve curve) {
		if (curve instanceof Line) {
			prepareXorMode(curve);
			validateDraw(curve.getBounds());
			Line line = (Line) curve;
			gc.drawLine((int) line.getX1(), (int) line.getY1(),
					(int) line.getX2(), (int) line.getY2());
		} else if (curve instanceof Polyline) {
			prepareXorMode(curve);
			validateDraw(curve.getBounds());
			gc.drawPolyline(Geometry2SWT.toSWTPointArray((Polyline) curve));
		} else {
			return draw(curve.toPath());
		}
		finishXorMode();
		return this;
	}

	@Override
	public IGraphics draw(Path path) {
		prepareXorMode(path);
		validateDraw(path.getBounds());
		validateWindingRule(path);
		gc.drawPath(SwtGraphicsUtils.createSwtPath(path, gc.getDevice()));
		finishXorMode();
		return this;
	}

	@Override
	public IGraphics draw(Point p) {
		return draw(new Line(p, p));
	}

	@Override
	public IGraphics fill(IMultiShape multiShape) {
		return fill(multiShape.toPath());
	}

	@Override
	public IGraphics fill(IShape shape) {
		if (shape instanceof Rectangle) {
			prepareXorMode(shape);
			Rectangle r = (Rectangle) shape;
			validateFill(r);
			gc.fillRectangle((int) r.getX(), (int) r.getY(),
					(int) r.getWidth(), (int) r.getHeight());
		} else if (shape instanceof Ellipse) {
			prepareXorMode(shape);
			Ellipse e = (Ellipse) shape;
			validateFill(e.getBounds());
			gc.fillOval((int) e.getX(), (int) e.getY(), (int) e.getWidth(),
					(int) e.getHeight());
		} else if (shape instanceof Pie) {
			prepareXorMode(shape);
			Pie p = (Pie) shape;
			validateFill(p.getBounds());
			gc.fillArc((int) (p.getX() + 0.5), (int) (p.getY() + 0.5), (int) (p
					.getWidth() + 1), (int) (p.getHeight() + 1), (int) (p
					.getStartAngle().deg() + 0.5), (int) (p.getAngularExtent()
					.deg() + 0.5));
		} else if (shape instanceof RoundedRectangle) {
			prepareXorMode(shape);
			RoundedRectangle r = (RoundedRectangle) shape;
			validateFill(r.getBounds());
			gc.fillRoundRectangle((int) (r.getX() + 0.5),
					(int) (r.getY() + 0.5), (int) (r.getWidth() + 1),
					(int) (r.getHeight() + 1), (int) r.getArcWidth(),
					(int) r.getArcHeight());
		} else if (shape instanceof Polygon) {
			prepareXorMode(shape);
			Polygon p = (Polygon) shape;
			validateFill(p.getBounds());
			Point[] points = p.getPoints();
			int[] swtPointArray = new int[points.length + points.length];
			for (int i = 0; i < points.length; i++) {
				swtPointArray[2 * i] = (int) (points[i].x + 0.5);
				swtPointArray[2 * i + 1] = (int) (points[i].y + 0.5);
			}
			gc.fillPolygon(swtPointArray);
		} else {
			return fill(shape.toPath());
		}
		finishXorMode();
		return this;
	}

	@Override
	public IGraphics fill(Path path) {
		prepareXorMode(path);
		validateFill(path.getBounds());
		validateWindingRule(path);
		org.eclipse.swt.graphics.Path swtPath = SwtGraphicsUtils.createSwtPath(
				path, gc.getDevice());
		gc.fillPath(swtPath);
		swtPath.dispose();
		finishXorMode();
		return this;
	}

	@SuppressWarnings("deprecation")
	private void finishXorMode() {
		if (!isXorMode()) {
			return;
		}

		// in case no emulation is used, we have to leave the GC in a clean
		// state
		if (!SWT.getPlatform().equals("gtk")) {
			gc.setXORMode(false);
			return;
		}

		gc.dispose();
		gc = xmGc;

		ImageData dst = xmDstImg.getImageData();
		ImageData src = xmSrcImg.getImageData();
		assert dst.width == src.width;
		assert dst.height == src.height;

		int w = (int) xmBounds.getWidth();
		int h = (int) xmBounds.getHeight();
		int x0 = (int) xmBounds.getX();
		int y0 = (int) xmBounds.getY();
		assert w <= dst.width;
		assert h <= dst.height;
		assert x0 >= 0;
		assert y0 >= 0;

		ImageData res = new ImageData(w, h, dst.depth, dst.palette);

		int[] srcPixels = new int[w];
		int[] dstPixels = new int[w];

		for (int y = 0; y < h; y++) {
			dst.getPixels(x0, y0 + y, w, dstPixels, 0);
			src.getPixels(x0, y0 + y, w, srcPixels, 0);

			for (int x = 0; x < w; x++) {
				dstPixels[x] ^= srcPixels[x];
			}

			res.setPixels(0, y, w, dstPixels, 0);
		}

		// paint result at tight bounds
		Transform t = new Transform(gc.getDevice(), 1, 0, 0, 1, x0, y0);
		gc.setTransform(t);
		org.eclipse.swt.graphics.Image resImg = new org.eclipse.swt.graphics.Image(
				gc.getDevice(), res);
		gc.drawImage(resImg, 0, 0);
		t.dispose();

		// dispose off images, maybe: re-use them as layers (multi buffers)
		xmDstImg.dispose();
		xmSrcImg.dispose();
		resImg.dispose();

		// recover transformations
		validateGlobals();
	}

	@Override
	public int getDefaultDeviceDpi() {
		Device device = gc.getDevice();
		if (device == null) {
			throw new IllegalStateException("Device not set.");
		}
		org.eclipse.swt.graphics.Point dpi = device.getDPI();
		if (dpi == null) {
			throw new IllegalStateException("DPI not set.");
		}
		return dpi.x;
	}

	/**
	 * Returns the {@link GC} associated with this {@link SwtGraphics}.
	 * 
	 * @return the {@link GC} associated with this {@link SwtGraphics}
	 */
	public GC getGC() {
		return gc;
	}

	@Override
	public Dimension getTextDimension(String text) {
		validateFont();
		org.eclipse.swt.graphics.Point textExtent = gc.stringExtent(text);
		return new Dimension(textExtent.x, textExtent.y);
	}

	@Override
	public IGraphics paint(org.eclipse.gef4.graphics.image.Image image) {
		prepareXorMode(image.getBounds());
		validateBlit();
		gc.drawImage(SwtGraphicsUtils.createSwtImage(image), 0, 0);
		finishXorMode();
		return this;
	}

	@SuppressWarnings("deprecation")
	private void prepareXorMode(IGeometry g) {
		if (!isXorMode()) {
			return;
		}

		// in case the underlying ws supports xor mode, we do not have to
		// emulate
		if (!isEmulateXorMode() && !SWT.getPlatform().equals("gtk")) {
			gc.setXORMode(true);
			return;
		}

		// get current transformations
		double scaleFactor = computeResolutionScaleFactor();
		AffineTransform at = getAffineTransform().scale(scaleFactor,
				scaleFactor);

		// compute correct bounds
		g = g.getTransformed(at);
		xmBounds = g.getBounds();
		xmBounds.expand(getCurrentState().getLineWidth(), getCurrentState()
				.getLineWidth());

		// Ensure bounds' location is >= (0, 0) because we cannot draw outside
		// the canvas.
		if (xmBounds.getX() < 0) {
			xmBounds.setX(0);
		}
		if (xmBounds.getY() < 0) {
			xmBounds.setY(0);
		}

		// Ensure bounds' dimension is >= (1, 1) because we cannot create an
		// Image otherwise.
		if (xmBounds.getWidth() < 1) {
			xmBounds.setWidth(1);
		}
		if (xmBounds.getHeight() < 1) {
			xmBounds.setHeight(1);
		}

		Rectangle bigBounds = xmBounds.getUnioned(new Point());
		org.eclipse.swt.graphics.Rectangle swtBigBounds = Geometry2SWT
				.toSWTRectangle(bigBounds);

		// copy destination image
		xmDstImg = new org.eclipse.swt.graphics.Image(gc.getDevice(),
				swtBigBounds);
		gc.copyArea(xmDstImg, 0, 0);

		// create transparent source image
		ImageData src = new ImageData(swtBigBounds.width, swtBigBounds.height,
				gc.getDevice().getDepth(), new PaletteData(0xff, 0xff00,
						0xff0000));

		xmSrcImg = new org.eclipse.swt.graphics.Image(gc.getDevice(), src);

		// swap GC with image-GC
		xmGc = gc;
		gc = new GC(xmSrcImg);
	}

	private void setImagePattern(boolean background, Image image) {
		org.eclipse.swt.graphics.Pattern patternSwt = new org.eclipse.swt.graphics.Pattern(
				gc.getDevice(), SwtGraphicsUtils.createSwtImage(image));

		if (background) {
			/*
			 * Workaround for bug #399109: Invalidate the GC's state by feeding
			 * it a null pattern before setting the correct pattern.
			 */
			gc.setBackgroundPattern(null);
			gc.setBackgroundPattern(patternSwt);
		} else {
			gc.setForegroundPattern(null);
			gc.setForegroundPattern(patternSwt);
		}
	}

	private void validateAffineTransform() {
		AffineTransform tx = getAffineTransform();
		double scale = computeResolutionScaleFactor();
		tx.scale(scale, scale);
		double[] matrix = tx.getMatrix();
		float[] matrixSwt = new float[6];
		for (int i = 0; i < 6; i++) {
			matrixSwt[i] = (float) matrix[i];
		}
		Transform transformSwt = new Transform(gc.getDevice(), matrixSwt);
		gc.setTransform(transformSwt);
		// TODO: How to dispose the Transform? transformSwt.dispose(); is not
		// allowed while it is set in the GC.
	}

	private void validateBlit() {
		validateGlobals();
		validateInterpolationHint();
	}

	private void validateClippingArea() {
		Path clip = getCurrentState().getClippingAreaByReference();
		if (clip == null) {
			gc.setClipping((org.eclipse.swt.graphics.Rectangle) null);
		} else {
			org.eclipse.swt.graphics.Path clipSwt = SwtGraphicsUtils
					.createSwtPath(clip, gc.getDevice());
			// TODO: What about the winding rule?
			gc.setClipping(clipSwt);
			// TODO: How to dispose the Path? clipSwt.dispose(); is not allowed
			// while it is set in the GC.
		}
	}

	private void validateDraw(Rectangle bounds) {
		validateGlobals();
		validateStroke();
		validatePattern(bounds, getCurrentState().getDrawPatternByReference(),
				false);
	}

	private void validateFill(Rectangle bounds) {
		validateGlobals();
		validatePattern(bounds, getCurrentState().getFillPatternByReference(),
				true);
	}

	private void validateFont() {
		Font font = getCurrentState().getFontByReference();
		org.eclipse.swt.graphics.Font swtFont = SwtGraphicsUtils
				.createSwtFont(font);
		gc.setFont(swtFont);
	}

	private void validateGlobals() {
		validateAffineTransform();
		validateClippingArea();

		int antialias = getCurrentState().isAntiAliasing() ? SWT.ON : SWT.OFF;
		gc.setAntialias(antialias);
		gc.setTextAntialias(antialias);
	}

	private void validateInterpolationHint() {
		InterpolationHint interpolationHint = getCurrentState()
				.getInterpolationHint();
		gc.setInterpolation(interpolationHint == InterpolationHint.QUALITY ? SWT.HIGH
				: SWT.LOW);
	}

	private void validatePattern(Rectangle bounds, Pattern p, boolean background) {
		switch (p.getMode()) {
		case COLOR:
			Color color = p.getColor();
			gc.setAlpha(color.getAlpha());

			org.eclipse.swt.graphics.Color colorSwt = SwtGraphicsUtils
					.createSwtColor(color);
			if (background) {
				gc.setBackground(colorSwt);
			} else {
				gc.setForeground(colorSwt);
			}
			// TODO: How to dispose the Color? colorSwt.dispose(); is not
			// allowed while it is set in the GC.
			break;
		case GRADIENT:
			Gradient<?> gradient = p.getGradient();
			if (gradient instanceof Gradient.Linear) {
				Gradient.Linear linearGradient = (Gradient.Linear) gradient;
				setImagePattern(background,
						createGradientImage(bounds, linearGradient));
			} else if (gradient instanceof Gradient.Radial) {
				Gradient.Radial radialGradient = (Gradient.Radial) gradient;
				setImagePattern(background,
						createGradientImage(bounds, radialGradient));
			}
			break;
		case IMAGE:
			validateInterpolationHint();
			// TODO: How to dispose the Pattern?
			setImagePattern(background, p.getImage());
			break;
		}
	}

	private void validateStroke() {
		LineCap cap = getCurrentState().getLineCap();
		int swtCap;
		if (cap == LineCap.FLAT) {
			swtCap = SWT.CAP_FLAT;
		} else if (cap == LineCap.ROUND) {
			swtCap = SWT.CAP_ROUND;
		} else if (cap == LineCap.SQUARE) {
			swtCap = SWT.CAP_SQUARE;
		} else {
			throw new IllegalStateException(
					"The SwtGraphics does not allow LineCap values other than FLAT, ROUND, or SQUARE. Current LineCap = "
							+ cap);
		}

		LineJoin join = getCurrentState().getLineJoin();
		int swtJoin;
		if (join == LineJoin.BEVEL) {
			swtJoin = SWT.JOIN_BEVEL;
		} else if (join == LineJoin.ROUND) {
			swtJoin = SWT.JOIN_ROUND;
		} else if (join == LineJoin.MITER) {
			swtJoin = SWT.JOIN_MITER;
		} else {
			throw new IllegalStateException(
					"The SwtGraphics does not allow LineJoin values other than BEVEL, ROUND, or SQUARE. Current LineCap = "
							+ cap);
		}

		double[] dashes = getCurrentState().getDashArrayByReference();
		float[] swtDashes = new float[dashes.length];
		for (int i = 0; i < dashes.length; i++) {
			swtDashes[i] = (float) dashes[i];
		}

		gc.setLineAttributes(new LineAttributes((float) getCurrentState()
				.getLineWidth(), swtCap, swtJoin, SWT.LINE_CUSTOM, swtDashes,
				(float) getCurrentState().getDashBegin(),
				(float) getCurrentState().getMiterLimit()));
	}

	/**
	 * @param path
	 */
	private void validateWindingRule(Path path) {
		int windingRule = path.getWindingRule();
		if (windingRule == Path.WIND_EVEN_ODD) {
			gc.setFillRule(SWT.FILL_EVEN_ODD);
		} else if (windingRule == Path.WIND_NON_ZERO) {
			gc.setFillRule(SWT.FILL_WINDING);
		} else {
			throw new IllegalStateException(
					"The SwtGraphics does not provide other winding rules than WIND_EVEN_ODD or WIND_NON_ZERO. Current winding rule = "
							+ windingRule);
		}
	}

	private void validateWrite(Rectangle bounds) {
		validateGlobals();
		validateFont();

		Color writeBackground = getCurrentState()
				.getWriteBackgroundByReference();
		org.eclipse.swt.graphics.Color swtColor = SwtGraphicsUtils
				.createSwtColor(writeBackground);
		gc.setBackground(swtColor);
		// TODO: dispose swtColor

		Pattern writePattern = getCurrentState().getWritePatternByReference();
		validatePattern(bounds, writePattern, false);
	}

	@Override
	public IGraphics write(String text) {
		// scale appropriately to get the real font size when using the next
		// smaller integer font size
		Font font = getCurrentState().getFontByReference();
		double fontScale = font.getSize() / (int) font.getSize();
		scale(fontScale, fontScale);

		Dimension size = getTextDimension(text);
		Rectangle bounds = new Rectangle(new Point(), size);
		prepareXorMode(bounds);
		validateWrite(bounds);
		gc.drawString(text, 0, 0, getCurrentState()
				.getWriteBackgroundByReference().getAlpha() == 0);
		finishXorMode();

		// undo scaling
		scale(1d / fontScale, 1d / fontScale);

		return this;
	}

}
