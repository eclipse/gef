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
package org.eclipse.gef4.fx.nodes;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.geom.NoninvertibleTransformException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;

public class FXUtils {

	/**
	 * Forces the JavaFX runtime to update the mouse cursor. This is useful when
	 * you want to change the mouse cursor independently of mouse movement.
	 */
	public static void forceCursorUpdate(Scene scene) {
		try {
			Field mouseHandlerField = scene.getClass().getDeclaredField(
					"mouseHandler");
			mouseHandlerField.setAccessible(true);
			Object mouseHandler = mouseHandlerField.get(scene);
			Class<?> mouseHandlerClass = Class
					.forName("javafx.scene.Scene$MouseHandler");
			Method updateCursorMethod = mouseHandlerClass.getDeclaredMethod(
					"updateCursor", Cursor.class);
			updateCursorMethod.setAccessible(true);
			updateCursorMethod.invoke(mouseHandler, scene.getCursor());
			Method updateCursorFrameMethod = mouseHandlerClass
					.getDeclaredMethod("updateCursorFrame");
			updateCursorFrameMethod.setAccessible(true);
			updateCursorFrameMethod.invoke(mouseHandler);
		} catch (Exception x) {
			throw new IllegalStateException(x);
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
		AffineTransform tx = JavaFX2Geometry.toAffineTransform(node
				.getLocalToParentTransform());
		Node tmp = node;
		while (tmp.getParent() != null) {
			tmp = tmp.getParent();
			tx = JavaFX2Geometry.toAffineTransform(
					tmp.getLocalToParentTransform()).concatenate(tx);
		}
		return tx;
	}

	/**
	 * Performs picking on the scene graph beginning at the specified root node.
	 *
	 * @param sceneX
	 * @param sceneY
	 * @param root
	 * @return A list of {@link Node}s which contain the the given coordinate.
	 */
	public static List<Node> getNodesAt(Node root, double sceneX, double sceneY) {
		List<Node> picked = new ArrayList<Node>();

		// start with given root node
		List<Node> nodes = new ArrayList<Node>();
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
	 * Returns the current pointer location.
	 *
	 * @return The current pointer location.
	 */
	public static Point getPointerLocation() {
		// find pointer location (OS specific)
		String os = System.getProperty("os.name");
		if (os.startsWith("Mac OS X")) {
			// use special glass robot for MacOS
			com.sun.glass.ui.Robot robot = com.sun.glass.ui.Application
					.GetApplication().createRobot();
			return new Point(robot.getMouseX(), robot.getMouseY());
		} else {
			PointerInfo pi = MouseInfo.getPointerInfo();
			java.awt.Point mp = pi.getLocation();
			return new Point(mp.x, mp.y);
		}
	}

	public static AffineTransform getSceneToLocalTx(Node node) {
		try {
			// IMPORTANT: we make use of getLocalToSceneTx(Node) here to
			// compensate that the Transform provided by FX is updated lazily.
			// See getLocalToSceneTx(Node) for details.
			return getLocalToSceneTx(node).invert();
		} catch (NoninvertibleTransformException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static IGeometry localToParent(Node n, IGeometry g) {
		AffineTransform localToParentTx = JavaFX2Geometry.toAffineTransform(n
				.getLocalToParentTransform());
		return g.getTransformed(localToParentTx);
	}

	public static IGeometry localToScene(Node n, IGeometry g) {
		AffineTransform localToSceneTx = getLocalToSceneTx(n);
		return g.getTransformed(localToSceneTx);
	}

	public static Point localToScene(Node n, Point p) {
		AffineTransform localToSceneTx = getLocalToSceneTx(n);
		return localToSceneTx.getTransformed(p);
	}

	public static IGeometry parentToLocal(Node n, IGeometry g) {
		// retrieve transform from scene to target parent, by inverting target
		// parent to scene
		AffineTransform localToParentTx = JavaFX2Geometry.toAffineTransform(n
				.getLocalToParentTransform());
		AffineTransform parentToLocalTx = null;
		try {
			parentToLocalTx = localToParentTx.getCopy().invert();
		} catch (NoninvertibleTransformException e) {
			// TODO: How do we recover from this?!
			throw new IllegalStateException(e);
		}
		return g.getTransformed(parentToLocalTx);
	}

	public static IGeometry sceneToLocal(Node n, IGeometry g) {
		// retrieve transform from scene to target parent, by inverting target
		// parent to scene
		AffineTransform sceneToLocalTx = getSceneToLocalTx(n);
		return g.getTransformed(sceneToLocalTx);
	}

}
