/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Jan Köhnlein (itemis AG) - Support for multi-touch gestures (#427106)
 *
 *******************************************************************************/
package org.eclipse.gef4.swtfx;

import java.util.ArrayList;
import java.util.List;

import javafx.embed.swt.FXCanvas;
import javafx.stage.Window;

import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.gef4.swtfx.gestures.SwtToFXGestureConverter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

public class SwtFXCanvas extends FXCanvas {

	private SwtToFXGestureConverter gestureConverter;
	private List<FocusListener> focusListeners;

	public SwtFXCanvas(Composite parent, int style) {
		super(parent, style);
		gestureConverter = new SwtToFXGestureConverter(this);

		// add a focus listener to propagate focus events also to FocusListeners
		// registered on this canvas. FXCanvas will not notify them, as all
		// focus events are only forwarded to the embedded stage.
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				for (FocusListener l : focusListeners) {
					l.focusGained(e);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				for (FocusListener l : focusListeners) {
					l.focusLost(e);
				}
			}
		});
		focusListeners = new ArrayList<FocusListener>();
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		if (focusListeners == null) {
			// if we have not created our local field yet, we are within
			// initialization, where focus listeners have to be registered via
			// the super implementation.
			super.addFocusListener(listener);
		} else {
			focusListeners.add(listener);
		}
	}

	@Override
	public void dispose() {
		gestureConverter.dispose();
		focusListeners.clear();
		focusListeners = null;
		super.dispose();
	}

	public Window getStage() {
		return ReflectionUtils.getPrivateField(this, "stage");
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		if (focusListeners == null) {
			super.removeFocusListener(listener);
		} else {
			focusListeners.remove(listener);
		}
	}
}
