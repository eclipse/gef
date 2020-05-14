/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jan Köhnlein (itemis AG) - initial API and implementation (#427106)
 *
 *******************************************************************************/
package org.eclipse.gef.fx.examples.swt;

import org.eclipse.gef.fx.swt.canvas.FXCanvasEx;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.VBox;

public class SWT2FXGestureConversionDemo {

	private static Scene createScene() {
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.getChildren().add(new Label("Try some multitouch gestures"));
		final Label display = new Label();
		root.getChildren().add(display);
		root.addEventHandler(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				display.setText(event.getEventType().toString());
				System.out.println(event);
			}
		});
		root.addEventHandler(ZoomEvent.ANY, new EventHandler<ZoomEvent>() {
			@Override
			public void handle(ZoomEvent event) {
				display.setText(event.getEventType().toString());
				System.out.println(event);
			}
		});
		root.addEventHandler(RotateEvent.ANY, new EventHandler<RotateEvent>() {
			@Override
			public void handle(RotateEvent event) {
				display.setText(event.getEventType().toString());
				System.out.println(event);
			}
		});
		root.addEventHandler(SwipeEvent.ANY, new EventHandler<SwipeEvent>() {
			@Override
			public void handle(SwipeEvent event) {
				display.setText(event.getEventType().toString());
				System.out.println(event);
			}
		});
		return new Scene(root, 400, 300);
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		shell.setText("SWT to FX Gesture Conversion Demo");
		FXCanvasEx canvas = new FXCanvasEx(shell, SWT.NONE);
		Scene scene = createScene();
		canvas.setScene(scene);
		shell.open();
		shell.pack();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
