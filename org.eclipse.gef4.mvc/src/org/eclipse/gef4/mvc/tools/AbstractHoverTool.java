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
package org.eclipse.gef4.mvc.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef4.mvc.models.IContentModel;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.IHoverPolicy;

public class AbstractHoverTool<V> extends AbstractTool<V> implements
		PropertyChangeListener {

	protected IHoverModel<V> getHoverModel() {
		return getDomain().getViewer().getHoverModel();
	}

	@Override
	protected void registerListeners() {
		getDomain().getViewer().getContentModel()
				.addPropertyChangeListener(this);
		getDomain().getViewer().getSelectionModel()
				.addPropertyChangeListener(this);
	}

	@Override
	protected void unregisterListeners() {
		getDomain().getViewer().getSelectionModel()
				.removePropertyChangeListener(this);
		getDomain().getViewer().getContentModel()
				.removePropertyChangeListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		/*
		 * TODO: Viewer should flush interaction model data when contents
		 * changes.
		 */
		if (evt.getPropertyName().equals(IContentModel.CONTENTS_PROPERTY)) {
			hover(null);
		} else if (ISelectionModel.SELECTION_PROPERTY.equals(evt
				.getPropertyName())) {
			if (((List<IContentPart<V>>) evt.getNewValue())
					.contains(getHoverModel().getHover()))
				hover(null);
		}
	}

	public void hover(IContentPart<V> hovered) {
		if (hovered == null || getToolPolicy(hovered) != null
				&& getToolPolicy(hovered).isHoverable()) {
			getHoverModel().setHover(hovered);
		}
	}

	@SuppressWarnings("unchecked")
	private IHoverPolicy<V> getToolPolicy(IContentPart<V> hovered) {
		return hovered.getBound(IHoverPolicy.class);
	}

}
