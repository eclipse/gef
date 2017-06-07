/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.swt.canvas;

import org.eclipse.swt.widgets.Composite;

import javafx.embed.swt.FXCanvas;

/**
 * The {@link IFXCanvasFactory} provides a method for the creation of an
 * {@link FXCanvas} inside a {@link Composite}. Using the
 * {@link IFXCanvasFactory} allows to exchange the {@link FXCanvas}
 * implementation.
 *
 * @author anyssen
 *
 */
public interface IFXCanvasFactory {

	/**
	 * Creates an {@link FXCanvas} inside of the given {@link Composite}.
	 *
	 * @param parent
	 *            The parent {@link Composite} for the {@link FXCanvas} that is
	 *            created.
	 * @param style
	 *            The SWT style bits to be used for the {@link FXCanvas} that
	 *            gets created.
	 *
	 * @return The new {@link FXCanvas} that was created as a child of the given
	 *         {@link Composite}.
	 */
	public FXCanvas createCanvas(Composite parent, int style);

}
