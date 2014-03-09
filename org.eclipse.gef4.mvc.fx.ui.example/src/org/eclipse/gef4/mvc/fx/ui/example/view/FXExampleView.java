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

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXZoomBehavior;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.policies.FXZoomOnScrollPolicy;
import org.eclipse.gef4.mvc.fx.policies.IScrollPolicy;
import org.eclipse.gef4.mvc.fx.ui.example.FXExampleContentPartFactory;
import org.eclipse.gef4.mvc.fx.ui.example.FXExampleDomain;
import org.eclipse.gef4.mvc.fx.ui.example.FXExampleHandlePartFactory;
import org.eclipse.gef4.mvc.fx.ui.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.ui.view.FXView;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.policies.IZoomPolicy;

public class FXExampleView extends FXView {

	@Override
	protected FXViewer createViewer(FXCanvas canvas) {
		FXViewer viewer = super.createViewer(canvas);
		viewer.getRootPart().installBound(new FXSelectionBehavior());
		viewer.getRootPart().installBound(new FXZoomBehavior());
		viewer.getRootPart().installBound(IZoomPolicy.class, new IZoomPolicy.Impl<Node>());
		viewer.getRootPart().installBound(IScrollPolicy.class, new FXZoomOnScrollPolicy());
		return viewer;
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
	protected List<Object> getContents() {
		return Collections.<Object> singletonList(new FXGeometricModel());
	}

}
