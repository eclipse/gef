/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.tests;

import static org.junit.Assert.assertSame;

import org.eclipse.gef.fx.swt.canvas.FXCanvasEx;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Scene;

public class FXCanvasExTests {

	@Test
	public void getFXCanvas() throws Exception {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		FXCanvas canvas = new FXCanvas(shell, SWT.NONE);
		Group g = new Group();
		Scene scene = new Scene(g, 400, 400);
		canvas.setScene(scene);
		assertSame(canvas, FXCanvasEx.getFXCanvas(scene));
	}
}
