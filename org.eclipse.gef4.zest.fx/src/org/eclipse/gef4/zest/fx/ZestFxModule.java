package org.eclipse.gef4.zest.fx;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

public class ZestFxModule extends MvcFxModule {

	@Override
	protected void bindAbstractDomainAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		super.bindAbstractDomainAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(ILayoutModel.class).to(
				DefaultLayoutModel.class);
	}

	@Override
	protected void bindAbstractFXHandlePartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		super.bindAbstractFXHandlePartAdapters(adapterMapBinder);
		// TODO: resize relocate on handle drag policy cannot be bound
		// here because its constructor expects a reference point parameter
		// adapterMapBinder.addBinding(
		// FXClickDragTool.DRAG_TOOL_POLICY_KEY).to(
		// FXResizeRelocateOnHandleDragPolicy.class);
	}

	@Override
	protected void bindFXRootPart() {
		binder().bind(new TypeLiteral<IRootPart<Node>>() {
		}).annotatedWith(Names.named("AbstractViewer")).to(GraphRootPart.class);
	}

	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).annotatedWith(Names.named("AbstractViewer"))
				.to(ContentPartFactory.class);
	}

	private void bindNodeContentPartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(NodeLayoutPolicy.class).to(
				NodeLayoutPolicy.class);
		adapterMapBinder.addBinding(NodeLayoutBehavior.class).to(
				NodeLayoutBehavior.class);
		// interaction
		adapterMapBinder.addBinding(FXClickDragTool.DRAG_TOOL_POLICY_KEY).to(
				FXRelocateOnDragPolicy.class);
		// transaction
		adapterMapBinder.addBinding(FXResizeRelocatePolicy.class).to(
				FXResizeRelocatePolicy.class);
	}

	@Override
	protected void configure() {
		super.configure();
		bindIContentPartFactory();
		bindNodeContentPartAdapters(getAdapterMapBinder(NodeContentPart.class));
	}

}
