/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzillas #450285 & #487070
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.mvc.examples.logo.model.AbstractFXGeometricElement;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef.mvc.models.GridModel;
import org.eclipse.gef.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;

public class FXGeometricModelPart extends AbstractFXContentPart<Group> {

	private final ChangeListener<? super Boolean> snapToGridObserver = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			applySnapToGrid(newValue);
		}
	};
	private final ChangeListener<? super Number> gridCellSizeObserver = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			applyGridCellSize(getContent().gridCellWidthProperty().get(), getContent().gridCellHeightProperty().get());
		}
	};

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().add(index, child.getVisual());
	}

	protected void applyGridCellSize(int gridCellWidth, int gridCellHeight) {
		getViewer().getAdapter(GridModel.class).setGridCellWidth(gridCellWidth);
		getViewer().getAdapter(GridModel.class).setGridCellHeight(gridCellHeight);
	}

	protected void applySnapToGrid(boolean snapToGrid) {
		getViewer().getAdapter(GridModel.class).setSnapToGrid(snapToGrid);
	}

	@Override
	protected Group createVisual() {
		Group visual = new Group();
		visual.setAutoSizeChildren(false);
		return visual;
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		// register snap-to-grid property listener
		getContent().snapToGridProperty().addListener(snapToGridObserver);
		applySnapToGrid(getContent().snapToGridProperty().get());
		getContent().gridCellWidthProperty().addListener(gridCellSizeObserver);
		getContent().gridCellHeightProperty().addListener(gridCellSizeObserver);
		applyGridCellSize(getContent().gridCellWidthProperty().get(), getContent().gridCellHeightProperty().get());
	}

	@Override
	protected void doAddContentChild(Object contentChild, int index) {
		if (!(contentChild instanceof AbstractFXGeometricElement)) {
			throw new IllegalArgumentException("Cannot add content child: wrong type!");
		}
		getContent().getShapeVisuals().add(index, (AbstractFXGeometricElement<?>) contentChild);
	}

	@Override
	protected void doDeactivate() {
		// unregister snap-to-grid property listener
		getContent().snapToGridProperty().removeListener(snapToGridObserver);
		super.doDeactivate();
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		List<Object> objs = new ArrayList<>();
		objs.addAll(getContent().getShapeVisuals());
		return objs;
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		// apply snap-to-grid from model
		applySnapToGrid(getContent().snapToGridProperty().get());
	}

	@Override
	protected void doRemoveContentChild(Object contentChild) {
		getContent().getShapeVisuals().remove(contentChild);
	}

	@Override
	public FXGeometricModel getContent() {
		return (FXGeometricModel) super.getContent();
	}

	@Override
	public boolean isFocusable() {
		return false;
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().remove(child.getVisual());
	}

}
