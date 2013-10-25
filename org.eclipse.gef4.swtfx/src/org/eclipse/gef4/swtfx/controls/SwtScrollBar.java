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
package org.eclipse.gef4.swtfx.controls;

import org.eclipse.gef4.swtfx.Orientation;
import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.Event;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ScrollBar;

public class SwtScrollBar extends SwtControlAdapterNode<ScrolledComposite> {

	/*
	 * TODO: Implement a ScrollBarModel so that we can apply scroll changes even
	 * without having an actual SWT ScrollBar available.
	 */

	// private double size;

	// TODO: this is the wrong Orientation, use enum { HORIZONTAL, VERTICAL }
	private Orientation orientation;

	public SwtScrollBar(Orientation orientation) {
		super(null);
		this.orientation = orientation;
	}

	@Override
	public double computeMaxHeight(double width) {
		if (orientation == Orientation.HORIZONTAL) {
			return computePrefHeight(width);
		}
		return super.computeMaxHeight(width);
	}

	@Override
	public double computeMaxWidth(double height) {
		if (orientation == Orientation.VERTICAL) {
			return computePrefWidth(height);
		}
		return super.computeMaxWidth(height);
	}

	@Override
	public double computeMinHeight(double width) {
		if (orientation == Orientation.HORIZONTAL) {
			return computePrefHeight(width);
		}
		return 0;
	}

	@Override
	public double computeMinWidth(double height) {
		if (orientation == Orientation.VERTICAL) {
			return computePrefWidth(height);
		}
		return 0;
	}

	@Override
	public double computePrefHeight(double width) {
		if (orientation == Orientation.HORIZONTAL) {
			if (getControl() != null) {
				return getControl().computeTrim(0, 0, 0, 0).height;
			}
		}
		return super.computePrefHeight(width);
	}

	@Override
	public double computePrefWidth(double height) {
		if (orientation == Orientation.VERTICAL) {
			if (getControl() != null) {
				return getControl().computeTrim(0, 0, 0, 0).width;
			}
		}
		return super.computePrefWidth(height);
	}

	protected ScrolledComposite createScrolled(Orientation orientation) {
		int flags;
		switch (orientation) {
		case HORIZONTAL:
			flags = SWT.H_SCROLL;
			break;
		case VERTICAL:
			flags = SWT.V_SCROLL;
			break;
		default:
			throw new IllegalStateException("Unknown Orientation: "
					+ orientation);
		}

		ScrolledComposite scrolled = new ScrolledComposite(getScene(), flags);
		scrolled.setContent(null);
		scrolled.setMinSize(0, 0);
		scrolled.setAlwaysShowScrollBars(true);

		return scrolled;
	}

	public int getIncrement() {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			return swtBar.getIncrement();
		}
		return 0;
	}

	public int getMaximum() {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			return swtBar.getMaximum();
		}
		return 0;
	}

	public int getMinimum() {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			return swtBar.getMinimum();
		}
		return 0;
	}

	public int getPageIncrement() {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			return swtBar.getPageIncrement();
		}
		return 0;
	}

	public int getSelection() {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			return swtBar.getSelection();
		}
		return 0;
	}

	public ScrollBar getSwtBar() {
		ScrolledComposite c = getControl();
		if (c != null) {
			return orientation == Orientation.HORIZONTAL ? c.getHorizontalBar()
					: c.getVerticalBar();
		}
		return null;
	}

	public int getThumb() {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			return swtBar.getThumb();
		}
		return 0;
	}

	@Override
	protected void hookControl() {
		setControl(createScrolled(orientation));
		getSwtBar().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Event.fireEvent(SwtScrollBar.this, new ActionEvent(e,
						SwtScrollBar.this, ActionEvent.ACTION));
			}
		});
		super.hookControl();
	}

	@Override
	public void resize(double width, double height) {
		/*
		 * Note: We are setting the pref-width or pref-height here so that
		 * computePrefX() returns that size. This is not how it should work.
		 */

		if (orientation == Orientation.HORIZONTAL) {
			setPrefWidth(width);
			super.resize(width, computePrefHeight(width));
		} else if (orientation == Orientation.VERTICAL) {
			setPrefHeight(height);
			super.resize(computePrefWidth(height), height);
		}
	}

	public void setIncrement(int inc) {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			swtBar.setIncrement(inc);
		}
	}

	public void setMaximum(int max) {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			swtBar.setMaximum(max);
		}
	}

	public void setMinimum(int min) {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			swtBar.setMinimum(min);
		}
	}

	public void setPageIncrement(int pageInc) {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			swtBar.setPageIncrement(pageInc);
		}
	}

	public void setSelection(int sel) {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			swtBar.setSelection(sel);
		}
	}

	public void setThumb(int thumb) {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			swtBar.setThumb(thumb);
		}
	}

	public void setValues(int sel, int min, int max, int thumb, int inc,
			int pageInc) {
		ScrollBar swtBar = getSwtBar();
		if (swtBar != null) {
			swtBar.setValues(sel, min, max, thumb, inc, pageInc);
		}
	}

	@Override
	public String toString() {
		return "SwtScrollBar { range: " + "[" + getMinimum() + ";"
				+ getMaximum() + "]" + ", sel: " + getSelection() + ", thumb: "
				+ getThumb() + ", inc: " + getIncrement() + ";"
				+ getPageIncrement() + " }";
	}

	@Override
	protected void unhookControl() {
		super.unhookControl();
		getControl().dispose();
	}

}
