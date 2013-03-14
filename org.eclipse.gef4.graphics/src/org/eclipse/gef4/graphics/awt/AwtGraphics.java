/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics.awt;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.eclipse.gef4.geometry.convert.awt.Geometry2AWT;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.graphics.AbstractGraphics;
import org.eclipse.gef4.graphics.GraphicsState;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.IImageGraphics;
import org.eclipse.gef4.graphics.InterpolationHint;
import org.eclipse.gef4.graphics.Pattern;
import org.eclipse.gef4.graphics.image.Image;

/**
 * The AWT {@link IGraphics} implementation used to draw to the screen.
 * 
 * @author mwienand
 * 
 */
public class AwtGraphics extends AbstractGraphics {

	// TODO: XorMode is failing badly when xor'd things are out of the viewport.

	private static class XorComposite implements Composite {

		public static XorComposite INSTANCE = new XorComposite();

		private XorContext context = new XorContext();

		@Override
		public CompositeContext createContext(ColorModel srcColorModel,
				ColorModel dstColorModel, RenderingHints hints) {
			return context;
		}

	}

	private static class XorContext implements CompositeContext {

		/*
		 * TODO: check if this works "everywhere"
		 */

		public XorContext() {
		}

		@Override
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			// TODO: Find out which minimum/maximum values we really have.
			int w = Math.min(src.getWidth(), dstIn.getWidth());
			int h = Math.min(src.getHeight(), dstIn.getHeight());

