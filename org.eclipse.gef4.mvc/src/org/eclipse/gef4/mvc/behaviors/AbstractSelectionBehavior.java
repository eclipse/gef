/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - multi selection handles in root part
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.IProvider;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The AbstractSelectionFeedbackPolicy is responsible for creating and removing
 * selection feedback.
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public abstract class AbstractSelectionBehavior<V> extends AbstractBehavior<V>
		implements PropertyChangeListener {

	private List<IHandlePart<V>> handles;

	private IProvider<IGeometry> feedbackGeometryProvider = new IProvider<IGeometry>() {
		@Override
		public IGeometry get() {
			return getFeedbackGeometry();
		}
	};

	private IProvider<IGeometry> handleGeometryProvider = new IProvider<IGeometry>() {
		@Override
		public IGeometry get() {
			return getHandleGeometry();
		}
	};
	
	public IProvider<IGeometry> getFeedbackGeometryProvider() {
		return feedbackGeometryProvider;
	}
	
	public IProvider<IGeometry> getHandleGeometryProvider() {
		return handleGeometryProvider;
	}

	/**
	 * Returns an {@link IGeometry} for which visual selection feedback will be
	 * provided.
	 * 
	 * @return an {@link IGeometry} determining feedback positions
	 */
	protected abstract IGeometry getFeedbackGeometry();

	/**
	 * Returns an {@link IGeometry} for which selection handles will be
	 * provided.
	 * <p>
	 * Per default, the {@link #getFeedbackGeometry() feedback geometry} is
	 * returned.
	 * 
	 * @return an {@link IGeometry} determining handle positions
	 */
	protected abstract IGeometry getHandleGeometry();

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getSelectionModel()
				.addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getSelectionModel()
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(ISelectionModel.SELECTION_PROPERTY)) {
			List<IContentPart<V>> oldSelection = (List<IContentPart<V>>) event
					.getOldValue();
			List<IContentPart<V>> newSelection = (List<IContentPart<V>>) event
					.getNewValue();

			// multi-selection handles for the root part
			if (getHost() instanceof IRootPart) {
				if (oldSelection.size() > 1) {
					removeHandles(oldSelection);
				}
				if (newSelection.size() > 1) {
					addHandles(newSelection);
				}
				return;
			}

			boolean inOld = oldSelection.contains(getHost());
			boolean inNew = newSelection.contains(getHost());

			if (inOld) {
				removeHandles(Collections
						.singletonList((IContentPart<V>) getHost()));
				hideFeedback();
			}

			if (inNew) {
				if (newSelection.get(0) == getHost()) {
					showPrimaryFeedback();
					if (newSelection.size() <= 1) {
						addHandles(Collections
								.singletonList((IContentPart<V>) getHost()));
					}
				} else {
					showSecondaryFeedback();
				}
			}
		}
	}

	protected void addHandles(List<IContentPart<V>> anchorages) {
		handles = createHandles(anchorages);
		BehaviorUtils.<V> addHandles(getHost().getRoot(), anchorages, handles);
	}

	protected void removeHandles(List<IContentPart<V>> anchorages) {
		if (handles != null && !handles.isEmpty()) {
			BehaviorUtils.<V> removeHandles(getHost().getRoot(), anchorages,
					handles);
			handles.clear();
		}
	}

	protected abstract void hideFeedback();

	public List<IHandlePart<V>> createHandles(List<IContentPart<V>> targets) {
		IVisualPart<V> host = getHost();
		IHandlePartFactory<V> factory = getHandlePartFactory(host);
		List<IHandlePart<V>> handleParts = factory.createHandleParts(targets,
				this, Collections.emptyMap());
		return handleParts;
	}

	private IHandlePartFactory<V> getHandlePartFactory(IVisualPart<V> host) {
		return host.getRoot().getViewer().getHandlePartFactory();
	}

	protected abstract void showSecondaryFeedback();

	protected abstract void showPrimaryFeedback();

}
