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

	private AnchorPane rootAnchorPane;
	private ScrollPane scrollPane;
	private Group layers;
	private Pane primaryLayer;

	public FXRootEditPart() {
		rootAnchorPane = new AnchorPane();
		scrollPane = new ScrollPane();
		
		// fix 
		scrollPane.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				scrollPane.requestFocus();
			}
		});
		
		// anchor constraints for the ScrollPane
		rootAnchorPane.getChildren().add(scrollPane);
		AnchorPane.setBottomAnchor(scrollPane, 0d);
		AnchorPane.setTopAnchor(scrollPane, 0d);
		AnchorPane.setLeftAnchor(scrollPane, 0d);
		AnchorPane.setRightAnchor(scrollPane, 0d);
		
		layers = new Group();
		scrollPane.setContent(layers);
		primaryLayer = new Pane();
		layers.getChildren().add(primaryLayer);
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
	}

	@Override
	protected void unregisterVisual() {
		getViewer().getVisualPartMap().remove(layers);
	}

	@Override
	public Node getVisual() {
		return rootAnchorPane;
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
