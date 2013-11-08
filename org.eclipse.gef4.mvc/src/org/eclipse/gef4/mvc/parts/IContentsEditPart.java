package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.gef4.mvc.partviewer.IEditPartFactory;

public interface IContentsEditPart<V> extends IEditPart<V> {

	/**
	 * <img src="doc-files/dblack.gif"/>Sets the parent. This should only be
	 * called by the parent EditPart.
	 * 
	 * @param parent
	 *            the parent EditPart
	 */
	void setParent(IEditPart<V> parent);

	/**
	 * Returns the parent <code>EditPart</code>. This method should only be
	 * called internally or by helpers such as EditPolicies.
	 * 
	 * @return <code>null</code> or the parent {@link IEditPart}
	 */
	IEditPart<V> getParent();

	/**
	 * <img src="doc-files/dblack.gif"/>Sets the model. This method is made
	 * public to facilitate the use of {@link IEditPartFactory
	 * EditPartFactories} .
	 * 
	 * <P>
	 * IMPORTANT: This method should only be called once.
	 * 
	 * @param model
	 *            the Model
	 */
	void setModel(Object model);

	/**
	 * Returns the primary model object that this EditPart represents. EditParts
	 * may correspond to more than one model object, or even no model object. In
	 * practice, the Object returned is used by other EditParts to identify this
	 * EditPart. In addition, EditPolicies probably rely on this method to build
	 * Commands that operate on the model.
	 * 
	 * @return <code>null</code> or the primary model object
	 */
	Object getModel();

}
