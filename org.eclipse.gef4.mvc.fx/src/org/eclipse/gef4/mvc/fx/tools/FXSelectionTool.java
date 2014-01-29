package org.eclipse.gef4.mvc.fx.tools;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractSelectionTool;

public class FXSelectionTool extends AbstractSelectionTool<Node> {

	private EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			IVisualPart<Node> targetPart = FXToolUtils.getTargetPart(
					getDomain().getViewer(), event);
			if (targetPart == null) {
				return;
			}

			boolean append = event.isControlDown();
			if (targetPart instanceof IRootVisualPart) {
				select(null, append);
			} else if (targetPart instanceof IContentPart) {
				select((IContentPart<Node>) targetPart, append);
			} else {
				throw new IllegalArgumentException(
						"This tool only supports IRootVisualPart and IContentPart targets");
			}
		}
	};

	@Override
	public void activate() {
		super.activate();
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

	@Override
	public void deactivate() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		super.deactivate();
	}

}
