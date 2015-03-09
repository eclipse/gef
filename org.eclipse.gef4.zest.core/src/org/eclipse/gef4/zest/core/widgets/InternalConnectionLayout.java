package org.eclipse.gef4.zest.core.widgets;

import java.beans.PropertyChangeListener;

import org.eclipse.gef4.common.properties.PropertyStoreSupport;
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.LayoutProperties;

class InternalConnectionLayout implements IConnectionLayout {

	private final GraphConnection graphConnection;
	private InternalLayoutContext layoutContext;
	private PropertyStoreSupport ps = new PropertyStoreSupport(this);

	/**
	 * @param graphConnection
	 */
	InternalConnectionLayout(GraphConnection graphConnection,
			InternalLayoutContext layoutContext) {
		this.graphConnection = graphConnection;
		this.layoutContext = layoutContext;
		setp(LayoutProperties.VISIBLE_PROPERTY, graphConnection.isVisible());
	}

	public INodeLayout getSource() {
		return this.graphConnection.getSource().getLayout();
	}

	public INodeLayout getTarget() {
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

	private void setp(String name, Object value) {
		ps.setProperty(name, value);
	}

	private Object getp(String name) {
		return ps.getProperty(name);
	}

	public boolean isVisible() {
		return ((Boolean) getp(LayoutProperties.VISIBLE_PROPERTY))
				.booleanValue();
	}

	public void setVisible(boolean visible) {
		layoutContext.checkChangesAllowed();
		setp(LayoutProperties.VISIBLE_PROPERTY, visible);
	}

	void applyLayout() {
		boolean visible = isVisible();
		if (this.graphConnection.isVisible() != visible) {
			this.graphConnection.setVisible(visible);
		}
	}

	public void setProperty(String name, Object value) {
		if (LayoutProperties.VISIBLE_PROPERTY.equals(name)) {
			setVisible((Boolean) value);
		} else {
			setp(name, value);
		}
	}

	public Object getProperty(String name) {
		if (LayoutProperties.DIRECTED_PROPERTY.equals(name)) {
			return isDirected();
		} else if (LayoutProperties.VISIBLE_PROPERTY.equals(name)) {
			return isVisible();
		} else if (LayoutProperties.WEIGHT_PROPERTY.equals(name)) {
			return getWeight();
		} else {
			return getp(name);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		ps.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		ps.addPropertyChangeListener(listener);
	}

}