/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPart.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

/**
 * An {@link IVisualPart} that visualizes an underlying content model element.
 * 
 * @author anyssen
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

}
