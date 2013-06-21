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
package org.eclipse.gef4.swt.canvas.examples;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.canvas.Group;
import org.eclipse.gef4.swt.canvas.ShapeFigure;
import org.eclipse.gef4.swt.canvas.gc.CycleMethod;
import org.eclipse.gef4.swt.canvas.gc.LinearGradient;
import org.eclipse.gef4.swt.canvas.gc.PaintMode;
import org.eclipse.gef4.swt.canvas.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Canvas;
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
					getStart(), getEnd(), getCycleMode());
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

	private Canvas canvas;
	private Slider slider;
	private SlidableLinearGradient gradient = new SlidableLinearGradient(
			new Point(0, 0), new Point(200, 100), CycleMethod.REFLECT)
			.addStop(0, new RgbaColor(255, 255, 255))
			.addStop(0.2, new RgbaColor(255, 0, 0))
			.addStop(0.4, new RgbaColor(0, 255, 0))
			.addStop(0.6, new RgbaColor(0, 0, 255))
			.addStop(0.8, new RgbaColor(0, 0, 0))
			.addStop(1, new RgbaColor(255, 255, 0));

	public GradientSliderExample() {
		gradient.computeAuxVars();
	}

	@Override
	public void addUi(Group c) {
		this.canvas = c;
		slider = new Slider(c, SWT.NONE);
		slider.setMinimum(0);
		slider.setMaximum(100);
		slider.setBounds(20, 20, 200, 20);
		slider.addSelectionListener(this);

		ShapeFigure fig = new ShapeFigure(new Rectangle(0, 0, 200, 400));
		fig.getPaintStateByReference().getTransformByReference()
				.translate(20, 60);
		fig.getPaintStateByReference().getFillByReference()
				.setGradientByReference(gradient);
		fig.getPaintStateByReference().getFillByReference()
				.setMode(PaintMode.GRADIENT);

		c.addFigures(fig);

		// ShapeFigure fig = new ShapeFigure(new Rectangle(0, 0, 200, 400));
		// fig.translate(20, 60);
		// fig.fill(gradient);
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
		canvas.redraw();
	}
}
