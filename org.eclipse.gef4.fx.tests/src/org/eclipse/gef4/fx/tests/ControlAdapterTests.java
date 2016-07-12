/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
package org.eclipse.gef4.fx.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.eclipse.gef4.fx.swt.controls.FXControlAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Scene;

public class ControlAdapterTests {

	@Test
	public void getFXCanvas() throws Exception {
		Display display = new Display();
		Shell shell = new Shell(display);
		FXCanvas canvas = new FXCanvas(shell, SWT.NONE);
		Button button = new Button(canvas, SWT.PUSH);
		Group g = new Group();
		Scene scene = new Scene(g, 400, 400);
		canvas.setScene(scene);

		FXControlAdapter<Button> controlAdapter = new FXControlAdapter<>(button);
		Method m = FXControlAdapter.class.getDeclaredMethod("getFXCanvas", Scene.class);
		m.setAccessible(true);
		assertEquals(canvas, m.invoke(controlAdapter, scene));
	}
}
