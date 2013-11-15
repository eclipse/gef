package org.eclipse.gef4.mvc.aspects.selection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public abstract class AbstractSelectTool<V> extends AbstractTool<V> {

	@Override
	public void setDomain(IEditDomain<V> domain) {
		super.setDomain(domain);
		if (domain != null) {
			domain.setProperty(SelectionModel.class, new SelectionModel<V>());
		}
	}

	@SuppressWarnings("unchecked")
	protected ISelectionPolicy<V> getSelectionPolicy(IEditPart<V> editPart) {
		return editPart.getEditPolicy(ISelectionPolicy.class);
	}

	/**
	 * 
	 * @param targetEditPart
	 * @param append
	 * @return <code>true</code> on selection change, otherwise <code>false</code>
	 */
	public boolean select(IEditPart<V> targetEditPart, boolean append) {
		boolean changed = true;
		
		SelectionModel<V> selectionModel = getSelectionModel();
		// retrieve old selection
		List<IEditPart<V>> oldSelection = new ArrayList<IEditPart<V>>(
				selectionModel.getSelectedParts());
		// determine new selection
		if (targetEditPart == null) {
			// remove all selected
			selectionModel.deselectAll();
		} else {
			if (oldSelection.contains(targetEditPart)) {
				if (append) {
					// deselect the target edit part (ensure we get a new
					// primary selection)
					selectionModel.deselect(targetEditPart);
				} else {
					// target should become the new primary selection
//					selectionModel.select(targetEditPart);
					changed = false;
				}
			} else {
				if (append) {
					// append to current selection (as new primary)
					selectionModel.select(targetEditPart);
				} else {
					// clear old selection, target should become the only
					// selected
					selectionModel.deselectAll();
					selectionModel.select(targetEditPart);
				}				
			}
		}
		// handle adjustment of selection feedback (via edit policy)
		List<IEditPart<V>> newSelection = selectionModel.getSelectedParts();
		oldSelection.removeAll(newSelection);
		adjustFeedback(oldSelection, newSelection);
		
		return changed;
	}

	protected void adjustFeedback(List<IEditPart<V>> deselected,
			List<IEditPart<V>> selected) {
		// deselect unselected
		for (IEditPart<V> e : deselected) {
			getSelectionPolicy(e).deselect();
		}
		// select newly selected
		for (int i = 0; i < selected.size(); i++) {
			ISelectionPolicy<V> selectionPolicy = getSelectionPolicy(selected
					.get(i));
			if (i == 0) {
				selectionPolicy.selectPrimary();
			} else {
				selectionPolicy.selectSecondary();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected SelectionModel<V> getSelectionModel() {
		return getDomain().getProperty(SelectionModel.class);
	}

}
