package org.eclipse.gef4.zest.fx;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;

import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr;
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

	private static final double GAP_LENGTH = 7d;
	private static final double DASH_LENGTH = 7d;
	private static final Double DOT_LENGTH = 1d;

	private Edge edge;
	private GraphEdgeLayout edgeLayout;
	private Group visuals = new Group();
	private Text text = new Text();
	private FXCurveConnection curveVisual = new FXCurveConnection() {
		@Override
		protected void refreshGeometry() {
			super.refreshGeometry();
			Bounds textBounds = text.getLayoutBounds();
			Rectangle bounds = curveVisual.getCurveNode().getGeometry()
					.getBounds();
			text.setTranslateX(bounds.getX() + bounds.getWidth() / 2
					- textBounds.getWidth() / 2);
			text.setTranslateY(bounds.getY() + bounds.getHeight() / 2
					- textBounds.getHeight());
		}
	};

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
		visuals.setAutoSizeChildren(false);
		visuals.getChildren().addAll(curveVisual, text);
		text.setTextOrigin(VPos.TOP);
		Object label = edge.getAttrs().get(Attr.Key.LABEL.toString());
		if (label instanceof String) {
			text.setText((String) label);
		}
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
		return visuals;
	}

	protected void initEdgeLayout() {
		edgeLayout = ((GraphLayoutContext) getViewer().getDomain()
				.getProperty(ILayoutModel.class).getLayoutContext())
				.getEdgeLayout(edge);

		// decoration
		if (edgeLayout.isDirected()) {
			curveVisual.setEndDecoration(new ArrowHead());
		} else {
			curveVisual.setEndDecoration(null);
		}

		// color
		if (!edgeLayout.isVisible()) {
			curveVisual.getCurveNode().setStroke(Color.TRANSPARENT);
		} else {
			curveVisual.getCurveNode().setStroke(Color.BLACK);
		}

		// dashes
		Object style = edge.getAttrs()
				.get(Graph.Attr.Key.EDGE_STYLE.toString());
		if (style == Graph.Attr.Value.LINE_DASH) {
			curveVisual.getCurveNode().getStrokeDashArray()
					.setAll(DASH_LENGTH, GAP_LENGTH);
		} else if (style == Graph.Attr.Value.LINE_DASHDOT) {
			curveVisual.getCurveNode().getStrokeDashArray()
					.setAll(DASH_LENGTH, GAP_LENGTH, DOT_LENGTH, GAP_LENGTH);
		} else if (style == Graph.Attr.Value.LINE_DASHDOTDOT) {
			curveVisual
					.getCurveNode()
					.getStrokeDashArray()
					.setAll(DASH_LENGTH, GAP_LENGTH, DOT_LENGTH, GAP_LENGTH,
							DOT_LENGTH, GAP_LENGTH);
		} else if (style == Graph.Attr.Value.LINE_DOT) {
			curveVisual.getCurveNode().getStrokeDashArray()
					.setAll(DOT_LENGTH, GAP_LENGTH);
		} else {
			curveVisual.getCurveNode().getStrokeDashArray().clear();
		}
	}

	@Override
	public void refreshVisual() {
		if (edgeLayout == null) {
			return;
		}
	}

}
