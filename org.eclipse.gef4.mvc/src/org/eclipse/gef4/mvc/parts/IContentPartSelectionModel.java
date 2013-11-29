package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.gef4.mvc.IPropertyChangeSupport;

public interface IContentPartSelectionModel<V> extends IPropertyChangeSupport {

	public static final String SELECTION_PROPERTY = "selection";
	
	public abstract void appendSelection(IContentPart<V> editpart);

	public abstract void deselect(IContentPart<V> editpart);

	public abstract void deselectAll();

	public abstract List<IContentPart<V>> getSelected();

	public abstract void select(IContentPart<V>... editparts);

}