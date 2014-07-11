package org.eclipse.gef4.zest.core.widgets;

import org.eclipse.gef4.layout.PropertiesHelper;
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
		setp(PropertiesHelper.VISIBLE_PROPERTY, graphConnection.isVisible());
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

	// TODO: replace with PropertiesHelper.setX calls
	private void setp(String name, Object value) {
		ps.setProperty(name, value);
	}

	// TODO: replace with PropertiesHelper.getX calls
	private Object getp(String name) {
		return ps.getProperty(name);
	}

	public boolean isVisible() {
		return ((Boolean) getp(PropertiesHelper.VISIBLE_PROPERTY))
				.booleanValue();
	}

	public void setVisible(boolean visible) {
		layoutContext.checkChangesAllowed();
		setp(PropertiesHelper.VISIBLE_PROPERTY, visible);
	}

	void applyLayout() {
		boolean visible = isVisible();
		if (this.graphConnection.isVisible() != visible) {
			this.graphConnection.setVisible(visible);
		}
	}

	public void setProperty(String name, Object value) {
		if (PropertiesHelper.VISIBLE_PROPERTY.equals(name)) {
			setVisible((Boolean) value);
		} else {
			setp(name, value);
		}
	}

	public Object getProperty(String name) {
		if (PropertiesHelper.DIRECTED_PROPERTY.equals(name)) {
			return isDirected();
		} else if (PropertiesHelper.VISIBLE_PROPERTY.equals(name)) {
			return isVisible();
		} else if (PropertiesHelper.WEIGHT_PROPERTY.equals(name)) {
			return getWeight();
		} else {
			return getp(name);
		}
	}

}