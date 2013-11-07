package org.eclipse.gef4.mvc.tools;

import java.util.List;

import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;

public abstract class AbstractSelectTool<V> extends AbstractTool<V> {

	@SuppressWarnings("unchecked")
	protected ISelectionPolicy<V> getSelectionPolicy(IEditPart<V> editPart) {
		return editPart.getEditPolicy(ISelectionPolicy.class);
	}

	public void select(IEditPart<V> editPart, boolean append) {
		if (append) {
			// append to selection
			List<IEditPart<V>> oldSelection = getEditDomain()
					.getSelectionAndFocusModel().getSelectedParts();
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
			getEditDomain().getSelectionAndFocusModel().select(editPart);
		}
	}

	public void deselectAll() {
		List<IEditPart<V>> oldSelection = getEditDomain()
				.getSelectionAndFocusModel().getSelectedParts();
		if (!oldSelection.isEmpty()) {
			for (IEditPart<V> e : oldSelection) {
				getSelectionPolicy(e).deselect();
			}
			getEditDomain().getSelectionAndFocusModel().deselectAll();
		}

	}
}
