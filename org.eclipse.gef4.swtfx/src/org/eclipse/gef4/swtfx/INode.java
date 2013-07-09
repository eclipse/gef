/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swtfx;

import java.util.List;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.event.Event;
import org.eclipse.gef4.swtfx.event.EventType;
import org.eclipse.gef4.swtfx.event.IEventDispatcher;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.IEventTarget;
import org.eclipse.gef4.swtfx.event.TraverseEvent;

/**
 * The {@link INode} interface is a key abstraction of the SWT FX component.
 * There is an SWT Widget implementation called {@link Group} and lightweight
 * implementations called {@link IFigure figures}.
 * 
 * @author mwienand
 * 
 */
public interface INode extends IEventTarget {

	/**
	 * You can set the minimum, maximum, or preferred size of this {@link INode}
	 * (via {@link #setMinHeight(double)}, {@link #setMinWidth(double)}, ...).
	 * To indicate that one of those values should be computed (using one of the
	 * {@link #computeMaxHeight(double)}, ... methods) you can set the
	 * respective property to USE_COMPUTED_SIZE.
	 */
	public static final double USE_COMPUTED_SIZE = -1D;

	/**
	 * You can set the minimum, maximum, or preferred size of this {@link INode}
	 * (via {@link #setMinHeight(double)}, {@link #setMinWidth(double)}, ...).
	 * To indicate that one of min width, min height, max width, or max height
	 * equals the preferred size, you can set the respective value to
	 * USE_PREF_SIZE.
	 */
	public static final double USE_PREF_SIZE = -1D / 0D;

	/**
	 * Transforms the first passed-in absolute {@link Point} to the local
	 * coordinate system of this {@link INode}. The second passed-in
	 * {@link Point} is set to the transformed local coordinates, and therefore
	 * may not be <code>null</code>.
	 * 
	 * @param absoluteIn
	 * @param localOut
	 */
	public void absoluteToLocal(Point absoluteIn, Point localOut);

	/**
	 * Adds an event filter for the specified {@link EventType} to the list of
	 * event filters managed by this {@link INode}. An event filter is called
	 * during the "Capturing" phase of event processing.
	 * 
	 * @param type
	 *            the {@link EventType} for which the filter will be called
	 * @param filter
	 *            the {@link IEventHandler} which is called on events of the
	 *            specified type
	 */
	public <T extends Event> void addEventFilter(EventType<T> type,
			IEventHandler<T> filter);

	/**
	 * Adds an event handler for the specified {@link EventType} to the list of
	 * event handlers managed by this {@link INode}. An event handler is called
	 * during the "Bubbling" phase of event processing.
	 * 
	 * @param type
	 *            the {@link EventType} for which the handler will be called
	 * @param filter
	 *            the {@link IEventHandler} which is called on events of the
	 *            specified type
	 */
	public <T extends Event> void addEventHandler(EventType<T> type,
			IEventHandler<T> handler);

	/**
	 * Computes the node's maximum height in dependence of the given width.
	 * 
	 * @param width
	 * @return the node's maximum height in dependence of the given width
	 */
	public double computeMaxHeight(double width);

	/**
	 * Computes the node's maximum width in dependence of the given height.
	 * 
	 * @param width
	 * @return the node's maximum width in dependence of the given height
	 */
	public double computeMaxWidth(double height);

	/**
	 * Computes the node's minimum height in dependence of the given width.
	 * 
	 * @param width
	 * @return the node's minimum height in dependence of the given width
	 */
	public double computeMinHeight(double width);

	/**
	 * Computes the node's minimum width in dependence of the given height.
	 * 
	 * @param width
	 * @return the node's minimum width in dependence of the given height
	 */
	public double computeMinWidth(double height);

	/**
	 * Computes the node's preferred height in dependence of the given width.
	 * 
	 * @param width
	 * @return the node's preferred height in dependence of the given width
	 */
	public double computePrefHeight(double width);

	/**
	 * Computes the node's preferred width in dependence of the given height.
	 * 
	 * @param width
	 * @return the node's preferred width in dependence of the given height
	 */
	public double computePrefWidth(double height);

	// TODO: comment-in
	// /**
	// * Checks if the given {@link Point} is contained by this {@link INode}.
	// The
	// * point is interpreted as local to the coordinate system of this node.
	// *
	// * @param local
	// * @return <code>true</code> if the given {@link Point} is contained by
	// this
	// * {@link INode}, otherwise <code>false</code>
	// */
	// boolean contains(Point local);

