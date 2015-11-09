/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdaptableScopes;
import org.eclipse.gef4.common.inject.AdapterMap;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.behaviors.FXCursorBehavior;
import org.eclipse.gef4.mvc.fx.parts.ChopBoxAnchorProvider;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.VisualBoundsGeometryProvider;
import org.eclipse.gef4.mvc.fx.parts.VisualOutlineGeometryProvider;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeTranslateOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRotateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.zest.fx.behaviors.EdgeHidingBehavior;
import org.eclipse.gef4.zest.fx.behaviors.EdgeLabelHidingBehavior;
import org.eclipse.gef4.zest.fx.behaviors.EdgeLayoutBehavior;
import org.eclipse.gef4.zest.fx.behaviors.HidingBehavior;
import org.eclipse.gef4.zest.fx.behaviors.LayoutContextBehavior;
import org.eclipse.gef4.zest.fx.behaviors.NodeLayoutBehavior;
import org.eclipse.gef4.zest.fx.behaviors.OpenNestedGraphOnZoomBehavior;
import org.eclipse.gef4.zest.fx.behaviors.OpenParentGraphOnZoomBehavior;
import org.eclipse.gef4.zest.fx.behaviors.SynchronizeChildrenOnZoomBehavior;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.models.NavigationModel;
import org.eclipse.gef4.zest.fx.parts.ContentPartFactory;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;
import org.eclipse.gef4.zest.fx.parts.EdgeLabelPart;
import org.eclipse.gef4.zest.fx.parts.GraphContentPart;
import org.eclipse.gef4.zest.fx.parts.GraphRootPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.eclipse.gef4.zest.fx.parts.ZestFxCursorProvider;
import org.eclipse.gef4.zest.fx.parts.ZestFxExpandingHandlePart;
import org.eclipse.gef4.zest.fx.parts.ZestFxFeedbackPartFactory;
import org.eclipse.gef4.zest.fx.parts.ZestFxHandlePartFactory;
import org.eclipse.gef4.zest.fx.parts.ZestFxHidingHandlePart;
import org.eclipse.gef4.zest.fx.policies.ExpandFirstAnchorageOnClickPolicy;
import org.eclipse.gef4.zest.fx.policies.FocusAndSelectFirstAnchorageOnClickPolicy;
import org.eclipse.gef4.zest.fx.policies.HideFirstAnchorageOnClickPolicy;
import org.eclipse.gef4.zest.fx.policies.HideNodePolicy;
import org.eclipse.gef4.zest.fx.policies.HideOnTypePolicy;
import org.eclipse.gef4.zest.fx.policies.HoverFirstAnchorageOnHoverPolicy;
import org.eclipse.gef4.zest.fx.policies.NavigationPolicy;
import org.eclipse.gef4.zest.fx.policies.OffsetEdgeLabelOnDragPolicy;
import org.eclipse.gef4.zest.fx.policies.OpenNestedGraphOnDoubleClickPolicy;
import org.eclipse.gef4.zest.fx.policies.OpenParentGraphOnDoubleClickPolicy;

import com.google.common.reflect.TypeToken;
import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

/**
 * The {@link ZestFxModule} defines Zest.FX specific bindings additional to the
 * bindings defined within {@link MvcFxModule}.
 *
 * @author mwienand
 *
 */
public class ZestFxModule extends MvcFxModule {

