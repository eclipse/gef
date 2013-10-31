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
package org.eclipse.gef4.swtfx.layout;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.controls.SwtScrollBar;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;

/**
 * ScrollPane is a layout {@link Pane} which can display scroll bars. For both
 * scroll bars, you can specify the {@link ScrollBarPolicy} determining when to
 * add the scroll bar to the ScrollPane.
 * 
 * @author mwienand
 * 
 */
public class ScrollPane extends Pane {

	/**
	 * ScrollBarPolicy determines when to add a scroll bar to a ScrollPane.
	 * Default is {@link #AUTO}.
	 * 
	 * @see #NEVER
	 * @see #ALWAYS
	 * @see #AUTO
	 * 
	 * @author mwienand
	 * 
	 */
	public static enum ScrollBarPolicy {
		/**
		 * The scroll bar is <i>never</i> added to the ScrollPane.
		 */
		NEVER,

		/**
		 * The scroll bar is <i>always</i> added to the ScrollPane.
		 */
		ALWAYS,

		/**
		 * The scroll bar is <i>automatically</i> added to the ScrollPane, if
		 * its content node does not fit the ScrollPane's bounds.
		 */
		AUTO,
	}

	private SwtScrollBar hBar;

	private SwtScrollBar vBar;

	private ScrollBarPolicy hBarPolicy = ScrollBarPolicy.AUTO;

	private ScrollBarPolicy vBarPolicy = ScrollBarPolicy.AUTO;

	private Pane corner;

	private INode content;

	private Point offset = new Point();

	public ScrollPane() {
		hBar = new SwtScrollBar(
				org.eclipse.gef4.swtfx.controls.SwtScrollBar.Orientation.HORIZONTAL);
		vBar = new SwtScrollBar(
				org.eclipse.gef4.swtfx.controls.SwtScrollBar.Orientation.VERTICAL);
		corner = new Pane();
		addChildren(hBar, vBar, corner);

		addEventHandler(MouseEvent.MOUSE_SCROLLED,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						int clickCount = event.getClickCount();
						vBar.setSelection(vBar.getSelection() - clickCount);
						offset.y = vBar.getSelection();
					}
				});

		hBar.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						offset.x = hBar.getSelection();
					}
				});

		vBar.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						offset.y = vBar.getSelection();
					}
				});
	}

	@Override
	public void addChildren(INode... nodes) {
		throw new UnsupportedOperationException(
				"A ScrollPane does not work like this. You have to unset its content attribute instead.");
	}

	public SwtScrollBar getHBar() {
		return hBar;
	}

	public ScrollBarPolicy getHBarPolicy() {
		return hBarPolicy;
	}

	public SwtScrollBar getVBar() {
		return vBar;
	}

	public ScrollBarPolicy getVBarPolicy() {
		return vBarPolicy;
	}

	@Override
	public void layout() {
		super.layout();

		if (content != null) {
			Rectangle available = getLayoutBounds();
			Rectangle contentBounds = content.getLayoutBounds();

			double dx = available.getWidth() - contentBounds.getWidth();
			double dy = available.getHeight() - contentBounds.getHeight();

			/*
			 * If content bounds is greater than the available space, a scroll
			 * bar is needed, so that the remaining available space is further
			 * reduced.
			 */
			if (dx < 0) {
				available.shrink(0, 0, hBar.getControl()
						.computeTrim(0, 0, 0, 0).width, 0);
			}

			if (dy < 0) {
				available.shrink(0, 0, 0,
						vBar.getControl().computeTrim(0, 0, 0, 0).height);
			}

			hBar.setValues(hBar.getSelection(), 0,
					(int) contentBounds.getWidth(), (int) available.getWidth(),
					1, (int) available.getWidth());

			vBar.setValues(vBar.getSelection(), 0,
					(int) contentBounds.getHeight(),
					(int) available.getHeight(), 1, (int) available.getHeight());
		}
	}

	@Override
	public void layoutChildren() {
		if (content instanceof IParent) {
			content.autosize();
		}
		content.relocate(-offset.x, -offset.y);
	}

	@Override
	public void removeChildren(INode... nodes) {
		throw new UnsupportedOperationException(
				"A ScrollPane does not work like this. You have to set its content attribute instead.");
	}

	@Override
	public void resize(double width, double height) {
		super.resize(width, height);

		double hBarHeight = hBar.computePrefHeight(width);
		double vBarWidth = vBar.computePrefWidth(height);

		hBar.resizeRelocate(0, height - hBarHeight, width - vBarWidth,
				hBarHeight);
		vBar.resizeRelocate(width - vBarWidth, 0, vBarWidth, height
				- hBarHeight);
	}

	public void setContent(INode content) {
		if (this.content != null) {
			getChildrenUnmodifiable().remove(this.content);
		}
		this.content = content;
		addChildren(content);
	}

	public void sethBarPolicy(ScrollBarPolicy hBarPolicy) {
		this.hBarPolicy = hBarPolicy;
	}

	public void setvBarPolicy(ScrollBarPolicy vBarPolicy) {
		this.vBarPolicy = vBarPolicy;
	}

	@Override
	public String toString() {
		return "ScrollPane " + System.identityHashCode(this)
				+ " { children-count: " + getChildrenUnmodifiable().size()
				+ " }";
	}

}