	/**
	 * Checks if the given location is contained by this {@link INode}. The
	 * location is interpreted in the local coordinate system of the node.
	 * 
	 * @param localX
	 *            x coordinate in the local coordinate system of this node
	 * @param localY
	 *            y coordinate in the local coordinate system of this node
	 * @return <code>true</code> if the given location is contained by this
	 *         {@link INode}, otherwise <code>false</code>
	 */
	boolean contains(double localX, double localY);

	/**
	 * Returns the physical bounds of this {@link INode} in its local coordinate
	 * system, taking into account the layout-bounds and the clip of this node.
	 * 
	 * @return the physcial bounds of this node in its local coordinate system
	 */
	Rectangle getBoundsInLocal();

	/**
	 * Returns the physical bounds of this {@link INode} in the coordinate
	 * system of its parent, taking into account the bounds-in-local and the
	 * transformations specified for this node.
	 * 
	 * @return the physical bounds of this node in the coordinate system of its
	 *         parent
	 */
	Rectangle getBoundsInParent();

	/**
	 * Returns the content-bias of this {@link INode}.
	 * 
	 * @return the content-bias of this {@link INode}
	 */
	public Orientation getContentBias();

	/**
	 * Returns the {@link IEventDispatcher} used to dispatch events for this
	 * {@link INode}.
	 * 
	 * @return the {@link IEventDispatcher} used to dispatch events for this
	 *         {@link INode}
	 */
	public IEventDispatcher getEventDispatcher();

	/**
	 * Returns the logical bounds of this {@link INode}. The logical bounds are
	 * local to the coordinate system of this node. In most cases, they reflect
	 * the bounds of the geometric shape of the node. They do not take into
	 * account any transformations or clipping, but usually contain the stroke.
	 * 
	 * @return the logical bounds of this IFigure
	 */
	Rectangle getLayoutBounds();

	/**
	 * Returns the layout-x coordinate associated with this {@link INode}. The
	 * layout-x coordinate specifies a translation along the x-axis which is
	 * used to position the node according to some layout.
	 * 
	 * @return the layout-x coordinate of this {@link INode}
	 */
	public double getLayoutX();

	/**
	 * Returns the layout-y coordinate associated with this {@link INode}. The
	 * layout-y coordinate specifies a translation along the y-axis which is
	 * used to position the node according to some layout.
	 * 
	 * @return the layout-y coordinate of this {@link INode}
	 */
	public double getLayoutY();

	/**
	 * Returns an {@link AffineTransform} which will transform coordinates from
	 * the local coordinate system of this {@link INode} to the coordinate
	 * system of the screen.
	 * 
	 * @return an {@link AffineTransform} which will transform coordinates from
	 *         the local coordinate system of this {@link INode} to the
	 *         coordinate system of the screen.
	 */
	AffineTransform getLocalToAbsoluteTransform();

	/**
	 * Returns an {@link AffineTransform} which will transform coordinates from
	 * the local coordinate system of this {@link INode} to the coordinate
	 * system of its parent.
	 * 
	 * @return a {@link AffineTransform} which will transform coordinates from
	 *         the local coordinate system of this {@link INode} to the
	 *         coordinate system of its parent
	 */
	AffineTransform getLocalToParentTransform();

	public double getMaxHeight();

	public double getMaxWidth();

	public double getMinHeight();

	public double getMinWidth();

	/**
	 * Returns the {@link IParent parent node} or <code>null</code> if this is
	 * the root of the hierarchy or if the parent is an SWT widget.
	 * 
	 * @return the parent or <code>null</code> if this is the root of the
	 *         hierarchy
	 */
	public IParent getParentNode();

	/**
	 * Returns the pivot {@link Point} which is the anchor for transformations
	 * involving translate-x, translate-y, layout-x, layout-y, scale-x, scale-y,
	 * shear-x, shear-y, and rotate.
	 * 
	 * @return
	 */
	public Point getPivot();

	/**
	 * Returns the preferred size of this {@link INode}, i.e. the size at which
	 * the {@link INode} is displayed best.
	 * 
	 * @return the preferred size of this {@link INode}
	 */
	public Dimension getPreferredSize();

	public double getPrefHeight();

	public double getPrefWidth();

	/**
	 * Returns the rotation {@link Angle} associated with this {@link INode}.
	 * 
	 * @return the rotation {@link Angle} associated with this {@link INode}
	 */
	public Angle getRotationAngle();

	/**
	 * Returns the scale-x factor associated with this {@link INode}.
	 * 
	 * @return the scale-x factor associated with this {@link INode}
	 */
	public double getScaleX();

