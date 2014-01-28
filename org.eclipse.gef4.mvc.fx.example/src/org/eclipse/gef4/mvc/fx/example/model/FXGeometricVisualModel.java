package org.eclipse.gef4.mvc.fx.example.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.CurvedPolygon;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Rectangle;

public class FXGeometricVisualModel {

	private static final Color GEF_COLOR_BLUE = Color.rgb(97, 102, 170);
	private static final Effect GEF_SHADOW_EFFECT = createShadowEffect();

	private FXGeometricCurveVisual l1 = new FXGeometricCurveVisual(new Line(
			100, 75, 150, 75));
	private FXGeometricShapeVisual r1 = new FXGeometricShapeVisual(
			new Rectangle(50, 50, 50, 50), null, Color.RED, null, l1);
	private FXGeometricShapeVisual r2 = new FXGeometricShapeVisual(
			new Rectangle(150, 50, 50, 50), null, Color.RED, null, l1);

	public List<FXGeometricShapeVisual> getShapeVisuals() {
		List<FXGeometricShapeVisual> visualShapes = new ArrayList<FXGeometricShapeVisual>();

		// g shape
		visualShapes.add(new FXGeometricShapeVisual(createGBaseShape(),
				new AffineTransform(1, 0, 0, 1, 27, 146), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT));
		visualShapes.add(new FXGeometricShapeVisual(createGTopShape(),
				new AffineTransform(1, 0, 0, 1, 27, 146), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT));
		visualShapes.add(new FXGeometricShapeVisual(
				createGMiddleShape(), new AffineTransform(1, 0, 0, 1, 27, 146), GEF_COLOR_BLUE,
				GEF_SHADOW_EFFECT));
		
		// e shape
		visualShapes.add(new FXGeometricShapeVisual(createEShape(),
				new AffineTransform(1, 0, 0, 1, 101, 148), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT));
		
		// f shape
		visualShapes.add(new FXGeometricShapeVisual(createFShape(), null,
				GEF_COLOR_BLUE, GEF_SHADOW_EFFECT));

		// gDotShape
		visualShapes.add(new FXGeometricShapeVisual(createDotShape(), new
		 AffineTransform(1, 0, 0, 1, 87, 224), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT));
		
		// eDotShape
		visualShapes.add(new FXGeometricShapeVisual(createDotShape(), new
				 AffineTransform(1, 0, 0, 1, 170, 224), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT));
		
		// fDotShape
		visualShapes.add(new FXGeometricShapeVisual(createDotShape(), new
				 AffineTransform(1, 0, 0, 1, 225, 224), GEF_COLOR_BLUE, GEF_SHADOW_EFFECT));

		visualShapes.add(r1);
		visualShapes.add(r2);

		return visualShapes;
	}

	private static Effect createShadowEffect() {
		DropShadow outerShadow = new DropShadow();
		outerShadow.setColor(Color.GREY);
		return outerShadow;
	}

	public List<FXGeometricCurveVisual> getCurveVisuals() {
		List<FXGeometricCurveVisual> visualCurves = new ArrayList<FXGeometricCurveVisual>();
		visualCurves.add(l1);
		return visualCurves;
	}

