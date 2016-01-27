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

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdaptableScopes;
import org.eclipse.gef4.common.inject.AdapterMap;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHoverFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHoverHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXRectangleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeTranslateOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRotateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.providers.ChopBoxAnchorProvider;
import org.eclipse.gef4.mvc.fx.providers.GeometricOutlineProvider;
import org.eclipse.gef4.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;
import org.eclipse.gef4.zest.fx.behaviors.EdgeHidingBehavior;
import org.eclipse.gef4.zest.fx.behaviors.EdgeLabelHidingBehavior;
import org.eclipse.gef4.zest.fx.behaviors.EdgeLayoutBehavior;
import org.eclipse.gef4.zest.fx.behaviors.LayoutContextBehavior;
import org.eclipse.gef4.zest.fx.behaviors.NodeHidingBehavior;
import org.eclipse.gef4.zest.fx.behaviors.NodeLayoutBehavior;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.models.NavigationModel;
import org.eclipse.gef4.zest.fx.parts.ContentPartFactory;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;
import org.eclipse.gef4.zest.fx.parts.EdgeLabelPart;
import org.eclipse.gef4.zest.fx.parts.GraphContentPart;
import org.eclipse.gef4.zest.fx.parts.GraphRootPart;
import org.eclipse.gef4.zest.fx.parts.HideHoverHandlePart;
import org.eclipse.gef4.zest.fx.parts.HoverHandlePartFactory;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.eclipse.gef4.zest.fx.parts.SelectionHandlePartFactory;
import org.eclipse.gef4.zest.fx.parts.ShowHiddenNeighborsHoverHandlePart;
import org.eclipse.gef4.zest.fx.policies.HideFirstAnchorageOnClickPolicy;
import org.eclipse.gef4.zest.fx.policies.HideOnTypePolicy;
import org.eclipse.gef4.zest.fx.policies.HidePolicy;
import org.eclipse.gef4.zest.fx.policies.OffsetEdgeLabelOnDragPolicy;
import org.eclipse.gef4.zest.fx.policies.OpenNestedGraphOnDoubleClickPolicy;
import org.eclipse.gef4.zest.fx.policies.OpenParentGraphOnDoubleClickPolicy;
import org.eclipse.gef4.zest.fx.policies.SemanticZoomPolicy;
import org.eclipse.gef4.zest.fx.policies.ShowHiddenNeighborsOfFirstAnchorageOnClickPolicy;
import org.eclipse.gef4.zest.fx.policies.ShowHiddenNeighborsOnTypePolicy;
import org.eclipse.gef4.zest.fx.policies.ShowHiddenNeighborsPolicy;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

import javafx.scene.Node;

/**
 * The {@link ZestFxModule} defines Zest.FX specific bindings additional to the
 * bindings defined within {@link MvcFxModule}.
 *
 * @author mwienand
 *
 */
public class ZestFxModule extends MvcFxModule {