	/**
	 * Returns the scale-y factor associated with this {@link INode}.
	 * 
	 * @return the scale-y factor associated with this {@link INode}
	 */
	public double getScaleY();

	/**
	 * Returns a {@link List} of {@link AffineTransform}s which you can modify
	 * at will. These transformations are multiplied with each other in the
	 * prescribed order. The additional transformation attributes such as
	 * translateX, translateY, etc. are applied to an identity transformation
	 * matrix first.
	 * 
	 * @return the {@link List} of {@link AffineTransform}s associated with this
	 *         {@link INode}
	 */
	public List<AffineTransform> getTransforms();

	/**
	 * Returns the translate-x coordinate associated with this {@link INode}.
	 * 
	 * @return the translate-x coordinate associated with this {@link INode}
	 */
	public double getTranslateX();

	/**
	 * Returns the translate-y coordinate associated with this {@link INode}.
	 * 
	 * @return the translate-y coordinate associated with this {@link INode}
	 */
	public double getTranslateY();

	/**
	 * @return <code>true</code> if this {@link INode} currently has keyboard
	 *         focus, otherwise <code>false</code>
	 */
	public boolean isFocused();

	/**
	 * Determines if the focus of this {@link INode} is traversable, i.e. it can
	 * receive and release focus.
	 * 
	 * @return <code>true</code> if this {@link INode} is focus-traversable,
	 *         otherwise <code>false</code>
	 */
	public boolean isFocusTraversable();

	/**
	 * @return <code>true</code> if this {@link INode} is visible, otherwise
	 *         <code>false</code>
	 */
	public boolean isVisible();

	/**
	 * Transforms the given {@link Point}, interpreted as local to this
	 * {@link INode}'s coordinate system, to the coordinate system of the
	 * screen. The secondly given {@link Point} is set to the transformed
	 * absolute coordinates, and therefore may not be <code>null</code>.
	 * 
	 * @param localIn
	 *            local {@link Point}
	 * @param absoluteOut
	 *            is set to the transformed absolute coordinates
	 */
	public void localToAbsolute(Point localIn, Point absoluteOut);

	// TODO: we dont need isHovered(), do we?
	// public boolean isHovered();

	// TODO: we dont need isPressed(), do we?
	// public boolean isPressed();

	// TODO: we dont need isResizable(), do we?
	// public boolean isResizable();

	/**
	 * Transforms the first passed-in local {@link Point} to the coordinate
	 * system of this {@link INode}'s parent. The second passed-in {@link Point}
	 * is set to the transformed parent coordinates, and therefore may not be
	 * <code>null</code>.
	 * 
	 * @param localIn
	 * @param parentOut
	 */
	public void localToParent(Point localIn, Point parentOut);

	/**
	 * <p>
	 * Transforms the firstly given {@link Point}, interpreted as local to the
	 * coordinate system of this {@link INode}'s parent, into the local
	 * coordinate system of this {@link INode}. The secondly given {@link Point}
	 * is set to the transformed absolute coordinates, and therefore may not be
	 * <code>null</code>.
	 * </p>
	 * <p>
	 * It is safe for both points to refer to the same object.
	 * </p>
	 * 
	 * @param parentIn
	 *            {@link Point} with parent coordinates
	 * @param localOut
	 *            is set to the transformed local coordinates
	 */
	void parentToLocal(Point parentIn, Point localOut);

	/**
	 * Relocates this {@link INode} to the specified coordinates.
	 * 
	 * @param x
	 * @param y
	 */
	public void relocate(double x, double y);

	// TODO: comment-in
	// /**
	// * Transforms the given x and y coordinates, interpreted as local to this
	// * {@link INode}'s coordinate system, to the coordinate system of the
	// * screen. Returns a new {@link Point} holding the transformed absolute
	// * coordinates.
	// *
	// * @param localX
	// * local x coordinate
	// * @param localY
	// * local y coordinate
	// * @return a new {@link Point} holding the transformed absolute
	// coordinates
	// */
	// public Point localToAbsolute(double localX, double localY);
	//
	// /**
	// * Transforms the given x and y coordinates, interpreted as local to this
	// * {@link INode}'s coordinate system, to the coordinate system of the
	// * screen. The given {@link Point} is set to the transformed absolute
	// * coordinates and therefore may not be <code>null</code>.
	// *
	// * @param localX
	// * local x coordinate
	// * @param localY
	// * local y coordinate
	// * @param absoluteOut
	// * is set to the transformed absolute coordinates
	// */
	// public void localToAbsolute(double localX, double localY, Point
	// absoluteOut);
	//
	// /**
	// * Transforms the given {@link Point}, interpreted as local to this
	// * {@link INode}'s coordinate system, to the coordinate system of the
	// * screen. Returns a new {@link Point} holding the transformed absolute
	// * coordinates.
	// *
	// * @param local
	// * local {@link Point}
	// * @return a new {@link Point} holding the transformed absolute
	// coordinates
	// */
	// public Point localToAbsolute(Point local);

