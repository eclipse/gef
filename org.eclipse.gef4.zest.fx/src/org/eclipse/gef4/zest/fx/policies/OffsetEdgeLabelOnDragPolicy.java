/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.policies;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXInteractionPolicy;
import org.eclipse.gef4.mvc.fx.policies.CursorSupport;
import org.eclipse.gef4.mvc.fx.policies.IFXOnDragPolicy;
import org.eclipse.gef4.zest.fx.parts.EdgeLabelPart;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The {@link OffsetEdgeLabelOnDragPolicy} is an {@link IFXOnDragPolicy} that
 * can be installed on {@link EdgeLabelPart}s (see {@link #getHost()}). It moves
 * its {@link #getHost() host} when the {@link #getHost() host} is dragged.
 *
 * @author mwienand
 *
 */
public class OffsetEdgeLabelOnDragPolicy extends AbstractFXInteractionPolicy implements IFXOnDragPolicy {

	private CursorSupport cursorSupport = new CursorSupport(this);
	private double initialOffsetX;
	private double initialOffsetY;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		Point2D p = getHost().getVisual().sceneToLocal(initialOffsetX, initialOffsetY);
		Point2D q = getHost().getVisual().sceneToLocal(initialOffsetX + delta.width, initialOffsetY + delta.height);
		double dx = q.getX() - p.getX();
		double dy = q.getY() - p.getY();
		getHost().getOffset().setX(initialOffsetX + dx);
		getHost().getOffset().setY(initialOffsetY + dy);
	}

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		return cursorSupport;
	}

	@Override
	public EdgeLabelPart getHost() {
		return (EdgeLabelPart) super.getHost();
	}

	@Override
	public void hideIndicationCursor() {
		getCursorSupport().restoreCursor();
	}

	@Override
	public void press(MouseEvent e) {
		initialOffsetX = getHost().getOffset().getX();
		initialOffsetY = getHost().getOffset().getY();
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		Point2D p = getHost().getVisual().sceneToLocal(initialOffsetX, initialOffsetY);
		Point2D q = getHost().getVisual().sceneToLocal(initialOffsetX + delta.width, initialOffsetY + delta.height);
		double dx = q.getX() - p.getX();
		double dy = q.getY() - p.getY();
		getHost().getOffset().setX(initialOffsetX + dx);
		getHost().getOffset().setY(initialOffsetY + dy);
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

}