			int[] srcArgb = new int[4];
			int[] dstArgb = new int[4];

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					src.getPixel(x, y, srcArgb);
					dstIn.getPixel(x, y, dstArgb);
					for (int i = 0; i < 3; i++) {
						dstArgb[i] ^= srcArgb[i];
					}
					dstOut.setPixel(x, y, dstArgb);
				}
			}
		}

		@Override
		public void dispose() {
		}

	}

	private Graphics2D g;

	/**
	 * Constructs an {@link AwtGraphics} from the given {@link Graphics2D}.
	 * 
	 * @param g2d
	 */
	public AwtGraphics(Graphics2D g2d) {
		g = (Graphics2D) g2d.create();
	}

	@Override
	public void cleanUp() {
		g.dispose();
	}

	@Override
	public IImageGraphics createImageGraphics(Image image) {
		return new AwtImageGraphics(image);
	}

	@Override
	public IGraphics draw(ICurve curve) {
		validateDraw();
		if (curve instanceof Line) {
			Line line = (Line) curve;
			g.drawLine((int) line.getX1(), (int) line.getY1(),
					(int) line.getX2(), (int) line.getY2());
		} else {
			return draw(curve.toPath());
		}
		return this;
	}

	@Override
	public IGraphics draw(Path path) {
		validateDraw();
		g.draw(Geometry2AWT.toAWTPath(path));
		return this;
	}

	@Override
	public IGraphics draw(Point point) {
		validateDraw();
		int x = (int) point.x();
		int y = (int) point.y();
		g.drawLine(x, y, x, y);
		return this;
	}

	@Override
	public IGraphics fill(IMultiShape multiShape) {
		return fill(multiShape.toPath());
	}

	@Override
	public IGraphics fill(IShape shape) {
		validateFill();
		if (shape instanceof Rectangle) {
			Rectangle r = (Rectangle) shape;
			g.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth() + 1,
					(int) r.getHeight() + 1);
		} else if (shape instanceof Ellipse) {
			Ellipse e = (Ellipse) shape;
			g.fillOval((int) e.getX(), (int) e.getY(), (int) e.getWidth() + 1,
					(int) e.getHeight() + 1);
		} else if (shape instanceof Pie) {
			Pie p = (Pie) shape;
			g.fillArc((int) (p.getX() + 0.5), (int) (p.getY() + 0.5), (int) (p
					.getWidth() + 1), (int) (p.getHeight() + 1), (int) (p
					.getStartAngle().deg() + 0.5), (int) (p.getAngularExtent()
					.deg() + 0.5));

		} else if (shape instanceof RoundedRectangle) {
			RoundedRectangle r = (RoundedRectangle) shape;
			g.fillRoundRect((int) r.getX(), (int) r.getY(),
					(int) r.getWidth() + 1, (int) r.getHeight() + 1,
					(int) r.getArcWidth(), (int) r.getArcHeight());
		} else if (shape instanceof Polygon) {
			Polygon p = (Polygon) shape;
			Point[] points = p.getPoints();
			int[] x = new int[points.length];
			int[] y = new int[points.length];
			for (int i = 0; i < points.length; i++) {
				x[i] = (int) (points[i].x + 0.5);
				y[i] = (int) (points[i].y + 0.5);
			}
			g.fillPolygon(x, y, points.length);
		} else {
			return fill(shape.toPath());
		}
		return this;
	}

	@Override
	public IGraphics fill(Path path) {
		validateFill();
		g.fill(Geometry2AWT.toAWTPath(path));
		return this;
	}

	@Override
	public int getDefaultDeviceDpi() {
		return java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
	}

	/**
	 * Returns the {@link Graphics2D} that is associated with this
	 * {@link AwtGraphics}.
	 * 
	 * @return the {@link Graphics2D} that is associated with this
	 *         {@link AwtGraphics}
	 */
	public Graphics2D getGraphics2D() {
		return g;
	}

	@Override
	public Dimension getTextDimension(String text) {
		double fontScale = AwtGraphicsUtils.computeFontScale(getCurrentState()
				.getFontByReference());
		scale(fontScale, fontScale);
		validateWrite();
		FontMetrics fontMetrics = g.getFontMetrics();
		Rectangle2D stringBounds = fontMetrics.getStringBounds(text, g);
		scale(1d / fontScale, 1d / fontScale);
		return new Dimension(stringBounds.getWidth(), stringBounds.getHeight())
				.scale(fontScale);
	}

	@Override
	public boolean isXorMode() {
		return getCurrentState().isXorMode();
	}

	@Override
	public AwtGraphics paint(Image image) {
		validateBlit();
		g.drawImage(AwtGraphicsUtils.toAwtImage(image), 0, 0, null);
		return this;
	}

	@Override
	public IGraphics setXorMode(boolean xor) {
		getCurrentState().setXorMode(xor);
		return this;
	}

	private void validateBlit() {
		validateGlobals();

		InterpolationHint interp = getCurrentState().getInterpolationHint();
		g.setRenderingHint(
				RenderingHints.KEY_INTERPOLATION,
				interp == InterpolationHint.SPEED ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
						: RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	}

	private void validateDraw() {
		validateGlobals();
		validatePattern(getCurrentState().getDrawPatternByReference());

		GraphicsState s = getCurrentState();
		g.setStroke(AwtGraphicsUtils.toAwtBasicStroke(
				s.getDashArrayByReference(), s.getDashBegin(), s.getLineCap(),
				s.getLineJoin(), s.getLineWidth(), s.getMiterLimit()));
	}

	private void validateFill() {
		validateGlobals();
		validatePattern(getCurrentState().getFillPatternByReference());
	}

	private void validateGlobals() {
		g.setTransform(new AffineTransform(getCurrentState()
				.getAffineTransformByReference().getMatrix()));

		Path clip = getCurrentState().getClippingAreaByReference();
		g.setClip(clip == null ? null : Geometry2AWT.toAWTPath(clip));

		boolean aa = getCurrentState().isAntiAliasing();
		if (aa) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} else {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}

		if (getCurrentState().isXorMode()) {
			// g.setXORMode(AwtGraphicsUtils.toAwtColor(new Color(0, 0, 255)));
			// g.setComposite(AlphaComposite.Xor);
			// TODO: disable anti-aliasing as a workaround
			g.setComposite(XorComposite.INSTANCE);
		} else {
			// g.setPaintMode();
			g.setComposite(AlphaComposite.SrcOver);
		}
	}

	private void validatePattern(Pattern p) {
		switch (p.getMode()) {
		case COLOR:
			g.setColor(AwtGraphicsUtils.toAwtColor(p.getColor()));
			break;
		case GRADIENT:
			g.setPaint(AwtGraphicsUtils.toAwtGradient(p.getGradient()));
			break;
		case IMAGE:
			BufferedImage img = AwtGraphicsUtils.toAwtImage(p.getImage());
			g.setPaint(new TexturePaint(img, new Rectangle2D.Double(0, 0, img
					.getWidth(), img.getHeight())));
			break;
		}
	}

	private void validateWrite() {
		validateGlobals();
		validatePattern(getCurrentState().getWritePatternByReference());

		g.setBackground(AwtGraphicsUtils.toAwtColor(getCurrentState()
				.getWriteBackgroundByReference()));
		g.setFont(AwtGraphicsUtils.toAwtFont(getCurrentState()
				.getFontByReference()));
	}

	@Override
	public IGraphics write(String text) {
		double fontScale = AwtGraphicsUtils.computeFontScale(getCurrentState()
				.getFontByReference());
		scale(fontScale, fontScale);

		// TODO: Use LineMetrics.
		FontMetrics fontMetrics = g.getFontMetrics();
		int ascent = fontMetrics.getMaxAscent();
		translate(0, ascent);

		validateWrite();

		g.drawString(text, 0, 0);

		if (getCurrentState().getFontByReference().isUnderlined()) {
			g.drawLine(0, 0, fontMetrics.stringWidth(text), 0);
		}

		// undo transformations
		translate(0, -ascent);
		scale(1d / fontScale, 1d / fontScale);

		return this;
	}
}
