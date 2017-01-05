/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

/**
 * The {@link ZoomOutAction} is an {@link AbstractZoomAction} that decreases the
 * zoom level by multiplying it with <code>0.8</code> while preserving the
 * center of the diagram.
 *
 * @author mwienand
 *
 */
public class ZoomOutAction extends AbstractZoomAction {

	/**
	 *
	 */
	public ZoomOutAction() {
		super("Zoom Out");
	}

	@Override
	protected double determineZoomFactor(double currentZoomFactor) {
		return currentZoomFactor * 0.8;
	}
}
