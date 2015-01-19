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

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;

public class FXHoverHandleRootPart extends AbstractFXHandlePart<VBox> {

	public FXHoverHandleRootPart() {
		setAdapter(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY),
				new FXHoverOnHoverPolicy() {
					@Override
					public void hover(MouseEvent e) {
						// XXX: deactivate hover for this part
					}
				});
	}

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child,
			int index) {
		getVisual().getChildren().add(index, child.getVisual());
	}

	@Override
	protected VBox createVisual() {
		VBox vBox = new VBox();
		vBox.setPickOnBounds(true);
		return vBox;
	}

	@Override
	protected void doRefreshVisual(VBox visual) {
		// check if we have a host
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getAnchorages();
		if (anchorages.isEmpty()) {
			return;
		}

		// determine center location of host visual
		IVisualPart<Node, ? extends Node> anchorage = anchorages.keys()
				.iterator().next();
		refreshHandleLocation(anchorage.getVisual());
	}

	protected void refreshHandleLocation(Node hostVisual) {
		// position vbox top-right next to the host
		Bounds hostBounds = hostVisual.getLayoutBounds();
		double x = hostVisual.getLayoutX() + hostBounds.getMinX()
				+ hostBounds.getWidth();
		double y = hostVisual.getLayoutY() + hostBounds.getMinY();
		Point2D locationInScene = hostVisual.getParent() == null ? new Point2D(
				x, y) : hostVisual.getParent().localToScene(x, y);
		Point2D locationInLocal = getVisual().getParent().sceneToLocal(
				locationInScene);
		getVisual().setLayoutX(locationInLocal.getX());
		getVisual().setLayoutY(locationInLocal.getY());
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child,
			int index) {
		getVisual().getChildren().remove(index);
	}

}
