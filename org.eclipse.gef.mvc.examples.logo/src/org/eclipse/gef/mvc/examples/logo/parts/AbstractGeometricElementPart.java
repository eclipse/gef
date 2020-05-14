/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.parts;

import org.eclipse.gef.mvc.examples.logo.model.AbstractGeometricElement;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

public abstract class AbstractGeometricElementPart<N extends Node>
		extends AbstractContentPart<N> {

	private final ChangeListener<Object> contentObserver = new ChangeListener<Object>() {

		@Override
		public void changed(ObservableValue<? extends Object> observable,
				Object oldValue, Object newValue) {
			refreshVisual();
		}
	};

	@Override
	protected void doActivate() {
		super.doActivate();
		contentProperty().addListener(contentObserver);
	}

	@Override
	protected void doDeactivate() {
		contentProperty().removeListener(contentObserver);
		super.doDeactivate();
	}

	@Override
	protected void doRefreshVisual(N visual) {
		AbstractGeometricElement<?> content = getContent();
		if (visual.getEffect() != content.getEffect()) {
			visual.setEffect(content.getEffect());
		}
	}

	@Override
	public AbstractGeometricElement<?> getContent() {
		return (AbstractGeometricElement<?>) super.getContent();
	}

}
