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
package org.eclipse.gef4.mvc.fx.example.parts;

import java.net.URL;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;

import org.eclipse.gef4.fx.nodes.FXBlendImageView;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;

public class FXExampleDeleteHandlePart extends AbstractFXHandlePart {

	public static final String IMG_DELETE = "/delete_obj.gif";
	public static final String IMG_DELETE_DISABLED = "/delete_obj_disabled.gif";

	private FXBlendImageView blendImageView;

	@Override
	protected void doRefreshVisual() {
		// check if we have a host
		SetMultimap<IVisualPart<Node>, String> anchorages = getAnchorages();
		if (anchorages.isEmpty()) {
			return;
		}

		// determine center location of host visual
		IVisualPart<Node> host = anchorages.keys().iterator().next();
		Node hostVisual = host.getVisual();

		refreshHandleLocation(hostVisual);
	}

	protected Image getHoverImage() {
		URL resource = FXExampleDeleteHandlePart.class.getResource(IMG_DELETE);
		if (resource == null) {
			throw new IllegalStateException("Cannot find resource <"
					+ IMG_DELETE + ">.");
		}
		return new Image(resource.toExternalForm());
	}

	protected Image getImage() {
		URL resource = FXExampleDeleteHandlePart.class
				.getResource(IMG_DELETE_DISABLED);
		if (resource == null) {
			throw new IllegalStateException("Cannot find resource <"
					+ IMG_DELETE_DISABLED + ">.");
		}
		return new Image(resource.toExternalForm());
	}

	@Override
	public Node getVisual() {
		if (blendImageView == null) {
			blendImageView = new FXBlendImageView();
			blendImageView.imageProperty().set(getImage());
			blendImageView.hoverImageProperty().set(getHoverImage());
		}
		return blendImageView;
	}

	protected void refreshHandleLocation(Node hostVisual) {
		Bounds hostBounds = hostVisual.getLayoutBounds();
		double cx = hostVisual.getLayoutX()
				+ hostVisual.getLayoutBounds().getMinX()
				+ hostBounds.getWidth();
		double cy = hostVisual.getLayoutY()
				+ hostVisual.getLayoutBounds().getMinY();
		Point2D locationInScene = hostVisual.getParent() == null ? new Point2D(
				cx, cy) : hostVisual.getParent().localToScene(cx, cy);
		Point2D locationInLocal = blendImageView.getParent().sceneToLocal(
				locationInScene);

		// position handle at center of host
		blendImageView.setLayoutX(locationInLocal.getX()
				- blendImageView.getLayoutBounds().getMinX());
		blendImageView.setLayoutY(locationInLocal.getY()
				- blendImageView.getLayoutBounds().getWidth() / 2
				- blendImageView.getLayoutBounds().getMinY());
	}

}
