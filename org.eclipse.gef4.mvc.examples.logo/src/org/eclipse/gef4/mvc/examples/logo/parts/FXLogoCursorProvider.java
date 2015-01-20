/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.parts;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import com.google.inject.Provider;

public class FXLogoCursorProvider implements Provider<Map<KeyCode, Cursor>> {

	@Override
	public Map<KeyCode, Cursor> get() {
		HashMap<KeyCode, Cursor> key2cursor = new HashMap<KeyCode, Cursor>();
		key2cursor.put(KeyCode.CONTROL, new ImageCursor(new Image(
				FXLogoCursorProvider.class.getResource("/undo_edit.gif")
						.toExternalForm())));
		return key2cursor;
	}

}
