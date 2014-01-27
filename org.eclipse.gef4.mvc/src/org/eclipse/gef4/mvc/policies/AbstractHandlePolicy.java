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
package org.eclipse.gef4.mvc.policies;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.models.IFocusModel;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

/**
 * A HandlePolicy is responsible for creating hover, selection, and focus
 * handles for one specific content part. The policy is referred to by the
 * HandleTool. The policy is only used for single selection/hover/focus,
 * multiple selection/hover/focus will cause the HandleTool to create handles
 * for all selected/hovered/focused parts.
 * 
 * @author wienand
 * 
 */
abstract public class AbstractHandlePolicy<V> extends AbstractEditPolicy<V>
		implements PropertyChangeListener {

	private List<IHandlePart<V>> selectionHandles;
	private List<IHandlePart<V>> hoverHandles;
	private List<IHandlePart<V>> focusHandles;

	@Override
	public void activate() {
		super.activate();
		IVisualPartViewer<V> viewer = getHost().getRoot().getViewer();
		viewer.getSelectionModel().addPropertyChangeListener(this);
		viewer.getHoverModel().addPropertyChangeListener(this);
		viewer.getFocusModel().addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		IVisualPartViewer<V> viewer = getHost().getRoot().getViewer();
		viewer.getSelectionModel().removePropertyChangeListener(this);
		viewer.getHoverModel().removePropertyChangeListener(this);
		viewer.getFocusModel().removePropertyChangeListener(this);
		super.deactivate();
	}

	/**
	 * Responsible for creating handle parts for the host content part if the
	 * host content part is the only selected part.
	 */
	public void createSelectionHandles() {
		selectionHandles = createHandles();
		getHost().getRoot().addHandleParts(selectionHandles);
	}

	/*
	 * TODO: Differentiate between different handle creation methods.
	 */
	private List<IHandlePart<V>> createHandles() {
		IVisualPart<V> host = getHost();
		IHandlePartFactory<V> factory = getHandlePartFactory(host);
		if (host instanceof IContentPart) {
			List<IContentPart<V>> parts = new ArrayList<IContentPart<V>>(1);
			parts.add((IContentPart<V>) host);
			List<IHandlePart<V>> handleParts = factory.createHandleParts(parts);
			return handleParts;
		}
		return Collections.<IHandlePart<V>> emptyList();
	}

	private IHandlePartFactory<V> getHandlePartFactory(IVisualPart<V> host) {
		return host.getRoot().getViewer().getHandlePartFactory();
	}

	/**
	 * Responsible for creating handle parts for the host content part if the
	 * host content part has keyboard focus.
	 */
	public void createFocusHandles() {
		focusHandles = createHandles();
	}

	/**
	 * Responsible for creating handle parts for the host content part if the
	 * host content part is hovered.
	 */
	public void createHoverHandles() {
		hoverHandles = createHandles();
		getHost().getRoot().addHandleParts(hoverHandles);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		IRootVisualPart<V> rootPart = getHost().getRoot();
		if (rootPart == null) {
			return;
		}

		if (ISelectionModel.SELECTION_PROPERTY.equals(evt.getPropertyName())) {
			List<IContentPart<V>> oldSelection = (List<IContentPart<V>>) evt
					.getOldValue();
			List<IContentPart<V>> newSelection = (List<IContentPart<V>>) evt
					.getNewValue();
			onSelectionChange(rootPart, oldSelection, newSelection);
		} else if (IHoverModel.HOVER_PROPERTY.equals(evt.getPropertyName())) {
			IContentPart<V> oldHovered = (IContentPart<V>) evt.getOldValue();
			IContentPart<V> newHovered = (IContentPart<V>) evt.getNewValue();
			onHoverChange(rootPart, oldHovered, newHovered);
		} else if (IFocusModel.FOCUS_PROPERTY.equals(evt.getPropertyName())) {
			IContentPart<V> oldFocused = (IContentPart<V>) evt.getOldValue();
			IContentPart<V> newFocused = (IContentPart<V>) evt.getNewValue();
			onFocusChange(rootPart, oldFocused, newFocused);
		}
	}

	private void onSelectionChange(IRootVisualPart<V> rootPart,
			List<IContentPart<V>> oldSelection,
			List<IContentPart<V>> newSelection) {
		boolean inOld = oldSelection.contains(getHost());
		boolean inNew = newSelection.contains(getHost());
		if (inOld && !inNew || newSelection.size() > 1) {
			removeSelectionHandles();
		} else if (!inOld && inNew) {
			createSelectionHandles();
		}
	}

	private void removeSelectionHandles() {
		if (selectionHandles != null && !selectionHandles.isEmpty()) {
			getHost().getRoot().removeHandleParts(selectionHandles);
			selectionHandles.clear();
		}
	}

	private void onHoverChange(IRootVisualPart<V> rootPart,
			IContentPart<V> oldHovered, IContentPart<V> newHovered) {
		if (oldHovered == getHost()) {
			removeHoverHandles();
		} else if (newHovered == getHost()) {
			createHoverHandles();
		}
	}

	private void removeHoverHandles() {
		if (hoverHandles != null && !hoverHandles.isEmpty()) {
			getHost().getRoot().removeHandleParts(hoverHandles);
			hoverHandles.clear();
		}
	}
	
	private void removeFocusHandles() {
		if (focusHandles != null && !focusHandles.isEmpty()) {
			getHost().getRoot().removeHandleParts(focusHandles);
			focusHandles.clear();
		}
	}

	private void onFocusChange(IRootVisualPart<V> rootPart,
			IContentPart<V> oldFocused, IContentPart<V> newFocused) {
		if (oldFocused == getHost()) {
			removeFocusHandles();
		} else if (newFocused == getHost()) {
			createFocusHandles();
		}
	}

}
