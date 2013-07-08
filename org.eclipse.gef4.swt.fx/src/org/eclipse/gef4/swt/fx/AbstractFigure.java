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
import org.eclipse.gef4.swt.fx.gc.GraphicsContextState;

public abstract class AbstractFigure implements IFigure {

	// TODO: delegate to GraphicsContextState, do not allow direct access
	private GraphicsContextState paintState = new GraphicsContextState();

	private EventHandlerManager dispatcher = new EventHandlerManager();
	private IParent parent;
	private boolean focusTraversable = true;
	private double layoutX = 0;
	private double layoutY = 0;
	private Point pivot = new Point();
	private double scaleX = 1;
	private double scaleY = 1;
	private double translateX = 0;
	private double translateY = 0;
	private boolean visible = true;
	private Angle angle = Angle.fromRad(0);
	private List<AffineTransform> transforms = new LinkedList<AffineTransform>();

	@Override
	public void absoluteToLocal(Point absoluteIn, Point localOut) {
		parent.absoluteToControl(absoluteIn, localOut);
		parent.controlToLocal(localOut, localOut);
		parentToLocal(localOut, localOut);
	}

	@Override
	public <T extends Event> void addEventFilter(EventType<T> type,
			IEventHandler<T> filter) {
		dispatcher.addEventFilter(type, filter);
	}

	@Override
	public <T extends Event> void addEventHandler(EventType<T> type,
			IEventHandler<T> handler) {
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
	public Rectangle getBoundsInParent() {
		return NodeUtil.getBoundsInParent(this);
	}

	@Override
	public Orientation getContentBias() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEventDispatcher getEventDispatcher() {
		return dispatcher;
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
	public GraphicsContextState getPaintStateByReference() {
		return paintState;
	}

	@Override
	public IParent getParentNode() {
		return parent;
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
		return getParentNode().getFocusFigure() == this;
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

	public Point parentToLocal(double parentX, double parentY) {
		Point local = new Point();
		parentToLocal(new Point(parentX, parentY), local);
		return local;
	}

	@Override
	public void parentToLocal(Point parentIn, Point localOut) {
		NodeUtil.parentToLocal(this, parentIn, localOut);
	}

	@Override
	public void relocate(double x, double y) {
		NodeUtil.relocate(this, x, y);
	}

	@Override
	public <T extends Event> void removeEventFilter(EventType<T> type,
			IEventHandler<T> filter) {
		dispatcher.removeEventFilter(type, filter);
	}

	@Override
	public <T extends Event> void removeEventHandler(EventType<T> type,
			IEventHandler<T> handler) {
		dispatcher.removeEventHandler(type, handler);
	}

	@Override
	public boolean requestFocus() {
		return parent.setFocusFigure(this);
	}

	@Override
	public void resize(double width, double height) {
		// no resize per default
		// TODO: evaluate whether or not to throw the exception
		throw new UnsupportedOperationException("Cannot resize() this figure.");
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
	public void setParentNode(IParent parent) {
		this.parent = parent;
	}

	@Override
	public void setPivot(Point p) {
		pivot.setLocation(p);
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
		this.angle.setRad(angle.rad());
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

	@Override
	public void update() {
		if (parent != null) {
			parent.requestRedraw();
		}
	}

}
