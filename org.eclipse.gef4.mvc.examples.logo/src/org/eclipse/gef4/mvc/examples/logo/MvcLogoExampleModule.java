/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdaptableScopes;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.examples.logo.behaviors.FXClickableAreaBehavior;
import org.eclipse.gef4.mvc.examples.logo.parts.FXCreateCurveHoverHandlePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXDeleteHoverHandlePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXLogoContentPartFactory;
import org.eclipse.gef4.mvc.examples.logo.parts.FXLogoHoverHandlePartFactory;
import org.eclipse.gef4.mvc.examples.logo.parts.FXLogoSelectionHandlePartFactory;
import org.eclipse.gef4.mvc.examples.logo.policies.CloneCurvePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.CloneShapePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXBendCurvePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCloneOnClickPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCreateCurveOnDragPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCreationMenuItemProvider;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCreationMenuOnClickPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXDeleteFirstAnchorageOnClickPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXRelocateLinkedOnDragPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXResizeShapePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXTransformCurvePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXTransformShapePolicy;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHoverFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXRectangleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.policies.FXDeleteSelectedOnTypePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeTransformSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeTranslateOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRotateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.providers.ChopBoxAnchorProvider;
import org.eclipse.gef4.mvc.fx.providers.GeometricOutlineProvider;
import org.eclipse.gef4.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

import javafx.scene.Node;

public class MvcLogoExampleModule extends MvcFxModule {

	@Override
	protected void bindAbstractContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		// select on click
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXFocusAndSelectOnClickPolicy.class);
	}

	@Override
	protected void bindAbstractRootPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractRootPartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.role("1"))
				.to(FXCreationMenuOnClickPolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXCreationMenuOnClickPolicy.MENU_ITEM_PROVIDER_ROLE))
				.to(FXCreationMenuItemProvider.class);
		// interaction policy to delete on key type
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXDeleteSelectedOnTypePolicy.class);
	}

	protected void bindFXCreateCurveHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(FXCreateCurveOnDragPolicy.class);
	}

	protected void bindFXDeleteHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(FXDeleteFirstAnchorageOnClickPolicy.class);
	}

	protected void bindFXGeometricCurvePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hover on hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXHoverOnHoverPolicy.class);

		// geometry provider for selection feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for selection handles
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for hover feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// transaction policy for resize + transform
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXResizeConnectionPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXBendCurvePolicy.class);

		// interaction policy to relocate on drag
		adapterMapBinder.addBinding(AdapterKey.role("1"))
				.to(FXTranslateSelectedOnDragPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXTransformCurvePolicy.class);

		// cloning
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(CloneCurvePolicy.class);

		// clickable area resizing
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXClickableAreaBehavior.class);

		// clone on shift+click
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(FXCloneOnClickPolicy.class);
	}

	protected void bindFXGeometricShapePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hover on hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXHoverOnHoverPolicy.class);

		// geometry provider for selection feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		// geometry provider for selection handles
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for hover feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// register resize/transform policies (writing changes also to model)
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXTransformShapePolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXResizeShapePolicy.class);

		// relocate on drag (including anchored elements, which are linked)
		adapterMapBinder.addBinding(AdapterKey.role("1"))
				.to(FXTranslateSelectedOnDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.role("2"))
				.to(FXRelocateLinkedOnDragPolicy.class);

		// clone
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(CloneShapePolicy.class);

		// bind chopbox anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ChopBoxAnchorProvider.class);

		// clone on shift+click
		adapterMapBinder.addBinding(AdapterKey.role("1"))
				.to(FXCloneOnClickPolicy.class);
	}

	protected void bindFXRectangleSegmentHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// single selection: resize relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(FXResizeTranslateOnHandleDragPolicy.class);
		// rotate on drag + control
		adapterMapBinder.addBinding(AdapterKey.role("1"))
				.to(FXRotateSelectedOnHandleDragPolicy.class);

		// multi selection: scale relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.role("2"))
				.to(FXResizeTransformSelectedOnHandleDragPolicy.class);
	}

	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).toInstance(new FXLogoContentPartFactory());
	}

	@Override
	protected void bindIHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).annotatedWith(
				Names.named(SelectionBehavior.PART_FACTORIES_BINDING_NAME))
				.to(FXLogoSelectionHandlePartFactory.class)
				.in(AdaptableScopes.typed(FXViewer.class));
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).annotatedWith(Names.named(HoverBehavior.PART_FACTORIES_BINDING_NAME))
				.to(FXLogoHoverHandlePartFactory.class)
				.in(AdaptableScopes.typed(FXViewer.class));
	}

	@Override
	protected void configure() {
		super.configure();

		bindIContentPartFactory();

		// contents
		bindFXGeometricShapePartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), FXGeometricShapePart.class));
		bindFXGeometricCurvePartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), FXGeometricCurvePart.class));

		// rectangle handles
		bindFXRectangleSegmentHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(),
						FXRectangleSegmentHandlePart.class));

		// hover handles
		bindFXDeleteHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				FXDeleteHoverHandlePart.class));
		bindFXCreateCurveHandlePartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), FXCreateCurveHoverHandlePart.class));
	}

}