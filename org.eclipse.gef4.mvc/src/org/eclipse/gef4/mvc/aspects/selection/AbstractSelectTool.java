package org.eclipse.gef4.mvc.aspects.selection;

import java.util.List;

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public abstract class AbstractSelectTool<V> extends AbstractTool<V> {

	@Override
	public void setDomain(IEditDomain<V> domain) {
		super.setDomain(domain);
		domain.setProperty(SelectionModel.class, new SelectionModel<V>());
	}

	@SuppressWarnings("unchecked")
	protected ISelectionPolicy<V> getSelectionPolicy(IEditPart<V> editPart) {
		return editPart.getEditPolicy(ISelectionPolicy.class);
	}

	public void select(IEditPart<V> editPart, boolean append) {
		if (append) {
			// append to selection
			List<IEditPart<V>> oldSelection = getSelectionModel()
					.getSelectedParts();
			if (!oldSelection.isEmpty()) {
				getSelectionPolicy(oldSelection.get(0)).becomeSecondary();
				// viewer selection remains unaffected
			}
		} else {
			// clear selection
			deselectAll();
		}
		ISelectionPolicy<V> selectionPolicy = getSelectionPolicy(editPart);
		if (selectionPolicy != null) {
			selectionPolicy.selectPrimary();
			getSelectionModel().select(editPart);
		}
	}

	@SuppressWarnings("unchecked")
	protected SelectionModel<V> getSelectionModel() {
		return getDomain().getProperty(SelectionModel.class);
	}

	public void deselectAll() {
		List<IEditPart<V>> oldSelection = getSelectionModel()
				.getSelectedParts();
		if (!oldSelection.isEmpty()) {
			for (IEditPart<V> e : oldSelection) {
				getSelectionPolicy(e).deselect();
			}
			getSelectionModel().deselectAll();
		}

	}
}
