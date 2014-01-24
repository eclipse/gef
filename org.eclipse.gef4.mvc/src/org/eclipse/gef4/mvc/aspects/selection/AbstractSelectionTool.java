package org.eclipse.gef4.mvc.aspects.selection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

public abstract class AbstractSelectionTool<V> extends AbstractTool<V> implements PropertyChangeListener {

	@Override
	public void setDomain(IEditDomain<V> domain) {
		super.setDomain(domain);
	}

	@SuppressWarnings("unchecked")
	protected AbstractSelectionPolicy<V> getSelectionPolicy(
			IVisualPart<V> editPart) {
		return editPart.getEditPolicy(AbstractSelectionPolicy.class);
	}

	/**
	 * 
	 * @param targetEditPart
	 * @param append
	 * @return <code>true</code> on selection change, otherwise
	 *         <code>false</code>
	 */
	public boolean select(IContentPart<V> targetPart, boolean append) {
		// TODO: extract into tool policy
		boolean changed = true;

		ISelectionModel<V> selectionModel = getSelectionModel();
		// retrieve old selection
		List<IContentPart<V>> oldSelection = new ArrayList<IContentPart<V>>(
				selectionModel.getSelected());
		// determine new selection
		if (targetPart == null) {
			// remove all selected
			selectionModel.deselectAll();
		} else {
			if (oldSelection.contains(targetPart)) {
				if (append) {
					// deselect the target edit part (ensure we get a new
					// primary selection)
					selectionModel.deselect(targetPart);
				} else {
					// target should become the new primary selection
					// selectionModel.select(targetEditPart);
					changed = false;
				}
			} else {
				if (append) {
					// append to current selection (as new primary)
					selectionModel.select(targetPart);
				} else {
					// clear old selection, target should become the only
					// selected
					selectionModel.deselectAll();
					selectionModel.select(targetPart);
				}
			}
		}
		// handle adjustment of selection feedback (via edit policy)
		List<IContentPart<V>> newSelection = selectionModel.getSelected();
		oldSelection.removeAll(newSelection);
		adjustFeedback(oldSelection, newSelection);

		return changed;
	}

	protected void adjustFeedback(List<IContentPart<V>> deselected,
			List<IContentPart<V>> selected) {
		// deselect unselected
		for (IVisualPart<V> e : deselected) {
			AbstractSelectionPolicy<V> policy = getSelectionPolicy(e);
			if (policy != null)
				policy.deselect();
		}
		// select newly selected
		for (int i = 0; i < selected.size(); i++) {
			AbstractSelectionPolicy<V> policy = getSelectionPolicy(selected
					.get(i));
			if (policy != null) {
				if (i == 0) {
					policy.selectPrimary();
				} else {
					policy.selectSecondary();
				}
			}
		}
	}

	protected ISelectionModel<V> getSelectionModel() {
		return getDomain().getViewer().getContentPartSelection();
	}
	
	@Override
	public void activate() {
		super.activate();
		getDomain().getViewer().addPropertyChangeListener(this);
	}
	
	@Override
	public void deactivate() {
		getDomain().getViewer().removePropertyChangeListener(this);
		super.deactivate();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(IVisualPartViewer.CONTENTS_PROPERTY)) {
			select(null, false);
		}
	}

}
