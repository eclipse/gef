/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.fx.utils;

import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;

/**
 * The {@link NodeUtils} class contains utility methods for working with JavaFX:
 * <ul>
 * <li>transforming {@link IGeometry}s from/to different JavaFX coordinate
 * systems ({@link #localToParent(Node, IGeometry)},
 * {@link #localToScene(Node, IGeometry)}, {@link #localToScene(Node, Point)},
 * {@link #parentToLocal(Node, IGeometry)},
 * {@link #sceneToLocal(Node, IGeometry)})</li>
 * <li>determining the actual local-to-scene or scene-to-local transform for a
 * JavaFX {@link Node} ({@link #getLocalToSceneTx(Node)},
 * {@link #getSceneToLocalTx(Node)})</li>
 * <li>perform picking of {@link Node}s at a specific position within the JavaFX
 * scene graph ({@link #getNodesAt(Node, double, double)})</li>
 * </ul>
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class NodeUtils {

	/**
	 * Returns <code>true</code> if the given {@link Affine}s are equal.
	 * Otherwise returns <code>false</code>.
	 *
	 * @param a1
	 *            The first operand.
	 * @param a2
	 *            The second operand.
	 * @return <code>true</code> if the given {@link Affine}s are equal,
	 *         otherwise <code>false</code>.
	 */
	public static boolean equals(Affine a1, Affine a2) {
		// Affine does not properly implement equals, so we have to implement
		// that here
		return a1.getMxx() == a2.getMxx() && a1.getMxy() == a2.getMxy()
				&& a1.getMxz() == a2.getMxz() && a1.getMyx() == a2.getMyx()
				&& a1.getMyy() == a2.getMyy() && a1.getMyz() == a2.getMyz()
				&& a1.getMzx() == a2.getMzx() && a1.getMzy() == a2.getMzy()
				&& a1.getMzz() == a2.getMzz() && a1.getTx() == a2.getTx()
				&& a1.getTy() == a2.getTy() && a1.getTz() == a2.getTz();
	}

	/**
	 * Returns an {@link IGeometry} that corresponds whose outline represents
	 * the geometric outline of the given {@link Node}, excluding its stroke.
	 * <p>
	 * The {@link IGeometry} is specified within the local coordinate system of
	 * the given {@link Node}.
	 * <p>
	 * The following {@link Node}s are supported:
	 * <ul>
	 * <li>{@link Connection}
	 * <li>{@link GeometryNode}
	 * <li>{@link Arc}
	 * <li>{@link Circle}
	 * <li>{@link CubicCurve}
	 * <li>{@link Ellipse}
	 * <li>{@link Line}
	 * <li>{@link Path}
	 * <li>{@link Polygon}
	 * <li>{@link Polyline}
	 * <li>{@link QuadCurve}
	 * <li>{@link Rectangle}
	 * </ul>
	 *
	 * @param visual
	 *            The {@link Node} of which the geometric outline is returned.
	 * @return An {@link IGeometry} that corresponds to the geometric outline of
	 *         the given {@link Node}.
	 * @throws IllegalArgumentException
	 *             if the given {@link Node} is not supported.
	 */
	public static IGeometry getGeometricOutline(Node visual) {
		if (visual instanceof Connection) {
			Node curveNode = ((Connection) visual).getCurve();
			return localToParent(curveNode, getGeometricOutline(curveNode));
		} else if (visual instanceof GeometryNode) {
			// XXX: The geometry's position is specified relative to the
			// GeometryNode's layout bounds (which are fixed as (0, 0, width,
			// height) and includes the layoutX, layoutY (which we have to
			// compensate here)
			GeometryNode<?> geometryNode = (GeometryNode<?>) visual;
			IGeometry geometry = geometryNode.getGeometry();
			if (geometry != null) {
				return geometry.getTransformed(new AffineTransform().translate(
						-geometryNode.getLayoutX(),
						-geometryNode.getLayoutY()));
			} else {
				// if the geometry node has no geometry (yet), return an empty
				// geometry
				return new Rectangle();
			}
		} else if (visual instanceof Shape && !(visual instanceof Text)
				&& !(visual instanceof SVGPath)) {
			return Shape2Geometry.toGeometry((Shape) visual);
		} else {
			throw new IllegalArgumentException(
					"Cannot determine geometric outline for the given visual <"
							+ visual + ">.");
		}
	}

	/**
	 * Returns an {@link AffineTransform} which represents the transformation
	 * matrix to transform geometries from the local coordinate system of the
	 * given {@link Node} into the coordinate system of the {@link Scene}.
	 * <p>
	 * JavaFX {@link Node} provides a (lazily computed) local-to-scene-transform
	 * property which we could access to get that transform. Unfortunately, this
	 * property is not updated correctly, i.e. its value can differ from the
	 * actual local-to-scene-transform. Therefore, we compute the
	 * local-to-scene-transform for the given node here by concatenating the
	 * local-to-parent-transforms along the hierarchy.
	 * <p>
	 * Note that in situations where you do not need the actual transform, but
	 * instead perform a transformation, you can use the
	 * {@link Node#localToScene(Point2D) Node#localToScene(...)} methods on the
	 * <i>node</i> directly, because it does not make use of the
	 * local-to-scene-transform property, but uses localToParent() internally.
	 *
	 * @param node
	 *            The JavaFX {@link Node} for which the local-to-scene
	 *            transformation matrix is to be computed.
	 * @return An {@link AffineTransform} representing the local-to-scene
	 *         transformation matrix for the given {@link Node}.
	 */
	public static AffineTransform getLocalToSceneTx(Node node) {
		AffineTransform tx = FX2Geometry
				.toAffineTransform(node.getLocalToParentTransform());
		Node tmp = node;
		while (tmp.getParent() != null) {
			tmp = tmp.getParent();
			tx = FX2Geometry.toAffineTransform(tmp.getLocalToParentTransform())
					.concatenate(tx);
		}
		return tx;
	}

	/**
	 * Computes the nearest common ancestor for two given nodes.
	 *
	 * @param source
	 *            The first node.
	 * @param target
	 *            The second node.
	 * @return The nearest common ancestor in the scene graph.
	 */
	public static Node getNearestCommonAncestor(Node source, Node target) {
		if (source == target) {
			return source;
		}

		Set<Node> parents = new HashSet<>();
		Node m = source;
		Node n = target;
		while (m != null || n != null) {
			if (m != null) {
				if (parents.contains(m)) {
					return m;
				}
				parents.add(m);
				if (n != null && parents.contains(n)) {
					return n;
				}
				m = m.getParent();
			}
			if (n != null) {
				if (parents.contains(n)) {
					return n;
				}
				parents.add(n);
				if (m != null && parents.contains(m)) {
					return m;
				}
				n = n.getParent();
			}
		}

		// could not find a common parent
		return null;
	}

	/**
	 * Performs picking on the scene graph beginning at the specified root node
	 * and processing its transitive children.
	 *
	 * @param sceneX
	 *            The x-coordinate of the position to pick nodes at, interpreted
	 *            in scene coordinate space.
	 * @param sceneY
	 *            The y-coordinate of the position to pick nodes at, interpreted
	 *            in scene coordinate space.
	 * @param root
	 *            The root node at which to start with picking
	 * @return A list of {@link Node}s which contain the the given coordinate.
	 */
	public static List<Node> getNodesAt(Node root, double sceneX,
			double sceneY) {
		List<Node> picked = new ArrayList<>();

		// start with given root node
		List<Node> nodes = new ArrayList<>();
		nodes.add(root);

		while (!nodes.isEmpty()) {
			Node current = nodes.remove(0);
			// transform to local coordinates
			Point2D pLocal = current.sceneToLocal(sceneX, sceneY);
			// check if bounds contains (necessary to find children in mouse
			// transparent regions)
			if (!current.isMouseTransparent()
					&& current.getBoundsInLocal().contains(pLocal)) {
				// check precisely
				if (current.contains(pLocal)) {
					picked.add(0, current);
				}
				// test all children, too
				if (current instanceof Parent) {
					nodes.addAll(0,
							((Parent) current).getChildrenUnmodifiable());
				}
			}
		}
		return picked;
	}

	/**
	 * Creates a copy of the given {@link IGeometry} and resizes it to fit the
	 * (corrected) layout-bounds (see {@link #getShapeBounds(Node)}) of the
	 * given {@link Node}. The new, resized {@link IGeometry} is returned.
	 *
	 * @param visual
	 *            The visual of which the layout-bounds are used as the basis
	 *            for resizing the given {@link IGeometry}.
	 * @param geometry
	 *            The {@link IGeometry} that is resized to fit the layout-bounds
	 *            of the given {@link Node}.
	 * @return The new, resized {@link IGeometry}.
	 */
	public static IGeometry getResizedToShapeBounds(Node visual,
			IGeometry geometry) {
		Rectangle geometricBounds = geometry.getBounds();
		Rectangle shapeBounds = NodeUtils.getShapeBounds(visual);
		double dw = shapeBounds.getWidth() - geometricBounds.getWidth();
		double dh = shapeBounds.getHeight() - geometricBounds.getHeight();

		// geometric bounds match shape bounds, so nothing to do
		if (dw == 0 && dh == 0) {
			return geometry;
		}

		GeometryNode<IGeometry> geometryNode = new GeometryNode<>(geometry);
		geometryNode.relocateGeometry(shapeBounds.getX(), shapeBounds.getY());
		geometryNode.resizeGeometry(shapeBounds.getWidth(),
				shapeBounds.getHeight());
		return geometryNode.getGeometry();
	}

	/**
	 * Returns the scene-to-local transform for the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which the scene-to-local transform is
	 *            returned.
	 * @return The scene-to-local transform for the given {@link Node}.
	 */
	public static AffineTransform getSceneToLocalTx(Node node) {
		try {
			// XXX: We make use of getLocalToSceneTx(Node) here to
			// compensate that the Transform provided by FX is updated lazily.
			// See getLocalToSceneTx(Node) for details.
			return getLocalToSceneTx(node).invert();
		} catch (NoninvertibleTransformException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Returns the layout-bounds of the given {@link Node}, which might be
	 * adjusted to ensure that it exactly fits the visualization.
	 *
	 * @param node
	 *            The {@link Node} to retrieve the (corrected) layout-bounds of.
	 * @return A {@link Rectangle} representing the (corrected) layout-bounds.
	 */
	public static Rectangle getShapeBounds(Node node) {
		Bounds layoutBounds = node.getLayoutBounds();
		// Polygons don't paint exactly to their layout bounds but remain 0.5
		// pixels short in case they have a stroke and stroke type is CENTERED
		// or OUTSIDE. We compensate this there.
		double offset = 0;
		if (node instanceof Polygon && ((Polygon) node).getStroke() != null
				&& ((Polygon) node).getStrokeType() != StrokeType.INSIDE) {
			offset = 0.5;
		}
		return FX2Geometry.toRectangle(layoutBounds).shrink(offset, offset,
				offset, offset);
	}

	/**
	 * Creates a geometry whose outline represents the outline of the given
	 * {@link Node}, including its stroke.
	 * <p>
	 * The {@link IGeometry} is specified within the local coordinate system of
	 * the given {@link Node}.
	 *
	 * @param node
	 *            The node to infer an outline geometry for.
	 * @return An {@link IGeometry} from which the outline may be retrieved.
	 */
	public static IGeometry getShapeOutline(Node node) {
		try {
			IGeometry geometry = NodeUtils.getGeometricOutline(node);
			if (geometry != null) {
				// resize to layout-bounds to include stroke if not a curve
				return NodeUtils.getResizedToShapeBounds(node, geometry);
			}
			return FX2Geometry.toRectangle(node.getLayoutBounds());
		} catch (IllegalArgumentException e) {
			// fall back to layout-bounds
			return FX2Geometry.toRectangle(node.getLayoutBounds());
		}
	}

	/**
	 * Transforms the given {@link IGeometry} from the local coordinate system
	 * of the given {@link Node} into the coordinate system of the {@link Node}
	 * 's parent.
	 *
	 * @param n
	 *            The {@link Node} used to determine the transformation matrix.
	 * @param g
	 *            The {@link IGeometry} to transform.
	 * @return The new, transformed {@link IGeometry}.
	 */
	public static IGeometry localToParent(Node n, IGeometry g) {
		AffineTransform localToParentTx = FX2Geometry
				.toAffineTransform(n.getLocalToParentTransform());
		return g.getTransformed(localToParentTx);
	}

	/**
	 * Transforms the given {@link Point} from the local coordinate system of
	 * the given {@link Node} into the coordinate system of the {@link Node} 's
	 * parent.
	 *
	 * @param n
	 *            The {@link Node} used to determine the transformation matrix.
	 * @param p
	 *            The {@link Point} to transform.
	 * @return The new, transformed {@link Point}.
	 */
	public static Point localToParent(Node n, Point p) {
		AffineTransform localToParentTx = FX2Geometry
				.toAffineTransform(n.getLocalToParentTransform());
		return localToParentTx.getTransformed(p);
	}

	/**
	 * Transforms the given {@link IGeometry} from the local coordinate system
	 * of the given {@link Node} into scene coordinates.
	 *
	 * @param n
	 *            The {@link Node} used to determine the transformation matrix.
	 * @param g
	 *            The {@link IGeometry} to transform.
	 * @return The new, transformed {@link IGeometry}.
	 */
	public static IGeometry localToScene(Node n, IGeometry g) {
		AffineTransform localToSceneTx = getLocalToSceneTx(n);
		return g.getTransformed(localToSceneTx);
	}

	/**
	 * Transforms the given {@link Point} from the local coordinate system of
	 * the given {@link Node} into scene coordinates.
	 *
	 * @param n
	 *            The {@link Node} used to determine the transformation matrix.
	 * @param p
	 *            The {@link IGeometry} to transform.
	 * @return The new, transformed {@link Point}.
	 */
	public static Point localToScene(Node n, Point p) {
		AffineTransform localToSceneTx = getLocalToSceneTx(n);
		return localToSceneTx.getTransformed(p);
	}

	/**
	 * Transforms the given {@link IGeometry} from the parent coordinate system
	 * of the given {@link Node} into the local coordinate system of the
	 * {@link Node}.
	 *
	 * @param n
	 *            The {@link Node} used to determine the transformation matrix.
	 * @param g
	 *            The {@link IGeometry} to transform.
	 * @return The new, transformed {@link IGeometry}.
	 */
	public static IGeometry parentToLocal(Node n, IGeometry g) {
		// retrieve transform from scene to target parent, by inverting target
		// parent to scene
		AffineTransform localToParentTx = FX2Geometry
				.toAffineTransform(n.getLocalToParentTransform());
		AffineTransform parentToLocalTx = null;
		try {
			parentToLocalTx = localToParentTx.getCopy().invert();
		} catch (NoninvertibleTransformException e) {
			// TODO: How do we recover from this?!
			throw new IllegalStateException(e);
		}
		return g.getTransformed(parentToLocalTx);
	}

	/**
	 * Transforms the given {@link Point} from the parent coordinate system of
	 * the given {@link Node} into the local coordinate system of the
	 * {@link Node}.
	 *
	 * @param n
	 *            The {@link Node} used to determine the transformation matrix.
	 * @param p
	 *            The {@link Point} to transform.
	 * @return The new, transformed {@link Point}.
	 */
	public static Point parentToLocal(Node n, Point p) {
		// retrieve transform from scene to target parent, by inverting target
		// parent to scene
		AffineTransform localToParentTx = FX2Geometry
				.toAffineTransform(n.getLocalToParentTransform());
		AffineTransform parentToLocalTx = null;
		try {
			parentToLocalTx = localToParentTx.getCopy().invert();
		} catch (NoninvertibleTransformException e) {
			// TODO: How do we recover from this?!
			throw new IllegalStateException(e);
		}
		return parentToLocalTx.getTransformed(p);
	}

	/**
	 * Transforms the given {@link IGeometry} from scene coordinates to the
	 * local coordinate system of the given {@link Node}.
	 *
	 * @param n
	 *            The {@link Node} used to determine the transformation matrix.
	 * @param g
	 *            The {@link IGeometry} to transform.
	 * @return The new, transformed {@link IGeometry}.
	 */
	public static IGeometry sceneToLocal(Node n, IGeometry g) {
		// retrieve transform from scene to target parent, by inverting target
		// parent to scene
		AffineTransform sceneToLocalTx = getSceneToLocalTx(n);
		return g.getTransformed(sceneToLocalTx);
	}

	/**
	 * Transforms the given {@link Point} from scene coordinates to the local
	 * coordinate system of the given {@link Node}.
	 *
	 * @param n
	 *            The {@link Node} used to determine the transformation matrix.
	 * @param p
	 *            The {@link Point} to transform.
	 * @return The new, transformed {@link Point}.
	 */
	public static Point sceneToLocal(Node n, Point p) {
		// retrieve transform from scene to target parent, by inverting target
		// parent to scene
		AffineTransform sceneToLocalTx = getSceneToLocalTx(n);
		return sceneToLocalTx.getTransformed(p);
	}

	/**
	 * Assigns the transformation values of the <i>src</i> {@link Affine} to the
	 * <i>dst</i> {@link Affine}.
	 *
	 * @param dst
	 *            The destination {@link Affine}.
	 * @param src
	 *            The source {@link Affine}.
	 * @return The destination {@link Affine} for convenience.
	 */
	public static Affine setAffine(Affine dst, Affine src) {
		dst.setMxx(src.getMxx());
		dst.setMxy(src.getMxy());
		dst.setMxz(src.getMxz());
		dst.setMyx(src.getMyx());
		dst.setMyy(src.getMyy());
		dst.setMyz(src.getMyz());
		dst.setMzx(src.getMzx());
		dst.setMzy(src.getMzy());
		dst.setMzz(src.getMzz());
		dst.setTx(src.getTx());
		dst.setTy(src.getTy());
		dst.setTz(src.getTz());
		return dst;
	}
}
