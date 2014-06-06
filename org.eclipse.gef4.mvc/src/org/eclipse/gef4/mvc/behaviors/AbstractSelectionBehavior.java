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
import org.eclipse.gef4.mvc.bindings.IProvider;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;

/**
 * The AbstractSelectionFeedbackPolicy is responsible for creating and removing
 * selection feedback.
 * 
 * @author anyssen
 * 
 * @param <VR>
 */
public abstract class AbstractSelectionBehavior<VR> extends AbstractBehavior<VR>
		implements PropertyChangeListener {

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

	public void refreshFeedback() {
		List<IContentPart<VR>> selected = getHost().getRoot().getViewer()
				.getSelectionModel().getSelected();
		removeFeedback(selected);
		addFeedback(selected);
	}

	public void refreshHandles() {
		List<IContentPart<VR>> selected = getHost().getRoot().getViewer()
				.getSelectionModel().getSelected();
		removeHandles(selected);
		addHandles(selected);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(ISelectionModel.SELECTION_PROPERTY)) {
			List<IContentPart<VR>> oldSelection = (List<IContentPart<VR>>) event
					.getOldValue();
			List<IContentPart<VR>> newSelection = (List<IContentPart<VR>>) event
					.getNewValue();

			// multi-selection handles for the root part
			if (getHost() instanceof IRootPart) {
				if (oldSelection.size() > 1) {
					removeHandles(oldSelection);
					removeFeedback(oldSelection);
				}
				if (newSelection.size() > 1) {
					addFeedback(newSelection);
					addHandles(newSelection);
				}
				return;
			}

			boolean inOld = oldSelection.contains(getHost());
			boolean inNew = newSelection.contains(getHost());

			if (inOld) {
				removeHandles(Collections
						.singletonList((IContentPart<VR>) getHost()));
				removeFeedback(Collections
						.singletonList((IContentPart<VR>) getHost()));
			}

			if (inNew) {
				addFeedback(Collections
						.singletonList((IContentPart<VR>) getHost()));
				if (newSelection.get(0) == getHost()) {
					if (newSelection.size() <= 1) {
						addHandles(Collections
								.singletonList((IContentPart<VR>) getHost()));
					}
				}
			}
		}
	}
}
