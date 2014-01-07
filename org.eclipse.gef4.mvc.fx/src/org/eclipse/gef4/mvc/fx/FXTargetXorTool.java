package org.eclipse.gef4.mvc.fx;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.CompositeAndTool;
import org.eclipse.gef4.mvc.tools.ITool;

/**
 * The FXTargetXorTool can be used to activate/deactivate tools depending on the
 * target of the mouse pointer.
 * 
 * <blockquote>
 * 
 * <pre>
 * FXTargetXorTool tools = new FXTargetXorTool();
 * tools.addContentTools(new FXSelectionTool(), new FXRelocateTool());
 * tools.addHandleTools(new FXResizeTool());
 * tools.addVisualTools(new FXSelectionTool(), new HandleTool&lt;Node&gt;());
 * </pre>
 * 
 * </blockquote>
 * 
 * @author wienand
 * 
 */
public class FXTargetXorTool extends AbstractFXCompositeXorTool {

	private CompositeAndTool<Node> contentTools = new CompositeAndTool<Node>();
	private CompositeAndTool<Node> handleTools = new CompositeAndTool<Node>();
	private CompositeAndTool<Node> visualTools = new CompositeAndTool<Node>();

	public FXTargetXorTool() {
		// listen to mouse events only
		super(MouseEvent.ANY);
		add(contentTools);
		add(handleTools);
		add(visualTools);
	}

	@Override
	protected ITool<Node> determineTool(Event event) {
		EventTarget target = event.getTarget();

		if (target instanceof Node) {
			Node node = (Node) target;
			IVisualPart<Node> visualPart = getDomain().getViewer()
					.getVisualPartMap().get(node);

			if (visualPart instanceof IContentPart) {
				return contentTools;
			} else if (visualPart instanceof IHandlePart) {
				return handleTools;
			} else {
				return visualTools;
			}
		}

		return null;
	}

	private boolean isa(EventType<?> type, EventType<?> sType) {
		while (type != null) {
			if (type.equals(sType))
				return true;
			type = type.getSuperType();
		}
		return false;
	}

	@Override
	protected boolean isValid(Event event) {
		EventType<? extends Event> type = event.getEventType();
		return !isa(type, MouseEvent.MOUSE_ENTERED_TARGET)
				&& !isa(type, MouseEvent.MOUSE_EXITED_TARGET);
	}

	// TODO: use List instead of varargs
	public void addContentTools(ITool<Node>... tools) {
		for (ITool<Node> tool : tools) {
			contentTools.add(tool);
		}
	}

	public void addHandleTools(ITool<Node>... tools) {
		for (ITool<Node> tool : tools) {
			handleTools.add(tool);
		}
	}

	public void addVisualTools(ITool<Node>... tools) {
		for (ITool<Node> tool : tools) {
			visualTools.add(tool);
		}
	}

	/**
	 * Provides access to the {@link CompositeAndTool} which is activated for
	 * events with a content part as target.
	 * 
	 * @return the {@link CompositeAndTool} which is activated for events with a
	 *         content part as target
	 */
	public CompositeAndTool<Node> getContentTools() {
		return contentTools;
	}

	/**
	 * Provides access to the {@link CompositeAndTool} which is activated for
	 * events with a handle part as target.
	 * 
	 * @return the {@link CompositeAndTool} which is activated for events with a
	 *         handle part as target
	 */
	public CompositeAndTool<Node> getHandleTools() {
		return handleTools;
	}

	/**
	 * Provides access to the {@link CompositeAndTool} which is activated for
	 * events with a plain visual part as target.
	 * 
	 * @return the {@link CompositeAndTool} which is activated for events with a
	 *         plain visual part as target
	 */
	public CompositeAndTool<Node> getVisualTools() {
		return visualTools;
	}

}
