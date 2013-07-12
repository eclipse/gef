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
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.event.Event;
import org.eclipse.gef4.swtfx.event.EventType;
import org.eclipse.gef4.swtfx.event.IEventDispatchChain;
import org.eclipse.gef4.swtfx.event.IEventDispatcher;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.swt.widgets.Composite;

public class Group implements IParent {

	private IParent parent;

	public Group() {
	}

	@Override
	public void absoluteToControl(Point absoluteIn, Point controlOut) {
		// TODO Auto-generated method stub

	}

	@Override
	public void absoluteToLocal(Point absoluteIn, Point localOut) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addChildNodes(INode... nodes) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Event> void addEventFilter(EventType<T> type,
			IEventHandler<T> filter) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Event> void addEventHandler(EventType<T> type,
			IEventHandler<T> handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void autosize() {
		// TODO Auto-generated method stub

	}

	@Override
	public IEventDispatchChain buildEventDispatchChain(IEventDispatchChain tail) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void controlToLocal(Point controlIn, Point localOut) {
		// TODO Auto-generated method stub

	}

	@Override
	public Rectangle getBoundsInLocal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getBoundsInParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<INode> getChildNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Orientation getContentBias() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEventDispatcher getEventDispatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INode getFocusNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getLayoutBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILayouter getLayouter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getLayoutX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getLayoutY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AffineTransform getLocalToAbsoluteTransform() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AffineTransform getLocalToParentTransform() {
		// TODO Auto-generated method stub
		return null;
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
	public INode getNodeAt(Point localPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IParent getParentNode() {
		return parent;
	}

	@Override
	public Point getPivot() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getScaleX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getScaleY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Composite getSwtComposite() {
		IParent parent = this.parent;
		while (parent != null && !(parent instanceof Pane)) {
			return ((Pane) parent).getSwtComposite();
		}
		throw new IllegalStateException("Missing root Pane.");
	}

	@Override
	public List<AffineTransform> getTransforms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTranslateX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTranslateY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFocusTraversable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isManaged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isResizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void localToAbsolute(Point localIn, Point absoluteOut) {
		// TODO Auto-generated method stub

	}

	@Override
	public void localToParent(Point localIn, Point parentOut) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parentToLocal(Point parentIn, Point localOut) {
		// TODO Auto-generated method stub

	}

	@Override
	public void relocate(double x, double y) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Event> void removeEventFilter(EventType<T> type,
			IEventHandler<T> filter) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Event> void removeEventHandler(EventType<T> type,
			IEventHandler<T> handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requestFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void requestRedraw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resizeRelocate(double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setFocusNode(INode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFocusTraversable(boolean focusTraversable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLayouter(ILayouter layouter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLayoutX(double layoutX) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLayoutY(double layoutY) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void setPivot(Point p) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void setScaleX(double scaleX) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setScaleY(double scaleY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTranslateX(double translateX) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTranslateY(double translateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSwtBounds() {
		// TODO Auto-generated method stub

	}

}
