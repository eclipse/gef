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
package org.eclipse.gef4.mvc.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleContentPartFactory;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleDeleteHandlePart;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleHandlePartFactory;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.fx.example.policies.FXCreationMenuOnClickPolicy;
import org.eclipse.gef4.mvc.fx.example.policies.FXExampleDeleteFirstAnchorageOnClickPolicy;
import org.eclipse.gef4.mvc.fx.example.policies.FXExampleDeletionPolicy;
import org.eclipse.gef4.mvc.fx.example.policies.FXRelocateLinkedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.example.policies.FXResizeRelocateShapePolicy;
import org.eclipse.gef4.mvc.fx.parts.ChopBoxAnchorProvider;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.VisualBoundsGeometryProvider;
import org.eclipse.gef4.mvc.fx.parts.VisualOutlineGeometryProvider;
import org.eclipse.gef4.mvc.fx.policies.FXDeleteSelectedOnTypePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.policies.CreationPolicy;
import org.eclipse.gef4.mvc.policies.DeletionPolicy;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class FXExampleModule extends MvcFxModule {

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
		// geometry provider for selection handles
		adapterMapBinder
				.addBinding(
						AdapterKey
								.get(new TypeToken<Provider<IGeometry>>() {
								},
										FXDefaultHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(VisualBoundsGeometryProvider.class);
		adapterMapBinder
				.addBinding(
						AdapterKey
								.get(new TypeToken<Provider<IGeometry>>() {
								},
										FXDefaultFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(VisualOutlineGeometryProvider.class);

		// geometry provider for hover feedback
		adapterMapBinder
				.addBinding(
						AdapterKey
								.get(new TypeToken<Provider<IGeometry>>() {
								},
										FXDefaultFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(VisualBoundsGeometryProvider.class);

		// deletion policy
		adapterMapBinder.addBinding(AdapterKey.get(DeletionPolicy.class)).to(
				FXExampleDeletionPolicy.class);
	}

	@Override
	protected void bindAbstractRootPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractRootPartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.get(CreationPolicy.class)).to(
				CreationPolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY,
						"FXCreationMenuOnClick")).to(
				FXCreationMenuOnClickPolicy.class);
	}

	protected void bindFXExampleDeleteHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(
						AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY,
								"delete")).to(
						FXExampleDeleteFirstAnchorageOnClickPolicy.class);
	}

	protected void bindFXGeometricCurvePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// interaction policy to relocate on drag
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY)).to(
				FXRelocateOnDragPolicy.class);

		// interaction policy to delete on key type
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY))
				.to(FXDeleteSelectedOnTypePolicy.class);

	}

	@SuppressWarnings("serial")
	protected void bindFXGeometricShapePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {

		// interaction policies to relocate on drag (including anchored
		// elements, which are linked)
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY)).to(
				FXRelocateOnDragPolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY,
						"relocateLinked")).to(
				FXRelocateLinkedOnDragPolicy.class);

		// interaction policy to delete on key type
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY))
				.to(FXDeleteSelectedOnTypePolicy.class);

		// transaction policy to relocate (writing changes also to model)
		adapterMapBinder.addBinding(
				(AdapterKey.get(FXResizeRelocatePolicy.class))).to(
				FXResizeRelocateShapePolicy.class);

		// bind chopbox anchor provider
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<IFXAnchor>>() {
				})).to(ChopBoxAnchorProvider.class);
	}

	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).toInstance(new FXExampleContentPartFactory());
	}

	@Override
	protected void bindIHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).toInstance(new FXExampleHandlePartFactory());
	}

	@Override
	protected void configure() {
		super.configure();

		bindIContentPartFactory();

		bindFXGeometricShapePartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), FXGeometricShapePart.class));

		bindFXGeometricCurvePartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), FXGeometricCurvePart.class));

		bindFXExampleDeleteHandlePartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), FXExampleDeleteHandlePart.class));
	}

}