	@Override
	protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		bindFXFocusAndSelectOnClickPolicyAsAbstractContentPartAdapter(adapterMapBinder);
		bindHoverHandlesGeometryProviderAsAbstractContentPartAdapter(adapterMapBinder);
	}

	@Override
	protected void bindAbstractViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractViewerAdapters(adapterMapBinder);
		bindNavigationModelAsAbstractViewerAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link ChopBoxAnchorProvider} to the given adapter map
	 * binder that will insert the bindings into {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindChopBoxAnchorProviderAsNodeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ChopBoxAnchorProvider.class);
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
	protected void bindEdgeContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// layout and hidinig
		bindEdgeLayoutBehaviorAsEdgeContentPartAdapter(adapterMapBinder);
		bindEdgeHidingBehaviorAsEdgeContentPartAdapter(adapterMapBinder);

		// feedback and handles
		bindFXHoverOnHoverPolicyAsEdgeContentPartAdapter(adapterMapBinder);
		bindSelectionLinkFeedbackGeometryProviderAsEdgeContentPartAdapter(adapterMapBinder);
		bindSelectionFeedbackGeometryProviderAsEdgeContentPartAdapter(adapterMapBinder);
		bindHoverFeedbackGeometryProviderAsEdgeContentPartAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link EdgeHidingBehavior} to the given adapter map
	 * binder that will insert the bindings into {@link EdgeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindEdgeHidingBehaviorAsEdgeContentPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(EdgeHidingBehavior.class);
	}

	/**
	 * Adds a binding for {@link EdgeLabelHidingBehavior} to the given adapter
	 * map binder that will insert the bindings into {@link EdgeLabelPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindEdgeLabelHidingBehaviorAsEdgeLabelPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(EdgeLabelHidingBehavior.class);
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
	protected void bindEdgeLabelPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hiding
		bindEdgeLabelHidingBehaviorAsEdgeLabelPartAdapter(adapterMapBinder);
		// offset on drag
		bindOffsetEdgeLabelOnDragPolicyAsEdgeLabelPartAdapter(adapterMapBinder);
		bindFXHoverOnHoverPolicyAsEdgeLabelPartAdapter(adapterMapBinder);
		// feedback
		bindSelectionLinkFeedbackGeometryProviderAsEdgeLabelPartAdapter(adapterMapBinder);
		bindSelectionFeedbackGeometryProviderAsEdgeLabelPartAdapter(adapterMapBinder);
		bindHoverFeedbackGeometryProviderAsEdgeLabelPartAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link EdgeLayoutBehavior} to the given adapter map
	 * binder that will insert the bindings into {@link EdgeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindEdgeLayoutBehaviorAsEdgeContentPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(EdgeLayoutBehavior.class);
	}

	@Override
	protected void bindFXChangeViewportPolicyAsFXRootPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// overwrite default zoom policy to perform semantic zooming (navigating
		// nested graphs on zoom level changes)
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SemanticZoomPolicy.class);
	}

	/**
	 * Adds a binding for {@link FXFocusAndSelectOnClickPolicy} to the given
	 * adapter map binder that will insert the bindings into
	 * {@link AbstractContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindFXFocusAndSelectOnClickPolicyAsAbstractContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXFocusAndSelectOnClickPolicy.class);
	}

	/**
	 * Adds a binding for {@link FXHoverOnHoverPolicy} to the given adapter map
	 * binder that will insert the bindings into {@link EdgeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindFXHoverOnHoverPolicyAsEdgeContentPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverOnHoverPolicy.class);
	}

	/**
	 * Adds a binding for {@link FXHoverOnHoverPolicy} to the given adapter map
	 * binder that will insert the bindings into {@link EdgeLabelPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindFXHoverOnHoverPolicyAsEdgeLabelPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverOnHoverPolicy.class);
	}

	/**
	 * Bind resize and rotate behavior to {@link FXRectangleSegmentHandlePart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRectangleSegmentHandlePart} as a key.
	 */
	protected void bindFXRectangleSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		bindFXResizeTranslateOnHandleDragPolicyAsFXRectangleSegmentHandlePartAdapter(adapterMapBinder);
		bindFXRotateSelectedOnHandleDragPolicyAsFXRectangleSegmentHandlePartAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link FXResizePolicy} to the given adapter map binder
	 * that will insert the bindings into {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindFXResizePolicyAsNodeContentPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXResizePolicy.class);
	}

	/**
	 * Adds a binding for {@link FXResizeTranslateOnHandleDragPolicy} to the
	 * given adapter map binder that will insert the bindings into
	 * {@link FXRectangleSegmentHandlePart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindFXResizeTranslateOnHandleDragPolicyAsFXRectangleSegmentHandlePartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("ResizeRelocateOnHandleDrag"))
				.to(FXResizeTranslateOnHandleDragPolicy.class);
	}

	@Override
	protected void bindFXRootPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindFXRootPartAdapters(adapterMapBinder);
		bindOpenParentGraphOnDoubleClickPolicyAsFXRootPartAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link FXRotateSelectedOnHandleDragPolicy} to the
	 * given adapter map binder that will insert the bindings into
	 * {@link FXRectangleSegmentHandlePart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindFXRotateSelectedOnHandleDragPolicyAsFXRectangleSegmentHandlePartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("rotate")).to(FXRotateSelectedOnHandleDragPolicy.class);
	}

	/**
	 * Adds a binding for {@link FXTransformPolicy} to the given adapter map
	 * binder that will insert the bindings into {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindFXTransformPolicyAsNodeContentPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTransformPolicy.class);
	}

	/**
	 * Adds a binding for {@link FXTranslateSelectedOnDragPolicy} to the given
	 * adapter map binder that will insert the bindings into
	 * {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindFXTranslateSelectedOnDragPolicyAsNodeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTranslateSelectedOnDragPolicy.class);
	}

	@Override
	protected void bindFXViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindFXViewerAdapters(adapterMapBinder);
		bindHidingModelAsFXViewerAdapter(adapterMapBinder);
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
		bindGraphLayoutContextAsGraphContentPartAdapter(adapterMapBinder);
		bindLayoutContextBehaviorAsGraphContentPartAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link GraphLayoutContext} to the given adapter map
	 * binder that will insert the bindings into {@link GraphContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindGraphLayoutContextAsGraphContentPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(GraphLayoutContext.class);
	}

	/**
	 * Adds a binding for {@link HideOnTypePolicy} to the given adapter map
	 * binder that will insert the bindings into {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindHideOnTypePolicyAsNodeContentPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("hide")).to(HideOnTypePolicy.class);
	}

	/**
	 * Adds a binding for {@link HidePolicy} to the given adapter map binder
	 * that will insert the bindings into {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindHidePolicyAsNodeContentPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HidePolicy.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link HideHoverHandlePart} and all sub-classes. May be overwritten by
	 * sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link HideHoverHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHidingHoverHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("hide")).to(HideFirstAnchorageOnClickPolicy.class);
	}

	/**
	 * Adds a binding for {@link HidingModel} to the given adapter map binder
	 * that will insert the bindings into {@link FXViewer}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindHidingModelAsFXViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HidingModel.class);
	}

	/**
	 * Adds a binding for
	 * {@link FXDefaultHoverFeedbackPartFactory#HOVER_FEEDBACK_GEOMETRY_PROVIDER}
	 * with implementation {@link GeometricOutlineProvider} to the given adapter
	 * map binder that will insert the bindings into {@link EdgeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindHoverFeedbackGeometryProviderAsEdgeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
	}

	/**
	 * Adds a binding for
	 * {@link FXDefaultHoverFeedbackPartFactory#HOVER_FEEDBACK_GEOMETRY_PROVIDER}
	 * with implementation {@link ShapeBoundsProvider} to the given adapter map
	 * binder that will insert the bindings into {@link EdgeLabelPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindHoverFeedbackGeometryProviderAsEdgeLabelPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
	}

	/**
	 * Adds a binding for
	 * {@link FXDefaultHoverHandlePartFactory#HOVER_HANDLES_GEOMETRY_PROVIDER}
	 * with implementation {@link ShapeBoundsProvider} to the given adapter map
	 * binder that will insert the bindings into {@link AbstractContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindHoverHandlesGeometryProviderAsAbstractContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverHandlePartFactory.HOVER_HANDLES_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
	}

	/**
	 * Binds {@link IContentPartFactory} to {@link ContentPartFactory}.
	 */
	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).to(ContentPartFactory.class).in(AdaptableScopes.typed(FXViewer.class));
	}

	@Override
	protected void bindIHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).annotatedWith(Names.named(SelectionBehavior.PART_FACTORIES_BINDING_NAME))
				.to(SelectionHandlePartFactory.class).in(AdaptableScopes.typed(FXViewer.class));
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).annotatedWith(Names.named(HoverBehavior.PART_FACTORIES_BINDING_NAME)).to(HoverHandlePartFactory.class)
				.in(AdaptableScopes.typed(FXViewer.class));
	}

	@Override
	protected void bindIRootPart() {
		binder().bind(new TypeLiteral<IRootPart<Node, ? extends Node>>() {
		}).to(GraphRootPart.class).in(AdaptableScopes.typed(FXViewer.class));
	}

	/**
	 * Adds a binding for {@link LayoutContextBehavior} to the given adapter map
	 * binder that will insert the bindings into {@link GraphContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindLayoutContextBehaviorAsGraphContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(LayoutContextBehavior.class);
	}

	/**
	 * Adds a binding for {@link NavigationModel} to the given adapter map
	 * binder that will insert the bindings into {@link AbstractViewer}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindNavigationModelAsAbstractViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NavigationModel.class);
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
	protected void bindNodeContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// layout
		bindNodeLayoutBehaviorAsNodeContentPartAdapter(adapterMapBinder);
		// pruning
		bindHidePolicyAsNodeContentPartAdapter(adapterMapBinder);
		bindShowHiddenNeighborsPolicyAsNodeContentPartAdapter(adapterMapBinder);
		bindNodeHidingBehaviorAsNodeContentPartAdapter(adapterMapBinder);
		// interaction
		bindFXTranslateSelectedOnDragPolicyAsNodeContentPartAdapter(adapterMapBinder);
		bindShowHiddenNeighborsOnTypePolicyAsNodeContentPartAdapter(adapterMapBinder);
		bindHideOnTypePolicyAsNodeContentPartAdapter(adapterMapBinder);
		bindOpenNestedGraphOnDoubleClickPolicyAsNodeContentPartAdapter(adapterMapBinder);
		// transform policy for relocation
		bindFXTransformPolicyAsNodeContentPartAdapter(adapterMapBinder);
		// resize policy to resize nesting nodes
		bindFXResizePolicyAsNodeContentPartAdapter(adapterMapBinder);
		// anchor provider
		bindChopBoxAnchorProviderAsNodeContentPartAdapter(adapterMapBinder);
		// feedback and handles
		bindFXHoverOnHoverPolicyAsEdgeContentPartAdapter(adapterMapBinder);
		bindSelectionHandlesGeometryProviderAsNodeContentPartAdapter(adapterMapBinder);
		bindSelectionFeedbackGeometryProviderAsEdgeLabelPartAdapter(adapterMapBinder);
		bindHoverFeedbackGeometryProviderAsEdgeLabelPartAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link NodeHidingBehavior} to the given adapter map
	 * binder that will insert the bindings into {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindNodeHidingBehaviorAsNodeContentPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NodeHidingBehavior.class);
	}

	/**
	 * Adds a binding for {@link NodeLayoutBehavior} to the given adapter map
	 * binder that will insert the bindings into {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindNodeLayoutBehaviorAsNodeContentPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NodeLayoutBehavior.class);
	}

	/**
	 * Adds a binding for {@link OffsetEdgeLabelOnDragPolicy} to the given
	 * adapter map binder that will insert the bindings into
	 * {@link EdgeLabelPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindOffsetEdgeLabelOnDragPolicyAsEdgeLabelPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("OffsetOnDrag")).to(OffsetEdgeLabelOnDragPolicy.class);
	}

	/**
	 * Adds a binding for {@link OpenNestedGraphOnDoubleClickPolicy} to the
	 * given adapter map binder that will insert the bindings into
	 * {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindOpenNestedGraphOnDoubleClickPolicyAsNodeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("OpenNestedGraphOnDoubleClick"))
				.to(OpenNestedGraphOnDoubleClickPolicy.class);
	}

	/**
	 * Adds a binding for {@link OpenParentGraphOnDoubleClickPolicy} to the
	 * given adapter map binder that will insert the bindings into
	 * {@link FXRootPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindOpenParentGraphOnDoubleClickPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("OpenParentGraphOnDoubleClick"))
				.to(OpenParentGraphOnDoubleClickPolicy.class);
	}

	/**
	 * Adds a binding for
	 * {@link FXDefaultSelectionFeedbackPartFactory#SELECTION_FEEDBACK_GEOMETRY_PROVIDER}
	 * with implementation {@link GeometricOutlineProvider} to the given adapter
	 * map binder that will insert the bindings into {@link EdgeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindSelectionFeedbackGeometryProviderAsEdgeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
	}

	/**
	 * Adds a binding for
	 * {@link FXDefaultSelectionFeedbackPartFactory#SELECTION_FEEDBACK_GEOMETRY_PROVIDER}
	 * with implementation {@link ShapeBoundsProvider} to the given adapter map
	 * binder that will insert the bindings into {@link EdgeLabelPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindSelectionFeedbackGeometryProviderAsEdgeLabelPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
	}

	/**
	 * Adds a binding for
	 * {@link FXDefaultSelectionHandlePartFactory#SELECTION_HANDLES_GEOMETRY_PROVIDER}
	 * with implementation {@link ShapeBoundsProvider} to the given adapter map
	 * binder that will insert the bindings into {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindSelectionHandlesGeometryProviderAsNodeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
	}

	/**
	 * Adds a binding for
	 * {@link FXDefaultSelectionFeedbackPartFactory#SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER}
	 * with implementation {@link GeometricOutlineProvider} to the given adapter
	 * map binder that will insert the bindings into {@link EdgeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindSelectionLinkFeedbackGeometryProviderAsEdgeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
	}

	/**
	 * Adds a binding for
	 * {@link FXDefaultSelectionFeedbackPartFactory#SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER}
	 * with implementation {@link ShapeBoundsProvider} to the given adapter map
	 * binder that will insert the bindings into {@link EdgeLabelPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindSelectionLinkFeedbackGeometryProviderAsEdgeLabelPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link ShowHiddenNeighborsHoverHandlePart} and all sub-classes. May be
	 * overwritten by sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link ShowHiddenNeighborsHoverHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindShowHiddenNeighborsHoverHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		bindShowHiddenNeighborsOfFirstAnchorageOnClickPolicyAsShowHiddenNeighborsHoverHandlePartAdapter(
				adapterMapBinder);
	}

	/**
	 * Adds a binding for
	 * {@link ShowHiddenNeighborsOfFirstAnchorageOnClickPolicy} to the given
	 * adapter map binder that will insert the bindings into
	 * {@link ShowHiddenNeighborsHoverHandlePart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindShowHiddenNeighborsOfFirstAnchorageOnClickPolicyAsShowHiddenNeighborsHoverHandlePartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("showHiddenNeighbors"))
				.to(ShowHiddenNeighborsOfFirstAnchorageOnClickPolicy.class);
	}

	/**
	 * Adds a binding for {@link ShowHiddenNeighborsOnTypePolicy} to the given
	 * adapter map binder that will insert the bindings into
	 * {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindShowHiddenNeighborsOnTypePolicyAsNodeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("showHiddenNeighbors")).to(ShowHiddenNeighborsOnTypePolicy.class);
	}

	/**
	 * Adds a binding for {@link ShowHiddenNeighborsPolicy} to the given adapter
	 * map binder that will insert the bindings into {@link NodeContentPart}s.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the binding is added.
	 */
	protected void bindShowHiddenNeighborsPolicyAsNodeContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ShowHiddenNeighborsPolicy.class);
	}

	@Override
	protected void configure() {
		super.configure();

		bindIContentPartFactory();

		bindGraphContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), GraphContentPart.class));
		bindNodeContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), NodeContentPart.class));
		bindEdgeContentPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), EdgeContentPart.class));
		bindEdgeLabelPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), EdgeLabelPart.class));

		bindFXRectangleSegmentHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), FXRectangleSegmentHandlePart.class));
		bindHidingHoverHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), HideHoverHandlePart.class));
		bindShowHiddenNeighborsHoverHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), ShowHiddenNeighborsHoverHandlePart.class));
	}

}
