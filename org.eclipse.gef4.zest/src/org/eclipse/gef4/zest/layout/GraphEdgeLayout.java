package org.eclipse.gef4.zest.layout;

import java.util.Map.Entry;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.PropertyStoreSupport;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

public class GraphEdgeLayout implements ConnectionLayout {

	// property names
	public static final String WEIGHT_PROPERTY = "weight";
	public static final String DIRECTED_PROPERTY = "directed";

	// defaults
	private static final Double DEFAULT_WEIGHT = 0d;
	private static final Boolean DEFAULT_DIRECTED = false;
	private static final Boolean DEFAULT_VISIBLE = true;

	private GraphLayoutContext context;
	private Edge edge;
	private PropertyStoreSupport pss = new PropertyStoreSupport();

	public GraphEdgeLayout(GraphLayoutContext context, Edge edge) {
		this.context = context;
		this.edge = edge;

		// graph directed?
		Object type = context.getGraph().getAttrs()
				.get(Graph.Attr.Key.GRAPH_TYPE.toString());
		if (type == Graph.Attr.Value.CONNECTIONS_DIRECTED
				|| type == Graph.Attr.Value.GRAPH_DIRECTED) {
			setProperty(DIRECTED_PROPERTY, true);
		}

		// copy properties
		for (Entry<String, Object> e : edge.getAttrs().entrySet()) {
			setProperty(e.getKey(), e.getValue());
		}
	}

	@Override
	public Object getProperty(String name) {
		return pss.getProperty(name);
	}

	@Override
	public NodeLayout getSource() {
		return context.getNodeLayout(edge.getSource());
	}

	@Override
	public NodeLayout getTarget() {
		return context.getNodeLayout(edge.getTarget());
	}

	@Override
	public double getWeight() {
		Object weight = getProperty(WEIGHT_PROPERTY);
		if (!(weight instanceof Double)) {
			weight = DEFAULT_WEIGHT;
			setProperty(WEIGHT_PROPERTY, weight);
		}
		return ((Double) weight).doubleValue();
	}

	@Override
	public boolean isDirected() {
		Object directed = getProperty(DIRECTED_PROPERTY);
		if (!(directed instanceof Boolean)) {
			directed = DEFAULT_DIRECTED;
			setProperty(DIRECTED_PROPERTY, directed);
		}
		return ((Boolean) directed).booleanValue();
	}

	@Override
	public boolean isVisible() {
		Object visible = getProperty(VISIBLE_PROPERTY);
		if (!(visible instanceof Double)) {
			visible = DEFAULT_VISIBLE;
			setProperty(VISIBLE_PROPERTY, visible);
		}
		return ((Boolean) visible).booleanValue();
	}

	@Override
	public void setProperty(String name, Object value) {
		pss.setProperty(name, value);
	}

	@Override
	public void setVisible(boolean visible) {
		setProperty(VISIBLE_PROPERTY, visible);
	}

}
