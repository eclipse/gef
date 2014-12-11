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
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.parts.ChopBoxAnchorProvider;
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
import org.eclipse.gef4.zest.fx.behaviors.LayoutContextBehavior;
import org.eclipse.gef4.zest.fx.behaviors.NodeLayoutBehavior;
import org.eclipse.gef4.zest.fx.behaviors.PruningBehavior;
import org.eclipse.gef4.zest.fx.models.LayoutModel;
import org.eclipse.gef4.zest.fx.models.PruningModel;
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

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class ZestFxModule extends MvcFxModule {

	@SuppressWarnings("serial")
	@Override
	protected void bindAbstractContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY)).to(
				FXFocusAndSelectOnClickPolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY)).to(
						FXHoverOnHoverPolicy.class);
		// geometry provider for selection feedback
		adapterMapBinder
				.addBinding(
						AdapterKey
								.get(new TypeToken<Provider<IGeometry>>() {
								},
										FXDefaultFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(VisualBoundsGeometryProvider.class);
		// geometry provider for hover feedback
		adapterMapBinder
				.addBinding(
						AdapterKey
								.get(new TypeToken<Provider<IGeometry>>() {
								},
										FXDefaultFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(VisualBoundsGeometryProvider.class);
		// geometry provider for hover handles
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
				}, FXDefaultHandlePartFactory.HOVER_HANDLES_GEOMETRY_PROVIDER))
				.to(VisualBoundsGeometryProvider.class);
	}

	@Override
	protected void bindAbstractDomainAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractDomainAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.get(LayoutModel.class)).to(
				LayoutModel.class);
		adapterMapBinder.addBinding(AdapterKey.get(PruningModel.class)).to(
				PruningModel.class);
	}

	@Override
	protected void bindAbstractRootPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractRootPartAdapters(adapterMapBinder);
		adapterMapBinder
				.addBinding(AdapterKey.get(LayoutContextBehavior.class)).to(
						LayoutContextBehavior.class);
	}

	protected void bindEdgeContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(EdgeLayoutBehavior.class))
				.to(EdgeLayoutBehavior.class);
	}

	protected void bindGraphContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
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
		binder().bind(new TypeLiteral<IRootPart<Node, ? extends Node>>() {
		}).to(GraphRootPart.class);
	}

	@SuppressWarnings("serial")
	protected void bindNodeContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(NodeLayoutPolicy.class)).to(
				NodeLayoutPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(NodeLayoutBehavior.class))
				.to(NodeLayoutBehavior.class);
		adapterMapBinder.addBinding(AdapterKey.get(PruneNodePolicy.class)).to(
				PruneNodePolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(PruningBehavior.class)).to(
				PruningBehavior.class);
		// interaction
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY)).to(
				FXRelocateOnDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY))
				.to(PruneOnTypePolicy.class);
		// transaction
		adapterMapBinder.addBinding(
				AdapterKey.get(FXResizeRelocatePolicy.class)).to(
				FXResizeRelocatePolicy.class);

		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<? extends IFXAnchor>>() {
				})).to(ChopBoxAnchorProvider.class);
	}

	protected void bindPruningHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
	}

	@Override
	protected void configure() {
		super.configure();
		bindIContentPartFactory();
		bindGraphContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				GraphContentPart.class));
		bindNodeContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				NodeContentPart.class));
		bindEdgeContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				EdgeContentPart.class));
		bindPruningHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				ZestFxPruningHandlePart.class));
	}

}
