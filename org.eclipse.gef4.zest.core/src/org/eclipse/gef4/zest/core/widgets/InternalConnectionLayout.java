package org.eclipse.gef4.zest.core.widgets;

import java.beans.PropertyChangeListener;

import org.eclipse.gef4.common.notify.PropertyStoreSupport;
import org.eclipse.gef4.layout.LayoutPropertiesHelper;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

class InternalConnectionLayout implements ConnectionLayout {

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
		setp(LayoutPropertiesHelper.VISIBLE_PROPERTY,
				graphConnection.isVisible());
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

	private void setp(String name, Object value) {
		ps.setProperty(name, value);
	}

	private Object getp(String name) {
		return ps.getProperty(name);
	}

	public boolean isVisible() {
		return ((Boolean) getp(LayoutPropertiesHelper.VISIBLE_PROPERTY))
				.booleanValue();
	}

	public void setVisible(boolean visible) {
		layoutContext.checkChangesAllowed();
		setp(LayoutPropertiesHelper.VISIBLE_PROPERTY, visible);
	}

	void applyLayout() {
		boolean visible = isVisible();
		if (this.graphConnection.isVisible() != visible) {
			this.graphConnection.setVisible(visible);
		}
	}

	public void setProperty(String name, Object value) {
		if (LayoutPropertiesHelper.VISIBLE_PROPERTY.equals(name)) {
			setVisible((Boolean) value);
		} else {
			setp(name, value);
		}
	}

	public Object getProperty(String name) {
		if (LayoutPropertiesHelper.DIRECTED_PROPERTY.equals(name)) {
			return isDirected();
		} else if (LayoutPropertiesHelper.VISIBLE_PROPERTY.equals(name)) {
			return isVisible();
		} else if (LayoutPropertiesHelper.WEIGHT_PROPERTY.equals(name)) {
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