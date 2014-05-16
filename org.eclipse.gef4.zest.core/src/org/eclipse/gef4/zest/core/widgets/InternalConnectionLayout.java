package org.eclipse.gef4.zest.core.widgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

class InternalConnectionLayout implements ConnectionLayout {

	private static final String VISIBLE_PROPERTY = "visible";

	private final GraphConnection graphConnection;
	private InternalLayoutContext layoutContext;

	private Map<String, Object> attr = new HashMap<String, Object>();

	/**
	 * @param graphConnection
	 */
	InternalConnectionLayout(GraphConnection graphConnection,
			InternalLayoutContext layoutContext) {
		this.graphConnection = graphConnection;
		this.layoutContext = layoutContext;
		setAttr(VISIBLE_PROPERTY, graphConnection.isVisible());
	}

	public NodeLayout getSource() {
		return this.graphConnection.getSource().getLayout();
	}

	public NodeLayout getTarget() {
		return this.graphConnection.getDestination().getLayout();
	}

	public double getWeight() {
		return this.graphConnection.getWeightInLayout();
	}

	public boolean isDirected() {
		return !ZestStyles.checkStyle(
				this.graphConnection.getConnectionStyle(),
				ZestStyles.CONNECTIONS_DIRECTED);
	}

	public boolean isVisible() {
		return ((Boolean) getAttr(VISIBLE_PROPERTY)).booleanValue();
	}

	public void setVisible(boolean visible) {
		layoutContext.checkChangesAllowed();
		setAttr(VISIBLE_PROPERTY, visible);
	}

	void applyLayout() {
		boolean visible = isVisible();
		if (this.graphConnection.isVisible() != visible) {
			this.graphConnection.setVisible(visible);
		}
	}

	public void setAttr(String key, Object value) {
		attr.put(key, value);
	}

	public Object getAttr(String key) {
		return attr.get(key);
	}

}