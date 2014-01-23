package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef4.mvc.IPropertyChangeSupport;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public interface IHoverModel<V> extends IPropertyChangeSupport {

	/**
	 * The {@link IHoverModel} fires {@link PropertyChangeEvent}s when the
	 * hovered part changes. This is the name of the property that is delivered
	 * with the event.
	 */
	final public static String HOVER_PROPERTY = "Hover";

	/**
	 * Returns the currently hovered {@link IVisualPart} or <code>null</code> if
	 * no visual part is hovered.
	 * 
	 * @return the currently hovered {@link IVisualPart} or <code>null</code>
	 */
	public IVisualPart<V> getHover();

	/**
	 * Sets the hovered {@link IVisualPart} to the given value. The given part
	 * may be <code>null</code> in order to unhover.
	 * <p>
	 * Fires a {@link PropertyChangeEvent}.
	 * 
	 * @param cp
	 *            hovered {@link IVisualPart} or <code>null</code>
	 */
	public void setHover(IVisualPart<V> cp);

	/**
	 * Sets the hovered part to <code>null</code>.
	 * <p>
	 * Fires a {@link PropertyChangeEvent}.
	 */
	public void clearHover();

}
