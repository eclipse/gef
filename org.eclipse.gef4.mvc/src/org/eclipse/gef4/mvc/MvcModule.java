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
package org.eclipse.gef4.mvc;

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.bindings.AdaptableTypeListener;
import org.eclipse.gef4.mvc.bindings.AdapterMaps;
import org.eclipse.gef4.mvc.domain.AbstractDomain;
import org.eclipse.gef4.mvc.models.DefaultContentModel;
import org.eclipse.gef4.mvc.models.DefaultFocusModel;
import org.eclipse.gef4.mvc.models.DefaultHoverModel;
import org.eclipse.gef4.mvc.models.DefaultSelectionModel;
import org.eclipse.gef4.mvc.models.DefaultViewportModel;
import org.eclipse.gef4.mvc.models.DefaultZoomModel;
import org.eclipse.gef4.mvc.models.IContentModel;
import org.eclipse.gef4.mvc.models.IFocusModel;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.models.IViewportModel;
import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.AbstractFeedbackPart;
import org.eclipse.gef4.mvc.parts.AbstractHandlePart;
import org.eclipse.gef4.mvc.parts.AbstractRootPart;
import org.eclipse.gef4.mvc.parts.AbstractVisualPart;
import org.eclipse.gef4.mvc.policies.DefaultHoverPolicy;
import org.eclipse.gef4.mvc.policies.DefaultSelectionPolicy;
import org.eclipse.gef4.mvc.policies.DefaultZoomPolicy;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.google.inject.util.Types;

public class MvcModule<VR> extends AbstractModule {

	@Override
	protected void configure() {
		// register type listener to be notified about IAdaptable injections;
		// the listener will register a members injector that injects adapters
		// into appropriate subclasses
		AdaptableTypeListener adaptableTypeListener = new AdaptableTypeListener();
		requestInjection(adaptableTypeListener);
		bindListener(Matchers.any(), adaptableTypeListener);

		// bind domain adapters
		bindAbstractDomainAdapters(getAdapterMapBinder(AbstractDomain.class));

		// bind viewer adapters
		bindAbstractViewerAdapters(getAdapterMapBinder(AbstractViewer.class));

		// bind visual part (and subtypes) adapters; not that via type listener
		// and custom members injector, subclass adapters will additionally be
		// injected into the respective subclass.
		bindAbstractVisualPartAdapters(getAdapterMapBinder(AbstractVisualPart.class));
		bindAbstractRootPartAdapters(getAdapterMapBinder(AbstractRootPart.class));
		bindAbstractContentPartAdapters(getAdapterMapBinder(AbstractContentPart.class));
		bindAbstractFeedbackPartAdapters(getAdapterMapBinder(AbstractFeedbackPart.class));
		bindAbstractHandlePartAdapters(getAdapterMapBinder(AbstractHandlePart.class));
	}

	private void bindAbstractHandlePartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		// nothing to bind by default
	}

	protected void bindAbstractFeedbackPartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		// nothing to bind by default
	}

	protected void bindAbstractRootPartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		// register (default) behaviors
		adapterMapBinder.addBinding(ContentBehavior.class).to(
				Key.get(Types.newParameterizedType(ContentBehavior.class,
						new TypeLiteral<VR>() {
						}.getRawType().getClass())));

		// register (default) policies
		adapterMapBinder.addBinding(DefaultHoverPolicy.class).to(
				Key.get(Types.newParameterizedType(DefaultHoverPolicy.class,
						new TypeLiteral<VR>() {
						}.getRawType().getClass())));
		adapterMapBinder.addBinding(DefaultSelectionPolicy.class).to(
				Key.get(Types.newParameterizedType(
						DefaultSelectionPolicy.class, new TypeLiteral<VR>() {
						}.getRawType().getClass())));
		adapterMapBinder.addBinding(DefaultZoomPolicy.class).to(
				Key.get(Types.newParameterizedType(DefaultZoomPolicy.class,
						new TypeLiteral<VR>() {
						}.getRawType().getClass())));
	}

	protected void bindAbstractVisualPartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		// nothing to bind by default
	}

	protected void bindAbstractContentPartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {

		// bind default behaviors
		adapterMapBinder.addBinding(ContentBehavior.class).to(
				Key.get(Types.newParameterizedType(ContentBehavior.class,
						new TypeLiteral<VR>() {
						}.getRawType().getClass())));

		// bind default policies
		adapterMapBinder.addBinding(DefaultHoverPolicy.class).to(
				Key.get(Types.newParameterizedType(DefaultHoverPolicy.class,
						new TypeLiteral<VR>() {
						}.getRawType().getClass())));
		adapterMapBinder.addBinding(DefaultSelectionPolicy.class).to(
				Key.get(Types.newParameterizedType(
						DefaultSelectionPolicy.class, new TypeLiteral<VR>() {
						}.getRawType().getClass())));
		adapterMapBinder.addBinding(DefaultZoomPolicy.class).to(
				Key.get(Types.newParameterizedType(DefaultZoomPolicy.class,
						new TypeLiteral<VR>() {
						}.getRawType().getClass())));
	}

	protected void bindAbstractDomainAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		// bind IUndoContext and IOperationHistory to reasonable defaults
		binder().bind(IUndoContext.class)
				.annotatedWith(Names.named("AbstractDomain"))
				.toInstance(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		binder().bind(IOperationHistory.class)
				.annotatedWith(Names.named("AbstractDomain"))
				.to(DefaultOperationHistory.class);
	}

	protected void bindAbstractViewerAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		// bind (default) viewer models
		adapterMapBinder.addBinding(IContentModel.class).to(
				DefaultContentModel.class);
		adapterMapBinder.addBinding(IViewportModel.class).to(
				DefaultViewportModel.class);
		adapterMapBinder.addBinding(IZoomModel.class)
				.to(DefaultZoomModel.class);
		adapterMapBinder.addBinding(IFocusModel.class).to(
				Key.get(Types.newParameterizedType(DefaultFocusModel.class,
						new TypeLiteral<VR>() {
						}.getRawType().getClass())));
		adapterMapBinder.addBinding(IHoverModel.class).to(
				Key.get(Types.newParameterizedType(DefaultHoverModel.class,
						new TypeLiteral<VR>() {
						}.getRawType().getClass())));
		adapterMapBinder.addBinding(ISelectionModel.class).to(
				Key.get(Types.newParameterizedType(DefaultSelectionModel.class,
						new TypeLiteral<VR>() {
						}.getRawType().getClass())));
	}

	protected MapBinder<Class<?>, Object> getAdapterMapBinder(Class<?> type) {
		return MapBinder.newMapBinder(binder(), new TypeLiteral<Class<?>>() {
		}, new TypeLiteral<Object>() {
		}, AdapterMaps.typed(type));
	}
}
