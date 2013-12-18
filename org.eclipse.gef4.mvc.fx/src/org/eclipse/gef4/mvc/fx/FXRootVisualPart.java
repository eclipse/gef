package org.eclipse.gef4.mvc.fx;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import org.eclipse.gef4.mvc.parts.AbstractRootVisualPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.partviewer.IVisualPartViewer;

public class FXRootVisualPart extends AbstractRootVisualPart<Node> {

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

	public FXRootVisualPart() {
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

	public Pane getFeedbackLayer() {
		return feedbackLayer;
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
