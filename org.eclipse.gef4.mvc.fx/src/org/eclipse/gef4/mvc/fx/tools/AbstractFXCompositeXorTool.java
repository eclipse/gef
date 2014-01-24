package org.eclipse.gef4.mvc.fx.tools;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.tools.CompositeXorTool;
import org.eclipse.gef4.mvc.tools.ITool;

public abstract class AbstractFXCompositeXorTool extends CompositeXorTool<Node> {

	private EventType<?> eventType = Event.ANY;

	public AbstractFXCompositeXorTool() {
	}

	public AbstractFXCompositeXorTool(EventType<?> eventType) {
		this.eventType = eventType;
	}

	private EventHandler<Event> filter = new EventHandler<Event>() {
		@Override
		public void handle(Event event) {
			if (isValid(event)) {
				ITool<Node> tool = determineTool(event);
				selectTool(tool);
			}
		}
	};

	protected abstract ITool<Node> determineTool(Event event);

	protected boolean isValid(Event event) {
		return true;
	}

	@Override
	public void registerListeners() {
//		System.out.println(this + ".registerListeners()");
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventFilter(eventType, filter);
	}

	@Override
	public void unregisterListeners() {
//		System.out.println(this + ".unregisterListeners()");
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.removeEventFilter(eventType, filter);
	}

}
