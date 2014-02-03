package org.eclipse.gef4.mvc.fx.parts;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.AbstractRootPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

public class FXRootPart extends AbstractRootPart<Node> {

	/**
	 * Per default, a ScrollPane draws a border and background color. We do not
	 * want either.
	 */
	private static final String SCROLL_PANE_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);";

	private ScrollPane scrollPane;
	private StackPane layers;
	private Pane contentLayer;
	private Pane handleLayer;
	private Pane feedbackLayer;

	public FXRootPart() {
		scrollPane = new ScrollPane();
		scrollPane.setPannable(false);
		scrollPane.setStyle(SCROLL_PANE_STYLE);

		layers = new StackPane();
		scrollPane.setContent(new Group(layers));

		contentLayer = createLayer(false);
		handleLayer = createLayer(false);
		feedbackLayer = createLayer(true);
	}

	private Pane createLayer(boolean mouseTransparent) {
		Pane layer = new Pane();
		layer.setPickOnBounds(false);
		layer.setMouseTransparent(mouseTransparent);
		layers.getChildren().add(layer);
		return layer;
	}

	public ScrollPane getScrollPane() {
		return scrollPane;
	}

	public Pane getHandleLayer() {
		return handleLayer;
	}

	public Pane getContentLayer() {
		return contentLayer;
	}

	// TODO: we need feedback parts if we want to differentiate this (otherwise remove this layer)
	public Pane getFeedbackLayer() {
		return feedbackLayer;
	}

	@Override
	public void setViewer(IVisualPartViewer<Node> newViewer) {
		if (getViewer() != null) {
			unregisterFromVisualPartMap();
		}
		if (newViewer != null && !(newViewer instanceof FXViewer)) {
			throw new IllegalArgumentException();
		}
		super.setViewer(newViewer);
		if (getViewer() != null) {
			registerAtVisualPartMap();
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
	protected void registerAtVisualPartMap() {
		getViewer().getVisualPartMap().put(layers, this);
		for (Node child : layers.getChildren()) {
			// register root edit part also for the layers
			getViewer().getVisualPartMap().put(child, this);
		}
	}

	@Override
	protected void unregisterFromVisualPartMap() {
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

	@Override
	protected void addChildVisual(IVisualPart<Node> child, int index) {
		if (child instanceof IContentPart) {
			int contentLayerIndex = 0;
			for(int i=0; i<index; i++){
				if(i < getChildren().size() && getChildren().get(i) instanceof IContentPart){
					contentLayerIndex++;
				}
			}
			contentLayer.getChildren().add(contentLayerIndex, child.getVisual());
		} else {
			int handleLayerIndex = 0;
			for(int i=0; i<index; i++){
				if(i < getChildren().size() && !(getChildren().get(i) instanceof IContentPart)){
					handleLayerIndex++;
				}
			}
			handleLayer.getChildren().add(handleLayerIndex, child.getVisual());
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
