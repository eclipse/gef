package org.eclipse.gef4.mvc.tests.stubs;

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef4.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef4.common.adapt.inject.AdapterMaps;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.tests.ContentSynchronizationTests;
import org.eclipse.gef4.mvc.tests.ContentSynchronizationTests.ContentPartFactory;
import org.eclipse.gef4.mvc.tests.ContentSynchronizationTests.TreeContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class Module extends AbstractModule {
	@Override
	protected void configure() {
		install(new AdapterInjectionSupport());
		// undo context and operation history (required because of field
		// injections)
		binder().bind(IUndoContext.class).to(UndoContext.class).in(AdaptableScopes.typed(IDomain.class));
		binder().bind(IOperationHistory.class).to(DefaultOperationHistory.class)
				.in(AdaptableScopes.typed(IDomain.class));
		// bind default viewer models
		binder().bind(ContentModel.class).in(AdaptableScopes.typed(IViewer.class));
		// bind factories (required because of field injections)
		binder().bind(new TypeLiteral<IHandlePartFactory<Object>>() {
		}).to(HandlePartFactory.class);
		binder().bind(new TypeLiteral<IFeedbackPartFactory<Object>>() {
		}).to(FeedbackPartFactory.class);
		binder().bind(new TypeLiteral<IContentPartFactory<Object>>() {
		}).toInstance(new ContentPartFactory());
		// bind domain, viewer, and root part
		binder().bind(new TypeLiteral<IDomain<Object>>() {
		}).to(Domain.class);
		binder().bind(new TypeLiteral<IViewer<Object>>() {
		}).to(Viewer.class);
		binder().bind(new TypeLiteral<IRootPart<Object, ? extends Object>>() {
		}).to(RootPart.class);
		// bind Viewer as adapter for Domain
		AdapterMaps.getAdapterMapBinder(binder(), Domain.class).addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<IViewer<Object>>() {
				});
		// bind RootPart as viewer adapter
		MapBinder<AdapterKey<?>, Object> viewerAdapterMapBinder = AdapterMaps.getAdapterMapBinder(binder(),
				Viewer.class);
		viewerAdapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<IRootPart<Object, ? extends Object>>() {
				});
		viewerAdapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ContentModel.class);
		viewerAdapterMapBinder.addBinding(AdapterKey.defaultRole()).to(new TypeLiteral<HoverModel<Object>>() {
		});
		viewerAdapterMapBinder.addBinding(AdapterKey.defaultRole()).to(new TypeLiteral<SelectionModel<Object>>() {
		});
		// bind ContentBehavior for RootPart
		MapBinder<AdapterKey<?>, Object> rootPartAdapterMapBinder = AdapterMaps.getAdapterMapBinder(binder(),
				RootPart.class);
		rootPartAdapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<ContentBehavior<Object>>() {
				});
		// bind ContentBehavior for the TreeContentPart
		AdapterMaps.getAdapterMapBinder(binder(), TreeContentPart.class).addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<ContentBehavior<Object>>() {
				});
	}
}