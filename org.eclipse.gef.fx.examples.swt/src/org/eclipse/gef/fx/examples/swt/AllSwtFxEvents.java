/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.examples.swt;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;

public class AllSwtFxEvents extends AbstractFxSwtExample {

	public static void main(String[] args) {
		new AllSwtFxEvents();
	}

	public AllSwtFxEvents() {
		super("Embedded Input Events (SwtFx)");
	}

	@Override
	public Scene createScene() {
		Scene scene = new Scene(new Group(), 400, 400);
		scene.addEventFilter(InputEvent.ANY, new EventHandler<InputEvent>() {
			@Override
			public void handle(InputEvent event) {
				System.out.println(event);
			}
		});
		return scene;
	}

}
