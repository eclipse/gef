package org.eclipse.gef4.mvc.fx;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.partviewer.AbstractVisualPartViewer;

public class FXViewer extends AbstractVisualPartViewer<Node> {

	private FXCanvas canvas;

	public FXViewer(FXCanvas canvas) {
		this.canvas = canvas;
		setRootPart(createRootVisualPart());
	}

	/**
	 * Creates the {@link FXRootVisualPart} which provides the root element for
	 * the JavaFX {@link Scene}.
	 * 
	 * @return an {@link FXRootVisualPart}
	 */
	protected FXRootVisualPart createRootVisualPart() {
		return new FXRootVisualPart();
	}

	@Override
	public void setRootPart(IRootVisualPart<Node> editpart) {
		super.setRootPart(editpart);
		if (editpart != null) {
			canvas.setScene(new Scene((Parent) editpart.getVisual()));
		} else {
			canvas.setScene(null);
		}
	}

	public FXCanvas getCanvas() {
		return canvas;
	}

	@Override
	public void reveal(IVisualPart<Node> editpart) {
	}

}
