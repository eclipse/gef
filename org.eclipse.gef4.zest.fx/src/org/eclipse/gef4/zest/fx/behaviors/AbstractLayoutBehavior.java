/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.ILayoutModel;

public abstract class AbstractLayoutBehavior extends AbstractBehavior<Node> {

	private GraphLayoutContext glc;

	private PropertyChangeListener layoutContextListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ILayoutModel.LAYOUT_CONTEXT_PROPERTY.equals(evt
					.getPropertyName())) {
				onLayoutContextChange((GraphLayoutContext) evt.getOldValue(),
						(GraphLayoutContext) evt.getNewValue());
			}
		}
	};

	private ChangeListener<? super Bounds> layoutBoundsListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldBounds, Bounds newBounds) {
			onBoundsChange(oldBounds, newBounds);
		}
	};

	private Runnable onFlushChanges = new Runnable() {
		@Override
		public void run() {
			onFlushChanges();
		}
	};

	@Override
	public void activate() {
		super.activate();
		getDomainAdapter(ILayoutModel.class).addPropertyChangeListener(
				layoutContextListener);
		getHost().getVisual().layoutBoundsProperty()
				.addListener(layoutBoundsListener);
	}

	@Override
	public void deactivate() {
		getHost().getVisual().layoutBoundsProperty()
				.removeListener(layoutBoundsListener);
		getDomainAdapter(ILayoutModel.class).removePropertyChangeListener(
				layoutContextListener);
		super.deactivate();
	}

	public <T> T getDomainAdapter(Class<T> key) {
		return getHost().getRoot().getViewer().getDomain().getAdapter(key);
	}

	protected abstract void initializeLayout(GraphLayoutContext glc);

	protected abstract void onBoundsChange(Bounds oldBounds, Bounds newBounds);

	protected abstract void onFlushChanges();

	protected void onLayoutContextChange(GraphLayoutContext oldGlc,
			GraphLayoutContext newGlc) {
		if (oldGlc != null && oldGlc == glc) {
			oldGlc.removeOnFlushChanges(onFlushChanges);
			glc = null;
		}
		if (newGlc != null) {
			glc = newGlc;
			newGlc.addOnFlushChanges(onFlushChanges);
			initializeLayout(newGlc);
		}
	}

}
