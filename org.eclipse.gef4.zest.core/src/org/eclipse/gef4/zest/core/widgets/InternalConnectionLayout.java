package org.eclipse.gef4.zest.core.widgets;

import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

class InternalConnectionLayout implements ConnectionLayout {

	private final GraphConnection graphConnection;
	private InternalLayoutContext layoutContext;
	private boolean visible;

	/**
	 * @param graphConnection
	 */
	InternalConnectionLayout(GraphConnection graphConnection,
			InternalLayoutContext layoutContext) {
		this.graphConnection = graphConnection;
		this.layoutContext = layoutContext;
		this.visible = graphConnection.isVisible();
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
		return visible;
	}

	public void setVisible(boolean visible) {
		layoutContext.checkChangesAllowed();
		this.visible = visible;
	}

	void applyLayout() {
		if (this.graphConnection.isVisible() != this.visible) {
			this.graphConnection.setVisible(this.visible);
		}
	}
}