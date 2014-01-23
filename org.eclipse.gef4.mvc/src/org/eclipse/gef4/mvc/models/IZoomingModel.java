package org.eclipse.gef4.mvc.models;

import org.eclipse.gef4.mvc.IPropertyChangeSupport;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;

/**
 * The IZoomingModel is used to store the current viewer's zoom factor. A
 * zooming tool is used to update the IZoomingModel which in turn asks the
 * {@link IRootVisualPart} for an {@link IZoomingPolicy} which is used to adapt
 * the zoom factor from the model to the view.
 * 
 * @author wienand
 * 
 */
public interface IZoomingModel extends IPropertyChangeSupport {

	/**
	 * The IZoomingModel fires {@link PropertyChangeEvent}s when its zoom factor
	 * changes. This is the key used to identify the zoom factor property when
	 * listening to multiple property change supporters.
	 */
	public static final String ZOOM_FACTOR_PROPERTY = "ZoomFactor";

	/**
	 * This is the default/initial zoom factor.
	 */
	public static final double DEFAULT_ZOOM_FACTOR = 1d;

	/**
	 * Returns the zoom factor. The zoom factor is a positive value.
	 * 
	 * @return the zoom factor
	 */
	public double getZoomFactor();

	/**
	 * Sets the zoom factor to the given value. Fires a
	 * {@link PropertyChangeEvent}.
	 * 
	 * @param zoomFactor
	 *            a positive floating point value stored as the zoom factor
	 * @throws IllegalArgumentException
	 *             when <code><i>zoomFactor</i> &lt;= 0</code>
	 */
	public void setZoomFactor(double zoomFactor);

}
