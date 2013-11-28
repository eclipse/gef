package org.eclipse.gef4.mvc.javafx;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import org.eclipse.gef4.mvc.parts.AbstractRootVisualPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.partviewer.IVisualPartViewer;

public class FXRootVisualPart extends AbstractRootVisualPart<Node> {

	private ScrollPane scrollPane;
	private StackPane layers;
	private Pane contentLayer;
	private Pane handleLayer;
	
	public FXRootVisualPart() {
		scrollPane = new ScrollPane();
		scrollPane.setPannable(true);
		layers = new StackPane();
		scrollPane.setContent(layers);
		contentLayer = new Pane();
		contentLayer.setPickOnBounds(false);
		layers.getChildren().add(contentLayer);
		handleLayer = new Pane();
		handleLayer.setPickOnBounds(false);
		layers.getChildren().add(handleLayer);
	}

	public Pane getHandleLayer() {
		return handleLayer;
	}

	public Pane getContentLayer() {
		return contentLayer;
	}

	@Override
	public void setViewer(IVisualPartViewer<Node> newViewer) {
		if (getViewer() != null) {
			unregisterVisual();
		}
		if (newViewer != null && !(newViewer instanceof FXViewer)) {
			throw new IllegalArgumentException();
		}
		super.setViewer(newViewer);
		if (getViewer() != null) {
			registerVisual();
		}
	}

	@Override
	public FXViewer getViewer() {
		return (FXViewer) super.getViewer();
	}

	@Override
	public void refreshVisual() {
		// nothing to do
	}

	@Override
	protected void registerVisual() {
		getViewer().getVisualPartMap().put(layers, this);
		for (Node child : layers.getChildren()) {
			// register root edit part also for the layers
			getViewer().getVisualPartMap().put(child, this);
		}
	}

	@Override
	protected void unregisterVisual() {
		getViewer().getVisualPartMap().remove(layers);
		for (Node child : layers.getChildren()) {
			// register root edit part also for the layers
			getViewer().getVisualPartMap().remove(child);
		}
	}

	@Override
	public Node getVisual() {
		return scrollPane;
	}

	// TODO: this contract should be enfored by superclass
	@Override
	protected void addChildVisual(IVisualPart<Node> child, int index) {
		if (child instanceof IContentPart) {
			contentLayer.getChildren().add(index, child.getVisual());
		} else {
			handleLayer.getChildren().add(index, child.getVisual());
		}
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node> child) {
		if (child instanceof IContentPart) {
			contentLayer.getChildren().remove(child.getVisual());
		} else {
			handleLayer.getChildren().remove(child.getVisual());
		}
	}

}
