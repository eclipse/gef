/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.fx.examples.snippets;

import org.eclipse.gef.fx.examples.AbstractFxExample;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.VBox;

public class GesturesSnippet extends AbstractFxExample {

	public static void main(String[] args) {
		launch();
	}

	public GesturesSnippet() {
		super("GesturesSnippet");
	}

	@Override
	public Scene createScene() {
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.getChildren().add(new Label("Try some multitouch gestures"));
		final Label display = new Label();
		root.getChildren().add(display);
		root.addEventHandler(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				System.out.println("SCROLL " + event);
				System.out.println(event.isInertia());
				display.setText(event.getEventType().toString());
			}
		});
		root.addEventHandler(ZoomEvent.ANY, new EventHandler<ZoomEvent>() {
			@Override
			public void handle(ZoomEvent event) {
				System.out.println("ZOOM " + event);
				display.setText(event.getEventType().toString());
			}
		});
		root.addEventHandler(RotateEvent.ANY, new EventHandler<RotateEvent>() {
			@Override
			public void handle(RotateEvent event) {
				System.out.println("ROTATE " + event);
				display.setText(event.getEventType().toString());
			}
		});
		root.addEventHandler(SwipeEvent.ANY, new EventHandler<SwipeEvent>() {
			@Override
			public void handle(SwipeEvent event) {
				System.out.println("SWIPE " + event);
				display.setText(event.getEventType().toString());
			}
		});
		return new Scene(root, 300, 300);
	}

}
