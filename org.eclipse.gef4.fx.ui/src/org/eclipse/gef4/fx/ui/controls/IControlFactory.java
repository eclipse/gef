/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.ui.controls;

import javafx.embed.swt.FXCanvas;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The {@link IControlFactory} can be used in conjunction with
 * {@link FXControlAdapter} to create the wrapped SWT {@link Control} when the
 * surrounding {@link FXCanvas} changes.
 *
 * @author anyssen
 *
 * @param <T>
 *            The kind of {@link Control} to be created by this factory
 */
// TODO: move to Common.UI
public interface IControlFactory<T extends Control> {

	/**
	 * Creates the {@link Control} as a child of the given {@link Composite}.
	 *
	 * @param parent
	 *            The {@link Composite} in which to create the {@link Control}.
	 * @return The new {@link Control}.
	 */
	public T createControl(Composite parent);

}
