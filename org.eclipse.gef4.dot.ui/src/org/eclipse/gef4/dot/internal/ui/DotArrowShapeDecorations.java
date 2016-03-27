/************************************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - Add support for arrowType edge decorations (bug #477980)
 *
 ***********************************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import org.eclipse.gef4.dot.internal.parser.arrowtype.AbstractArrowShape;
import org.eclipse.gef4.dot.internal.parser.arrowtype.ArrowShape;
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
	 * @param arrowSize
	 *            The size of the arrow shape decoration.
	 * 
	 * @param isGraphDirected
	 *            true if the graph is directed, false otherwise
	 *
	 * @return The default dot arrow shape decoration
	 */
	public static Shape getDefault(double arrowSize, boolean isGraphDirected) {

		Shape shape = isGraphDirected ? new Normal(arrowSize) : null;
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
	 * @param arrowSize
	 *            The size of the arrow shape decoration.
	 * 
	 * @return The dot arrow shape decoration.
	 */
	public static Shape get(ArrowType arrowType, double arrowSize) {
		Shape shape = null;

		// TODO: handle multiple-shapes
		AbstractArrowShape firstShape = arrowType.getArrowShapes().get(0);

		if (firstShape instanceof DeprecatedArrowShape) {
			switch (((DeprecatedArrowShape) firstShape).getShape()) {
			case EDIAMOND:
				// "ediamond" is deprecated, use "odiamond"
				shape = new Diamond(arrowSize);
				setOpen(shape);
				break;
			case OPEN:
				// "open" is deprecated, use "vee"
				shape = new Vee(arrowSize);
				break;
			case HALFOPEN:
				// "halfopen" is deprecated, use "lvee"
				shape = new Vee(arrowSize);
				setSide(shape, "l"); //$NON-NLS-1$
				break;
			case EMPTY:
				// "empty" is deprecated, use "onormal"
				shape = new Normal(arrowSize);
				setOpen(shape);
				break;
			case INVEMPTY:
				// "invempty" is deprecated, use "oinv"
				shape = new Inv(arrowSize);
				setOpen(shape);
				break;
			default:
				break;
			}
		} else {
			ArrowShape arrowShape = (ArrowShape) firstShape;
			shape = getPrimitiveShape(arrowShape.getShape(), arrowSize);
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

	private static Shape getPrimitiveShape(PrimitiveShape primitiveShape,
			double arrowSize) {
		switch (primitiveShape) {
		case BOX:
			return new Box(arrowSize);
		case CROW:
			return new Crow(arrowSize);
		case CURVE:
			return new Curve(arrowSize);
		case ICURVE:
			return new ICurve(arrowSize);
		case DIAMOND:
			return new Diamond(arrowSize);
		case DOT:
			return new Dot(arrowSize);
		case INV:
			return new Inv(arrowSize);
		case NONE:
			return null;
		case NORMAL:
			return new Normal(arrowSize);
		case TEE:
			return new Tee(arrowSize);
		case VEE:
			return new Vee(arrowSize);
		default:
			return null;
		}
	}

	private static class Box extends Polygon {
		private Box(double arrowSize) {
			super(0, arrowSize * size / 2, 0, -arrowSize * size / 2,
					arrowSize * size, -arrowSize * size / 2, arrowSize * size,
					arrowSize * size / 2);

		}
	}

	private static class Crow extends Polygon {
		private Crow(double arrowSize) {
			super(arrowSize * size / 2, 0, 0, -arrowSize * size / 2,
					arrowSize * size, 0, 0, arrowSize * size / 2);
		}
	}

	private static class Curve extends Arc {
		private Curve(double arrowSize) {
			super(arrowSize * size / 2, // centerX
					0, // centerY
					arrowSize * size / 2, // radiusX
					arrowSize * size / 2, // radiusY
					90, // startAngle
					180// length
			);
			setStyle("-fx-fill: white"); //$NON-NLS-1$
		}
	}

	private static class Diamond extends Polygon {
		private Diamond(double arrowSize) {
			super(0, 0, arrowSize * size / 2, -arrowSize * size / 3,
					arrowSize * size, 0, arrowSize * size / 2,
					arrowSize * size / 3);
		}
	}

	private static class Dot extends Circle {
		private Dot(double arrowSize) {
			super(0, 0, arrowSize * size / 2);
		}
	}

	private static class ICurve extends Arc {
		private ICurve(double arrowSize) {
			super(0, // centerX
					0, // centerY
					arrowSize * size / 2, // radiusX
					arrowSize * size / 2, // radiusY
					90, // startAngle
					-180// length
			);
			setStyle("-fx-fill: white"); //$NON-NLS-1$
		}
	}

	private static class Inv extends Polygon {
		private Inv(double arrowSize) {
			super(0, arrowSize * size / 3, arrowSize * size, 0, 0,
					-arrowSize * size / 3);
		}
	}

	private static class Normal extends Polygon {
		private Normal(double arrowSize) {
			super(0, 0, arrowSize * size, -arrowSize * size / 3,
					arrowSize * size, arrowSize * size / 3);
		}
	}

	private static class Tee extends Polygon {
		private Tee(double arrowSize) {
			super(0, -arrowSize * size / 2, arrowSize * size / 4,
					-arrowSize * size / 2, arrowSize * size / 4,
					arrowSize * size / 2, 0, arrowSize * size / 2);
		}
	}

	private static class Vee extends Polygon {
		private Vee(double arrowSize) {
			super(0, 0, arrowSize * size, -arrowSize * size / 2,
					2 * arrowSize * size / 3, 0, arrowSize * size,
					arrowSize * size / 2);
		}
	}

	private static double size = 10;

}
