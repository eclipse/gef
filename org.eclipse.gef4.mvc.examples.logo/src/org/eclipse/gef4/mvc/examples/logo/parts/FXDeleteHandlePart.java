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
package org.eclipse.gef4.mvc.examples.logo.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javafx.scene.Node;
import javafx.scene.image.Image;

import org.eclipse.gef4.fx.nodes.FXBlendImageView;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.collect.SetMultimap;

public class FXDeleteHandlePart extends AbstractFXHandlePart<FXBlendImageView> {

	public static final String IMG_DELETE = "/delete_obj.gif";
	public static final String IMG_DELETE_DISABLED = "/delete_obj_disabled.gif";

	private boolean registered = false;
	private final PropertyChangeListener parentAnchoragesChangeListener = new PropertyChangeListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (IVisualPart.ANCHORAGES_PROPERTY.equals(evt.getPropertyName())) {
				onParentAnchoragesChanged(
						(SetMultimap<IVisualPart<Node, ? extends Node>, String>) evt
								.getOldValue(),
						(SetMultimap<IVisualPart<Node, ? extends Node>, String>) evt
								.getNewValue());
			}
		}
	};

	@Override
	protected FXBlendImageView createVisual() {
		FXBlendImageView blendImageView = new FXBlendImageView();
		blendImageView.imageProperty().set(getImage());
		blendImageView.hoverImageProperty().set(getHoverImage());
		return blendImageView;
	}

	@Override
	protected void doRefreshVisual(FXBlendImageView visual) {
		// automatically layed out by its parent
	}

	protected Image getHoverImage() {
		URL resource = FXDeleteHandlePart.class.getResource(IMG_DELETE);
		if (resource == null) {
			throw new IllegalStateException("Cannot find resource <"
					+ IMG_DELETE + ">.");
		}
		return new Image(resource.toExternalForm());
	}

	protected Image getImage() {
		URL resource = FXDeleteHandlePart.class
				.getResource(IMG_DELETE_DISABLED);
		if (resource == null) {
			throw new IllegalStateException("Cannot find resource <"
					+ IMG_DELETE_DISABLED + ">.");
		}
		return new Image(resource.toExternalForm());
	}

	protected void onParentAnchoragesChanged(
			SetMultimap<IVisualPart<Node, ? extends Node>, String> oldAnchorages,
			SetMultimap<IVisualPart<Node, ? extends Node>, String> newAnchorages) {
		if (!registered && getViewer() != null) {
			register(getViewer());
		}
	}

	@Override
	protected void register(IViewer<Node> viewer) {
		if (registered) {
			return;
		}
		super.register(viewer);
		registered = true;
	}

	@Override
	public void setParent(IVisualPart<Node, ? extends Node> newParent) {
		if (getParent() != null) {
			getParent().removePropertyChangeListener(
					parentAnchoragesChangeListener);
		}
		if (newParent != null) {
			newParent.addPropertyChangeListener(parentAnchoragesChangeListener);
		}
		super.setParent(newParent);
	}

	@Override
	protected void unregister(IViewer<Node> viewer) {
		if (!registered) {
			return;
		}
		super.unregister(viewer);
		registered = false;
	}

}