	/**
	 * Removes the given {@link IEventHandler event filter} from the list of
	 * listeners managed by this {@link INode} for the specified
	 * {@link EventType}.
	 * 
	 * @param type
	 * @param filter
	 */
	public <T extends Event> void removeEventFilter(EventType<T> type,
			IEventHandler<T> filter);

	/**
	 * Removes the given {@link IEventHandler event handler} from the list of
	 * listeners managed by this {@link INode} for the specified
	 * {@link EventType}.
	 * 
	 * @param type
	 * @param filter
	 */
	public <T extends Event> void removeEventHandler(EventType<T> type,
			IEventHandler<T> handler);

	// TODO
	// public void relocateResize(double x, double y, double width, double
	// height);

	/**
	 * Tries to bind keyboard events to this {@link INode}. Uses the SWT
	 * forceFocus() method.
	 * 
	 * @return <code>true</code> on success, otherwise <code>false</code>
	 */
	public boolean requestFocus();

	/**
	 * Resizes this {@link INode} to the specified width and height.
	 * 
	 * @param width
	 * @param height
	 */
	public void resize(double width, double height);

	// TODO: comment-in
	// /**
	// * Transforms the given coordinates, interpreted as local to the
	// coordinate
	// * system of this {@link INode}'s parent, into the local coordinate system
	// * of this {@link INode}.
	// *
	// * @param parentX
	// * parent x coordinate
	// * @param parentY
	// * parent y coordinate
	// * @return a new {@link Point} holding the transformed local coordinates
	// */
	// Point parentToLocal(double parentX, double parentY);
	//
	// /**
	// * Transforms the given coordinates, interpreted as local to the
	// coordinate
	// * system of this {@link INode}'s parent, into the local coordinate system
	// * of this {@link INode}. The passed-in {@link Point} is set to the
	// * transformed absolute coordinates, and therefore may not be
	// * <code>null</code>.
	// *
	// * @param parentX
	// * parent x coordinate
	// * @param parentY
	// * parent y coordinate
	// * @param localOut
	// * is set to the transformed local coordinates
	// */
	// void parentToLocal(double parentX, double parentY, Point localOut);
	//
	// /**
	// * Transforms the given {@link Point}, interpreted as local to the
	// * coordinate system of this {@link INode}'s parent, into the local
	// * coordinate system of this {@link INode}.
	// *
	// * @param parent
	// * @return a new {@link Point} holding the transformed local coordinates
	// */
	// Point parentToLocal(Point parent);

	/**
	 * Assigns the node a new location and size.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void resizeRelocate(double x, double y, double width, double height);

	/**
	 * Sets the focusTraversable property of this {@link INode}. If the focus of
	 * an {@link INode} is traversable, it will react to {@link TraverseEvent}s
	 * by switching through the hierarchy.
	 * 
	 * @param focusTraversable
	 */
	public void setFocusTraversable(boolean focusTraversable);

	/**
	 * Sets the layout-x attribute.
	 * 
	 * @param layoutX
	 */
	public void setLayoutX(double layoutX);

	/**
	 * Sets the layout-y attribute.
	 * 
	 * @param layoutY
	 */
	public void setLayoutY(double layoutY);

	public void setMaxHeight(double height);

	public void setMaxWidth(double width);

	public void setMinHeight(double height);

	public void setMinWidth(double width);

	/**
	 * Sets the pivot {@link Point} to the passed-in {@link Point}.
	 * 
	 * @param p
	 */
	public void setPivot(Point p);

	public void setPrefHeight(double height);

	public void setPrefWidth(double width);

	/**
	 * Sets the rotation-angle attribute.
	 * 
	 * @param angle
	 */
	public void setRotationAngle(Angle angle);

	/**
	 * Sets the scale-x attribute.
	 * 
	 * @param scaleX
	 */
	public void setScaleX(double scaleX);

	/**
	 * Sets the scale-y attribute.
	 * 
	 * @param scaleY
	 */
	public void setScaleY(double scaleY);

	/**
	 * Sets the translate-x attribute.
	 * 
	 * @param translateX
	 */
	public void setTranslateX(double translateX);

	/**
	 * Sets the translate-y attribute.
	 * 
	 * @param translateY
	 */
	public void setTranslateY(double translateY);

}
