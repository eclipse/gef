/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.viewer;

import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.swtfx.SwtFXCanvas;

import com.google.inject.Inject;

public class FXCanvasViewer extends FXViewer {

	@Inject
	public FXCanvasViewer(final SwtFXCanvas canvas) {
		setSceneContainer(new FXCanvasSceneContainer(canvas));
	}
}
