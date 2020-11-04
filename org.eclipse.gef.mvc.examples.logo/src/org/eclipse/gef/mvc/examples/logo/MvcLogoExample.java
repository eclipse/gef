/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.BezierCurve;
import org.eclipse.gef.geometry.planar.CurvedPolygon;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.PolyBezier;
import org.eclipse.gef.mvc.examples.AbstractMvcExample;
import org.eclipse.gef.mvc.examples.logo.model.AbstractGeometricElement;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve.InterpolationStyle;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve.RoutingStyle;
import org.eclipse.gef.mvc.examples.logo.model.GeometricShape;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.inject.Module;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;

public class MvcLogoExample extends AbstractMvcExample {

	public static final double GEF_STROKE_WIDTH = 3.5;

	public static final Color GEF_COLOR_BLUE = Color.rgb(135, 150, 220);

	public static final Color GEF_COLOR_GREEN = Color.rgb(99, 123, 71);

	public static final Effect GEF_SHADOW_EFFECT = createShadowEffect();

	public static final Double[] GEF_DASH_PATTERN = new Double[] { 13d, 8d };

	public static List<? extends AbstractGeometricElement<?>> createContentViewerContents() {
		// selection handles
		GeometricShape topLeftSelectionHandle = new GeometricShape(createHandleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 12, 15), Color.WHITE, GEF_SHADOW_EFFECT);
		GeometricShape topRightSelectionHandle = new GeometricShape(createHandleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 243, 15), Color.WHITE, GEF_SHADOW_EFFECT);

		GeometricShape bottomLeftSelectionHandle = new GeometricShape(createHandleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 12, 109), Color.WHITE, GEF_SHADOW_EFFECT);

		GeometricShape bottomRightSelectionHandle = new GeometricShape(createHandleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 243, 109), Color.WHITE, GEF_SHADOW_EFFECT);

		GeometricCurve selectionBoundsTopLine = new GeometricCurve(
				new Point[] { new Point(), new Point(140, 24), new Point() }, GEF_COLOR_GREEN, GEF_STROKE_WIDTH,
				GEF_DASH_PATTERN, null, RoutingStyle.STRAIGHT, InterpolationStyle.POLYGON);

		GeometricCurve selectionBoundsLeftLine = new GeometricCurve(
				new Point[] { new Point(), new Point(19, 70), new Point() }, GEF_COLOR_GREEN, GEF_STROKE_WIDTH,
				GEF_DASH_PATTERN, null, RoutingStyle.STRAIGHT, InterpolationStyle.POLYBEZIER);

		GeometricCurve selectionBoundsBottomLine = new GeometricCurve(
				new Point[] { new Point(), new Point(140, 118), new Point() }, GEF_COLOR_GREEN, GEF_STROKE_WIDTH,
				new Double[] { 15d, 10d }, null, RoutingStyle.ORTHOGONAL, InterpolationStyle.POLYGON);

		GeometricCurve selectionBoundsRightLine = new GeometricCurve(
				new Point[] { new Point(), new Point(250, 70), new Point() }, GEF_COLOR_GREEN, GEF_STROKE_WIDTH,
				new Double[] { 15d, 10d }, null, RoutingStyle.ORTHOGONAL, InterpolationStyle.POLYBEZIER);

		// g shapes
		GeometricShape gBaseShape = new GeometricShape(createGBaseShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 27, 22), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);

		GeometricShape gTopShape = new GeometricShape(createGTopShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 27, 22), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);

		GeometricShape gMiddleShape = new GeometricShape(createGMiddleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 27, 22), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);

		// e shape
		GeometricShape eShape = new GeometricShape(createEShapeGeometry(), new AffineTransform(1, 0, 0, 1, 100, 22),
				GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);

		// f shape
		GeometricShape fShape = new GeometricShape(createFShapeGeometry(), new AffineTransform(1, 0, 0, 1, 175, 22),
				GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);

		// gDotShape
		GeometricShape gDotShape = new GeometricShape(createDotShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 87, 104), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);

		// eDotShape
		GeometricShape eDotShape = new GeometricShape(createDotShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 170, 104), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);

		// fDotShape
		GeometricShape fDotShape = new GeometricShape(createDotShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 225, 104), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);

		// fDotShape
		GeometricShape cursorShape = new GeometricShape(createCursorShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 227, 45), Color.WHITE, 2, Color.BLACK, GEF_SHADOW_EFFECT);

		// anchor curves to shapes
		selectionBoundsTopLine.addSourceAnchorage(topLeftSelectionHandle);
		selectionBoundsTopLine.addTargetAnchorage(topRightSelectionHandle);

		selectionBoundsLeftLine.addSourceAnchorage(topLeftSelectionHandle);
		selectionBoundsLeftLine.addTargetAnchorage(bottomLeftSelectionHandle);

		selectionBoundsBottomLine.addSourceAnchorage(bottomLeftSelectionHandle);
		selectionBoundsBottomLine.addTargetAnchorage(bottomRightSelectionHandle);

		selectionBoundsRightLine.addSourceAnchorage(topRightSelectionHandle);
		selectionBoundsRightLine.addTargetAnchorage(bottomRightSelectionHandle);

		// anchor g-letter shape fragments to its base shape and a point to each
		// letter shapes
		gDotShape.addAnchorage(gBaseShape);
		gMiddleShape.addAnchorage(gBaseShape);
		gTopShape.addAnchorage(gBaseShape);
		eDotShape.addAnchorage(eShape);
		fDotShape.addAnchorage(fShape);

		List<AbstractGeometricElement<? extends IGeometry>> visualShapes = new ArrayList<>();

		// add all shapes in z-order
		visualShapes.add(selectionBoundsTopLine);
		visualShapes.add(selectionBoundsLeftLine);
		visualShapes.add(selectionBoundsBottomLine);
		visualShapes.add(selectionBoundsRightLine);

		visualShapes.add(topLeftSelectionHandle);
		visualShapes.add(topRightSelectionHandle);
		visualShapes.add(bottomLeftSelectionHandle);
		visualShapes.add(bottomRightSelectionHandle);

		visualShapes.add(gTopShape);
		visualShapes.add(gMiddleShape);
		visualShapes.add(gBaseShape);

		visualShapes.add(eShape);
		visualShapes.add(fShape);
		visualShapes.add(gDotShape);
		visualShapes.add(eDotShape);
		visualShapes.add(fDotShape);

		visualShapes.add(cursorShape);
		return visualShapes;
	}

	private static IShape createCursorShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<>();
		segments.add(new Line(0, 31, 12, 41));
		segments.add(new Line(12, 41, 12, 32));
		segments.add(new Line(12, 32, 26, 32));
		segments.add(new Line(26, 32, 26, 47));
		segments.add(new Line(26, 47, 17, 47));
		segments.add(new Line(17, 47, 28, 59));
		segments.add(new Line(28, 59, 40, 47));
		segments.add(new Line(40, 47, 30, 47));
		segments.add(new Line(30, 47, 30, 32));
		segments.add(new Line(30, 32, 46, 32));
		segments.add(new Line(46, 32, 46, 41));
		segments.add(new Line(46, 41, 57, 30));
		segments.add(new Line(57, 30, 46, 18));
		segments.add(new Line(46, 18, 46, 28));
		segments.add(new Line(46, 28, 30, 28));
		segments.add(new Line(30, 28, 30, 12));
		segments.add(new Line(30, 12, 39, 12));
		segments.add(new Line(39, 12, 28, 0));
		segments.add(new Line(28, 0, 17, 12));
		segments.add(new Line(17, 12, 26, 12));
		segments.add(new Line(26, 12, 26, 28));
		segments.add(new Line(26, 28, 12, 28));
		segments.add(new Line(12, 28, 12, 18));
		segments.add(new Line(12, 18, 0, 31));
		return new CurvedPolygon(segments);
	}

	private static IShape createDotShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<>();
		segments.add(new Line(3, 0, 0, 4));
		segments.add(new Line(0, 4, 4, 9));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(4, 9, 8, 5, 3, 0).toBezier()));
		return new CurvedPolygon(segments);
	}

	private static IShape createEShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<>();
		segments.add(new Line(1, 10, 6, 10));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(6, 10, 5, 25, 7, 52, 6, 70, 6, 81).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(6, 81, 5, 81, 3, 84).toBezier()));
		segments.add(new Line(3, 84, 3, 87));
		segments.add(new Line(3, 87, 64, 86));
		segments.add(new Line(64, 86, 65, 79));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(65, 79, 59, 81, 51, 82).toBezier()));
		segments.add(new Line(51, 82, 12, 82));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(12, 82, 11, 56, 11, 30).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(11, 30, 27, 30, 45, 31).toBezier()));
		segments.add(new Line(45, 31, 48, 25));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(48, 25, 35, 27, 19, 27, 10, 26).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(10, 26, 10, 20, 11, 10).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(11, 10, 24, 11, 31, 11, 51, 12).toBezier()));
		segments.add(new Line(51, 12, 55, 6));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(55, 6, 45, 7, 33, 8, 15, 7, 7, 6).toBezier()));
		segments.add(new Line(7, 6, 1, 10));
		return new CurvedPolygon(segments);
	}

	private static IShape createFShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<>();
		segments.add(new Line(3, 13, 3, 23));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(3, 23, 10, 25, 17, 27).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(17, 27, 16, 34, 14, 44, 10, 46).toBezier()));
		segments.add(new Line(10, 46, 10, 53));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(10, 53, 8, 70, 7, 83).toBezier()));
		segments.add(new Line(7, 83, 13, 89));
		segments.add(new Line(13, 89, 17, 85));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(17, 85, 18, 69, 20, 54).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(20, 54, 32, 52, 45, 50).toBezier()));
		segments.add(new Line(45, 50, 45, 45));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(45, 45, 33, 46, 22, 46).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(22, 46, 23, 34, 25, 24).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(25, 24, 42, 24, 50, 21, 59, 18, 67, 16).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(67, 16, 67, 12, 64, 10).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(64, 10, 54, 14, 42, 16, 28, 16).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(28, 16, 29, 11, 29, 8, 31, 0).toBezier()));
		segments.add(new Line(31, 0, 24, 3));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(24, 3, 24, 8, 23, 12).toBezier()));
		segments.add(new Line(23, 12, 20, 11));
		segments.add(new Line(20, 11, 20, 15));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(20, 15, 13, 15, 3, 13).toBezier()));
		return new CurvedPolygon(segments);
	}

	private static IShape createGBaseShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<>();
		segments.add(new Line(0, 55, 10, 54));
		segments.add(new Line(10, 54, 13, 42));
		segments.add(new Line(13, 42, 23, 42));
		segments.add(new Line(23, 42, 25, 32));
		segments.add(new Line(25, 32, 37, 33));
		segments.add(new Line(37, 33, 42, 23));
		segments.add(new Line(42, 23, 54, 24));
		segments.add(new Line(54, 24, 57, 12));
		segments.add(new Line(57, 12, 65, 28));
		segments.add(new Line(65, 28, 60, 31));
		segments.add(new Line(60, 31, 57, 30));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(57, 30, 45, 35, 22, 53, 14, 73, 40, 69).toBezier()));
		segments.add(new Line(40, 69, 39, 61));
		segments.add(new Line(39, 61, 46, 58));
		segments.add(new Line(46, 58, 54, 63));
		segments.add(new Line(54, 63, 62, 57));
		segments.add(new Line(62, 57, 67, 58));
		segments.add(new Line(67, 58, 67, 65));
		segments.add(new Line(67, 65, 55, 66));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(55, 66, 53, 77, 51, 86).toBezier()));
		segments.add(new Line(51, 86, 47, 86));
		segments.add(new Line(47, 86, 46, 71));
		segments.add(new Line(46, 71, 25, 84));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(25, 84, 17, 86, 14, 84).toBezier()));
		segments.add(new Line(14, 84, 0, 55));
		return new CurvedPolygon(segments);
	}

	private static IShape createGMiddleShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<>();
		segments.add(new Line(37, 48, 38, 56));
		segments.add(new Line(38, 56, 45, 52));
		segments.add(new Line(45, 52, 54, 57));
		segments.add(new Line(54, 57, 62, 51));
		segments.add(new Line(62, 51, 67, 54));
		segments.add(new Line(67, 54, 67, 45));
		segments.add(new Line(67, 45, 62, 44));
		segments.add(new Line(62, 44, 62, 48));
		segments.add(new Line(62, 48, 37, 48));
		return new CurvedPolygon(segments);
	}

	private static IShape createGTopShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<>();
		segments.add(new Line(0, 51, 8, 49));
		segments.add(new Line(8, 49, 9, 38));
		segments.add(new Line(9, 38, 20, 38));
		segments.add(new Line(20, 38, 24, 26));
		segments.add(new Line(24, 26, 35, 27));
		segments.add(new Line(35, 27, 39, 15));
		segments.add(new Line(39, 15, 51, 17));
		segments.add(new Line(51, 17, 54, 8));
		segments.add(new Line(54, 8, 52, 4));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(52, 4, 40, 9, 14, 28, 1, 43, 0, 51).toBezier()));
		return new CurvedPolygon(segments);
	}

	private static IShape createHandleShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<>();
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(1, 1, 9, 0, 17, 1).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(17, 1, 16, 8, 17, 16).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(17, 16, 7, 15, 1, 16).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(1, 16, 0, 8, 1, 1).toBezier()));
		return new CurvedPolygon(segments);
	}

	public static List<GeometricShape> createPaletteViewerContents() {
		final List<GeometricShape> paletteContents = new ArrayList<>();
		final GeometricShape handlePrototype = new GeometricShape(createHandleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 23, 5), Color.WHITE, GEF_SHADOW_EFFECT);
		paletteContents.add(handlePrototype);
		final GeometricShape cursorPrototype = new GeometricShape(createCursorShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 5, 32), Color.WHITE, 2, Color.BLACK, GEF_SHADOW_EFFECT);
		paletteContents.add(cursorPrototype);
		final GeometricShape eShapePrototype = new GeometricShape(createEShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 100, 22), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);
		paletteContents.add(eShapePrototype);
		final GeometricShape fShapePrototype = new GeometricShape(createFShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 175, 22), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);
		paletteContents.add(fShapePrototype);
		final GeometricShape dotShapePrototype = new GeometricShape(createDotShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 87, 104), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT);
		paletteContents.add(dotShapePrototype);
		return paletteContents;
	}

	private static Effect createShadowEffect() {
		DropShadow outerShadow = new DropShadow();
		outerShadow.setRadius(3);
		outerShadow.setSpread(0.2);
		outerShadow.setOffsetX(3);
		outerShadow.setOffsetY(3);
		outerShadow.setColor(new Color(0.3, 0.3, 0.3, 1));

		Distant light = new Distant();
		light.setAzimuth(-135.0f);

		Lighting l = new Lighting();
		l.setLight(light);
		l.setSurfaceScale(3.0f);

		Blend effects = new Blend(BlendMode.MULTIPLY);
		effects.setTopInput(l);
		effects.setBottomInput(outerShadow);

		return effects;
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public MvcLogoExample() {
		super("GEF MVC Logo Example");
	}

	@Override
	protected Module createModule() {
		return new MvcLogoExampleModule();
	}

	protected IViewer getPaletteViewer() {
		return getDomain().getAdapter(AdapterKey.get(IViewer.class, MvcLogoExampleModule.PALETTE_VIEWER_ROLE));
	}

	@Override
	protected void hookViewers() {
		getPrimaryStage().setScene(
				new Scene(new MvcLogoExampleViewersComposite(getContentViewer(), getPaletteViewer()).getComposite()));
	}

	@Override
	protected void populateViewerContents() {
		getContentViewer().getContents().setAll(createContentViewerContents());
		getPaletteViewer().getContents().setAll(createPaletteViewerContents());
		getPrimaryStage().sizeToScene();
	}

}
