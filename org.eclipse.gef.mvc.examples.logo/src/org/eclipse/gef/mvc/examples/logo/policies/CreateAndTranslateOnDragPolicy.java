/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.policies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef.mvc.examples.logo.parts.FXGeometricModelPart;
import org.eclipse.gef.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef.mvc.examples.logo.parts.PaletteElementPart;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.policies.AbstractFXInteractionPolicy;
import org.eclipse.gef.mvc.fx.policies.IFXOnDragPolicy;
import org.eclipse.gef.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.operations.DeselectOperation;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.CreationPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.reflect.TypeToken;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class CreateAndTranslateOnDragPolicy extends AbstractFXInteractionPolicy implements IFXOnDragPolicy {

	private FXGeometricShapePart createdShapePart;
	private Map<AdapterKey<? extends IFXOnDragPolicy>, IFXOnDragPolicy> dragPolicies;

	@Override
	public void drag(MouseEvent event, Dimension delta) {
		if (createdShapePart == null) {
			return;
		}

		// forward drag events to bend target part
		if (dragPolicies != null) {
			for (IFXOnDragPolicy dragPolicy : dragPolicies.values()) {
				dragPolicy.drag(event, delta);
			}
		}
	}

	@Override
	public void abortDrag() {
		if (createdShapePart == null) {
			return;
		}

		// forward event to bend target part
		if (dragPolicies != null) {
			for (IFXOnDragPolicy dragPolicy : dragPolicies.values()) {
				dragPolicy.abortDrag();
			}
		}

		createdShapePart = null;
		dragPolicies = null;
	}

	protected FXViewer getContentViewer() {
		Map<AdapterKey<? extends IViewer<Node>>, IViewer<Node>> viewers = getHost().getRoot().getViewer().getDomain()
				.getViewers();
		for (Entry<AdapterKey<? extends IViewer<Node>>, IViewer<Node>> e : viewers.entrySet()) {
			if (FXDomain.CONTENT_VIEWER_ROLE.equals(e.getKey().getRole())) {
				return (FXViewer) e.getValue();
			}
		}
		throw new IllegalStateException("Cannot find content viewer!");
	}

	@Override
	public PaletteElementPart getHost() {
		return (PaletteElementPart) super.getHost();
	}

	protected Point getLocation(MouseEvent e) {
		Point2D location = ((FXViewer) getHost().getRoot().getViewer()).getCanvas().getContentGroup()
				.sceneToLocal(e.getSceneX(), e.getSceneY());
		return new Point(location.getX(), location.getY());
	}

	@Override
	public void hideIndicationCursor() {
	}

	@SuppressWarnings("serial")
	@Override
	public void startDrag(MouseEvent event) {
		// find model part
		IRootPart<Node, ? extends Node> contentRoot = getContentViewer().getRootPart();
		IVisualPart<Node, ? extends Node> modelPart = contentRoot.getChildrenUnmodifiable().get(0);
		if (!(modelPart instanceof FXGeometricModelPart)) {
			throw new IllegalStateException("Cannot find FXGeometricModelPart.");
		}

		// copy the prototype
		FXGeometricShape copy = getHost().getContent().getCopy();
		// determine coordinates of prototype's origin in model coordinates
		Point2D localToScene = getHost().getVisual().localToScene(0, 0);
		Point2D originInModel = modelPart.getVisual().sceneToLocal(localToScene.getX(), localToScene.getY());
		// initially move to the originInModel
		double[] matrix = copy.getTransform().getMatrix();
		copy.getTransform().setTransform(matrix[0], matrix[1], matrix[2], matrix[3], originInModel.getX(),
				originInModel.getY());

		// create copy of host's geometry using CreationPolicy from root part
		CreationPolicy<Node> creationPolicy = contentRoot.getAdapter(new TypeToken<CreationPolicy<Node>>() {
		});
		init(creationPolicy);
		createdShapePart = (FXGeometricShapePart) creationPolicy.create(copy, (FXGeometricModelPart) modelPart,
				HashMultimap.<IContentPart<Node, ? extends Node>, String> create());
		commit(creationPolicy);

		// disable refresh visuals for the created shape part
		storeAndDisableRefreshVisuals(createdShapePart);

		// build operation to deselect all but the new part
		List<IContentPart<Node, ? extends Node>> toBeDeselected = new ArrayList<>(
				getContentViewer().getAdapter(new TypeToken<SelectionModel<Node>>() {
				}).getSelectionUnmodifiable());
		toBeDeselected.remove(createdShapePart);
		DeselectOperation<Node> deselectOperation = new DeselectOperation<>(getContentViewer(), toBeDeselected);

		// execute on stack
		try {
			getHost().getRoot().getViewer().getDomain().execute(deselectOperation, new NullProgressMonitor());
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}

		// find drag target part
		dragPolicies = createdShapePart.getAdapters(FXClickDragTool.ON_DRAG_POLICY_KEY);
		if (dragPolicies != null) {
			for (IFXOnDragPolicy dragPolicy : dragPolicies.values()) {
				dragPolicy.startDrag(event);
			}
		}
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		if (createdShapePart == null) {
			return;
		}

		// forward event to bend target part
		if (dragPolicies != null) {
			for (IFXOnDragPolicy dragPolicy : dragPolicies.values()) {
				dragPolicy.endDrag(e, delta);
			}
		}

		restoreRefreshVisuals(createdShapePart);
		createdShapePart = null;
		dragPolicies = null;
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

}
