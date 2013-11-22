package org.eclipse.gef4.mvc.javafx;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import org.eclipse.gef4.mvc.parts.AbstractRootEditPart;
import org.eclipse.gef4.mvc.parts.IContentsEditPart;
import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;

public class FXRootEditPart extends AbstractRootEditPart<Node> {

	private ScrollPane scrollPane;
	private Group layers;
	private Pane primaryLayer;
	private Pane handleLayer;

	public FXRootEditPart() {
		scrollPane = new ScrollPane();
		scrollPane.setPannable(true);
		layers = new Group();
		scrollPane.setContent(layers);
		primaryLayer = new Pane();
		layers.getChildren().add(primaryLayer);
		handleLayer = new Pane();
		layers.getChildren().add(handleLayer);
	}
	
	public Pane getHandleLayer() {
		return handleLayer;
	}

	public Pane getPrimaryLayer() {
		return primaryLayer;
	}

	@Override
	public void setViewer(IEditPartViewer<Node> newViewer) {
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
		for(Node child : layers.getChildren()){
			// register root edit part also for the layers
			getViewer().getVisualPartMap().put(child, this);
		}
	}

	@Override
	protected void unregisterVisual() {
		getViewer().getVisualPartMap().remove(layers);
		for(Node child : layers.getChildren()){
			// register root edit part also for the layers
			getViewer().getVisualPartMap().remove(child);
		}
	}

	@Override
	public Node getVisual() {
		return scrollPane;
	}

	@Override
	protected void addChildVisual(IContentsEditPart<Node> child, int index) {
		primaryLayer.getChildren().add(index, child.getVisual());
	}

	@Override
	protected void removeChildVisual(IContentsEditPart<Node> child) {
		primaryLayer.getChildren().remove(child.getVisual());
	}

	@Override
	protected boolean isNodeModel(Object model) {
		return true;
	}

	@Override
	protected boolean isConnectionModel(Object model) {
		return false;
	}

}
