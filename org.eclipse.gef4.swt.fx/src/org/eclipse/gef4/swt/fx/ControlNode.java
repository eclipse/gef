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
package org.eclipse.gef4.swt.fx;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.fx.event.Event;
import org.eclipse.gef4.swt.fx.event.EventHandlerManager;
import org.eclipse.gef4.swt.fx.event.EventType;
import org.eclipse.gef4.swt.fx.event.IEventDispatchChain;
import org.eclipse.gef4.swt.fx.event.IEventDispatcher;
import org.eclipse.gef4.swt.fx.event.IEventHandler;
import org.eclipse.gef4.swt.fx.event.SwtEventForwarder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The ControlFigure class wraps the {@link INode} interface around arbitrary
 * SWT {@link Control}s.
 * 
 * @author mwienand
 * 
 */
public class ControlNode<T extends Control> implements INode {

	/**
	 * Registers SWT event listeners and wraps the SWT events in GEF4 event
	 * objects.
	 */
	private SwtEventForwarder swtEventDispatcher;

	/**
	 * The wrapped {@link Control}.
	 */
	private T control;

	/**
	 * The {@link EventHandlerManager} manages the registering of event handlers
	 * and event filters. What's more, it dispatches incoming events to the
	 * registered handlers and filters.
	 */
	private EventHandlerManager dispatcher = new EventHandlerManager();

	/**
	 * Focus-traversable property.
	 */
	private boolean focusTraversable = true;

	/**
	 * Horizontal translation used for layouting the node.
	 */
	private double layoutX = 0;

	/**
	 * Vertical translation used for layouting the node.
	 */
	private double layoutY = 0;

	/**
	 * Pivot point for all local transformations (layout-x/y, translate-x/y,
	 * scale-x/y, rotation-angle). Before those transformations are applied, a
	 * translation by the pivot point is executed first. And after those
	 * transformations are applied, the translation by the pivot point is
	 * inverted.
	 */
	private Point pivot = new Point();

	/**
	 * Horizontal scale factor.
	 */
	private double scaleX = 1;

	/**
	 * Vertical scale factor.
	 */
	private double scaleY = 1;

	/**
	 * Horizontal translation.
	 */
	private double translateX = 0;

	/**
	 * Vertical translation.
	 */
	private double translateY = 0;

	/**
	 * Visibility property.
	 */
	private boolean visible = true;

	/**
	 * Rotation angle.
	 */
	private Angle angle = Angle.fromRad(0);

	/**
	 * List of additional transformations.
	 */
	private List<AffineTransform> transforms = new LinkedList<AffineTransform>();

	/**
	 * Constructs a new {@link ControlNode} for the passed-in {@link Control}.
	 * An ControlFigure implements the {@link INode} interface for arbitrary SWT
	 * controls. This wrapper is used, so that the {@link INode} interface can
	 * be used as the central abstraction throughout the API.
	 * 
	 * @param control
	 */
	public ControlNode(T control) {
		this.control = control;
		swtEventDispatcher = new SwtEventForwarder(this);
	}

	@Override
	public void absoluteToLocal(Point absoluteIn, Point localOut) {
		NodeUtil.absoluteToLocal(this, absoluteIn, localOut);
	}

	@Override
	public <E extends Event> void addEventFilter(EventType<E> type,
			IEventHandler<E> filter) {
		dispatcher.addEventFilter(type, filter);
	}

	@Override
	public <E extends Event> void addEventHandler(EventType<E> type,
			IEventHandler<E> handler) {
		dispatcher.addEventHandler(type, handler);
	}

	@Override
	public IEventDispatchChain buildEventDispatchChain(IEventDispatchChain tail) {
		return NodeUtil.buildEventDispatchChain(this, tail);
	}

	@Override
	public double computeMaxHeight(double width) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computeMaxWidth(double height) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computeMinHeight(double width) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computeMinWidth(double height) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computePrefHeight(double width) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computePrefWidth(double height) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean contains(double localX, double localY) {
		return getLayoutBounds().contains(localX, localY);
	}

