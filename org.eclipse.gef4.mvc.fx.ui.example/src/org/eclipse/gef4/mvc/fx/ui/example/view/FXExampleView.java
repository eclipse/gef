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
package org.eclipse.gef4.mvc.fx.ui.example.view;

import java.util.Collections;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXZoomBehavior;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.example.domain.FXExampleDomain;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleContentPartFactory;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.policies.FXZoomOnScrollPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXZoomOnZoomPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXScrollTool;
import org.eclipse.gef4.mvc.fx.tools.FXZoomTool;
import org.eclipse.gef4.mvc.fx.ui.view.FXView;
import org.eclipse.gef4.mvc.fx.viewer.IFXViewer;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

public class FXExampleView extends FXView {

	@Override
	protected void configureViewer(IFXViewer viewer) {
		super.configureViewer(viewer);

		// behaviors
		viewer.getRootPart().setAdapter(FXSelectionBehavior.class,
				new FXSelectionBehavior());
		viewer.getRootPart().setAdapter(FXZoomBehavior.class,
				new FXZoomBehavior());

		// interaction policies
		viewer.getRootPart().setAdapter(FXZoomTool.TOOL_POLICY_KEY,
				new FXZoomOnZoomPolicy());
		viewer.getRootPart().setAdapter(FXScrollTool.TOOL_POLICY_KEY,
				new FXZoomOnScrollPolicy());
	}

	@Override
	protected FXDomain createDomain() {
		return new FXExampleDomain();
	}

	@Override
	protected IContentPartFactory<Node> getContentPartFactory() {
		return new FXExampleContentPartFactory();
	}

	@Override
	protected IHandlePartFactory<Node> getHandlePartFactory() {
		return new FXExampleHandlePartFactory();
	}

	@Override
	protected IFeedbackPartFactory<Node> getFeedbackPartFactory() {
		return new FXDefaultFeedbackPartFactory();
	}

	@Override
	protected List<Object> getContents() {
		return Collections.<Object> singletonList(new FXGeometricModel());
	}

}
