package org.eclipse.gef4.mvc.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;

public class SelectionAndFocusModel<V> {
	
	private IEditPart<V> focus;
	private List<IEditPart<V>> selection = new ArrayList<IEditPart<V>>();

	/**
	 * @see IEditPartViewer#appendSelection(IEditPart)
	 */
	public void appendSelection(IEditPart<V> editpart) {
		selection.add(editpart);
	}

	/**
	 * @see IEditPartViewer#deselect(IEditPart)
	 */
	public void deselect(IEditPart<V> editpart) {
		selection.remove(editpart);
	}

	/**
	 * @see IEditPartViewer#deselectAll()
	 */
	public void deselectAll() {
		selection.clear();
	}

	
	/**
	 * @see IEditPartViewer#getFocusPart()
	 */
	public IEditPart<V> getFocusPart() {
		return focus;
	}
	
	public List<IEditPart<V>> getSelectedParts() {
		return Collections.unmodifiableList(selection);
	}
	
	/**
	 * @see IEditPartViewer#setFocusPart(IEditPart)
	 */
	public void setFocusPart(IEditPart<V> part) {
		focus = part;
	}
	
	/**
	 * @see IEditPartViewer#select(IEditPart)
	 */
	public void select(IEditPart<V> editpart) {
		selection.remove(editpart);
		selection.add(0,editpart);
	}
}