	@Override
	public Rectangle getBoundsInLocal() {
		Rectangle layoutBounds = getLayoutBounds();
		return layoutBounds;
	}

	@Override
	public Rectangle getBoundsInParent() {
		return NodeUtil.getBoundsInParent(this);
	}

	@Override
	public Orientation getContentBias() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the associated SWT {@link Control}.
	 * 
	 * @return the associated SWT {@link Control}
	 */
	public T getControl() {
		return control;
	}

	@Override
	public IEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public Rectangle getLayoutBounds() {
		org.eclipse.swt.graphics.Point size = control.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true);
		return new Rectangle(0, 0, size.x, size.y);
	}

	@Override
	public double getLayoutX() {
		return layoutX;
	}

	@Override
	public double getLayoutY() {
		return layoutY;
	}

	@Override
	public AffineTransform getLocalToAbsoluteTransform() {
		return NodeUtil.getLocalToAbsoluteTransform(this);
	}

	@Override
	public AffineTransform getLocalToParentTransform() {
		return NodeUtil.getLocalToParentTransform(this);
	}

	@Override
	public double getMaxHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaxWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMinHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMinWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IParent getParentNode() {
		Composite parent = control.getParent();
		if (parent instanceof IParent) {
			return (IParent) parent;
		}
		return null;
	}

	@Override
	public Point getPivot() {
		return pivot;
	}

	@Override
	public Dimension getPreferredSize() {
		return getLayoutBounds().getSize();
	}

	@Override
	public double getPrefHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPrefWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Angle getRotationAngle() {
		return angle;
	}

	@Override
	public double getScaleX() {
		return scaleX;
	}

	@Override
	public double getScaleY() {
		return scaleY;
	}

	@Override
	public List<AffineTransform> getTransforms() {
		return transforms;
	}

	@Override
	public double getTranslateX() {
		return translateX;
	}

	@Override
	public double getTranslateY() {
		return translateY;
	}

	@Override
	public boolean isFocused() {
		// TODO
		return false;
	}

	@Override
	public boolean isFocusTraversable() {
		return focusTraversable;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void localToAbsolute(Point localIn, Point absoluteOut) {
		NodeUtil.localToAbsolute(this, localIn, absoluteOut);
	}

	@Override
	public void localToParent(Point localIn, Point parentOut) {
		NodeUtil.localToParent(this, localIn, parentOut);
	}

	@Override
	public void parentToLocal(Point parentIn, Point localOut) {
		NodeUtil.parentToLocal(this, parentIn, localOut);
	}

	@Override
	public void relocate(double x, double y) {
		NodeUtil.relocate(this, x, y);
		control.setLocation((int) x, (int) y);
	}

	@Override
	public <E extends Event> void removeEventFilter(EventType<E> type,
			IEventHandler<E> filter) {
		dispatcher.removeEventFilter(type, filter);
	}

	@Override
	public <E extends Event> void removeEventHandler(EventType<E> type,
			IEventHandler<E> handler) {
		dispatcher.removeEventHandler(type, handler);
	}

	@Override
	public boolean requestFocus() {
		return control.forceFocus();
	}

	@Override
	public void resize(double width, double height) {
		control.setSize((int) width, (int) height);
	}

	@Override
	public void resizeRelocate(double x, double y, double width, double height) {
		NodeUtil.resizeRelocate(this, x, y, width, height);
	}

	@Override
	public void setFocusTraversable(boolean focusTraversable) {
		this.focusTraversable = focusTraversable;
	}

	@Override
	public void setLayoutX(double layoutX) {
		this.layoutX = layoutX;
	}

	@Override
	public void setLayoutY(double layoutY) {
		this.layoutY = layoutY;
	}

	@Override
	public void setMaxHeight(double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMaxWidth(double width) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMinHeight(double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMinWidth(double width) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPivot(Point pivot) {
		this.pivot = pivot;
	}

	@Override
	public void setPrefHeight(double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPrefWidth(double width) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRotationAngle(Angle angle) {
		this.angle = angle;
	}

	@Override
	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	@Override
	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	@Override
	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}

	@Override
	public void setTranslateY(double translateY) {
		this.translateY = translateY;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
