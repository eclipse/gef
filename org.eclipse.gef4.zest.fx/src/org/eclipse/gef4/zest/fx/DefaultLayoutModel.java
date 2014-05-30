package org.eclipse.gef4.zest.fx;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.layout.interfaces.LayoutContext;

public class DefaultLayoutModel implements ILayoutModel {

	private LayoutContext layoutContext;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public LayoutContext getLayoutContext() {
		return layoutContext;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void setLayoutContext(LayoutContext context) {
		LayoutContext oldContext = layoutContext;
		layoutContext = context;
		if (context != oldContext) {
			pcs.firePropertyChange(LAYOUT_CONTEXT_PROPERTY, oldContext,
					layoutContext);
		}
	}

}
