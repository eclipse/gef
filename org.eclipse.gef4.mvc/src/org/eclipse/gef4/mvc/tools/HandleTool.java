package org.eclipse.gef4.mvc.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;

public class HandleTool<V> extends AbstractTool<V> implements
		PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		// TODO: does the viewer need to provide a property change mechanism as
		// well, so we are notified in case contentpartselection changes??
		getDomain().getViewer().getContentPartSelection()
				.addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		getDomain().getViewer().getContentPartSelection()
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ISelectionModel.SELECTION_PROPERTY.equals(evt
				.getPropertyName())) {
			IRootVisualPart<V> rootPart = getRoot();
			if (rootPart != null) {
				List<IHandlePart<V>> currentHandleParts = rootPart
						.getHandleParts();
				if (!currentHandleParts.isEmpty()) {
					rootPart.removeHandleParts(currentHandleParts);
				}
				if (getHandlePartFactory() != null) {
					List<IContentPart<V>> newSelection = (List<IContentPart<V>>) evt
							.getNewValue();
					if (!newSelection.isEmpty()) {
						rootPart.addHandleParts(getHandlePartFactory()
								.createHandleParts(newSelection));
					}
				}
			}
		}

	}

	private IHandlePartFactory<V> getHandlePartFactory() {
		return getDomain().getViewer().getHandlePartFactory();
	}

	private IRootVisualPart<V> getRoot() {
		return getDomain().getViewer().getRootPart();
	}

}
