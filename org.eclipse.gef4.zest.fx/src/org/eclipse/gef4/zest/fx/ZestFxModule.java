/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx;

import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.VisualBoundsGeometryProvider;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.zest.fx.behaviors.EdgeLayoutBehavior;
import org.eclipse.gef4.zest.fx.behaviors.NodeLayoutBehavior;
import org.eclipse.gef4.zest.fx.behaviors.SubgraphBehavior;
import org.eclipse.gef4.zest.fx.models.DefaultLayoutModel;
import org.eclipse.gef4.zest.fx.models.ILayoutModel;
import org.eclipse.gef4.zest.fx.models.SubgraphModel;
import org.eclipse.gef4.zest.fx.parts.ContentPartFactory;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;
import org.eclipse.gef4.zest.fx.parts.GraphContentPart;
import org.eclipse.gef4.zest.fx.parts.GraphRootPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.eclipse.gef4.zest.fx.parts.ZestFxHandlePartFactory;
import org.eclipse.gef4.zest.fx.parts.ZestFxPruningHandlePart;
import org.eclipse.gef4.zest.fx.policies.NodeLayoutPolicy;
import org.eclipse.gef4.zest.fx.policies.PruneNodePolicy;
import org.eclipse.gef4.zest.fx.policies.PruneOnTypePolicy;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class ZestFxModule extends MvcFxModule {

	@Override
	protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY)).to(
				FXFocusAndSelectOnClickPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY)).to(FXHoverOnHoverPolicy.class);

		// geometry provider for selection feedback
		adapterMapBinder.addBinding(
				AdapterKey.get(Provider.class, FXDefaultFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER)).to(
				VisualBoundsGeometryProvider.class);

		// geometry provider for hover feedback
		adapterMapBinder.addBinding(
				AdapterKey.get(Provider.class, FXDefaultFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER)).to(
				VisualBoundsGeometryProvider.class);

		// geometry provider for hover handles
		adapterMapBinder.addBinding(
				AdapterKey.get(Provider.class, FXDefaultHandlePartFactory.HOVER_HANDLES_GEOMETRY_PROVIDER)).to(
				VisualBoundsGeometryProvider.class);
	}

	@Override
	protected void bindAbstractDomainAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractDomainAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.get(ILayoutModel.class)).to(DefaultLayoutModel.class);
		adapterMapBinder.addBinding(AdapterKey.get(SubgraphModel.class)).to(SubgraphModel.class);
	}

	protected void bindEdgeContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(EdgeLayoutBehavior.class)).to(EdgeLayoutBehavior.class);
	}

	protected void bindGraphContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	}

	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).to(ContentPartFactory.class);
	}

	@Override
	protected void bindIHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).to(ZestFxHandlePartFactory.class);
	}

	@Override
	protected void bindIRootPart() {
		binder().bind(new TypeLiteral<IRootPart<Node>>() {
		}).to(GraphRootPart.class);
	}

	protected void bindNodeContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(NodeLayoutPolicy.class)).to(NodeLayoutPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(NodeLayoutBehavior.class)).to(NodeLayoutBehavior.class);
		adapterMapBinder.addBinding(AdapterKey.get(PruneNodePolicy.class)).to(PruneNodePolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(SubgraphBehavior.class)).to(SubgraphBehavior.class);
		// interaction
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY)).to(
				FXRelocateOnDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY)).to(PruneOnTypePolicy.class);
		// transaction
		adapterMapBinder.addBinding(AdapterKey.get(FXResizeRelocatePolicy.class)).to(FXResizeRelocatePolicy.class);
	}

	protected void bindPruningHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// FIXME: bind NoHoverPolicy to TypeLiteral<HoverPolicy<Node>>
		// Are type literal bindings global?
		// adapterMapBinder.addBinding(AdapterKey.get(HoverPolicy.class)).to(
		// NoHoverPolicy.class);
	}

	@Override
	protected void configure() {
		super.configure();
		bindIContentPartFactory();
		bindGraphContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), GraphContentPart.class));
		bindNodeContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), NodeContentPart.class));
		bindEdgeContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), EdgeContentPart.class));
		bindPruningHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), ZestFxPruningHandlePart.class));
	}

}
