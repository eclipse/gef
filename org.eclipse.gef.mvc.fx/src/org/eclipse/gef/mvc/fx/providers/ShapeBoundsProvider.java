/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.providers;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.scene.Node;

/**
 * The {@link ShapeBoundsProvider} is a {@link Provider
 * Provider&lt;IGeometry&gt;} that returns a {@link Rectangle} that corresponds
 * to the layout-bounds of its host visual, i.e. it includes the geometric
 * bounds and the stroke of the visual. The {@link Rectangle} is specified
 * within the local coordinate system of the host visual.
 *
 * @author mwienand
 *
 */
public class ShapeBoundsProvider
		extends IAdaptable.Bound.Impl<IVisualPart<? extends Node>>
		implements Provider<IGeometry> {

	private double leftPadding;
	private double topPadding;
	private double rightPadding;
	private double bottomPadding;

	/**
	 * Constructs a new {@link ShapeBoundsProvider} without padding.
	 */
	public ShapeBoundsProvider() {
		this(0, 0, 0, 0);
	}

	/**
	 * Constructs a new {@link ShapeBoundsProvider} with the given padding.
	 *
	 * @param padding
	 *            The padding that is applied around the shape.
	 */
	public ShapeBoundsProvider(double padding) {
		this(padding, padding, padding, padding);
	}

	/**
	 * Constructs a new {@link ShapeBoundsProvider} with the given padding.
	 *
	 * @param leftPadding
	 *            The padding that is applied on the left of the shape.
	 * @param topPadding
	 *            The padding that is applied on the top of the shape.
	 * @param rightPadding
	 *            The padding that is applied on the right of the shape.
	 * @param bottomPadding
	 *            The padding that is applied on the bottom of the shape.
	 */
	public ShapeBoundsProvider(double leftPadding, double topPadding,
			double rightPadding, double bottomPadding) {
		this.leftPadding = leftPadding;
		this.topPadding = topPadding;
		this.rightPadding = rightPadding;
		this.bottomPadding = bottomPadding;
	}

	@Override
	public Rectangle get() {
		return FX2Geometry
				.toRectangle(getAdaptable().getVisual().getLayoutBounds())
				.getExpanded(leftPadding, topPadding, rightPadding,
						bottomPadding);
	}

}