	@SuppressWarnings("serial")
	@Override
	protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY))
				.to(FXFocusAndSelectOnClickPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY)).to(FXHoverOnHoverPolicy.class);
		// geometry provider for selection feedback
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
		}, FXDefaultFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER)).to(VisualBoundsGeometryProvider.class);
		// geometry provider for hover feedback
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
		}, FXDefaultFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER)).to(VisualBoundsGeometryProvider.class);
		// geometry provider for hover handles
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
		}, FXDefaultHandlePartFactory.HOVER_HANDLES_GEOMETRY_PROVIDER)).to(VisualBoundsGeometryProvider.class);
	}

	@SuppressWarnings("serial")
	@Override
	protected void bindAbstractFXHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractFXHandlePartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY, "ResizeRelocateOnHandleDrag"))
				.to(FXResizeTranslateOnHandleDragPolicy.class);
		// rotate on drag + control
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY, "rotate"))
				.to(FXRotateSelectedOnHandleDragPolicy.class);
		// change cursor for rotation
		adapterMapBinder.addBinding(AdapterKey.get(FXCursorBehavior.class)).to(FXCursorBehavior.class);
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<Map<KeyCode, Cursor>>>() {
		}, FXCursorBehavior.CURSOR_PROVIDER_ROLE)).to(ZestFxCursorProvider.class);
	}

	@Override
	protected void bindAbstractViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractViewerAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.get(NavigationModel.class)).to(NavigationModel.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link EdgeContentPart}
	 * and all sub-classes. May be overwritten by sub-classes to change the
	 * default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link EdgeContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	@SuppressWarnings("serial")
	protected void bindEdgeContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// layout
		adapterMapBinder.addBinding(AdapterKey.get(EdgeLayoutBehavior.class)).to(EdgeLayoutBehavior.class);
		// hiding
		adapterMapBinder.addBinding(AdapterKey.get(EdgeHidingBehavior.class)).to(EdgeHidingBehavior.class);
		// selection link feedback
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
		}, FXDefaultFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(VisualOutlineGeometryProvider.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link EdgeLabelPart} and
	 * all sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link EdgeLabelPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	@SuppressWarnings("serial")
	protected void bindEdgeLabelPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hiding
		adapterMapBinder.addBinding(AdapterKey.get(EdgeLabelHidingBehavior.class)).to(EdgeLabelHidingBehavior.class);
		// offset on drag
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY, "OffsetOnDrag"))
				.to(OffsetEdgeLabelOnDragPolicy.class);
		// select anchorage on click
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY))
				.to(FocusAndSelectFirstAnchorageOnClickPolicy.class);
		// selection link feedback
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
		}, FXDefaultFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(VisualOutlineGeometryProvider.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link ZestFxExpandingHandlePart} and all sub-classes. May be overwritten
	 * by sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link ZestFxExpandingHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindExpandingHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY, "hoverFirstAnchorage"))
				.to(HoverFirstAnchorageOnHoverPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY, "expandOnClick"))
				.to(ExpandFirstAnchorageOnClickPolicy.class);
	}

	@Override
	protected void bindFXRootPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindFXRootPartAdapters(adapterMapBinder);
		adapterMapBinder
				.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY, "OpenParentGraphOnDoubleClick"))
				.to(OpenParentGraphOnDoubleClickPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(OpenParentGraphOnZoomBehavior.class))
				.to(OpenParentGraphOnZoomBehavior.class);
		adapterMapBinder.addBinding(AdapterKey.get(NavigationPolicy.class)).to(NavigationPolicy.class);
	}

	@Override
	protected void bindFXViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindFXViewerAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.get(HidingModel.class)).to(HidingModel.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link GraphContentPart}
	 * and all sub-classes. May be overwritten by sub-classes to change the
	 * default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link GraphContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindGraphContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(GraphLayoutContext.class)).to(GraphLayoutContext.class);
		adapterMapBinder.addBinding(AdapterKey.get(LayoutContextBehavior.class)).to(LayoutContextBehavior.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link ZestFxHidingHandlePart} and all sub-classes. May be overwritten by
	 * sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link ZestFxHidingHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHidingHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY, "hoverFirstAnchorage"))
				.to(HoverFirstAnchorageOnHoverPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY, "hideOnClick"))
				.to(HideFirstAnchorageOnClickPolicy.class);
	}

	/**
	 * Binds {@link IContentPartFactory} to {@link ContentPartFactory}.
	 */
	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).to(ContentPartFactory.class).in(AdaptableScopes.typed(FXViewer.class));
	}

	@Override
	protected void bindIFeedbackPartFactory() {
		binder().bind(new TypeLiteral<IFeedbackPartFactory<Node>>() {
		}).to(ZestFxFeedbackPartFactory.class).in(AdaptableScopes.typed(FXViewer.class));
	}

	@Override
	protected void bindIHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).to(ZestFxHandlePartFactory.class).in(AdaptableScopes.typed(FXViewer.class));
	}

	@Override
	protected void bindIRootPart() {
		binder().bind(new TypeLiteral<IRootPart<Node, ? extends Node>>() {
		}).to(GraphRootPart.class).in(AdaptableScopes.typed(FXViewer.class));
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link NodeContentPart}
	 * and all sub-classes. May be overwritten by sub-classes to change the
	 * default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link NodeContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	@SuppressWarnings("serial")
	protected void bindNodeContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// layout
		adapterMapBinder.addBinding(AdapterKey.get(NodeLayoutBehavior.class)).to(NodeLayoutBehavior.class);
		// pruning
		adapterMapBinder.addBinding(AdapterKey.get(HideNodePolicy.class)).to(HideNodePolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(HidingBehavior.class)).to(HidingBehavior.class);
		// interaction
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY))
				.to(FXTranslateSelectedOnDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY)).to(HideOnTypePolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY, "OpenNestedGraphOnDoubleClick"))
				.to(OpenNestedGraphOnDoubleClickPolicy.class);
		// synchronize children on zoom
		adapterMapBinder.addBinding(AdapterKey.get(SynchronizeChildrenOnZoomBehavior.class))
				.to(SynchronizeChildrenOnZoomBehavior.class);
		// replace contents with nested graph on zoom
		adapterMapBinder.addBinding(AdapterKey.get(OpenNestedGraphOnZoomBehavior.class))
				.to(OpenNestedGraphOnZoomBehavior.class);
		// transform policy for relocation
		adapterMapBinder.addBinding(AdapterKey.get(FXTransformPolicy.class)).to(FXTransformPolicy.class);
		// resize policy to resize nesting nodes
		adapterMapBinder.addBinding(AdapterKey.get(FXResizePolicy.class)).to(FXResizePolicy.class);
		// provider
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {
		})).to(ChopBoxAnchorProvider.class);
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
		}, FXDefaultHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER)).to(VisualBoundsGeometryProvider.class);
	}

	@Override
	protected void configure() {
		super.configure();
		bindIContentPartFactory();

		bindGraphContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), GraphContentPart.class));
		bindNodeContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), NodeContentPart.class));
		bindEdgeContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), EdgeContentPart.class));
		bindHidingHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), ZestFxHidingHandlePart.class));
		bindExpandingHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), ZestFxExpandingHandlePart.class));
		bindEdgeLabelPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), EdgeLabelPart.class));
	}

}
