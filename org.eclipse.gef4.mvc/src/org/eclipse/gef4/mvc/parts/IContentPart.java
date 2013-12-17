package org.eclipse.gef4.mvc.parts;

import org.eclipse.gef4.mvc.anchors.IAnchor;

/**
 * An {@link IEditPart} that visualizes an underlying content model element.
 * 
 * @author nyssen
 */
// TODO: parameterize with model; rename model operations -> content
public interface IContentPart<V> extends IVisualPart<V> {

	/**
	 * <img src="doc-files/dblack.gif"/>Sets the model. This method is made
	 * public to facilitate the use of {@link IContentPartFactory
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

	void synchronizeContentChildren();
	
	// returns an anchor (linked to this part as anchorage), which is used to fix the given part
	IAnchor<V> getAnchor(IContentPart<V> fixedPart);

}
