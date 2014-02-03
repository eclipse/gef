package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultContentModel implements IContentModel {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private List<Object> contents = new ArrayList<Object>();	

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void setContents(List<Object> contents) {
		List<Object> oldContents = Collections.unmodifiableList(new ArrayList<Object>(this.contents));
		this.contents.clear();
		this.contents.addAll(contents);
		pcs.firePropertyChange(CONTENTS_PROPERTY, oldContents, getContents());
	}
	
	@Override
	public List<Object> getContents() {
		return Collections.unmodifiableList(this.contents);
	}
}
