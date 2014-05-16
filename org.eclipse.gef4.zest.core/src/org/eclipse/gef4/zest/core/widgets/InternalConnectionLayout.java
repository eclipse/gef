package org.eclipse.gef4.zest.core.widgets;

import org.eclipse.gef4.layout.PropertyStoreSupport;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

class InternalConnectionLayout implements ConnectionLayout {

	private final GraphConnection graphConnection;
	private InternalLayoutContext layoutContext;
	private PropertyStoreSupport ps = new PropertyStoreSupport();

	/**
	 * @param graphConnection
	 */
	InternalConnectionLayout(GraphConnection graphConnection,
			InternalLayoutContext layoutContext) {
		this.graphConnection = graphConnection;
		this.layoutContext = layoutContext;
		setProperty(ConnectionLayout.VISIBLE_PROPERTY, graphConnection.isVisible());
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
		return ((Boolean) getProperty(ConnectionLayout.VISIBLE_PROPERTY)).booleanValue();
	}

	public void setVisible(boolean visible) {
		layoutContext.checkChangesAllowed();
		setProperty(ConnectionLayout.VISIBLE_PROPERTY, visible);
	}

	void applyLayout() {
		boolean visible = isVisible();
		if (this.graphConnection.isVisible() != visible) {
			this.graphConnection.setVisible(visible);
		}
	}

	public void setProperty(String name, Object value) {
		ps.setProperty(name, value);
	}

	public Object getProperty(String name) {
		return ps.getProperty(name);
	}

}