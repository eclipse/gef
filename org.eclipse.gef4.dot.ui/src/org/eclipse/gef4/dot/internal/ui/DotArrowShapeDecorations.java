/************************************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG)   - Add support for arrowType edge decorations (bug #477980)
 *
 ***********************************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import org.eclipse.gef4.dot.internal.parser.arrowtype.ArrowShape;
import org.eclipse.gef4.dot.internal.parser.arrowtype.ArrowShapes;
import org.eclipse.gef4.dot.internal.parser.arrowtype.ArrowType;
import org.eclipse.gef4.dot.internal.parser.arrowtype.DeprecatedArrowShape;
import org.eclipse.gef4.dot.internal.parser.arrowtype.PrimitiveShape;

import javafx.collections.ObservableList;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;

public class DotArrowShapeDecorations {
	/**
	 * Returns the default dot arrow shape decoration for directed/non-directed
	 * graphs.
	 * 
	 * @param isGraphDirected
	 *            true if the graph is directed, false otherwise
	 * 
	 * @return The default dot arrow shape decoration
	 */
	public static Shape getDefault(boolean isGraphDirected) {

		Shape shape = isGraphDirected ? new Normal() : null;
		useRoundStrokeLineJoin(shape);
		return shape;
	}

	/**
	 * Returns the dot arrow shape decoration corresponding to the
	 * <i>arrowType</i> parameter.
	 * 
	 * @param arrowType
	 *            The arrow type for which the dot edge decoration should be
	 *            determined.
	 * 
	 * @return The dot arrow shape decoration.
	 */
	public static Shape get(ArrowType arrowType) {
		Shape shape = null;
		if (arrowType instanceof DeprecatedArrowShape) {
			// TODO: handle multiple-shapes
			String firstArrowShape = ((DeprecatedArrowShape) arrowType)
					.getArrowShapes().get(0);
			switch (firstArrowShape) {
			case "ediamond": //$NON-NLS-1$
				// "ediamond" is deprecated, use "odiamond"
				shape = new Diamond();
				setOpen(shape);
				break;
			case "open": //$NON-NLS-1$
				// "open" is deprecated, use "vee"
				shape = new Vee();
				break;
			case "halfopen": //$NON-NLS-1$
				// "halfopen" is deprecated, use "lvee"
				shape = new Vee();
				setSide(shape, "l"); //$NON-NLS-1$
				break;
			case "empty": //$NON-NLS-1$
				// "empty" is deprecated, use "onormal"
				shape = new Normal();
				setOpen(shape);
				break;
			case "invempty": //$NON-NLS-1$
				// "invempty" is deprecated, use "oinv"
				shape = new Inv();
				setOpen(shape);
				break;
			default:
				break;
			}
		} else {
			// TODO: handle multiple-shapes
			ArrowShape arrowShape = ((ArrowShapes) arrowType).getArrowShapes()
					.get(0);

			shape = getPrimitiveShape(arrowShape.getShape());
			if (arrowShape.isOpen()) {
				setOpen(shape);
			}

			if (arrowShape.getSide() != null) {
				setSide(shape, arrowShape.getSide());
			}
		}

		useRoundStrokeLineJoin(shape);

		return shape;
	}

	private static void setOpen(Shape shape) {
		shape.setStyle("-fx-fill: white"); //$NON-NLS-1$
	}

	private static void setSide(Shape shape, String side) {
		if (shape instanceof Polygon) {
			setSide((Polygon) shape, side);
		} else if (shape instanceof Arc) {
			setSide((Arc) shape, side);
		}
	}

	private static void setSide(Polygon polygon, String side) {
		// setting the side of a polygon based shape to left/right means to use
		// 0.0 instead of the negative/positive y coordinates
		ObservableList<Double> points = polygon.getPoints();
		for (int i = 1; i < points.size(); i += 2) {
			double yCoordinate = points.get(i);
			if (yCoordinate < 0 && side.equals("l") //$NON-NLS-1$
					|| yCoordinate > 0 && side.equals("r")) { //$NON-NLS-1$
				points.remove(i);
				points.add(i, 0.0);
			}
		}
	}

	private static void setSide(Arc arc, String side) {
		// setting the side of an arc based shape to left/right
		if (arc instanceof Curve && side.equals("l")) {//$NON-NLS-1$
			arc.setStartAngle(180);
			arc.setLength(90);
		}
		if (arc instanceof ICurve && side.equals("l")) {//$NON-NLS-1$
			arc.setStartAngle(0);
			arc.setLength(-90);
		}
		if (arc instanceof Curve && side.equals("r")) {//$NON-NLS-1$
			arc.setLength(90);
		}
		if (arc instanceof ICurve && side.equals("r")) {//$NON-NLS-1$
			arc.setLength(-90);
		}
	}

	private static void useRoundStrokeLineJoin(Shape shape) {
		if (shape != null) {
			shape.setStrokeLineJoin(StrokeLineJoin.ROUND);
		}
	}

	private static Shape getPrimitiveShape(PrimitiveShape primitiveShape) {
		switch (primitiveShape) {
		case BOX:
			return new Box();
		case CROW:
			return new Crow();
		case CURVE:
			return new Curve();
		case ICURVE:
			return new ICurve();
		case DIAMOND:
			return new Diamond();
		case DOT:
			return new Dot();
		case INV:
			return new Inv();
		case NONE:
			return null;
		case NORMAL:
			return new Normal();
		case TEE:
			return new Tee();
		case VEE:
			return new Vee();
		default:
			return null;
		}
	}

	private static class Box extends Polygon {
		private Box() {
			super(0, size / 2, 0, -size / 2, size, -size / 2, size, size / 2);

		}
	}

	private static class Crow extends Polygon {
		private Crow() {
			super(size / 2, 0, 0, -size / 2, size, 0, 0, size / 2);
		}
	}

	private static class Curve extends Arc {
		private Curve() {
			super(size / 2, // centerX
					0, // centerY
					size / 2, // radiusX
					size / 2, // radiusY
					90, // startAngle
					180// length
			);
			setStyle("-fx-fill: white"); //$NON-NLS-1$
		}
	}

	private static class Diamond extends Polygon {
		private Diamond() {
			super(0, 0, size / 2, -size / 3, size, 0, size / 2, size / 3);
		}
	}

	private static class Dot extends Circle {
		private Dot() {
			super(0, 0, size / 2);
		}
	}

	private static class ICurve extends Arc {
		private ICurve() {
			super(0, // centerX
					0, // centerY
					size / 2, // radiusX
					size / 2, // radiusY
					90, // startAngle
					-180// length
			);
			setStyle("-fx-fill: white"); //$NON-NLS-1$
		}
	}

	private static class Inv extends Polygon {
		private Inv() {
			super(0, size / 3, size, 0, 0, -size / 3);
		}
	}

	private static class Normal extends Polygon {
		private Normal() {
			super(0, 0, size, -size / 3, size, size / 3);
		}
	}

	private static class Tee extends Polygon {
		private Tee() {
			super(0, -size / 2, size / 4, -size / 2, size / 4, size / 2, 0,
					size / 2);
		}
	}

	private static class Vee extends Polygon {
		private Vee() {
			super(0, 0, size, -size / 2, 2 * size / 3, 0, size, size / 2);
		}
	}

	private static int size = 10;

}
