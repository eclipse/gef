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
package org.eclipse.gef4.graphics;

import java.util.Arrays;
import java.util.Stack;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.Pattern.Mode;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.font.Font;
import org.eclipse.gef4.graphics.image.Image;

/**
 * The AbstractGraphics class serves as the basis for any {@link IGraphics}
 * implementation as it provides all the accessors and state related stuff.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractGraphics implements IGraphics {

	private Stack<GraphicsState> states = new Stack<GraphicsState>();

	public AbstractGraphics() {
	}

	/**
	 * Computes the resolution scale factor used to ensure resolution
	 * independence. The scale factor is the quotient of physical and logical
	 * DPI.
	 * 
	 * @return the resolution scale factor
	 */
	protected double computeResolutionScaleFactor() {
		return (double) getDeviceDpi() / (double) getLogicalDpi();
	}

	@Override
	public AffineTransform getAffineTransform() {
		return getCurrentState().getAffineTransformByReference().getCopy();
	}

	@Override
	public Path getClip() {
		Path clip = getCurrentState().getClippingAreaByReference();
		return clip == null ? null : clip.getCopy();
	}

	/**
	 * Returns the current state of this {@link AbstractGraphics}.
	 * 
	 * @return the current state of this {@link AbstractGraphics}
	 */
	protected GraphicsState getCurrentState() {
		return states.peek();
	}

	@Override
	public double[] getDashArray() {
		double[] dashes = getCurrentState().getDashArrayByReference();
		return Arrays.copyOf(dashes, dashes.length);
	}

	@Override
	public double getDashBegin() {
		return getCurrentState().getDashBegin();
	}

	@Override
	public int getDeviceDpi() {
		return getCurrentState().getDeviceDpi();
	}

	@Override
	public Pattern getDrawPattern() {
		return getCurrentState().getDrawPatternByReference().getCopy();
	}

	@Override
	public Color getDrawPatternColor() {
		return getCurrentState().getDrawPatternByReference().getColor();
	}

	@Override
	public Gradient<?> getDrawPatternGradient() {
		return getCurrentState().getDrawPatternByReference().getGradient();
	}

	@Override
	public org.eclipse.gef4.graphics.image.Image getDrawPatternImage() {
		return getCurrentState().getDrawPatternByReference().getImage();
	}

	@Override
	public Pattern.Mode getDrawPatternMode() {
		return getCurrentState().getDrawPatternByReference().getMode();
	}

	@Override
	public Pattern getFillPattern() {
		return getCurrentState().getFillPatternByReference().getCopy();
	}

	@Override
	public Color getFillPatternColor() {
		return getCurrentState().getFillPatternByReference().getColor();
	}

	@Override
	public Gradient<?> getFillPatternGradient() {
		return getCurrentState().getFillPatternByReference().getGradient();
	}

	@Override
	public Image getFillPatternImage() {
		return getCurrentState().getFillPatternByReference().getImage();
	}

	@Override
	public Pattern.Mode getFillPatternMode() {
		return getCurrentState().getFillPatternByReference().getMode();
	}

	@Override
	public Font getFont() {
		return getCurrentState().getFontByReference().getCopy();
	}

	@Override
	public String getFontFamily() {
		return getCurrentState().getFontByReference().getFamily();
	}

	@Override
	public double getFontSize() {
		return getCurrentState().getFontByReference().getSize();
	}

	@Override
	public int getFontStyle() {
		return getCurrentState().getFontByReference().getStyle();
	}

	@Override
	public InterpolationHint getInterpolationHint() {
		return getCurrentState().getInterpolationHint();
	}

	@Override
	public LineCap getLineCap() {
		return getCurrentState().getLineCap();
	}

	@Override
	public LineJoin getLineJoin() {
		return getCurrentState().getLineJoin();
	}

	@Override
	public double getLineWidth() {
		return getCurrentState().getLineWidth();
	}

	@Override
	public int getLogicalDpi() {
		return getCurrentState().getLogicalDpi();
	}

	@Override
	public double getMiterLimit() {
		return getCurrentState().getMiterLimit();
	}

	@Override
	public GraphicsState getState() {
		return getCurrentState().getCopy();
	}

	@Override
	public Color getWriteBackground() {
		return getCurrentState().getWriteBackgroundByReference().getCopy();
	}

	@Override
	public Pattern getWritePattern() {
		return getCurrentState().getWritePatternByReference().getCopy();
	}

	@Override
	public Color getWritePatternColor() {
		return getCurrentState().getWritePatternByReference().getColor();
	}

	@Override
	public Gradient<?> getWritePatternGradient() {
		return getCurrentState().getWritePatternByReference().getGradient();
	}

	@Override
	public Image getWritePatternImage() {
		return getCurrentState().getWritePatternByReference().getImage();
	}

	@Override
	public Pattern.Mode getWritePatternMode() {
		return getCurrentState().getWritePatternByReference().getMode();
	}

	/**
	 * Initializes this {@link IGraphics} by pushing the initial
	 * {@link GraphicsState} to the states stack. The initial state uses the
	 * default values provided by the {@link IGraphics} interface for the
	 * individual properties. The device DPI are set to the value returned by
	 * {@link #getDefaultDeviceDpi()}. The default transformation matrix is
	 */
	protected void initialize() {
		states.push(new GraphicsState(IGraphics.DEFAULT_AFFINE_TRANSFORM,
				IGraphics.DEFAULT_ANTI_ALIASING,
				IGraphics.DEFAULT_CLIPPING_AREA, IGraphics.DEFAULT_DASH_ARRAY,
				IGraphics.DEFAULT_DASH_BEGIN, IGraphics.DEFAULT_LINE_WIDTH,
				IGraphics.DEFAULT_MITER_LIMIT, IGraphics.DEFAULT_LINE_CAP,
				IGraphics.DEFAULT_LINE_JOIN, IGraphics.DEFAULT_DRAW_PATTERN,
				IGraphics.DEFAULT_FILL_PATTERN,
				IGraphics.DEFAULT_WRITE_PATTERN,
				IGraphics.DEFAULT_WRITE_BACKGROUND, IGraphics.DEFAULT_FONT,
				IGraphics.DEFAULT_INTERPOLATION_HINT,
				IGraphics.DEFAULT_XOR_MODE, getDefaultDeviceDpi(),
				IGraphics.DEFAULT_LOGICAL_DPI,
				IGraphics.DEFAULT_EMULATE_XOR_MODE).getCopy());
	}

	@Override
	public IGraphics intersectClip(IMultiShape toClip) {
		if (toClip == null) {
			throw new IllegalArgumentException(
					"The given IMultiShape may not be null.");
		}
		return intersectClip(toClip.toPath());
	}

	@Override
	public IGraphics intersectClip(IShape toIntersect) {
		if (toIntersect == null) {
			throw new IllegalArgumentException(
					"The given IShape may not be null.");
		}
		return intersectClip(toIntersect.toPath());
	}

	@Override
	public IGraphics intersectClip(Path toIntersect) {
		if (toIntersect == null) {
			throw new IllegalArgumentException(
					"The given Path may not be null.");
		}
		Path clip = getCurrentState().getClippingAreaByReference();
		getCurrentState().setClippingAreaByReference(
				clip == null ? toIntersect : Path.intersect(clip, toIntersect));
		return this;
	}

	@Override
	public boolean isAntiAliasing() {
		return getCurrentState().isAntiAliasing();
	}

	@Override
	public boolean isEmulateXorMode() {
		return getCurrentState().isEmulateXorMode();
	}

	@Override
	public boolean isXorMode() {
		return getCurrentState().isXorMode();
	}

	@Override
	public IGraphics popState() {
		if (states.size() == 1) {
			throw new IllegalStateException(
					"You have to push a GraphicsState first.");
		}
		states.pop();
		return this;
	}

	@Override
	public IGraphics pushState() {
		if (states.isEmpty()) {
			throw new IllegalStateException(
					"No initial GraphicsState pushed! The IGraphics is responsible for pushing an initial GraphicsState on construction.");
		}
		GraphicsState priorState = getCurrentState();
		states.push(priorState.getCopy());
		return this;
	}

	@Override
	public IGraphics restoreState() {
		popState();
		pushState();
		return this;
	}

	@Override
	public IGraphics rotate(Angle theta) {
		if (theta == null) {
			throw new IllegalArgumentException(
					"The given Angle may not be null.");
		}
		getCurrentState().getAffineTransformByReference().rotate(theta.rad());
		return this;
	}

	@Override
	public IGraphics scale(double sx, double sy) {
		getCurrentState().getAffineTransformByReference().scale(sx, sy);
		return this;
	}

	@Override
	public IGraphics setAffineTransform(AffineTransform transformations) {
		if (transformations == null) {
			getCurrentState().getAffineTransformByReference().setToIdentity();
		} else {
			getCurrentState().getAffineTransformByReference().setTransform(
					transformations);
		}
		return this;
	}

	@Override
	public IGraphics setAntiAliasing(boolean aa) {
		getCurrentState().setAntiAliasing(aa);
		return this;
	}

	@Override
	public IGraphics setClip(Path clip) {
		getCurrentState().setClippingAreaByReference(
				clip == null ? null : clip.getCopy());
		return this;
	}

	@Override
	public IGraphics setDashArray(double... dashArray) {
		if (dashArray == null) {
			throw new IllegalArgumentException(
					"The given dash array may not be null.");
		}
		getCurrentState().setDashArrayByReference(
				Arrays.copyOf(dashArray, dashArray.length));
		return this;
	}

	@Override
	public IGraphics setDashBegin(double dashBegin) {
		getCurrentState().setDashBegin(dashBegin);
		return this;
	}

	@Override
	public IGraphics setDeviceDpi(int dpi) {
		getCurrentState().setDeviceDpi(dpi);
		return this;
	}

	@Override
	public IGraphics setDraw(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		return setDrawPatternMode(Mode.COLOR).setDrawPatternColor(color);
	}

	@Override
	public IGraphics setDraw(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		return setDrawPatternMode(Mode.GRADIENT).setDrawPatternGradient(
				gradient);
	}

	@Override
	public IGraphics setDraw(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		return setDrawPatternMode(Mode.IMAGE).setDrawPatternImage(image);
	}

	@Override
	public IGraphics setDrawPattern(Pattern pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException(
					"The given Pattern may not be null.");
		}
		getCurrentState().setDrawPatternByReference(pattern.getCopy());
		return this;
	}

	@Override
	public IGraphics setDrawPatternColor(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		getCurrentState().getDrawPatternByReference().setColor(color);
		return this;
	}

	@Override
	public IGraphics setDrawPatternGradient(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		getCurrentState().getDrawPatternByReference().setGradient(gradient);
		return this;
	}

	@Override
	public IGraphics setDrawPatternImage(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		getCurrentState().getDrawPatternByReference().setImage(image);
		return this;
	}

	@Override
	public IGraphics setDrawPatternMode(Pattern.Mode drawMode) {
		if (drawMode == null) {
			throw new IllegalArgumentException(
					"The given Pattern.Mode may not be null.");
		}
		getCurrentState().getDrawPatternByReference().setMode(drawMode);
		return this;
	}

	@Override
	public IGraphics setEmulateXorMode(boolean emulateXorMode) {
		getCurrentState().setEmulateXorMode(emulateXorMode);
		return this;
	}

	@Override
	public IGraphics setFill(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		return setFillPatternMode(Mode.COLOR).setFillPatternColor(color);
	}

	@Override
	public IGraphics setFill(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		return setFillPatternMode(Mode.GRADIENT).setFillPatternGradient(
				gradient);
	}

	@Override
	public IGraphics setFill(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		return setFillPatternMode(Mode.IMAGE).setFillPatternImage(image);
	}

	@Override
	public IGraphics setFillPattern(Pattern pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException(
					"The given Pattern may not be null.");
		}
		getCurrentState().setFillPatternByReference(pattern.getCopy());
		return this;
	}

	@Override
	public IGraphics setFillPatternColor(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		getCurrentState().getFillPatternByReference().setColor(color);
		return this;
	}

	@Override
	public IGraphics setFillPatternGradient(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		getCurrentState().getFillPatternByReference().setGradient(gradient);
		return this;
	}

	@Override
	public IGraphics setFillPatternImage(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		getCurrentState().getFillPatternByReference().setImage(image);
		return this;
	}

	@Override
	public IGraphics setFillPatternMode(Pattern.Mode fillMode) {
		if (fillMode == null) {
			throw new IllegalArgumentException(
					"The given Pattern.Mode may not be null.");
		}
		getCurrentState().getFillPatternByReference().setMode(fillMode);
		return this;
	}

	@Override
	public IGraphics setFont(Font font) {
		if (font == null) {
			throw new IllegalArgumentException(
					"The given Font may not be null.");
		}
		getCurrentState().setFontByReference(font.getCopy());
		return this;
	}

	@Override
	public IGraphics setFontFamily(String family) {
		if (family == null) {
			throw new IllegalArgumentException(
					"The given String may not be null.");
		}
		getCurrentState().getFontByReference().setFamily(family);
		return this;
	}

	@Override
	public IGraphics setFontSize(double size) {
		getCurrentState().getFontByReference().setSize(size);
		return this;
	}

	@Override
	public IGraphics setFontStyle(int style) {
		getCurrentState().getFontByReference().setStyle(style);
		return this;
	}

	@Override
	public IGraphics setInterpolationHint(InterpolationHint interpolationHint) {
		if (interpolationHint == null) {
			throw new IllegalArgumentException(
					"The given InterpolationHint may not be null.");
		}
		getCurrentState().setInterpolationHint(interpolationHint);
		return this;
	}

	@Override
	public IGraphics setLineCap(LineCap lineCap) {
		if (lineCap == null) {
			throw new IllegalArgumentException(
					"The given LineCap may not be null.");
		}
		getCurrentState().setLineCap(lineCap);
		return this;
	}

	@Override
	public IGraphics setLineJoin(LineJoin lineJoin) {
		if (lineJoin == null) {
			throw new IllegalArgumentException(
					"The given LineJoin may not be null.");
		}
		getCurrentState().setLineJoin(lineJoin);
		return this;
	}

	@Override
	public IGraphics setLineWidth(double lineWidth) {
		getCurrentState().setLineWidth(lineWidth);
		return this;
	}

	@Override
	public IGraphics setLogicalDpi(int dpi) {
		getCurrentState().setLogicalDpi(dpi);
		return this;
	}

	@Override
	public IGraphics setMiterLimit(double miterLimit) {
		getCurrentState().setMiterLimit(miterLimit);
		return this;
	}

	@Override
	public IGraphics setState(GraphicsState state) {
		setAffineTransform(state.getAffineTransformByReference());
		setAntiAliasing(state.isAntiAliasing());
		setClip(state.getClippingAreaByReference());
		setDeviceDpi(state.getDeviceDpi());
		setDrawPattern(state.getDrawPatternByReference());
		setFillPattern(state.getFillPatternByReference());
		setFont(state.getFontByReference());
		setInterpolationHint(state.getInterpolationHint());
		setLogicalDpi(state.getLogicalDpi());
		setStroke(state.getLineWidth(), state.getLineCap(),
				state.getLineJoin(), state.getMiterLimit(),
				state.getDashBegin(), state.getDashArrayByReference());
		setWritePattern(state.getWritePatternByReference());
		setWriteBackground(state.getWriteBackgroundByReference());
		setXorMode(state.isXorMode());
		return this;
	}

	@Override
	public IGraphics setStroke(double width, double... dashes) {
		setLineWidth(width);
		return setDashArray(dashes);
	}

	@Override
	public IGraphics setStroke(double width, LineCap cap, LineJoin join,
			double miterLimit) {
		setLineWidth(width);
		setLineCap(cap);
		setLineJoin(join);
		return setMiterLimit(miterLimit);
	}

	@Override
	public IGraphics setStroke(double width, LineCap cap, LineJoin join,
			double miterLimit, double dashBegin, double... dashes) {
		setLineWidth(width);
		setLineCap(cap);
		setLineJoin(join);
		setMiterLimit(miterLimit);
		setDashBegin(dashBegin);
		return setDashArray(dashes);
	}

	@Override
	public IGraphics setWrite(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		return setWritePatternMode(Mode.COLOR).setWritePatternColor(color);
	}

	@Override
	public IGraphics setWrite(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		return setWritePatternMode(Mode.GRADIENT).setWritePatternGradient(
				gradient);
	}

	@Override
	public IGraphics setWrite(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		return setWritePatternMode(Mode.IMAGE).setWritePatternImage(image);
	}

	@Override
	public IGraphics setWriteBackground(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		getCurrentState().setWriteBackgroundByReference(color.getCopy());
		return this;
	}

	@Override
	public IGraphics setWritePattern(Pattern pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException(
					"The given Pattern may not be null.");
		}
		getCurrentState().setWritePatternByReference(pattern.getCopy());
		return this;
	}

	@Override
	public IGraphics setWritePatternColor(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		getCurrentState().getWritePatternByReference().setColor(color);
		return this;
	}

	@Override
	public IGraphics setWritePatternGradient(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		getCurrentState().getWritePatternByReference().setGradient(gradient);
		return this;
	}

	@Override
	public IGraphics setWritePatternImage(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		getCurrentState().getWritePatternByReference().setImage(image);
		return this;
	}

	@Override
	public IGraphics setWritePatternMode(Pattern.Mode mode) {
		if (mode == null) {
			throw new IllegalArgumentException(
					"The given Pattern.Mode may not be null.");
		}
		getCurrentState().getWritePatternByReference().setMode(mode);
		return this;
	}

	@Override
	public IGraphics setXorMode(boolean xor) {
		getCurrentState().setXorMode(xor);
		return this;
	}

	@Override
	public IGraphics shear(double shx, double shy) {
		getCurrentState().getAffineTransformByReference().shear(shx, shy);
		return this;
	}

	@Override
	public String toString() {
		return "IGraphics()";
	}

	@Override
	public IGraphics transform(AffineTransform transformations) {
		getCurrentState().getAffineTransformByReference().concatenate(
				transformations);
		return this;
	}

	@Override
	public IGraphics translate(double x, double y) {
		getCurrentState().getAffineTransformByReference().translate(x, y);
		return this;
	}

	@Override
	public IGraphics unionClip(IMultiShape toShow) {
		if (toShow == null) {
			throw new IllegalArgumentException(
					"The given IMultiShape may not be null.");
		}
		return unionClip(toShow.toPath());
	}

	@Override
	public IGraphics unionClip(IShape toShow) {
		if (toShow == null) {
			throw new IllegalArgumentException(
					"The given IShape may not be null.");
		}
		return unionClip(toShow.toPath());
	}

	@Override
	public IGraphics unionClip(Path toShow) {
		if (toShow == null) {
			throw new IllegalArgumentException(
					"The given Path may not be null.");
		}
		Path clip = getCurrentState().getClippingAreaByReference();
		getCurrentState().setClippingAreaByReference(
				clip == null ? null : Path.add(clip, toShow));
		return this;
	}

}
