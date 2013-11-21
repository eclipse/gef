package org.eclipse.gef4.mvc.aspects.selection;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;

public class SelectionModel<V> {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	private static final String SELECTION = "selection";
	private List<IEditPart<V>> selection = new ArrayList<IEditPart<V>>();

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * @see IEditPartViewer#appendSelection(IEditPart)
	 */
	public void appendSelection(IEditPart<V> editpart) {
		List<IEditPart<V>> oldSelection = getSelectionCopy();
		selection.add(editpart);
		propertyChangeSupport.firePropertyChange(SELECTION, oldSelection,
				getSelectionCopy());
	}

	/**
	 * @see IEditPartViewer#deselect(IEditPart)
	 */
	public void deselect(IEditPart<V> editpart) {
		List<IEditPart<V>> oldSelection = getSelectionCopy();
		selection.remove(editpart);
		propertyChangeSupport.firePropertyChange(SELECTION, oldSelection,
				getSelectionCopy());
	}

	/**
	 * @see IEditPartViewer#deselectAll()
	 */
	public void deselectAll() {
		List<IEditPart<V>> oldSelection = getSelectionCopy();
		selection.clear();
		propertyChangeSupport.firePropertyChange(SELECTION, oldSelection,
				getSelectionCopy());
	}

	public List<IEditPart<V>> getSelection() {
		return Collections.unmodifiableList(selection);
	}

	private List<IEditPart<V>> getSelectionCopy() {
		List<IEditPart<V>> oldSelection = new ArrayList<IEditPart<V>>(selection);
		return oldSelection;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @see IEditPartViewer#select(IEditPart)
	 */
	public void select(IEditPart<V> editpart) {
		List<IEditPart<V>> oldSelection = getSelectionCopy();
		selection.remove(editpart);
		selection.add(0, editpart);
		propertyChangeSupport.firePropertyChange(SELECTION, oldSelection,
				getSelectionCopy());
	}

	public void setSelection(List<IEditPart<V>> selection) {
		List<IEditPart<V>> oldSelection = getSelectionCopy();
		this.selection.clear();
		this.selection.addAll(selection);
		propertyChangeSupport.firePropertyChange(SELECTION, oldSelection,
				getSelectionCopy());
	}
}
