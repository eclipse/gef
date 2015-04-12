/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Polygon;

import org.eclipse.gef4.fx.nodes.FXImageViewHoverOverlay;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.collect.SetMultimap;

public class FXRotateHandlePart extends AbstractFXHandlePart<FXImageViewHoverOverlay> {

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

	private Image createIcon(Color color) {
		// icon divided into 3 parts: dashed arc, solid arc, arrow head
		// the dashed arc fades out the icon
		Arc dashedArc = new Arc(0, 0, 8, 8, 270, 90);
		dashedArc.setFill(null);
		dashedArc.setStroke(color);
		dashedArc.setStrokeWidth(1.5);
		dashedArc.getStrokeDashArray().setAll(1d, 2d);
		// the solid arc represents circular motion
		Arc solidArc = new Arc(0, 0, 8, 8, 90, 180);
		solidArc.setFill(null);
		solidArc.setStroke(color);
		solidArc.setStrokeWidth(2.5);
		// the arrow indicates direction
		Polygon arrow = new Polygon(0, -10, 0, -6, 4, -8);
		arrow.setFill(color);
		arrow.setStroke(color);
		arrow.setStrokeWidth(2.5);
		// assemble together in one group
		Group group = new Group(dashedArc, solidArc, arrow);
		// scale down to match the size of other handles
		group.setScaleX(0.8);
		group.setScaleY(0.8);
		return group.snapshot(new SnapshotParameters() {
			{
				setFill(Color.TRANSPARENT);
			}
		}, null);
	}

	@Override
	protected FXImageViewHoverOverlay createVisual() {
		// make it pickable on bounds, so that the user can click into the icon
		// even when there is empty space
		FXImageViewHoverOverlay blendImageView = new FXImageViewHoverOverlay();
		blendImageView.getBaseImageView().setPickOnBounds(true);
		blendImageView.getOverlayImageView().setPickOnBounds(true);
		// load images (disabled/active)
		blendImageView.baseImageProperty().set(getImage());
		blendImageView.overlayImageProperty().set(getHoverImage());
		return blendImageView;
	}

	@Override
	protected void doRefreshVisual(FXImageViewHoverOverlay visual) {
		// automatically layed out by its parent
	}

	protected Image getHoverImage() {
		return createIcon(Color.BLUE);
	}

	protected Image getImage() {
		return createIcon(Color.BLACK);
	}

	/**
	 * A visual part is registered, and thus put into the visual-part-map, as
	 * soon as it obtains a link to the viewer via either its parent part or its
	 * anchorages.
	 * <p>
	 * However, this handle part has a parent handle part, and the corresponding
	 * parent-child relationship is established before the parent handle part
	 * obtains a link to the viewer. Therefore, this handle part would not be
	 * registered correctly, when the parent handle part actually obtains a link
	 * to the viewer.
	 * <p>
	 * Since we know that handle parts obtain a link to the viewer via their
	 * anchorages, this method reacts to changes to the anchorages of its parent
	 * handle part, and registers this part as soon as a link to the viewer is
	 * obtained.
	 *
	 * @param oldParentAnchorages
	 * @param newParentAnchorages
	 */
	protected void onParentAnchoragesChanged(
			SetMultimap<IVisualPart<Node, ? extends Node>, String> oldParentAnchorages,
			SetMultimap<IVisualPart<Node, ? extends Node>, String> newParentAnchorages) {
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
