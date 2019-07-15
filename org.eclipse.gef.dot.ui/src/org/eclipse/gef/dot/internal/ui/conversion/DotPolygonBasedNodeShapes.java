/************************************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #441352)
 *
 ***********************************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import org.eclipse.gef.dot.internal.language.shape.PolygonBasedNodeShape;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.RoundedRectangle;

import javafx.scene.Node;

class DotPolygonBasedNodeShapes {

	/**
	 * Returns the JavaFX node corresponding to the <i>polygonShape</i>
	 * parameter.
	 * 
	 * @param polygonShape
	 *            The polygon shape for which the JavaFX node should be
	 *            determined.
	 * 
	 * @return The JavaFX node.
	 */
	static Node get(PolygonBasedNodeShape polygonShape) {
		IGeometry geometry = null;
		// (0,0) (100,0)
		// (0,100) (100,100)
		switch (polygonShape) {
		case BOX:
		case RECT:
		case RECTANGLE:
		case SQUARE:
			geometry = new Rectangle();
			break;
		case CDS:
			geometry = new Polygon(0, 100, 0, 0, 70, 0, 100, 50, 70, 100);
			break;
		case CIRCLE:
		case DOUBLECIRCLE:
		case ELLIPSE:
		case OVAL:
		case POINT:
			geometry = new Ellipse(new Rectangle());
			break;
		case DIAMOND:
			geometry = new Polygon(0, 50, 50, 0, 100, 50, 50, 100);
			break;
		case FOLDER:
			geometry = new Polygon(0, 100, 0, 10, 50, 10, 55, 0, 95, 0, 100, 10,
					100, 100);
			break;
		case HOUSE:
			geometry = new Polygon(0, 100, 0, 40, 50, 0, 100, 40, 100, 100);
			break;
		case INVHOUSE:
			geometry = new Polygon(0, 0, 100, 0, 100, 60, 50, 100, 0, 60);
			break;
		case INVTRAPEZIUM:
			geometry = new Polygon(0, 0, 100, 0, 75, 100, 25, 100);
			break;
		case INVTRIANGLE:
			geometry = new Polygon(0, 10, 100, 10, 50, 100);
			break;
		case HEXAGON:
			geometry = new Polygon(25, 100, 0, 50, 25, 0, 75, 0, 100, 50, 75,
					100);
			break;
		case LARROW:
			geometry = new Polygon(0, 50, 40, 0, 40, 15, 100, 15, 100, 85, 40,
					85, 40, 100);
			break;
		case LPROMOTER:
			geometry = new Polygon(0, 50, 40, 0, 40, 15, 100, 15, 100, 100, 70,
					100, 70, 85, 40, 85, 40, 100);
			break;
		case NONE:
		case PLAINTEXT:
			return new DotNoneShape();
		case DOUBLEOCTAGON:
		case OCTAGON:
			geometry = new Polygon(0, 70, 0, 30, 30, 0, 70, 0, 100, 30, 100, 70,
					70, 100, 30, 100);
			break;
		case PARALLELOGRAM:
			geometry = new Polygon(0, 100, 25, 0, 100, 0, 75, 100);
			break;
		case PENTAGON:
			geometry = new Polygon(25, 100, 0, 40, 50, 0, 100, 40, 75, 100);
			break;
		case RARROW:
			geometry = new Polygon(0, 85, 0, 15, 60, 15, 60, 0, 100, 50, 60,
					100, 60, 85);
			break;
		case RPROMOTER:
			geometry = new Polygon(0, 100, 0, 15, 60, 15, 60, 0, 100, 50, 60,
					100, 60, 85, 30, 85, 30, 100);
			break;
		case SEPTAGON:
			geometry = new Polygon(0, 60, 15, 15, 50, 0, 85, 15, 100, 60, 75,
					100, 25, 100);
			break;
		case STAR:
			geometry = new Polygon(15, 100, 30, 60, 0, 40, 40, 40, 50, 0, 60,
					40, 100, 40, 70, 60, 85, 100, 50, 75);
			break;
		case TRAPEZIUM:
			geometry = new Polygon(0, 100, 25, 0, 75, 0, 100, 100);
			break;
		case TRIANGLE:
			geometry = new Polygon(0, 50, 50, 0, 100, 50);
			break;
		case ASSEMBLY:
		case BOX3D:
		case COMPONENT:
		case CYLINDER:
		case EGG:
		case FIVEPOVERHANG:
		case INSULATOR:
		case MCIRCLE:
		case MDIAMOND:
		case MSQUARE:
		case NOTE:
		case NOVERHANG:
		case PLAIN:
		case POLYGON:
		case PRIMERSITE:
		case PROMOTER:
		case PROTEASESITE:
		case PROTEINSTAB:
		case RESTRICTIONSITE:
		case RIBOSITE:
		case RNASTAB:
		case SIGNATURE:
		case TAB:
		case TERMINATOR:
		case THREEPOVERHANG:
		case TRIPLEOCTAGON:
		case UNDERLINE:
		case UTR:
			// TODO: handle the polygon shapes
			return null;
		}
		return new GeometryNode<>(geometry);
	}

	/**
	 * Returns an inner JavaFX node corresponding to the <i>polygonShape</i>
	 * parameter.
	 * 
	 * @param polygonShape
	 *            The polygon shape for which the inner JavaFX node should be
	 *            determined.
	 * 
	 * @return The inner JavaFX node.
	 */
	static Node getInner(PolygonBasedNodeShape polygonShape) {
		IGeometry geometry = null;
		// (0,0) (100,0)
		// (0,100) (100,100)
		switch (polygonShape) {
		case DOUBLECIRCLE:
			geometry = new Ellipse(0, 0, 100, 100);
			break;
		case DOUBLEOCTAGON:
			geometry = new Polygon(0, 70, 0, 30, 30, 0, 70, 0, 100, 30, 100, 70,
					70, 100, 30, 100);
			break;
		}

		return geometry != null ? new GeometryNode<>(geometry) : null;
	}

	/**
	 * Returns the distance that should be preserved between the outer and inner
	 * shapes.
	 * 
	 * @param polygonShape
	 *            The {@link PolygonBasedNodeShape} for which to retrieve the
	 *            inner distance.
	 * @return the distance that should be preserved between the outer and inner
	 *         shapes.
	 */
	static double getInnerDistance(PolygonBasedNodeShape polygonShape) {
		switch (polygonShape) {
		case DOUBLECIRCLE:
		case DOUBLEOCTAGON:
			return 5d;
		}
		return 0d;
	}

	/**
	 * Returns the 'rounded' JavaFX node corresponding to the
	 * <i>polygonShape</i> parameter and the style=rounded dot parameter.
	 * 
	 * @param polygonShape
	 *            The polygon shape for which the 'rounded' JavaFX node should
	 *            be determined.
	 * 
	 * @return The JavaFX node.
	 */
	static Node getRoundedStyled(PolygonBasedNodeShape polygonShape) {
		IGeometry geometry = null;
		switch (polygonShape) {
		case BOX:
		case RECT:
		case RECTANGLE:
		case SQUARE:
			geometry = new RoundedRectangle(new Rectangle(), 25, 25);
			break;
		default:
			return get(polygonShape);
		}
		return new GeometryNode<>(geometry);
	}
}
