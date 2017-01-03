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
 * The {@link ResetViewportAction} is a {@link FitToSizeAction} that restricts
 * the zoom level to <code>1.0</code>.
 *
 * @author wienand
 *
 */
public class ResetViewportAction extends FitToSizeAction {

	@Override
	protected double getMaxZoom() {
		return 1d;
	}

	@Override
	protected double getMinZoom() {
		return 1d;
	}
}