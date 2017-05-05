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

import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.services.IDisposable;

/**
 * The {@link IViewerAction} is a JFace {@link IAction} that implements
 * {@link IDisposable} and supports an {@link #init(IViewer)} method where the
 * active {@link IViewer} for the {@link IViewerAction} is provided.
 *
 * @author mwienand
 *
 */
public interface IViewerAction extends IAction, IViewerDependent {
}