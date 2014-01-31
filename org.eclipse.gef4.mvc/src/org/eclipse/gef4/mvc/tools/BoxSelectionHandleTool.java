/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef4.mvc.models.IFocusModel;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

/**
 * The HandleTool creates and removes handle parts dependent on the
 * {@link ISelectionModel}, {@link IHoverModel}, and {@link IFocusModel}. For
 * single selection the selected part is asked to create the handles by itself
 * via its Abstract
 * 
 * @author anyssen
 * 
 * @param <V>
 *            type of visual
 */
public class BoxSelectionHandleTool<V> extends AbstractTool<V> implements
		PropertyChangeListener {

	private List<IHandlePart<V>> handleParts;

	@Override
	public void activate() {
		super.activate();
		IVisualPartViewer<V> viewer = getDomain().getViewer();
		viewer.getSelectionModel().addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		IVisualPartViewer<V> viewer = getDomain().getViewer();
		viewer.getSelectionModel().removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		IRootPart<V> rootPart = getRoot();
		if (rootPart == null) {
			return;
		}

		if (ISelectionModel.SELECTION_PROPERTY.equals(evt.getPropertyName())) {
			onSelectionChange(rootPart, evt);
		}
	}

	private void onSelectionChange(IRootPart<V> rootPart,
			PropertyChangeEvent evt) {
		@SuppressWarnings("unchecked")
		List<IContentPart<V>> newSelection = (List<IContentPart<V>>) evt
				.getNewValue();
		@SuppressWarnings("unchecked")
		List<IContentPart<V>> oldSelection = (List<IContentPart<V>>) evt
				.getOldValue();
		removeHandles(rootPart, oldSelection);
		if (newSelection.size() > 1) {
			// create multi selection handles
			addHandles(rootPart, newSelection);
		}
	}

	private void addHandles(IRootPart<V> rootPart,
			List<IContentPart<V>> newSelection) {
		// attach to anchorages
		if (getHandlePartFactory() != null) {
			handleParts = getHandlePartFactory().createSelectionHandleParts(
					newSelection);
		}
		if (handleParts != null && !handleParts.isEmpty()) {
			rootPart.addHandleParts(handleParts);
			for (IContentPart<V> selected : newSelection) {
				for (IVisualPart<V> handlePart : handleParts) {
					selected.addAnchored(handlePart);
				}
			}
		}
	}

	private void removeHandles(IRootPart<V> rootPart,
			List<IContentPart<V>> oldSelection) {
		// attach to anchorages
		if (handleParts != null && !handleParts.isEmpty()) {
			rootPart.removeHandleParts(handleParts);
			for (IContentPart<V> selected : oldSelection) {
				for (IVisualPart<V> handlePart : handleParts) {
					selected.removeAnchored(handlePart);
				}
			}
			handleParts.clear();
		}
	}

	private IHandlePartFactory<V> getHandlePartFactory() {
		return getDomain().getViewer().getHandlePartFactory();
	}

	private IRootPart<V> getRoot() {
		return getDomain().getViewer().getRootPart();
	}

}
