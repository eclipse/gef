/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

/**
 * TODO: Add semantic values to this property.
 * 
 * The PositionProperty is changed when the (global) position of a corresponding
 * JavaFX {@link Node} changes, i.e. when the {@link Transform} or
 * {@link Bounds} properties of any Node in the hierarchy changes. Therefore,
 * you can add {@link ChangeListener}s to the PositionProperty to get notified
 * on visual node changes.
 * 
 * @author mwienand
 * 
 */
public class PositionProperty extends SimpleObjectProperty<Object> {
}
