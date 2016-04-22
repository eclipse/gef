/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen  (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import org.eclipse.gef4.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;

/**
 * Abstract base class for {@link IConnectionRouter}s.
 */
public abstract class AbstractRouter implements IConnectionRouter {

	private ReadOnlyMapWrapper<AnchorKey, Point> positionHintsProperty = new ReadOnlyMapWrapperEx<>(
			FXCollections.<AnchorKey, Point> observableHashMap());

	@Override
	public ReadOnlyMapProperty<AnchorKey, Point> positionHintsProperty() {
		return positionHintsProperty.getReadOnlyProperty();
	}

}
