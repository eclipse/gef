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
import org.eclipse.gef4.common.inject.AdaptableScopes;
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
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRotatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRotateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.zest.fx.behaviors.EdgeLayoutBehavior;
import org.eclipse.gef4.zest.fx.behaviors.HidingBehavior;
import org.eclipse.gef4.zest.fx.behaviors.LayoutContextBehavior;
import org.eclipse.gef4.zest.fx.behaviors.NodeLayoutBehavior;
import org.eclipse.gef4.zest.fx.behaviors.OpenNestedGraphOnZoomBehavior;
import org.eclipse.gef4.zest.fx.behaviors.OpenParentGraphOnZoomBehavior;
import org.eclipse.gef4.zest.fx.behaviors.SynchronizeChildrenOnZoomBehavior;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.models.LayoutModel;
import org.eclipse.gef4.zest.fx.models.ViewportStackModel;
import org.eclipse.gef4.zest.fx.parts.ContentPartFactory;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;
import org.eclipse.gef4.zest.fx.parts.GraphContentPart;
import org.eclipse.gef4.zest.fx.parts.GraphRootPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.eclipse.gef4.zest.fx.parts.ZestFxHandlePartFactory;
import org.eclipse.gef4.zest.fx.parts.ZestFxHidingHandlePart;
import org.eclipse.gef4.zest.fx.policies.HideNodePolicy;
import org.eclipse.gef4.zest.fx.policies.HideOnTypePolicy;
import org.eclipse.gef4.zest.fx.policies.OpenNestedGraphOnDoubleClickPolicy;
import org.eclipse.gef4.zest.fx.policies.OpenParentGraphOnDoubleClickPolicy;

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
	protected void bindAbstractFXHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractFXHandlePartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY,
						"ResizeRelocateOnHandleDrag")).to(
				FXResizeRelocateOnHandleDragPolicy.class);
		// rotate on drag + control
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY, "rotate"))
				.to(FXRotateSelectedOnHandleDragPolicy.class);
	}

	@Override
	protected void bindAbstractViewerAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractViewerAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.get(ViewportStackModel.class))
				.to(ViewportStackModel.class);
	}

	protected void bindEdgeContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(EdgeLayoutBehavior.class))
				.to(EdgeLayoutBehavior.class);
	}

	@Override
	protected void bindFXRootPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindFXRootPartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY,
						"OpenParentGraphOnDoubleClick")).to(
				OpenParentGraphOnDoubleClickPolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(OpenParentGraphOnZoomBehavior.class)).to(
				OpenParentGraphOnZoomBehavior.class);
	}

	@Override
	protected void bindFXViewerAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindFXViewerAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.get(HidingModel.class)).to(
				HidingModel.class);
	}

	protected void bindGraphContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(LayoutModel.class)).to(
				LayoutModel.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXRotatePolicy.class)).to(
				FXRotatePolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey.get(LayoutContextBehavior.class)).to(
						LayoutContextBehavior.class);
	}

	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).to(ContentPartFactory.class)
				.in(AdaptableScopes.typed(FXViewer.class));
	}

	@Override
	protected void bindIHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).to(ZestFxHandlePartFactory.class)
				.in(AdaptableScopes.typed(FXViewer.class));
	}

	@Override
	protected void bindIRootPart() {
		binder().bind(new TypeLiteral<IRootPart<Node, ? extends Node>>() {
		}).to(GraphRootPart.class).in(AdaptableScopes.typed(FXViewer.class));
	}

	@SuppressWarnings("serial")
	protected void bindNodeContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// layout
		adapterMapBinder.addBinding(AdapterKey.get(NodeLayoutBehavior.class))
				.to(NodeLayoutBehavior.class);
		// pruning
		adapterMapBinder.addBinding(AdapterKey.get(HideNodePolicy.class)).to(
				HideNodePolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(HidingBehavior.class)).to(
				HidingBehavior.class);
		// interaction
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY)).to(
				FXRelocateOnDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY))
				.to(HideOnTypePolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY,
						"OpenNestedGraphOnDoubleClick")).to(
				OpenNestedGraphOnDoubleClickPolicy.class);
		// synchronize children on zoom
		adapterMapBinder.addBinding(
				AdapterKey.get(SynchronizeChildrenOnZoomBehavior.class)).to(
				SynchronizeChildrenOnZoomBehavior.class);
		// replace contents with nested graph on zoom
		adapterMapBinder.addBinding(
				AdapterKey.get(OpenNestedGraphOnZoomBehavior.class)).to(
				OpenNestedGraphOnZoomBehavior.class);
		// transaction
		adapterMapBinder.addBinding(
				AdapterKey.get(FXResizeRelocatePolicy.class)).to(
				FXResizeRelocatePolicy.class);
		// transform policy for relocation
		adapterMapBinder.addBinding(AdapterKey.get(FXTransformPolicy.class))
				.to(FXTransformPolicy.class);
		// resize policy to resize nesting nodes
		adapterMapBinder.addBinding(AdapterKey.get(FXResizePolicy.class)).to(
				FXResizePolicy.class);
		// rotate nodes
		adapterMapBinder.addBinding(AdapterKey.get(FXRotatePolicy.class)).to(
				FXRotatePolicy.class);
		// provider
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<? extends IFXAnchor>>() {
				})).to(ChopBoxAnchorProvider.class);
		adapterMapBinder
				.addBinding(
						AdapterKey
								.get(new TypeToken<Provider<IGeometry>>() {
								},
										FXDefaultHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(VisualBoundsGeometryProvider.class);
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
				ZestFxHidingHandlePart.class));
	}

}
