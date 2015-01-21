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
package org.eclipse.gef4.mvc.fx.behaviors;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

public class FXCursorBehavior extends AbstractBehavior<Node> {

	public static final String CURSOR_PROVIDER_ROLE = "cursorProvider";

	private boolean inGesture = false;
	private Cursor initialCursor = null;

	private final EventHandler<? super KeyEvent> keyPressedHandler = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent event) {
			Map<KeyCode, Cursor> key2cursor = getCursorProvider().get();
			if (key2cursor.containsKey(event.getCode())) {
				changeCursor(key2cursor.get(event.getCode()));
			}
		}
	};

	private final EventHandler<? super KeyEvent> keyReleasedHandler = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent event) {
			restoreInitialCursor();
			// remove pressed listener
			getHost().getVisual().pressedProperty()
					.removeListener(pressedListener);
		}
	};

	private final EventHandler<? super MouseEvent> mouseEnteredHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			// stop listening on 'pressed' changes
			getHost().getVisual().pressedProperty()
					.removeListener(pressedListener);

			// register key handler
			Scene scene = getHost().getVisual().getScene();
			scene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
			scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);

			// query cursor provider
			Provider<Map<KeyCode, Cursor>> cursorProvider = getCursorProvider();
			Map<KeyCode, Cursor> key2cursor = cursorProvider.get();

			// set new cursor depending on modifier
			setNewCursor(event.isAltDown(), event.isControlDown(),
					event.isMetaDown(), event.isShiftDown(),
					event.isShortcutDown(), key2cursor, scene);
		}
	};

	private final ChangeListener<Boolean> pressedListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldPressed, Boolean newPressed) {
			if (!newPressed.booleanValue()) {
				getHost().getVisual().pressedProperty()
						.removeListener(pressedListener);
				if (!getHost().getVisual().isHover()) {
					restoreInitialCursor();
					// unregister key handler
					Scene scene = getHost().getVisual().getScene();
					scene.removeEventFilter(KeyEvent.KEY_PRESSED,
							keyPressedHandler);
					scene.removeEventFilter(KeyEvent.KEY_RELEASED,
							keyReleasedHandler);
				}
			}
		}
	};

	private final EventHandler<? super MouseEvent> mouseExitedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			// check if pressed
			if (getHost().getVisual().isPressed()) {
				// restore initial cursor when it's released
				getHost().getVisual().pressedProperty()
						.addListener(pressedListener);
			} else {
				restoreInitialCursor();
				// unregister key handler
				Scene scene = getHost().getVisual().getScene();
				scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
				scene.removeEventFilter(KeyEvent.KEY_RELEASED,
						keyReleasedHandler);
			}
		}
	};

	public FXCursorBehavior() {
	}

	@Override
	public void activate() {
		super.activate();
		Node visual = getHost().getVisual();
		visual.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		visual.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
	}

	protected void changeCursor(Cursor cursor) {
		Scene scene = getHost().getVisual().getScene();
		if (!inGesture) {
			// save initial cursor
			inGesture = true;
			initialCursor = scene.getCursor();
		}
		scene.setCursor(cursor);
		FXUtils.forceCursorUpdate(scene);
	}

	@Override
	public void deactivate() {
		restoreInitialCursor();
		// remove mouse listeners
		Node visual = getHost().getVisual();
		visual.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		visual.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
		// remove keyboard listeners
		Scene scene = getHost().getVisual().getScene();
		scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
		scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);
		// remove pressed listener
		getHost().getVisual().pressedProperty().removeListener(pressedListener);
		super.deactivate();
	}

	@SuppressWarnings("serial")
	protected Provider<Map<KeyCode, Cursor>> getCursorProvider() {
		return getHost().<Provider<Map<KeyCode, Cursor>>> getAdapter(
				AdapterKey.get(new TypeToken<Provider<Map<KeyCode, Cursor>>>() {
				}, CURSOR_PROVIDER_ROLE));
	}

	protected void restoreInitialCursor() {
		inGesture = false;
		Scene scene = getHost().getVisual().getScene();
		scene.setCursor(initialCursor);
		FXUtils.forceCursorUpdate(scene);
	}

	private void setNewCursor(boolean isAltDown, boolean isControlDown,
			boolean isMetaDown, boolean isShiftDown, boolean isShortcutDown,
			Map<KeyCode, Cursor> key2cursor, Scene scene) {
		Cursor cursor = null;
		if (isAltDown && key2cursor.containsKey(KeyCode.ALT)) {
			cursor = key2cursor.get(KeyCode.ALT);
		} else if (isControlDown && key2cursor.containsKey(KeyCode.CONTROL)) {
			cursor = key2cursor.get(KeyCode.CONTROL);
		} else if (isMetaDown && key2cursor.containsKey(KeyCode.META)) {
			cursor = key2cursor.get(KeyCode.META);
		} else if (isShiftDown && key2cursor.containsKey(KeyCode.SHIFT)) {
			cursor = key2cursor.get(KeyCode.SHIFT);
		} else if (isShortcutDown && key2cursor.containsKey(KeyCode.SHORTCUT)) {
			cursor = key2cursor.get(KeyCode.SHORTCUT);
		}
		if (cursor != null) {
			changeCursor(cursor);
		}
	}

}
