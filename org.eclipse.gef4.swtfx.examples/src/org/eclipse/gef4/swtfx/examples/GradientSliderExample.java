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
package org.eclipse.gef4.swtfx.examples;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.AbstractFigure;
import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.gc.CycleMethod;
import org.eclipse.gef4.swtfx.gc.LinearGradient;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Slider;

public class GradientSliderExample implements IExample, SelectionListener {

	private static class SlidableLinearGradient extends LinearGradient {
		private double distanceOffset = 0;

		public SlidableLinearGradient(Point from, Point to,
				CycleMethod cycleMethod) {
			super(from, to, cycleMethod);
		}

		@Override
		public SlidableLinearGradient addStop(double percentualDistance,
				RgbaColor color) {
			super.addStop(percentualDistance, color);
			return this;
		}

		@Override
		public double computePercentualDistance(Point p) {
			return distanceOffset + super.computePercentualDistance(p);
		}

		@Override
		public SlidableLinearGradient getCopy() {
			SlidableLinearGradient copy = new SlidableLinearGradient(
					getStart(), getEnd(), getCycleMethod());
			this.copyInto(copy);
			copy.setDistanceOffset(getDistanceOffset());
			return copy;
		}

		public double getDistanceOffset() {
			return distanceOffset;
		}

		public void setDistanceOffset(double d) {
			distanceOffset = d;
		}
	}

	public static void main(String[] args) {
		new Example(new GradientSliderExample());
	}

	private IParent root;
	private Slider slider;
	private SlidableLinearGradient gradient = new SlidableLinearGradient(
			new Point(0, 0), new Point(200, 100), CycleMethod.REFLECT)
			.addStop(0, new RgbaColor(255, 255, 255))
			.addStop(0.2, new RgbaColor(255, 0, 0))
			.addStop(0.4, new RgbaColor(0, 255, 0))
			.addStop(0.6, new RgbaColor(0, 0, 255))
			.addStop(0.8, new RgbaColor(0, 0, 0))
			.addStop(1, new RgbaColor(255, 255, 0));
	private AbstractFigure fig;

	public GradientSliderExample() {
		gradient.computeAuxVars();
	}

	@Override
	public void addUi(IParent c) {
		this.root = c;

		slider = new Slider(c.getScene(), SWT.NONE);
		slider.setMinimum(0);
		slider.setMaximum(100);
		slider.addSelectionListener(this);
		SwtControlAdapterNode<Slider> sliderNode = new SwtControlAdapterNode<Slider>(slider);

		fig = new ShapeFigure(new Rectangle(0, 0, 200, 400));
		fig.setFill(gradient);

		c.addChildNodes(sliderNode, fig);

		sliderNode.resizeRelocate(20, 20, 200, 20);
		fig.relocate(20, 60);
	}

	@Override
	public int getHeight() {
		return 500;
	}

	@Override
	public String getTitle() {
		return "Gradient Animation";
	}

	@Override
	public int getWidth() {
		return 300;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		int selection = slider.getSelection();
		if (selection < 0) {
			selection = 0;
		} else if (selection > 100) {
			selection = 100;
		}

		double distOffset = selection / 90d; // TODO: Why not /100d but /90d?
		gradient.setDistanceOffset(distOffset);
		fig.setFill(gradient);
		root.getScene().refreshVisuals();
	}
}
