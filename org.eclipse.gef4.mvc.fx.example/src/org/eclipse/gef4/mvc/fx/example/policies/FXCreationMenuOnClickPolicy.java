package org.eclipse.gef4.mvc.fx.example.policies;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricModelPart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.CreationPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

// TODO: only applicable for FXRootPart
public class FXCreationMenuOnClickPolicy extends AbstractFXClickPolicy {

	private boolean isMenuOpen = false;
	private Popup popup;

	@Override
	public void click(MouseEvent e) {
		// close menu if open
		if (isMenuOpen) {
			closeMenu();
		}

		// open menu on right click
		if (MouseButton.SECONDARY.equals(e.getButton())) {
			EventTarget target = e.getTarget();
			if (target instanceof Node) {
				Node targetNode = (Node) target;
				// check if the event is relevant for us
				if (getHost().getVisual().getScene() == targetNode.getScene()) {
					openMenu(e);
				}
			}
		}
	}

	private void closeMenu() {
		// remove menu items
		popup.hide();
		popup = null;
		isMenuOpen = false;
	}

	protected void create(Object content) {
		IRootPart<Node> root = getHost().getRoot();
		IViewer<Node> viewer = root.getViewer();

		// find model part
		IVisualPart<Node> modelPart = root.getChildren().get(0);
		if (!(modelPart instanceof FXGeometricModelPart)) {
			throw new IllegalStateException("Cannot find FXGeometricModelPart.");
		}

		// build create operation
		CreationPolicy<Node> creationPolicy = root
				.<CreationPolicy<Node>> getAdapter(CreationPolicy.class);
		creationPolicy.init();
		creationPolicy.create((FXGeometricModelPart) modelPart, content);

		// execute on stack
		viewer.getDomain().execute(creationPolicy.commit());

		closeMenu();
	}

	private List<Node> getMenuItems(final MouseEvent e) {
		List<Node> items = new ArrayList<Node>();
		// handle shape
		FXGeometryNode<IGeometry> handleShapeNode = new FXGeometryNode<IGeometry>(
				FXGeometricModel.createHandleShapeGeometry());
		handleShapeNode.setFill(FXGeometricModel.GEF_COLOR_BLUE);
		handleShapeNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// TODO: compute position (e.getSceneX(), e.getSceneY())
				FXGeometricShape handleShape = new FXGeometricShape(
						FXGeometricModel.createHandleShapeGeometry(),
						new AffineTransform(1, 0, 0, 1, e.getSceneX(), e
								.getSceneY()), Color.WHITE,
						FXGeometricModel.GEF_SHADOW_EFFECT);
				create(handleShape);
			}
		});
		items.add(handleShapeNode);
		// TODO
		return items;
	}

	public boolean isMenuOpen() {
		return isMenuOpen;
	}

	private void openMenu(final MouseEvent e) {
		popup = new Popup();
		popup.setX(e.getScreenX());
		popup.setY(e.getScreenY());
		popup.getContent().addAll(getMenuItems(e));
		popup.show(getHost().getVisual().getScene().getWindow());
		isMenuOpen = true;
	}

}
