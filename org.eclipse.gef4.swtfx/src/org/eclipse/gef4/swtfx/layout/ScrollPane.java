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

import org.eclipse.gef4.swtfx.Orientation;
import org.eclipse.gef4.swtfx.controls.SwtScrollBar;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;

public class ScrollPane extends Pane {

	public static enum ScrollBarPolicy {
		NEVER, ALWAYS, AUTO,
	}

	private SwtScrollBar hBar;

	private SwtScrollBar vBar;

	private ScrollBarPolicy hBarPolicy = ScrollBarPolicy.AUTO;

	private ScrollBarPolicy vBarPolicy = ScrollBarPolicy.AUTO;

	public ScrollPane() {
		hBar = new SwtScrollBar(Orientation.HORIZONTAL);
		vBar = new SwtScrollBar(Orientation.VERTICAL);
		addChildNodes(hBar, vBar);
		addEventHandler(MouseEvent.MOUSE_SCROLLED,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						int clickCount = event.getClickCount();
						vBar.setSelection(vBar.getSelection() - clickCount);
					}
				});
	}

	public SwtScrollBar getHBar() {
		return hBar;
	}

	public ScrollBarPolicy gethBarPolicy() {
		return hBarPolicy;
	}

	public SwtScrollBar getVBar() {
		return vBar;
	}

	public ScrollBarPolicy getvBarPolicy() {
		return vBarPolicy;
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

	public void sethBarPolicy(ScrollBarPolicy hBarPolicy) {
		this.hBarPolicy = hBarPolicy;
	}

	public void setvBarPolicy(ScrollBarPolicy vBarPolicy) {
		this.vBarPolicy = vBarPolicy;
	}

}
