package org.eclipse.gef4.mvc.policies;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef4.mvc.models.IZoomModel;

/**
 * The AbstractZoomPolicy registers a listener on the {@link IZoomModel} and
 * notifies subclasses about zoom factor changes in order for subclasses to
 * apply the new zoom factor.
 * 
 * @author wienand
 * 
 */
abstract public class AbstractZoomPolicy<V> extends AbstractEditPolicy<V>
		implements PropertyChangeListener {

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (IZoomModel.ZOOM_FACTOR_PROPERTY.equals(evt.getPropertyName())) {
			applyZoomFactor((Double) evt.getNewValue());
		}
	}

	/**
	 * Applies the given zoom factor in the context of this policy. For example,
	 * you can register the policy on the root visual part and apply it to all
	 * layers.
	 * 
	 * @param zoomFactor
	 */
	abstract protected void applyZoomFactor(Double zoomFactor);

}
