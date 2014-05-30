package org.eclipse.gef4.zest.fx;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.Node;
import javafx.scene.shape.Polyline;

import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.layout.GraphEdgeLayout;
import org.eclipse.gef4.zest.layout.GraphLayoutContext;

public class EdgeContentPart extends AbstractFXContentPart {

	public static class ArrowHead extends Polyline implements IFXDecoration {
		public ArrowHead() {
			super(15.0, 0.0, 10.0, 0.0, 10.0, 3.0, 0.0, 0.0, 10.0, -3.0, 10.0,
					0.0);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(15, 0);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
	}

	private Edge edge;
	private GraphEdgeLayout edgeLayout;

	private FXCurveConnection curveVisual = new FXCurveConnection();

	private PropertyChangeListener layoutContextListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ILayoutModel.LAYOUT_CONTEXT_PROPERTY.equals(evt
					.getPropertyName())) {
				GraphLayoutContext layoutContext = (GraphLayoutContext) getViewer()
						.getDomain().getProperty(ILayoutModel.class)
						.getLayoutContext();
				if (layoutContext != null) {
					initEdgeLayout();
				}
			}
		}
	};

	public EdgeContentPart(Edge content) {
		edge = content;
	}

	@Override
	public void activate() {
		super.activate();
		getViewer().getDomain().getProperty(ILayoutModel.class)
				.addPropertyChangeListener(layoutContextListener);
	}

	@Override
	public void attachVisualToAnchorageVisual(IVisualPart<Node> anchorage,
			Node anchorageVisual) {
		IContentPart<Node> sourcePart = anchorage.getRoot().getViewer()
				.getContentPartMap().get(edge.getSource());
		IContentPart<Node> targetPart = anchorage.getRoot().getViewer()
				.getContentPartMap().get(edge.getTarget());

		IFXAnchor anchor = ((AbstractFXContentPart) anchorage).getAnchor(this);
		if (anchorage == sourcePart) {
			curveVisual.setStartAnchor(anchor);
		} else if (anchorage == targetPart) {
			curveVisual.setEndAnchor(anchor);
		}

		super.attachVisualToAnchorageVisual(anchorage, anchorageVisual);
	}

	@Override
	public Node getVisual() {
		return curveVisual;
	}

	protected void initEdgeLayout() {
		edgeLayout = ((GraphLayoutContext) getViewer().getDomain()
				.getProperty(ILayoutModel.class).getLayoutContext())
				.getEdgeLayout(edge);
		if (edgeLayout.isDirected()) {
			curveVisual.setStartDecoration(new ArrowHead());
		} else {
			curveVisual.setStartDecoration(null);
		}
	}

	@Override
	public void refreshVisual() {
		if (edgeLayout == null) {
			return;
		}
	}

}
