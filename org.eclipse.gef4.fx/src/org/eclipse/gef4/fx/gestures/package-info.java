/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial text
 *
 *******************************************************************************/
/**
 * Several atomic JavaFX events can form a gesture. This package provides {@link org.eclipse.gef4.fx.gestures.FXMouseDragGesture},
 * {@link org.eclipse.gef4.fx.gestures.FXPinchSpreadGesture}, and {@link org.eclipse.gef4.fx.gestures.FXRotateGesture}.
 * A gesture is implemented as an abstract class, with different abstract methods for the various parts of the gesture,
 * i.e. <code>press()</code>, <code>drag()</code>, and <code>release()</code> in the case of an
 * {@link org.eclipse.gef4.fx.gestures.FXMouseDragGesture}. Besides, every gesture provides a <code>setScene()</code>
 * method to register/unregister JavaFX event listeners.
 */
package org.eclipse.gef4.fx.gestures;