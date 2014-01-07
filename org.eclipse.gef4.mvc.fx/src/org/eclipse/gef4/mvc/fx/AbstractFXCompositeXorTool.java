package org.eclipse.gef4.mvc.fx;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.tools.AbstractCompositeXorTool;
import org.eclipse.gef4.mvc.tools.ITool;

public abstract class AbstractFXCompositeXorTool extends
		AbstractCompositeXorTool<Node> {

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
	protected void register() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventFilter(eventType, filter);
	}

	@Override
	protected void unregister() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.removeEventFilter(eventType, filter);
	}

}
