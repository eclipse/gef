package org.eclipse.gef4.swtfx.controls;

import org.eclipse.gef4.swtfx.Orientation;
import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;

public class SwtScrollBar extends SwtControlAdapterNode<ScrolledComposite> {

	private double size;
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
				return getControl().getHorizontalBar().getSize().x;
			}
		}
		return super.computePrefHeight(width);
	}

	@Override
	public double computePrefWidth(double height) {
		if (orientation == Orientation.VERTICAL) {
			if (getControl() != null) {
				return getControl().getVerticalBar().getSize().y;
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

		ScrolledComposite scrolledComposite = new ScrolledComposite(getScene(),
				flags);
		scrolledComposite.setBackground(getScene().getDisplay().getSystemColor(
				SWT.COLOR_BLUE));
		scrolledComposite.setAlwaysShowScrollBars(true);

		return scrolledComposite;
	}

	@Override
	protected void hookControl() {
		setControl(createScrolled(orientation));
		super.hookControl();
	}

	@Override
	public void resize(double width, double height) {
		if (orientation == Orientation.HORIZONTAL) {
			super.resize(width, computePrefHeight(width));
		} else if (orientation == Orientation.VERTICAL) {
			super.resize(computePrefWidth(height), height);
		}
	}

	@Override
	protected void unhookControl() {
		super.unhookControl();
		getControl().dispose();
	}

	@Override
	public void updateSwtBounds() {
		if (getControl() == null) {
			return;
		}
	}

}
