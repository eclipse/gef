package org.eclipse.gef4.mvc.parts;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface IContentPartSelectionModel<V> {

	public static final String SELECTION_PROPERTY = "selection";
	
	// TODO: check whether propertyChangeListener is to be replaced with a
	// dedicated listener
	public abstract void addPropertyChangeListener(
			PropertyChangeListener listener);

	public abstract void appendSelection(IContentPart<V> editpart);

	public abstract void deselect(IContentPart<V> editpart);

	public abstract void deselectAll();

	public abstract List<IContentPart<V>> getSelected();

	public abstract void removePropertyChangeListener(
			PropertyChangeListener listener);

	public abstract void select(IContentPart<V>... editparts);

}