	private IShape createGTopShape() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(0, 47, 8, 45));
		segments.add(new Line(8, 45, 9, 34));
		segments.add(new Line(9, 34, 20, 34));
		segments.add(new Line(20, 34, 24, 22));
		segments.add(new Line(24, 22, 35, 23));
		segments.add(new Line(35, 23, 39, 11));
		segments.add(new Line(39, 11, 51, 13));
		segments.add(new Line(51, 13, 54, 4));
		segments.add(new Line(54, 4, 52, 0));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(52, 0, 40,
				5, 14, 24, 1, 39, 0, 47).toBezier()));
		return new CurvedPolygon(segments);
	}

	private IShape createGBaseShape() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(0, 51, 10, 50));
		segments.add(new Line(10, 50, 13, 38));
		segments.add(new Line(13, 38, 23, 38));
		segments.add(new Line(23, 38, 25, 28));
		segments.add(new Line(25, 28, 37, 29));
		segments.add(new Line(37, 29, 42, 19));
		segments.add(new Line(42, 19, 54, 20));
		segments.add(new Line(54, 20, 57, 8));
		segments.add(new Line(57, 8, 65, 24));
		segments.add(new Line(65, 24, 60, 27));
		segments.add(new Line(60, 27, 57, 26));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(57, 26, 45,
				31, 22, 49, 14, 69, 40, 65).toBezier()));
		segments.add(new Line(40, 65, 39, 57));
		segments.add(new Line(39, 57, 46, 54));
		segments.add(new Line(46, 54, 54, 59));
		segments.add(new Line(54, 59, 62, 53));
		segments.add(new Line(62, 53, 67, 54));
		segments.add(new Line(67, 54, 67, 61));
		segments.add(new Line(67, 61, 55, 62));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(55, 62, 53,
				73, 51, 82).toBezier()));
		segments.add(new Line(51, 82, 47, 82));
		segments.add(new Line(47, 82, 46, 67));
		segments.add(new Line(46, 67, 25, 80));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(25, 80, 17,
				82, 14, 80).toBezier()));
		segments.add(new Line(14, 80, 0, 51));
		return new CurvedPolygon(segments);
	}

	private IShape createGMiddleShape() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(37, 44, 38, 52));
		segments.add(new Line(38, 52, 45, 48));
		segments.add(new Line(45, 48, 54, 53));
		segments.add(new Line(54, 53, 62, 47));
		segments.add(new Line(62, 47, 67, 50));
		segments.add(new Line(67, 50, 67, 41));
		segments.add(new Line(67, 41, 62, 40));
		segments.add(new Line(62, 40, 62, 44));
		segments.add(new Line(62, 44, 37, 44));
		return new CurvedPolygon(segments);
	}

	private IShape createDotShape() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(3, 0, 0, 4));
		segments.add(new Line(0, 4, 4, 9));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(4, 9, 8, 5,
				3, 0).toBezier()));
		return new CurvedPolygon(segments);
	}

	private IShape createEShape() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(0, 4, 5, 4));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(5, 4,
				4, 19, 6, 46, 5, 64, 5, 75).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(5, 75,
				4, 77, 2, 78).toBezier()));
		segments.add(new Line(2, 78, 2, 81));
		segments.add(new Line(2, 81, 63, 80));
		segments.add(new Line(63, 80, 64, 73));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(64, 73,
				58, 75, 50, 76).toBezier()));
		segments.add(new Line(50, 76, 11, 76));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(11, 76,
				10, 50, 10, 24).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(10, 24,
				26, 24, 44, 25).toBezier()));
		segments.add(new Line(44, 25, 47, 19));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(47, 19,
				34, 21, 18, 21, 9, 20).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(9, 20,
				9, 14, 10, 4).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(10, 4,
				23, 6, 34, 6, 41, 5, 45, 6, 50, 6).toBezier()));
		segments.add(new Line(50, 6, 54, 0));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(54, 0,
				44, 1, 32, 2, 14, 1, 6, 0).toBezier()));
		segments.add(new Line(6, 0, 0, 4));
		return new CurvedPolygon(segments);
	}

	private IShape createFShape() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(178, 155, 178, 165));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(178, 165,
				185, 167, 192, 168).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(192, 168,
				191, 176, 189, 186, 185, 188).toBezier()));
		segments.add(new Line(185, 188, 185, 195));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(185, 195,
				183, 212, 182, 225).toBezier()));
		segments.add(new Line(182, 225, 188, 231));
		segments.add(new Line(188, 231, 192, 227));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(192, 227,
				193, 211, 195, 196).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(195, 196,
				207, 194, 220, 192).toBezier()));
		segments.add(new Line(220, 192, 220, 187));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(220, 187,
				208, 188, 197, 188).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(197, 188,
				198, 176, 200, 168).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(200, 168,
				217, 168, 225, 165, 237, 160, 242, 158).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(242, 158,
				242, 154, 239, 152).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(239, 152,
				229, 156, 217, 158, 203, 158).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(203, 158,
				204, 153, 204, 150, 206, 142).toBezier()));
		segments.add(new Line(206, 142, 199, 145));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(199, 145,
				199, 150, 198, 154).toBezier()));
		segments.add(new Line(198, 154, 195, 153));
		segments.add(new Line(195, 153, 195, 157));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(195, 157,
				188, 157, 178, 155).toBezier()));
		return new CurvedPolygon(segments);
	}

}
